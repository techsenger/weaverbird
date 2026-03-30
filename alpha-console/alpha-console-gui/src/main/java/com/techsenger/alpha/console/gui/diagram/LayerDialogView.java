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

package com.techsenger.alpha.console.gui.diagram;

import com.techsenger.tabshell.core.style.SizeConstants;
import com.techsenger.tabshell.kit.dialog.page.AbstractPageDialogView;
import com.techsenger.toolkit.fx.utils.ButtonUtils;
import com.techsenger.toolkit.fx.utils.NodeUtils;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 *
 * @author Pavel Castornii
 */
class LayerDialogView extends AbstractPageDialogView<LayerPageView, LayerDialogViewModel> {

    private final Button resetButton = new Button("Reset");

    private final HBox pageBox = new HBox(getPageListView(), getPageContainer());

    LayerDialogView(LayerDialogViewModel viewModel) {
        super(viewModel);
        var list = new ArrayList<LayerPageView>();
        for (var p : viewModel.getPages()) {
            var page = new LayerPageView(p);
            list.add(page);
        }
        getPageListView().setItems(FXCollections.observableArrayList(list));
    }

    @Override
    public void requestFocus() {
        NodeUtils.requestFocus(getContentPane());

    }

    @Override
    protected void build(LayerDialogViewModel viewModel) {
        super.build(viewModel);
        getPageListView().setPrefWidth(250);
        getPageListView().setMinWidth(250);

        HBox.setHgrow(getPageContainer(), Priority.ALWAYS);
        pageBox.setPadding(new Insets(SizeConstants.INSET, SizeConstants.INSET, 0, SizeConstants.INSET));
        pageBox.setSpacing(SizeConstants.INSET);
        VBox.setVgrow(pageBox, Priority.ALWAYS);

        getButtonBox().getChildren().addAll(resetButton, getCancelButton(), getOkButton());
        ButtonUtils.makeEqualWidth(resetButton, getCancelButton(), getOkButton());

        this.getContentPane().getChildren().addAll(pageBox, getButtonBox());
    }

    @Override
    protected void addHandlers(LayerDialogViewModel viewModel) {
        super.addHandlers(viewModel);
        this.resetButton.setOnAction(e -> viewModel.reset());
    }
}
