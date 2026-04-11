package com.techsenger.alpha.gui.session;

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
//package com.techsenger.alpha.console.gui.session;
//
//import atlantafx.base.theme.Styles;
//import com.techsenger.alpha.api.net.session.Protocol;
//import com.techsenger.alpha.api.net.session.ClientSession;
//import com.techsenger.alpha.console.gui.style.ConsoleIcons;
//import com.techsenger.mvvm4fx.core.ComponentHelper;
//import com.techsenger.toolkit.fx.Spacer;
//import com.techsenger.toolkit.fx.utils.NodeUtils;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import javafx.beans.property.SimpleBooleanProperty;
//import javafx.beans.property.SimpleIntegerProperty;
//import javafx.beans.property.SimpleObjectProperty;
//import javafx.beans.property.SimpleStringProperty;
//import javafx.geometry.Insets;
//import javafx.scene.control.Button;
//import javafx.scene.control.Label;
//import javafx.scene.control.TableColumn;
//import javafx.scene.control.TableView;
//import javafx.scene.control.cell.TextFieldTableCell;
//import javafx.scene.layout.HBox;
//import javafx.scene.layout.Priority;
//import javafx.scene.layout.VBox;
//import javafx.util.converter.LocalDateTimeStringConverter;
//
///**
// *
// * @author Pavel Castornii
// */
//public class SessionDialogView<T extends SessionDialogViewModel>
//        extends AbstractSimpleDialogView<SessionDialogViewModel> {
//
//    private final Button newButton = new Button("New Session");
//
//    private final Button closeButton = new Button("Close Session");
//
//    private final Button refreshButton = new Button(null, new FontIconView(ConsoleIcons.REFRESH));
//
//    private final HBox buttonBox = new HBox(newButton, closeButton, new Spacer(), refreshButton);
//
//    private final TableView<ClientSession> table = new TableView<>();
//
//    private final DialogManager dialogManager;
//
//    private boolean sortEnabled = true;
//
//    public SessionDialogView(SessionDialogViewModel viewModel, DialogManager dialogManager) {
//        super(viewModel);
//        this.dialogManager = dialogManager;
//    }
//
//    @Override
//    public void requestFocus() {
//        NodeUtils.requestFocus(getContentPane());
//    }
//
//    @Override
//    protected void build(SessionDialogViewModel viewModel) {
//        super.build(viewModel);
//        newButton.getStyleClass().addAll(Styles.DENSE);
//        closeButton.getStyleClass().addAll(Styles.DENSE);
//        refreshButton.getStyleClass().addAll(Styles.DENSE, StyleClasses.ICONED_BUTTON);
//        buttonBox.setSpacing(SizeConstants.INSET);
//
//        table.setItems(viewModel.getSessions());
//        table.getStyleClass().add(Styles.DENSE);
//        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
//        table.setPlaceholder(new Label(""));
//        VBox.setVgrow(table, Priority.ALWAYS);
//
//        TableColumn<ClientSession, String> nameColumn = new TableColumn<>("Name");
//        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
//        TableColumn<ClientSession, String> hostColumn = new TableColumn<>("Host");
//        hostColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getHost()));
//        TableColumn<ClientSession, String> loginNameColumn = new TableColumn<>("Login Name");
//        loginNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLoginName()));
//        TableColumn<ClientSession, Integer> portColumn = new TableColumn<>("Port");
//        portColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getPort())
//              .asObject());
//        TableColumn<ClientSession, Protocol> protocolColumn = new TableColumn<>("Protocol");
//        protocolColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getProtocol()));
//        TableColumn<ClientSession, Boolean> secureColumn = new TableColumn<>("Secure");
//        secureColumn.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().isSecure()));
//        TableColumn<ClientSession, LocalDateTime> openedAtColumn = new TableColumn<>("Opened At");
//        openedAtColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getOpenedAt()));
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        openedAtColumn.setCellFactory(column ->
//                new TextFieldTableCell<>(new LocalDateTimeStringConverter(formatter, formatter)));
//
//        table.getColumns().addAll(nameColumn, hostColumn, loginNameColumn, portColumn,  protocolColumn, secureColumn,
//                openedAtColumn);
//        table.getSortOrder().add(nameColumn);
//
//        //box
//        var box = new VBox(buttonBox, table);
//        box.setSpacing(SizeConstants.INSET);
//        box.setPadding(new Insets(SizeConstants.INSET));
//        VBox.setVgrow(box, Priority.ALWAYS);
//        this.getContentPane().getChildren().add(box);
//        getFocusTrap().activate();
//    }
//
//    @Override
//    protected void addListeners(SessionDialogViewModel viewModel) {
//        super.addListeners(viewModel);
//        viewModel.getSortRequired().addListener(v -> {
//            if (v) {
//                table.sort();
//            }
//        });
//    }
//
//    @Override
//    protected void addHandlers(SessionDialogViewModel viewModel) {
//        super.addHandlers(viewModel);
//        newButton.setOnAction(e -> viewModel.createNewSession());
//        closeButton.setOnAction(e -> viewModel.closeSession());
//        refreshButton.setOnAction(e -> viewModel.refreshSessions());
//    }
//
//    @Override
//    protected void bind(SessionDialogViewModel viewModel) {
//        super.bind(viewModel);
//        viewModel.sessionProperty().bind(table.getSelectionModel().selectedItemProperty());
//    }
//
//    @Override
//    protected ComponentHelper<?> createComponentHelper() {
//        return new SessionDialogHelper(this); //PEREDELATI,
//    }
//
//    void openDialog(DialogView<?> dialog) {
//        this.dialogManager.openDialog(dialog);
//    }
//}
