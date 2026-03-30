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

package com.techsenger.alpha.console.gui.file;

import com.techsenger.alpha.console.gui.style.ConsoleIcons;
import com.techsenger.tabshell.core.TabShellView;
import com.techsenger.tabshell.core.tab.TabView;
import com.techsenger.tabshell.kit.core.style.CoreIcons;
import com.techsenger.tabshell.kit.core.style.StyleClasses;
import com.techsenger.tabshell.kit.material.textarea.ExtendedTextArea;
import com.techsenger.tabshell.kit.material.textarea.RichTextFxUtils;
import com.techsenger.tabshell.kit.text.viewer.AbstractViewerTabView;
import com.techsenger.tabshell.material.icon.FontIconView;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import org.fxmisc.richtext.LineNumberFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
public class FileTabView extends AbstractViewerTabView<FileTabViewModel> {

    private static final Logger logger = LoggerFactory.getLogger(FileTabView.class);

    private final Button clearButton = new Button(null, new FontIconView(CoreIcons.CLEAR));

    private final Button dependencyButton = new Button(null, new FontIconView(ConsoleIcons.DEPENDENCY));

    public FileTabView(TabShellView<?> tabShell, FileTabViewModel viewModel) {
        super(tabShell, viewModel, new ExtendedTextArea());
    }

    @Override
    public FileTabViewModel getViewModel() {
        return (FileTabViewModel) super.getViewModel();
    }

    @Override
    protected void build(FileTabViewModel viewModel) {
        super.build(viewModel);
        var root = this.getNode();
        var textArea = this.getTextArea();

        if (viewModel.getFileType() == SupportedFileType.XML) {
            textArea.getStyleClass().add("xml-text-area");
            final var highlighter = new XmlHighlighter();
            textArea.textProperty().addListener((obs, oldText, newText) -> {
                textArea.setStyleSpans(0, highlighter.computeHighlighting(newText));
            });
            dependencyButton.setOnAction((event) ->  {
                viewModel.openConvertedXmlTab(this.getTextArea().selectedTextProperty().getValue());
            });
            dependencyButton.setTooltip(new Tooltip("Convert Dependencies"));
        } else if (viewModel.getFileType() == SupportedFileType.SCRIPT) {
            textArea.getStyleClass().add("script-text-area");
            final var highlighter = new ScriptHighlighter();
            textArea.textProperty().addListener((obs, oldText, newText) -> {
                textArea.setStyleSpans(0, highlighter.computeHighlighting(newText));
            });
        }

        clearButton.setTooltip(new Tooltip("Clear"));
        clearButton.getStyleClass().add(StyleClasses.ICONED_BUTTON);

        if (viewModel.getFileType() == SupportedFileType.XML) {
            this.getToolBar().getItems().addAll(clearButton, getWrapTextButton(), this.dependencyButton);
        } else {
            this.getToolBar().getItems().addAll(clearButton, getWrapTextButton());
        }
//        JavaFxUtils.makeIconButtons(textWrapButton, logEventButton);
//        JavaFxUtils.bindHeightTo(getFindTextField(), textWrapButton, logEventButton);

//        textWrapButton.setSelected(true);
//        textArea.wrapTextProperty().bindBidirectional(textWrapButton.selectedProperty());
        textArea.setEditable(true);
        textArea.setParagraphGraphicFactory(LineNumberFactory.get(textArea));
        //in richtextfx padding via css doesn't work, so, we do this way
        textArea.setPadding(new Insets(0, 0, 0, 0));

        clearButton.setOnAction((t) -> this.getTextArea().clear());

        getTopPane().getChildren().addAll(getToolBar(), this.getTextScrollPane());
        var css = FileTabView.class.getResource("file.css").toExternalForm();
        getContentPane().getStylesheets().add(css);
        viewModel.readFile();
        RichTextFxUtils.scrollToTop(textArea);
    }

    public void openBottomTab(TabView<?> view) {
        getBottomTabManager().openTab(view);
    }
}
