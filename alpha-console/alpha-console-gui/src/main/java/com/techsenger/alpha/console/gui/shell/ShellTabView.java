/*
 * Copyright 2018-2026 Pavel Castornii.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.techsenger.alpha.console.gui.shell;

import com.techsenger.alpha.api.Framework;
import com.techsenger.alpha.api.FrameworkMode;
import com.techsenger.alpha.api.message.MessagePrinter;
import com.techsenger.alpha.console.gui.session.SessionsToolBarView;
import com.techsenger.alpha.console.gui.textstyle.StyledText;
import com.techsenger.alpha.console.gui.utils.LogPrinterView;
import com.techsenger.mvvm4fx.core.ComponentHelper;
import com.techsenger.tabshell.core.TabShellView;
import com.techsenger.tabshell.kit.core.style.CoreIcons;
import com.techsenger.tabshell.kit.core.style.StyleClasses;
import com.techsenger.tabshell.kit.core.style.StyleUtils;
import com.techsenger.tabshell.kit.material.textarea.ExtendedTextArea;
import com.techsenger.tabshell.kit.material.textarea.RichTextFxUtils;
import com.techsenger.tabshell.kit.material.textarea.TextAreaStyle;
import com.techsenger.tabshell.kit.text.viewer.AbstractViewerTabView;
import com.techsenger.tabshell.material.icon.FontIconView;
import com.techsenger.toolkit.fx.utils.NodeUtils;
import com.techsenger.toolkit.fx.value.ValueUtils;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Tooltip;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.fxmisc.richtext.Caret;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.fxmisc.richtext.model.StyledDocument;

/**
 *
 * @author Pavel Castornii
 */
public class ShellTabView extends AbstractViewerTabView<ShellTabViewModel> {

    private static class BlockingTextArea extends ExtendedTextArea {

        private final ShellTabViewModel viewModel;

        private boolean outputPrinting = false;

        BlockingTextArea(ShellTabViewModel viewModel) {
            this.viewModel = viewModel;
            addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
                if (getCaretPosition() < viewModel.getPromptPosition()) {
                    if ((event.isControlDown() && event.getCode() == KeyCode.V)
                            || (event.isShiftDown() && event.getCode() == KeyCode.INSERT)) {
                        event.consume();
                    }
                } else if (getCaretPosition() == viewModel.getPromptPosition()) {
                    if (event.getCode() == KeyCode.BACK_SPACE || event.getCode() == KeyCode.LEFT) {
                        event.consume();
                    }
                }
            });
        }

        @Override
        public void replaceText(int start, int end, String text) {
            if (outputPrinting || getCaretPosition() >= viewModel.getPromptPosition()) {
                super.replaceText(start, end, text);
            }
        }

        @Override
        public void replace(int start, int end, StyledDocument<Collection<String>, String,
                Collection<TextAreaStyle>> replacement) {
            if (outputPrinting || getCaretPosition() >= viewModel.getPromptPosition()) {
                super.replace(start, end, replacement);
            }
        }

        @Override
        public void replace(int start, int end, String seg, Collection<TextAreaStyle> style) {
            if (outputPrinting || getCaretPosition() >= viewModel.getPromptPosition()) {
                super.replace(start, end, seg, style);
            }
        }

        public boolean isOutputPrinting() {
            return outputPrinting;
        }

        public void setOutputPrinting(boolean outputPrinting) {
            this.outputPrinting = outputPrinting;
        }
    }

    private final StackPane stackPane = new StackPane();

    private AttributeWindowView attributeWindow;

    /**
     * Character width in text area. Required for message printer.
     */
    private double characterWidth;

    private final BlockingTextArea textArea;

    private final ShellHighlighter highlighter;

    private final ChangeListener<? super Font> fontListener = (ov, t, t1) -> {
        characterWidth = StyleUtils.getMonospaceCharWidth(t1);
    };

    private final LogMessagePrinter logPrinter;

    private final MessagePrinter printer;

    private final SessionsToolBarView sessions;

    private final Button clearButton = new Button(null, new FontIconView(CoreIcons.CLEAR));

    private final MenuItem cutItem = new MenuItem("Cut", new FontIconView(CoreIcons.CUT));

    private final MenuItem pasteItem = new MenuItem("Paste", new FontIconView(CoreIcons.PASTE));

    public ShellTabView(TabShellView<?> tabShell, ShellTabViewModel viewModel) {
        super(tabShell, viewModel, new BlockingTextArea(viewModel));
        this.textArea = (BlockingTextArea) getTextArea();
        this.logPrinter = new LogMessagePrinter(textArea, getTextScrollPane());
        this.printer = new TextAreaMessagePrinter(textArea);
        if (viewModel.getSessions() != null) {
            sessions = new SessionsToolBarView(viewModel.getSessions());
            sessions.initialize();
        } else {
            sessions = null;
        }
        this.highlighter =  new ShellHighlighter(viewModel.getCommandInfosManager());
    }

    public void cut() {
        getTextArea().cut();
    }

    public void paste() {
        getTextArea().paste();
    }

    @Override
    protected void build(ShellTabViewModel viewModel) {
        super.build(viewModel);
        var scrollPane = this.getTextScrollPane();

        var toolBars = new HBox(getToolBar());
        clearButton.setTooltip(new Tooltip("Clear"));
        clearButton.getStyleClass().add(StyleClasses.ICONED_BUTTON);
        //toolbar
        getToolBar().getItems().addAll(clearButton, new Separator(Orientation.VERTICAL), getCopyButton(),
                getFindButton());
        if (Framework.getMode() == FrameworkMode.CLIENT) {
            toolBars.getChildren().add(sessions.getNode());
        }
        HBox.setHgrow(getToolBar(), Priority.ALWAYS);
        textArea.getStyleClass().add("shell-text-area");
        textArea.setEditable(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        stackPane.getChildren().add(scrollPane);
        stackPane.setMargin(scrollPane, new Insets(0, 0, 0, 0));
        VBox.setVgrow(stackPane, Priority.ALWAYS);

        var css = ShellTabView.class.getResource("shell.css").toExternalForm();
        getTopPane().getStylesheets().add(css);
        css = LogPrinterView.class.getResource("log-levels.css").toExternalForm();
        getTopPane().getStylesheets().add(css);
        getTopPane().getChildren().addAll(toolBars, stackPane);
        cutItem.setAccelerator(new KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_DOWN));
        pasteItem.setAccelerator(new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN));
        getTextAreaMenu().getItems().addAll(cutItem, getCopyItem(), pasteItem);
        NodeUtils.requestFocus(this.getTextArea());
    }

    @Override
    protected void addListeners(ShellTabViewModel viewModel) {
        super.addListeners(viewModel);

        viewModel.getPromptTexts().addListener((value) -> {
            if (value != null) {
                this.printPromptTexts(value);
                if (!textArea.isFocused()) {
                    textArea.requestFocus();
                }
            }
        });
        viewModel.getTexts().addListener((value) -> {
            if (value != null) {
                this.printTexts(value);
                if (!textArea.isFocused()) {
                    textArea.requestFocus();
                }
            }
        });

        textArea.caretPositionProperty().addListener((ov, t, t1) -> {
            if (t1 < viewModel.getPromptPosition()) {
                textArea.setShowCaret(Caret.CaretVisibility.OFF);
            } else {
                textArea.setShowCaret(Caret.CaretVisibility.AUTO);
            }
        });

        //highliter work only 1) when it is enabled and 2) processes only text after prompt
        //1) because we don't need to highlight words in messages, errors etc
        //2) because highlighter clears all other style classes for example, it can clear styles of messages, errors etc
        textArea.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.length() > 0 && viewModel.isHighlighterEnabled()) {
                textArea.setStyleSpans(viewModel.getPromptPosition(),
                        highlighter.computeHighlighting(newText.substring(viewModel.getPromptPosition())));
            }
        });

        var settings = viewModel.getSettings();
        ValueUtils.callAndAddListener(settings.fontProperty(), fontListener);
        textArea.widthProperty().addListener((ov, oldV, newV) -> recalculateOutputWidth());
        viewModel.getMessages().addListener((messages) -> this.printer.print(messages));
        viewModel.getLogMessages().addListener((messages) -> this.logPrinter.print(messages));
    }

    @Override
    protected void addHandlers(ShellTabViewModel viewModel) {
        super.addHandlers(viewModel);
        clearButton.setOnAction((e) -> {
            if (!textArea.isOutputPrinting()) {
                textArea.setOutputPrinting(true);
                textArea.clear();
                viewModel.showPrompt();
                textArea.moveTo(viewModel.getPromptLength());
                textArea.requestFocus();
                textArea.setShowCaret(Caret.CaretVisibility.AUTO);
                textArea.setOutputPrinting(false);
            }
        });
        //to disable text gragging
        textArea.setOnSelectionDropped(EH -> { });
        textArea.setOnKeyPressed((event) -> {
            if (event.getCode() == KeyCode.SPACE && event.isControlDown()) {
                event.consume();
                viewModel.openAttributeWindow();
            } else if (event.getCode() == KeyCode.ESCAPE && !event.isControlDown()) {
                event.consume();
                viewModel.closeAttributeWindow();
            }
        });

        textArea.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
            if (textArea.getCaretPosition() >= viewModel.getPromptPosition()) {
                if (event.getCode() == KeyCode.UP) {
                    var currentWindowView = this.attributeWindow;
                    if (currentWindowView != null) {
                        currentWindowView.getViewModel().selectUp();
                    } else {
                        var index = viewModel.scrollHistoryUp();
                        scrollHistory(index);
                    }
                    event.consume();
                } else if (event.getCode() == KeyCode.DOWN) {
                    var currentWindowView = this.attributeWindow;
                    if (currentWindowView != null) {
                        currentWindowView.getViewModel().selectDown();
                    } else {
                        var index = viewModel.scrollHistoryDown();
                        scrollHistory(index);
                    }
                    event.consume();
                } else if (event.getCode() == KeyCode.ENTER) {
                    event.consume();
                    var currentWindowView = this.attributeWindow;
                    if (currentWindowView != null) {
                        viewModel.doOnListViewSelected();
                        viewModel.closeAttributeWindow();
                    } else {
                        //we add eol manually to prevent adding the line separator in the middle of the line
                        this.textArea.appendText("\n");
                        viewModel.executeCommands();
                    }
                } else if (event.getCode() == KeyCode.HOME) {
                    event.consume();
                    this.textArea.moveTo(viewModel.getPromptPosition());
                }
            }
        });
        cutItem.setOnAction((t) -> getTextArea().cut());
        pasteItem.setOnAction((t) -> getTextArea().paste());
        getTextArea().addEventHandler(ContextMenuEvent.CONTEXT_MENU_REQUESTED, event -> {
            cutItem.setDisable(!viewModel.isCutItemValid());
            pasteItem.setDisable(!viewModel.isPasteItemValid());
        });
    }

    @Override
    protected void postInitialize(ShellTabViewModel viewModel) {
        super.postInitialize(viewModel);
        this.getViewModel().showPrompt();
        this.getTextArea().requestFocus();
    }

    @Override
    protected void removeListeners(ShellTabViewModel viewModel) {
        super.removeListeners(viewModel);
        var settings = viewModel.getSettings();
        settings.fontProperty().removeListener(fontListener);
    }

    @Override
    protected void postDeinitialize(ShellTabViewModel viewModel) {
        super.postDeinitialize(viewModel);
        viewModel.getCommandInfosManager().deinitialize();
    }

    @Override
    protected ComponentHelper<?> createComponentHelper() {
        return new ShellTabHelper(this);
    }

    @Override
    protected ExtendedTextArea getTextArea() {
        return super.getTextArea();
    }

    void openAttributeWindow(AttributeWindowView attributeWindow) {
        this.attributeWindow = attributeWindow;
        this.stackPane.getChildren().add(this.attributeWindow.getNode());
        this.attributeWindow.getListView().setOnMouseClicked((e) -> {
            if (e.getClickCount() == 2) {
                getViewModel().doOnListViewSelected();
                getViewModel().closeAttributeWindow();
            }
        });
    }

    void closeAttributeWindow() {
        this.stackPane.getChildren().remove(this.attributeWindow.getNode());
        this.attributeWindow.deinitialize();
        this.attributeWindow = null;
    }

    private void scrollHistory(int index) {
        var textArea = this.getTextArea();
        var viewModel = this.getViewModel();
        var lastCommands = viewModel.getLastCommands();
        if (0 <= index &&  index < lastCommands.size()) {
            viewModel.setCommandScrollingIndex(index);
            textArea.deleteText(viewModel.getPromptPosition(), textArea.getText().length());
            textArea.appendText(lastCommands.get(index));
        } else if (index >= lastCommands.size()) {
            viewModel.setCommandScrollingIndex(-1);
            textArea.deleteText(viewModel.getPromptPosition(), textArea.getText().length());
        }
    }

    private void recalculateOutputWidth() {
        var viewModel = getViewModel();
        //vertical bar width is set in em, so, when font size changes vertical bar width changes too.
        var scrollWidth = 1.35 * viewModel.getSettings().getFont().getSize();
        var padding = 5;
        var r = Math.round((textArea.getWidth() - (padding + scrollWidth)) / this.characterWidth);
        viewModel.outputWidthProperty().set((int) r);
    }

    private void printPromptTexts(List<StyledText> texts) {
        var viewModel = this.getViewModel();
        viewModel.setHighlighterEnabled(false);
        this.printTexts(texts);
        viewModel.setPromptPosition(textArea.getText().length());
        viewModel.setHighlighterEnabled(true);
        this.textArea.requestFocus();
    }

    private void printTexts(List<StyledText> texts) {
        this.textArea.setOutputPrinting(true);
        int oldLength = textArea.getLength();
        StringBuilder sb = new StringBuilder();
        String style = null;
        StyleSpansBuilder<Collection<TextAreaStyle>> ssb = new StyleSpansBuilder<>(texts.size());
        for (var styledText : texts) {
            style = styledText.getStyle();
            var text = styledText.getText();
            sb.append(text);
            if (style == null) {
                if (texts.size() > 1) {
                    ssb.add(TextAreaStyle.EMPTY, text.length());
                }
            } else {
                ssb.add(Collections.singleton(new TextAreaStyle(style)), text.length());
            }
        }
        textArea.appendText(sb.toString());
        if (style != null || texts.size() > 1) {
            textArea.setStyleSpans(oldLength, (StyleSpans) ssb.create());
        }
        RichTextFxUtils.scrollToBottom(textArea);
        this.textArea.setOutputPrinting(false);
    }

}
