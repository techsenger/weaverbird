/*
 * Copyright 2018-2025 Pavel Castornii.
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

package com.techsenger.alpha.console.gui.settings;

import com.techsenger.tabshell.core.page.PageView;
import com.techsenger.tabshell.core.style.SizeConstants;
import com.techsenger.tabshell.kit.dialog.page.AbstractPageDialogView;
import com.techsenger.toolkit.fx.utils.ButtonUtils;
import com.techsenger.toolkit.fx.utils.NodeUtils;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 *
 * @author Pavel Castornii
 */
public class SettingsDialogView extends AbstractPageDialogView<PageView<?>, SettingsDialogViewModel> {

    private final HBox pageBox = new HBox(getPageListView(), getPageContainer());

    public SettingsDialogView(SettingsDialogViewModel viewModel) {
        super(viewModel);
        var list = new ArrayList<PageView<?>>();
        list.addAll(List.of(new AppearancePageView(viewModel.getAppearance()),
                new DiagramPageView(viewModel.getDiagram())));
        if (viewModel.getConnections() != null) {
            list.add(new ConnectionsPageView(viewModel.getConnections()));
        }
        getPageListView().setItems(FXCollections.observableArrayList(list));
    }

    @Override
    public void requestFocus() {
        NodeUtils.requestFocus(getOkButton());
    }

    @Override
    public SettingsDialogViewModel getViewModel() {
        return (SettingsDialogViewModel) super.getViewModel();
    }

    @Override
    protected void build(SettingsDialogViewModel viewModel) {
        super.build(viewModel);
        getPageListView().setPrefWidth(200);
        getPageListView().setMinWidth(200);
        HBox.setHgrow(getPageContainer(), Priority.ALWAYS);

        pageBox.setPadding(new Insets(SizeConstants.INSET, SizeConstants.INSET, 0, SizeConstants.INSET));
        pageBox.setSpacing(SizeConstants.INSET);
        VBox.setVgrow(pageBox, Priority.ALWAYS);

        getButtonBox().getChildren().addAll(getCancelButton(), getOkButton());
        ButtonUtils.makeEqualWidth(getCancelButton(), getOkButton());

        getContentPane().getChildren().addAll(pageBox, getButtonBox());
    }
}

