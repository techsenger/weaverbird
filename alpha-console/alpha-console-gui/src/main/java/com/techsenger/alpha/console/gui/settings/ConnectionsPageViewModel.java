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
//package com.techsenger.alpha.console.gui.settings;
//
//import com.techsenger.alpha.api.net.session.Protocol;
//import com.techsenger.tabshell.core.page.AbstractPageViewModel;
//import com.techsenger.tabshell.core.page.PageKey;
//import java.util.ArrayList;
//import javafx.beans.property.ObjectProperty;
//import javafx.beans.property.SimpleObjectProperty;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//
///**
// *
// * @author Pavel Castornii
// */
//class ConnectionsPageViewModel extends AbstractPageViewModel {
//
//    private static final PageKey key = new PageKey("Connection Page");
//
//    private final ObservableList<ConnectionSettings> connections;
//
//    private final ObjectProperty<ConnectionSettings> connection = new SimpleObjectProperty<>();
//
//    private final ObservableList<Protocol> protocols = FXCollections.observableArrayList(Protocol.values());
//
//    ConnectionsPageViewModel(ConsoleSettings settings) {
//        setTitle("Connections");
//        var originalConnections = settings.getConnections();
//        this.connections = FXCollections.observableArrayList(new ArrayList<>(originalConnections));
//    }
//
//    @Override
//    public PageKey getKey() {
//        return key;
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
//    ObservableList<Protocol> getProtocols() {
//        return protocols;
//    }
//
//    void addConnection() {
//        var connection = new ConnectionSettings();
//        connection.setName("NewConnection");
//        this.connections.add(connection);
//        this.connection.set(connection);
//    }
//
//    void removeConnection() {
//        var c = this.connection.get();
//        if (c != null) {
//            this.connections.remove(c);
//        }
//    }
//}
