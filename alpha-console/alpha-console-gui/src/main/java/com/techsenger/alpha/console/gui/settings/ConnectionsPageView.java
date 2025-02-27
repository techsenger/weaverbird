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

import atlantafx.base.controls.ToggleSwitch;
import atlantafx.base.theme.Styles;
import com.techsenger.alpha.api.net.session.Protocol;
import com.techsenger.alpha.console.gui.style.ConsoleIcons;
import com.techsenger.tabshell.core.page.AbstractPageView;
import com.techsenger.tabshell.core.style.SizeConstants;
import com.techsenger.tabshell.kit.core.style.StyleClasses;
import com.techsenger.tabshell.material.icon.FontIconView;
import com.techsenger.toolkit.fx.Spacer;
import com.techsenger.toolkit.fx.utils.NodeUtils;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.converter.NumberStringConverter;

/**
 *
 * @author Pavel Castornii
 */
class ConnectionsPageView extends AbstractPageView<ConnectionsPageViewModel> {

    private final TableView<ConnectionSettings> table = new TableView<>();

    private final Button addButton = new Button(null, new FontIconView(ConsoleIcons.ADD));

    private final Button removeButton = new Button(null, new FontIconView(ConsoleIcons.REMOVE));

    private final HBox titleBox = new HBox(getTitleLabel(), new Spacer(), addButton, removeButton);

    private final GridPane gridPane = new GridPane();

    private final VBox main = new VBox(titleBox, table, gridPane);

    private final Label nameLabel = new Label("Name");

    private final TextField nameTextField = new TextField();

    private final Label loginNameLabel = new Label("Login Name");

    private final TextField loginNameTextField = new TextField();

    private final Label loginPasswordLabel = new Label("Login Password");

    private final PasswordField loginPasswordTextField = new PasswordField();

    private final Label hostLabel = new Label("Host");

    private final TextField hostTextField = new TextField();

    private final Label portLabel = new Label("Port");

    private final TextField portTextField = new TextField();

    private final Label protocolLabel = new Label("Protocol");

    private final ComboBox<Protocol> protocolComboBox =
            new ComboBox<>(FXCollections.observableArrayList(Protocol.values()));

    private final Label secureLabel = new Label("Secure");

    private final ToggleSwitch secureSwitch = new ToggleSwitch();

    ConnectionsPageView(ConnectionsPageViewModel viewModel) {
        super(viewModel);
    }

    @Override
    public void requestFocus() {
        NodeUtils.requestFocus(this.table);
    }

    @Override
    public Region getNode() {
        return this.main;
    }

    @Override
    protected void build(ConnectionsPageViewModel viewModel) {
        super.build(viewModel);
        table.getStyleClass().add(Styles.DENSE);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label(""));
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<ConnectionSettings, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        TableColumn<ConnectionSettings, String> hostColumn = new TableColumn<>("Host");
        hostColumn.setCellValueFactory(cellData -> cellData.getValue().hostProperty());
        TableColumn<ConnectionSettings, String> loginNameColumn = new TableColumn<>("Login Name");
        loginNameColumn.setCellValueFactory(cellData -> cellData.getValue().loginNameProperty());
        table.getColumns().addAll(nameColumn, hostColumn, loginNameColumn);
        table.setItems(viewModel.getConnections());

        addButton.getStyleClass().addAll(Styles.DENSE, StyleClasses.ICONED_BUTTON);
        removeButton.getStyleClass().addAll(Styles.DENSE, StyleClasses.ICONED_BUTTON);
        titleBox.setSpacing(SizeConstants.INSET);
        titleBox.setAlignment(Pos.TOP_LEFT);

        for (int i = 0; i < 4; i++) {
            var cc = new ColumnConstraints();
            gridPane.getColumnConstraints().add(cc);
            if (i % 2 == 0) {
                cc.setHgrow(Priority.NEVER);
            } else {
                cc.setHgrow(Priority.ALWAYS);
            }
        }
        HBox.setHgrow(gridPane, Priority.ALWAYS);
        gridPane.setVgap(SizeConstants.INSET);
        gridPane.setHgap(SizeConstants.INSET);

        this.nameLabel.setMinWidth(Region.USE_PREF_SIZE);
        gridPane.add(nameLabel, 0, 0);
        gridPane.add(nameTextField, 1, 0);
        this.nameTextField.getStyleClass().add(Styles.DENSE);
        GridPane.setHgrow(this.nameTextField, Priority.ALWAYS);
        GridPane.setColumnSpan(nameTextField, 3);

        gridPane.add(loginNameLabel, 0, 1);
        this.loginNameLabel.setMinWidth(Region.USE_PREF_SIZE);
        gridPane.add(loginNameTextField, 1, 1);
        loginNameTextField.getStyleClass().add(Styles.DENSE);
        GridPane.setHgrow(this.loginNameTextField, Priority.ALWAYS);
        this.loginPasswordLabel.setMinWidth(Region.USE_PREF_SIZE);
        gridPane.add(loginPasswordLabel, 2, 1);
        gridPane.add(loginPasswordTextField, 3, 1);
        GridPane.setHgrow(this.loginPasswordTextField, Priority.ALWAYS);
        loginPasswordTextField.getStyleClass().add(Styles.DENSE);

        hostLabel.setMinWidth(Region.USE_PREF_SIZE);
        gridPane.add(hostLabel, 0, 2);
        gridPane.add(hostTextField, 1, 2);
        GridPane.setHgrow(hostTextField, Priority.ALWAYS);
        hostTextField.getStyleClass().add(Styles.DENSE);
        portLabel.setMinWidth(Region.USE_PREF_SIZE);
        gridPane.add(portLabel, 2, 2);
        gridPane.add(portTextField, 3, 2);
        GridPane.setHgrow(portTextField, Priority.ALWAYS);
        portTextField.getStyleClass().add(Styles.DENSE);

        protocolLabel.setMinWidth(Region.USE_PREF_SIZE);
        gridPane.add(protocolLabel, 0, 3);
        gridPane.add(protocolComboBox, 1, 3);
        GridPane.setHgrow(protocolComboBox, Priority.ALWAYS);
        protocolComboBox.setItems(viewModel.getProtocols());
        protocolComboBox.setMaxWidth(Double.MAX_VALUE);
        protocolComboBox.getStyleClass().add(Styles.DENSE);
        secureLabel.setMinWidth(Region.USE_PREF_SIZE);
        gridPane.add(secureLabel, 2, 3);
        gridPane.add(secureSwitch, 3, 3);

        main.setSpacing(SizeConstants.INSET);
        VBox.setVgrow(main, Priority.ALWAYS);
    }

    @Override
    protected void addListeners(ConnectionsPageViewModel viewModel) {
        super.addListeners(viewModel);
        viewModel.connectionProperty().addListener((ov, oldV, newV) -> this.table.getSelectionModel().select(newV));
        this.table.getSelectionModel().selectedItemProperty().addListener((ov, oldV, newV) -> {
                viewModel.connectionProperty().set(newV);
                updateFormBindings(oldV, newV);
        });
        this.protocolComboBox.getSelectionModel().selectedItemProperty().addListener((ov, oldV, newV) -> {
            var c = viewModel.connectionProperty().get();
            if (c != null) {
                c.setProtocol(newV);
            }
        });
    }

    @Override
    protected void addHandlers(ConnectionsPageViewModel viewModel) {
        super.addHandlers(viewModel);
        this.addButton.setOnAction(e -> viewModel.addConnection());
        this.removeButton.setOnAction(e -> viewModel.removeConnection());
    }

    private void updateFormBindings(ConnectionSettings oldC, ConnectionSettings newC) {
        if (oldC != null) {
            unbindForm(oldC);
        }
        if (newC != null) {
            bindForm(newC);
        } else {
            clearForm();
        }
    }

    private void unbindForm(ConnectionSettings oldC) {
        nameTextField.textProperty().unbindBidirectional(oldC.nameProperty());
        loginNameTextField.textProperty().unbindBidirectional(oldC.loginNameProperty());
        loginPasswordTextField.textProperty().unbindBidirectional(oldC.loginPasswordProperty());
        hostTextField.textProperty().unbindBidirectional(oldC.hostProperty());
        portTextField.textProperty().unbindBidirectional(oldC.portProperty());
        secureSwitch.selectedProperty().unbindBidirectional(oldC.secureProperty());
    }

    private void bindForm(ConnectionSettings newC) {
        nameTextField.textProperty().bindBidirectional(newC.nameProperty());
        loginNameTextField.textProperty().bindBidirectional(newC.loginNameProperty());
        loginPasswordTextField.textProperty().bindBidirectional(newC.loginPasswordProperty());
        hostTextField.textProperty().bindBidirectional(newC.hostProperty());
        portTextField.textProperty().bindBidirectional(newC.portProperty(), new NumberStringConverter());
        protocolComboBox.getSelectionModel().select(newC.getProtocol());
        secureSwitch.selectedProperty().bindBidirectional(newC.secureProperty());
    }

    private void clearForm() {
        nameTextField.textProperty().set(null);
        loginNameTextField.textProperty().set(null);
        loginPasswordTextField.textProperty().set(null);
        hostTextField.textProperty().set(null);
        portTextField.textProperty().set(null);
        protocolComboBox.getSelectionModel().select(null);
        secureSwitch.selectedProperty().set(false);
    }
}
