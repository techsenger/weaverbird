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

package com.techsenger.weaverbird.gui.console;

import com.techsenger.shellfx.core.popup.AbstractPopupView;
import com.techsenger.toolkit.core.Pair;
import com.techsenger.toolkit.fx.utils.ListViewUtils;
import com.techsenger.toolkit.fx.utils.NodeUtils;
import com.techsenger.toolkit.fx.utils.ScrollPosition;
import com.techsenger.toolkit.fx.value.ValueUtils;
import com.techsenger.weaverbird.executor.api.command.CommandInfo;
import com.techsenger.weaverbird.executor.api.command.ParameterDescriptor;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import jfx.incubator.scene.control.richtext.RichTextArea;
import jfx.incubator.scene.control.richtext.model.StyleAttributeMap;

/**
 * @param <VM> the ViewModel type
 * @author Pavel Castornii
 */
public class CompletionPopupView<VM extends CompletionPopupViewModel<?>> extends AbstractPopupView<VM> {

    private final ListView<CompletionItem<?>> listView = new ListView<>();

    private final RichTextArea textArea = new RichTextArea();

    private final SplitPane splitPane = new SplitPane(listView, textArea);

    public CompletionPopupView(VM viewModel) {
        super(viewModel);
    }

    @Override
    public void requestFocus() {
        NodeUtils.requestFocus(listView);
    }

    @Override
    protected void build() {
        super.build();
        textArea.setPadding(new Insets(0, 5, 0, 5));
        textArea.setWrapText(true);
        textArea.getStyleClass().add("popup-text-area");
        splitPane.setOrientation(Orientation.HORIZONTAL);
        splitPane.getStyleClass().add("popup-split-pane");
        splitPane.setDividerPositions(0.35);
        VBox.setVgrow(splitPane, Priority.ALWAYS);
        getContentBox().getChildren().add(splitPane);
        getContentBox().getStyleClass().add("autocomplete-popup-box");

        getContentBox().setPrefSize(CompletionPopupConstants.WIDTH, CompletionPopupConstants.HEIGHT);
        getContentBox().setMinSize(CompletionPopupConstants.WIDTH, CompletionPopupConstants.HEIGHT);
        getContentBox().setMaxSize(CompletionPopupConstants.WIDTH, CompletionPopupConstants.HEIGHT);
        listView.setCellFactory(lv -> {
            ListCell<CompletionItem<?>> cell = new ListCell<>() {
                @Override
                protected void updateItem(CompletionItem<?> item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getText());
                }
            };
            cell.setOnMouseClicked(event -> {
                if (!cell.isEmpty() && event.getClickCount() == 2) {
                    getViewModel().onItemSubmitted(cell.getItem());
                }
            });
            return cell;
        });
    }

    @Override
    protected void bind() {
        super.bind();
        listView.setItems(getViewModel().getItems());
        getViewModel().itemWrapper().bind(listView.getSelectionModel().selectedItemProperty());
    }

    @Override
    protected void addListeners() {
        super.addListeners();
        var viewModel = getViewModel();
        ValueUtils.callAndAddListener(
                listView.getSelectionModel().selectedItemProperty(), (ov, oldV, newV) -> updateInfo(newV));
        viewModel.itemSource().addListener(this::selectItem);
    }

    @Override
    protected void addHandlers() {
        super.addHandlers();
        getContentBox().addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                getViewModel().onClose();
            } else if (event.getCode() == KeyCode.ENTER) {
                getViewModel().onItemSubmitted(this.listView.getSelectionModel().getSelectedItem());
            }
        });
    }

    protected ListView<CompletionItem<?>> getListView() {
        return listView;
    }

    protected RichTextArea getTextArea() {
        return textArea;
    }

    private void selectItem(Pair<CompletionItem<?>, Direction> selection) {
        listView.getSelectionModel().select(selection.getFirst());
        var index = listView.getSelectionModel().getSelectedIndex();
        switch (selection.getSecond()) {
            case UP -> ListViewUtils.scrollToIfNeeded(listView, index, ScrollPosition.CENTER);
            case DOWN -> ListViewUtils.scrollToIfNeeded(listView, index, ScrollPosition.END);
            case NONE -> {
                // no scrolling needed
            }
        }
    }

    private void updateInfo(CompletionItem<?> item) {
        if (item == null) {
            return;
        }
        var boldStyle = StyleAttributeMap.builder().setBold(true).build();
        textArea.clear();
        if (getViewModel().getType() == CompletionType.COMMAND) {
            var command = ((CompletionItem<CommandInfo>) item).getElement();
            textArea.appendText("Description: ", boldStyle);
            textArea.appendText(command.getDescription() + "\n\n", StyleAttributeMap.EMPTY);
            textArea.appendText("Module: ", boldStyle);
            textArea.appendText(command.getModuleName(), StyleAttributeMap.EMPTY);
        } else {
            var parameter = ((CompletionItem<ParameterDescriptor>) item).getElement();
            textArea.appendText("Description: ", boldStyle);
            textArea.appendText(parameter.getDescription() + "\n\n", StyleAttributeMap.EMPTY);
            textArea.appendText("Required: ", boldStyle);
            textArea.appendText(String.valueOf(parameter.isRequired()) + "\n\n", StyleAttributeMap.EMPTY);
            textArea.appendText("Alias: ", boldStyle);
            textArea.appendText(parameter.getShortName(), StyleAttributeMap.EMPTY);
        }
    }
}
