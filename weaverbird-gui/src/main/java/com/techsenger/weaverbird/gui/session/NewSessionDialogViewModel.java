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
//import com.techsenger.weaverbird.console.gui.keys.ConsoleComponentKeys;
//import com.techsenger.weaverbird.console.gui.settings.ConnectionSettings;
//import com.techsenger.weaverbird.console.gui.settings.ConsoleSettings;
//import com.techsenger.tabshell.core.dialog.DialogKey;
//import com.techsenger.tabshell.core.dialog.DialogScope;
//import com.techsenger.tabshell.kit.dialog.AbstractSimpleDialogViewModel;
//import javafx.beans.property.BooleanProperty;
//import javafx.beans.property.ObjectProperty;
//import javafx.beans.property.SimpleBooleanProperty;
//import javafx.beans.property.SimpleObjectProperty;
//import javafx.beans.property.SimpleStringProperty;
//import javafx.beans.property.StringProperty;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//
///**
// *
// * @author Pavel Castornii
// */
//class NewSessionDialogViewModel extends AbstractSimpleDialogViewModel {
//
//    private final ObservableList<ConnectionSettings> connections;
//
//    private final ObjectProperty<ConnectionSettings> connection = new SimpleObjectProperty<>();
//
//    private final StringProperty name = new SimpleStringProperty();
//
//    private final BooleanProperty connectionError = new SimpleBooleanProperty();
//
//    private final BooleanProperty nameError = new SimpleBooleanProperty();
//
//    NewSessionDialogViewModel(ConsoleSettings settings) {
//        super(DialogScope.SHELL, false);
//        setPrefWidth(600);
//        setTitle("New Session");
//        connection.addListener((ov, oldV, newV) -> {
//            if (newV != null) {
//                name.set(newV.getName() + newV.getSessionCounter().get());
//            }
//            validateConnection();
//        });
//        name.addListener((ov, oldV, newV) -> {
//            validateName();
//        });
//        this.connections = FXCollections.observableArrayList(settings.getConnections());
//    }
//
//    @Override
//    public DialogKey getKey() {
//        return ConsoleComponentKeys.NEW_SESSION_DIALOG;
//    }
//
//    ObservableList<ConnectionSettings> getConnections() {
//        return connections;
//    }
//
//    ObjectProperty<ConnectionSettings> connectionProperty() {
//        return connection;
//    }
//
//    StringProperty nameProperty() {
//        return name;
//    }
//
//    BooleanProperty connectionErrorProperty() {
//        return connectionError;
//    }
//
//    BooleanProperty nameErrorProperty() {
//        return nameError;
//    }
//
//    boolean validate() {
//        return validateConnection() && validateName();
//    }
//
//    void preDeinitialize() {
//        if (connection.get() != null) {
//            connection.get().getSessionCounter().getAndIncrement();
//        }
//    }
//
//    private boolean validateName() {
//        if (name.get().isBlank()) {
//            nameError.set(true);
//        } else {
//            nameError.set(false);
//        }
//        return !nameError.get();
//    }
//
//    private boolean validateConnection() {
//        connectionError.set(connection.get() == null);
//        return !connectionError.get();
//    }
//}
