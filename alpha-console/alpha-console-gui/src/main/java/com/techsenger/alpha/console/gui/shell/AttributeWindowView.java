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

import atlantafx.base.theme.Styles;
import com.techsenger.alpha.console.gui.window.AbstractWindowView;
import com.techsenger.tabshell.kit.material.FxUtils;
import com.techsenger.tabshell.kit.material.textarea.ExtendedTextArea;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.StyleClassedTextArea;

/**
 * The main idea of modal window - it is displayed as it were with focus, but focus is always on text area.
 *
 * @author Pavel Castornii
 */
class AttributeWindowView extends AbstractWindowView<AttributeWindowViewModel> {

    private final ListView<String> listView = new ListView<String>();

    private final StyleClassedTextArea textArea = new StyleClassedTextArea();

    private final VirtualizedScrollPane scrollPane = new VirtualizedScrollPane(this.textArea);

    AttributeWindowView(AttributeWindowViewModel viewModel,
            ExtendedTextArea mainTextArea) {
        super(viewModel, mainTextArea);
    }

    public ListView<String> getListView() {
        return listView;
    }

    public StyleClassedTextArea getTextArea() {
        return textArea;
    }

    @Override
    public Node getFocusNode() {
        return this.listView;
    }

    @Override
    public AttributeWindowViewModel getViewModel() {
        return (AttributeWindowViewModel) super.getViewModel();
    }

    @Override
    public void requestFocus() {
        listView.requestFocus();
    }

    @Override
    protected void build(AttributeWindowViewModel viewModel) {
        super.build(viewModel);
        textArea.setPadding(new Insets(0, 5, 0, 5));
        textArea.setWrapText(true);
        textArea.getStyleClass().add("window-text-area");
        var splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.HORIZONTAL);
        splitPane.getStyleClass().add("window-split-pane");
        VBox.setVgrow(splitPane, Priority.ALWAYS);
        splitPane.getItems().add(listView);
        this.getNode().getChildren().add(splitPane);
        splitPane.getItems().add(scrollPane);

        //note - stackpange doesn't include menu bar
        StackPane.setMargin(this.getNode(), new Insets(viewModel.getTop(), 0, 0, viewModel.getLeft()));
        listView.getStyleClass().add(Styles.DENSE);
    }

    @Override
    protected void bind(AttributeWindowViewModel viewModel) {
        super.bind(viewModel);
        listView.itemsProperty().bind(viewModel.currentAttributesProperty());
    }

    @Override
    protected void addListeners(AttributeWindowViewModel viewModel) {
        super.addListeners(viewModel);
        listView.getSelectionModel().selectedItemProperty().addListener((ov, t, t1) ->
                viewModel.doOnAttributeChanged(t1, ov.getValue()));
        viewModel.selectedAttributeIndexProperty().addListener((ov, oldValue, newValue) -> {
            listView.getSelectionModel().select(newValue.intValue());
            var value = newValue.intValue();
            if (!FxUtils.isItemVisible(listView, value)) {
                listView.scrollTo(value);
            }
            this.printDescription(value);
        });
        listView.getSelectionModel().selectedIndexProperty().addListener((ov, oldValue, newValue) ->
                viewModel.selectedAttributeIndexProperty().set(newValue.intValue()));
    }

    private void printDescription(int index) {
        var textArea = this.getTextArea();
        textArea.clear();
        var currentDescriptions = this.getViewModel().getCurrentDescriptions();
        if (index < 0 || currentDescriptions == null || currentDescriptions.size() <= index) {
            return;
        }
        var descriptions = currentDescriptions.get(index);
        for (var description: descriptions) {
            if (description.getStyle() != null) {
                textArea.append(description.getText(), description.getStyle());
            } else {
                textArea.appendText(description.getText());
            }
        }
    }
}
