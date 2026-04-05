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

package com.techsenger.alpha.gui.console;

import com.techsenger.alpha.core.api.message.Message;
import com.techsenger.alpha.core.api.message.MessageType;
import com.techsenger.alpha.executor.api.command.CommandInfo;
import com.techsenger.tabshell.core.ShellFxView;
import com.techsenger.tabshell.core.tab.AbstractTabFxView;
import com.techsenger.tabshell.material.Anchors;
import com.techsenger.tabshell.material.icon.FontIconView;
import com.techsenger.tabshell.material.style.StyleClasses;
import com.techsenger.tabshell.material.style.StyleUtils;
import com.techsenger.tabshell.shared.style.SharedIcons;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import jfx.incubator.scene.control.richtext.RichTextArea;
import jfx.incubator.scene.control.richtext.StyleHandlerRegistry;
import jfx.incubator.scene.control.richtext.TextPos;
import jfx.incubator.scene.control.richtext.model.StyleAttribute;
import jfx.incubator.scene.control.richtext.model.StyleAttributeMap;

/**
 *
 * @author Pavel Castornii
 */
public class ConsoleTabFxView<P extends ConsoleTabPresenter<?, ?>> extends AbstractTabFxView<P> implements ConsoleTabView {

    private static final class CssRichTextArea extends RichTextArea {

        private static final StyleHandlerRegistry registry = createRegistry();

        private static StyleHandlerRegistry createRegistry() {
            // brings in the handlers from the base class
            StyleHandlerRegistry.Builder b = StyleHandlerRegistry.builder(RichTextArea.styleHandlerRegistry);
            // adds a handler for the new attribute
            b.setSegHandler(CSS_ATTRIBUTE, (c, cx, style) -> {
                cx.addStyle(style);
            });
            return b.build();
        }

        @Override
        public StyleHandlerRegistry getStyleHandlerRegistry() {
            return registry;
        }
    };

    protected static final StyleAttribute<String> CSS_ATTRIBUTE
            = new StyleAttribute("CSS_ATTRIBUTE", String.class, false);

    protected class Composer extends AbstractTabFxView<P>.Composer implements ConsoleTabComposer {

        private final ConsoleTabFxView<P> view = ConsoleTabFxView.this;

        private AttributePopupFxView<?> attributePopup;

        @Override
        public void addCommandPopup(Map<String, CommandInfo> commandsByName) {
            this.attributePopup = createCommandPopup(commandsByName);
            this.attributePopup.getPresenter().initialize();
            var topLeft = calculatePopupPosition();
            addPopup(this.attributePopup, Anchors.topLeft(topLeft.getY(), topLeft.getX()));
            view.caret.setOpacity(0);
            this.attributePopup.requestFocus();
        }

        @Override
        public void removePopup() {
            if (this.attributePopup == null) {
                return;
            }
            this.attributePopup.getComposer().remove();
            view.caret.setOpacity(1);
            view.textArea.requestFocus();
        }

        protected AttributePopupFxView<?> createCommandPopup(Map<String, CommandInfo> commandsByName) {
            var view = new AttributePopupFxView<>();
            var presenter = new AttributePopupPresenter<>(view, commandsByName,
                    AttributePopupType.COMMAND, getPresenter());
            return view;
        }

        Point2D calculatePopupPosition() {
            Node caret = getCaret();

            Bounds boundsInScreen = caret.localToScreen(caret.getBoundsInLocal());
            if (boundsInScreen == null) {
                return new Point2D(0, 0);
            }

            Bounds boundsInStackPane = getWrapperPane().screenToLocal(boundsInScreen);
            if (boundsInStackPane == null) {
                return new Point2D(0, 0);
            }

            double caretX = boundsInStackPane.getMinX();
            double caretY = boundsInStackPane.getMinY();
            double caretBottom = boundsInStackPane.getMaxY();

            double x;
            double y;

            // vertical position: show below caret if there is enough space, otherwise show above
            if (caretBottom + AttributePopupConstants.V_MARGIN + AttributePopupConstants.HEIGHT >
                    getWrapperPane().getHeight()) {
                y = caretY - AttributePopupConstants.V_MARGIN - AttributePopupConstants.HEIGHT;
            } else {
                y = caretBottom + AttributePopupConstants.V_MARGIN;
            }

            // horizontal position: align left edge with caret if there is enough space,
            // otherwise align right edge with caret
            if (caretX + AttributePopupConstants.WIDTH > getWrapperPane().getWidth()) {
                x = caretX - AttributePopupConstants.WIDTH;
            } else {
                x = caretX;
            }

            return new Point2D(x, y);
        }
    }

    private Font font;

    /**
     * Character width in text area. Required for message printer.
     */
    private double characterWidth;

    private final RichTextArea textArea = new CssRichTextArea();

    /**
     * The index of the paragraph with current prompt.
     */
    private TextPos promptPos;

//    private final ShellHighlighter highlighter;

//    private final LogMessagePrinter logPrinter;

//    private final MessagePrinter printer;

//    private final SessionsToolBarView sessions;

    private final Button clearButton = new Button(null, new FontIconView(SharedIcons.CLEAR));

    private final MenuItem cutItem = new MenuItem("Cut", new FontIconView(SharedIcons.CUT));

    private final MenuItem pasteItem = new MenuItem("Paste", new FontIconView(SharedIcons.PASTE));

    private Node caret;

    public ConsoleTabFxView(ShellFxView<?> shell) {
        super(shell);
//        this.logPrinter = new LogMessagePrinter(textArea, getTextScrollPane());
//        this.printer = new TextAreaMessagePrinter(textArea);
//        if (viewModel.getSessions() != null) {
//            sessions = new SessionsToolBarView(viewModel.getSessions());
//            sessions.initialize();
//        } else {
//            sessions = null;
//        }
//        this.highlighter =  new ShellHighlighter(viewModel.getCommandInfosManager());
    }

    public void cut() {
        this.textArea.cut();
    }

    public void paste() {
        this.textArea.paste();
    }

    @Override
    public void requestFocus() {
//        NodeUtils.requestFocus(textArea);
        textArea.requestFocus();
    }

    @Override
    public void printPrompt(String prompt) {
//        var viewModel = this.getViewModel();
//        viewModel.setHighlighterEnabled(false);
        this.printTexts(List.of(new StyledText(StyleAttributeMap.EMPTY, prompt)), false); // adds a new paragraph
        this.promptPos = textArea.getCaretPosition();
//        viewModel.setPromptPosition(textArea.getText().length());
//        viewModel.setHighlighterEnabled(true);
//        this.textArea.requestFocus();
    }

    @Override
    public void printMessages(List<Message> messages) {
        var texts = new ArrayList<StyledText>();
        for (var msg : messages) {
            var style = StyleAttributeMap.EMPTY;
            if (msg.getType() == MessageType.ERROR) {
                style = TextAreaCssStyles.ERROR;
            }
            var text = new StyledText(style, msg.getText());
            texts.add(text);
        }
        printTexts(texts, true);
    }

    @Override
    public void setHighlighterEnabled(boolean value) {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setMonospaceFont(Font font) {
        this.font = font;
        this.characterWidth = StyleUtils.getMonospaceCharSize(font).getWidth();
    }

    @Override
    public void apppendText(String text) {
        textArea.appendText(text, StyleAttributeMap.EMPTY);
        moveCaretToEnd();
    }

    @Override
    public Composer getComposer() {
        return (Composer) super.getComposer();
    }

    @Override
    protected void build() {
        super.build();

//        var toolBars = new HBox(getToolBar());
//        clearButton.setTooltip(new Tooltip("Clear"));
//        clearButton.getStyleClass().add(StyleClasses.ICONED_BUTTON);
//        //toolbar
//        getToolBar().getItems().addAll(clearButton, new Separator(Orientation.VERTICAL), getCopyButton(),
//                getFindButton());
//        if (Framework.getMode() == FrameworkMode.CLIENT) {
//            toolBars.getChildren().add(sessions.getNode());
//        }
//        HBox.setHgrow(getToolBar(), Priority.ALWAYS);
        textArea.getStyleClass().addAll("console-text-area", StyleClasses.MONOSPACE);
        textArea.setEditable(true);
        VBox.setVgrow(textArea, Priority.ALWAYS);
//        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
//
//        stackPane.getChildren().add(scrollPane);
//        stackPane.setMargin(scrollPane, new Insets(0, 0, 0, 0));
//        VBox.setVgrow(stackPane, Priority.ALWAYS);

        var css = ConsoleTabFxView.class.getResource("console.css").toExternalForm();
        getWrapperPane().getStylesheets().add(css);
        getContentBox().getChildren().add(textArea);
//        css = LogPrinterView.class.getResource("log-levels.css").toExternalForm();
//        getTopPane().getStylesheets().add(css);
//        getTopPane().getChildren().addAll(toolBars, stackPane);
        cutItem.setAccelerator(new KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_DOWN));
        pasteItem.setAccelerator(new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN));
//        getTextAreaMenu().getItems().addAll(cutItem, getCopyItem(), pasteItem);
    }

    @Override
    protected void addListeners() {
        super.addListeners();
        textArea.caretPositionProperty().addListener((ov, oldV, newV) -> {
            textArea.setEditable(isInEditableRegion(newV));
        });
//        //highliter work only 1) when it is enabled and 2) processes only text after prompt
//        //1) because we don't need to highlight words in messages, errors etc
//        //2) because highlighter clears all other style classes for example, it can clear styles of messages, errors etc
//        textArea.textProperty().addListener((obs, oldText, newText) -> {
//            if (newText.length() > 0 && viewModel.isHighlighterEnabled()) {
//                textArea.setStyleSpans(viewModel.getPromptPosition(),
//                        highlighter.computeHighlighting(newText.substring(viewModel.getPromptPosition())));
//            }
//        });
//
//        var settings = viewModel.getSettings();
//        ValueUtils.callAndAddListener(settings.fontProperty(), fontListener);
//        textArea.widthProperty().addListener((ov, oldV, newV) -> recalculateOutputWidth());
//        viewModel.getMessages().addListener((messages) -> this.printer.print(messages));
//        viewModel.getLogMessages().addListener((messages) -> this.logPrinter.print(messages));
    }

    @Override
    protected void addHandlers() {
//        super.addHandlers();
//        clearButton.setOnAction((e) -> {
//            if (!textArea.isOutputPrinting()) {
//                textArea.setOutputPrinting(true);
//                textArea.clear();
//                viewModel.showPrompt();
//                textArea.moveTo(viewModel.getPromptLength());
//                textArea.requestFocus();
//                textArea.setShowCaret(Caret.CaretVisibility.AUTO);
//                textArea.setOutputPrinting(false);
//            }
//        });
//        //to disable text gragging
//        textArea.setOnSelectionDropped(EH -> { });
//        textArea.setOnKeyPressed((event) -> {
//            if (event.getCode() == KeyCode.SPACE && event.isControlDown()) {
//                event.consume();
//                viewModel.openAttributeWindow();
//            } else if (event.getCode() == KeyCode.ESCAPE && !event.isControlDown()) {
//                event.consume();
//                viewModel.closeAttributeWindow();
//            }
//        });
//
        textArea.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
            var pos = textArea.getCaretPosition();
            if (isInEditableRegion(pos)) {
                if (event.getCode() == KeyCode.UP) {
//                    var currentWindowView = this.attributeWindow;
//                    if (currentWindowView != null) {
//                        currentWindowView.getViewModel().selectUp();
//                    } else {
//                        var index = viewModel.scrollHistoryUp();
//                        scrollHistory(index);
//                    }
                    event.consume();
                } else if (event.getCode() == KeyCode.DOWN) {
//                    var currentWindowView = this.attributeWindow;
//                    if (currentWindowView != null) {
//                        currentWindowView.getViewModel().selectDown();
//                    } else {
//                        var index = viewModel.scrollHistoryDown();
//                        scrollHistory(index);
//                    }
                    event.consume();
                } else if (event.getCode() == KeyCode.ENTER) {
                    event.consume();
                    textArea.appendText("\n");
//                    var currentWindowView = this.attributeWindow;
//                    if (currentWindowView != null) {
//                        viewModel.doOnListViewSelected();
//                        viewModel.closeAttributeWindow();
//                    } else {
                        //we add eol manually to prevent adding the line separator in the middle of the line
                        getPresenter().onCommandsSubmitted(getTextsWithPrompt(), calculateOutputWidth());
//                    }
                } else if (event.getCode() == KeyCode.HOME) {
                    event.consume();
                    textArea.select(this.promptPos);
                } else if (event.getCode() == KeyCode.DELETE) {
                    var selection = textArea.getSelection();
                    if (selection != null && !isInEditableRegion(selection.getAnchor())) {
                        event.consume();
                    }
                } else if (event.getCode() == KeyCode.X) { // cut
                    var selection = textArea.getSelection();
                    if (event.isShiftDown() && selection != null && !isInEditableRegion(selection.getAnchor())) {
                        event.consume();
                    }
                } else if (event.getCode() == KeyCode.BACK_SPACE) {
                    if (pos.index() == promptPos.index() && pos.offset() == promptPos.offset()) {
                        event.consume();
                    }
                } else if (event.getCode() == KeyCode.SPACE) {
                    if (event.isControlDown()) {
                        getPresenter().onAutocompleteRequest();
                    }
                } else if (event.getCode() == KeyCode.ESCAPE) {
                    getPresenter().onPopupClose();
                }
            }
        });
//        cutItem.setOnAction((t) -> getTextArea().cut());
//        pasteItem.setOnAction((t) -> getTextArea().paste());
//        getTextArea().addEventHandler(ContextMenuEvent.CONTEXT_MENU_REQUESTED, event -> {
//            cutItem.setDisable(!viewModel.isCutItemValid());
//            pasteItem.setDisable(!viewModel.isPasteItemValid());
//        });
    }

    protected Node getCaret() {
        if (this.caret == null) {
             this.caret = textArea.lookup(".caret");
        }
        return this.caret;
    }

    @Override
    protected Composer createComposer() {
        return new ConsoleTabFxView.Composer();
    }

//    @Override
//    protected ComponentHelper<?> createComponentHelper() {
//        return new ShellTabHelper(this);
//    }
//
//    protected ExtendedTextArea getTextArea() {
//        return super.getTextArea();
//    }
//
//    void openAttributeWindow(AttributeWindowView attributeWindow) {
//        this.attributeWindow = attributeWindow;
//        this.stackPane.getChildren().add(this.attributeWindow.getNode());
//        this.attributeWindow.getListView().setOnMouseClicked((e) -> {
//            if (e.getClickCount() == 2) {
//                getViewModel().doOnListViewSelected();
//                getViewModel().closeAttributeWindow();
//            }
//        });
//    }
//
//    void closeAttributeWindow() {
//        this.stackPane.getChildren().remove(this.attributeWindow.getNode());
//        this.attributeWindow.deinitialize();
//        this.attributeWindow = null;
//    }
//
//    private void scrollHistory(int index) {
//        var textArea = this.getTextArea();
//        var viewModel = this.getViewModel();
//        var lastCommands = viewModel.getLastCommands();
//        if (0 <= index &&  index < lastCommands.size()) {
//            viewModel.setCommandScrollingIndex(index);
//            textArea.deleteText(viewModel.getPromptPosition(), textArea.getText().length());
//            textArea.appendText(lastCommands.get(index));
//        } else if (index >= lastCommands.size()) {
//            viewModel.setCommandScrollingIndex(-1);
//            textArea.deleteText(viewModel.getPromptPosition(), textArea.getText().length());
//        }
//    }

    private int calculateOutputWidth() {
        //vertical bar width is set in em, so, when font size changes vertical bar width changes too.
        var scrollWidth = 1.35 * font.getSize();
        var padding = 5;
        var r = Math.round((textArea.getWidth() - (padding + scrollWidth)) / this.characterWidth);
        return (int) r;
    }

    private boolean isInEditableRegion(TextPos pos) {
        if (promptPos == null) {
            return true;
        }
        var result = (pos.index() == promptPos.index() && pos.offset()>= promptPos.offset())
                    || pos.index() > promptPos.index();
        return result;
    }

    private List<String> getTextsWithPrompt() {
        int count = textArea.getParagraphCount();
        List<String> result = new ArrayList<>();
        for (int i = promptPos.index(); i < count; i++) {
            result.add(textArea.getModel().getPlainText(i));
        }
        return result;
    }

    private void printTexts(List<StyledText> texts, boolean newLine) {
        for (var styledText : texts) {
            var style = styledText.getStyle();
            var text = styledText.getText();
            textArea.appendText(text, style);
            if (newLine) {
                textArea.appendText("\n");
            }
            moveCaretToEnd();
        }
    }

    private void moveCaretToEnd() {
        //textArea.select(textArea.getDocumentEnd(), textArea.getDocumentEnd());
        int lastPara = textArea.getParagraphCount() - 1;
        lastPara = Math.max(0, lastPara);
        String lastText = textArea.getModel().getPlainText(lastPara);
        TextPos end = TextPos.ofLeading(lastPara, lastText.length());
        textArea.select(end);
    }

}
