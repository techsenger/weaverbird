package com.techsenger.weaverbird.gui.session;

///*
// * Copyright 2018-2026 Pavel Castornii.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.techsenger.weaverbird.console.gui.session;
//
//import atlantafx.base.theme.Styles;
//import com.techsenger.weaverbird.console.gui.settings.ConnectionSettings;
//import com.techsenger.tabshell.core.style.SizeConstants;
//import com.techsenger.tabshell.kit.dialog.AbstractSimpleDialogView;
//import com.techsenger.toolkit.fx.utils.ButtonUtils;
//import com.techsenger.toolkit.fx.utils.NodeUtils;
//import javafx.geometry.Insets;
//import javafx.scene.control.ComboBox;
//import javafx.scene.control.Label;
//import javafx.scene.control.TextField;
//import javafx.scene.layout.ColumnConstraints;
//import javafx.scene.layout.GridPane;
//import javafx.scene.layout.Priority;
//import javafx.scene.layout.Region;
//import javafx.scene.layout.VBox;
//
///**
// *
// * @author Pavel Castornii
// */
//class NewSessionDialogView extends AbstractSimpleDialogView<NewSessionDialogViewModel> {
//
//    private final Label connectionLabel = new Label("Connection");
//
//    private final ComboBox<ConnectionSettings> connectionComboBox = new ComboBox<>();
//
//    private final Label nameLabel = new Label("Name");
//
//    private final TextField nameTextField = new TextField();
//
//    private final GridPane gridPane = new GridPane();
//
//    NewSessionDialogView(NewSessionDialogViewModel viewModel) {
//        super(viewModel);
//    }
//
//    @Override
//    public void requestFocus() {
//        NodeUtils.requestFocus(connectionComboBox);
//    }
//
//    @Override
//    protected void build(NewSessionDialogViewModel viewModel) {
//        super.build(viewModel);
//        var c0 = new ColumnConstraints();
//        var c1 = new ColumnConstraints();
//        c1.setHgrow(Priority.ALWAYS);
//        gridPane.getColumnConstraints().addAll(c0, c1);
//        VBox.setVgrow(gridPane, Priority.ALWAYS);
//        gridPane.setVgap(SizeConstants.INSET);
//        gridPane.setHgap(SizeConstants.INSET);
//
//        connectionLabel.setMinWidth(Region.USE_PREF_SIZE);
//        connectionLabel.setFocusTraversable(false);
//        gridPane.add(connectionLabel, 0, 0);
//        GridPane.setHgrow(connectionComboBox, Priority.ALWAYS);
//        connectionComboBox.setMaxWidth(Double.MAX_VALUE);
//        connectionComboBox.setCellFactory(lv -> new ConnectionListCell<ConnectionSettings>());
//        connectionComboBox.setButtonCell(new ConnectionListCell<ConnectionSettings>());
//        connectionComboBox.getStyleClass().add(Styles.DENSE);
//        connectionComboBox.setItems(viewModel.getConnections());
//        gridPane.add(connectionComboBox, 1, 0);
//
//        nameLabel.setMinWidth(Region.USE_PREF_SIZE);
//        nameLabel.setFocusTraversable(false);
//        gridPane.add(nameLabel, 0, 1);
//        GridPane.setHgrow(nameTextField, Priority.ALWAYS);
//        nameTextField.getStyleClass().add(Styles.DENSE);
//        gridPane.add(nameTextField, 1, 1);
//
//        var wrapper = new VBox(gridPane);
//        wrapper.setPadding(new Insets(SizeConstants.INSET, SizeConstants.INSET, 0, SizeConstants.INSET));
//        VBox.setVgrow(wrapper, Priority.ALWAYS);
//
//        getButtonBox().getChildren().addAll(getCancelButton(), getOkButton());
//        ButtonUtils.makeEqualWidth(getCancelButton(), getOkButton());
//        getContentPane().getChildren().addAll(wrapper, getButtonBox());
//        getFocusTrap().activate();
//    }
//
//    @Override
//    protected void bind(NewSessionDialogViewModel viewModel) {
//        super.bind(viewModel);
//        viewModel.connectionProperty().bind(connectionComboBox.valueProperty());
//        viewModel.nameProperty().bindBidirectional(nameTextField.textProperty());
//    }
//
//    @Override
//    protected void addListeners(NewSessionDialogViewModel viewModel) {
//        super.addListeners(viewModel);
//        viewModel.connectionErrorProperty().addListener((ov, oldV, newV) -> {
//            connectionComboBox.pseudoClassStateChanged(Styles.STATE_DANGER, newV);
//        });
//        viewModel.nameErrorProperty().addListener((ov, oldV, newV) -> {
//            nameTextField.pseudoClassStateChanged(Styles.STATE_DANGER, newV);
//        });
//    }
//
//    @Override
//    protected void preDeinitialize(NewSessionDialogViewModel viewModel) {
//        super.preDeinitialize(viewModel);
//        viewModel.preDeinitialize();
//    }
//}
