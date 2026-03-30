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
import com.techsenger.tabshell.core.tab.AbstractTabView;
import com.techsenger.tabshell.core.tab.ComponentTab;
import com.techsenger.tabshell.kit.material.textarea.ExtendedTextArea;
import com.techsenger.tabshell.kit.material.textarea.RichTextFxUtils;
import com.techsenger.tabshell.material.icon.FontIconView;
import javafx.geometry.Insets;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.LineNumberFactory;

/**
 *
 * @author Pavel Castornii
 */
public class ConvertedXmlTabView extends AbstractTabView<ConvertedXmlTabViewModel> {

    private final ExtendedTextArea codeArea = new ExtendedTextArea();

    private final VirtualizedScrollPane scrollPane = new VirtualizedScrollPane(codeArea);

    private final ContextMenu textAreaMenu = new ContextMenu();

    private final MenuItem cutItem = new MenuItem("Cut", new FontIconView(ConsoleIcons.CUT));

    private final MenuItem copyItem = new MenuItem("Copy", new FontIconView(ConsoleIcons.COPY));

    private final MenuItem pasteItem = new MenuItem("Paste", new FontIconView(ConsoleIcons.PASTE));

    private final ToggleButton lineWrapButton = new ToggleButton(null, new FontIconView(ConsoleIcons.WRAP));

    private final ToolBar toolBar = new ToolBar(lineWrapButton);

    private final VBox box = new VBox(toolBar, scrollPane);

    private final ComponentTab tab = new ComponentTab(this, "Output XML", box);

    private final XmlHighlighter highlighter = new XmlHighlighter();

    public ConvertedXmlTabView(ConvertedXmlTabViewModel viewModel) {
        super(viewModel);
    }

    @Override
    public boolean doOnCloseRequest() {
        return true;
    }

    @Override
    public void requestFocus() {
        this.codeArea.requestFocus();
    }

    @Override
    public ComponentTab getNode() {
        return this.tab;
    }

    @Override
    protected void build(ConvertedXmlTabViewModel viewModel) {
        super.build(viewModel);
        codeArea.appendText(viewModel.xmlCodeProperty().get());
        codeArea.setEditable(false);
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        //in richtextfx padding via css doesn't work, so, we do this way
        codeArea.setPadding(new Insets(0, 0, 0, 0));
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        cutItem.setDisable(true);
        pasteItem.setDisable(true);

        lineWrapButton.setTooltip(new Tooltip("Line Wrap"));

        codeArea.wrapTextProperty().bindBidirectional(lineWrapButton.selectedProperty());
        codeArea.getStyleClass().add("xml-text-area");
        RichTextFxUtils.scrollToTop(codeArea);
    }

    @Override
    protected void addListeners(ConvertedXmlTabViewModel viewModel) {
        super.addListeners(viewModel);
        codeArea.textProperty().addListener((obs, oldText, newText) -> {
            codeArea.setStyleSpans(0, highlighter.computeHighlighting(newText));
        });
    }
}
