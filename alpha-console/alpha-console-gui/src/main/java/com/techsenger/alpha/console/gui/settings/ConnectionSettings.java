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
//import com.techsenger.alpha.api.net.session.ConnectionBase;
//import com.techsenger.alpha.api.net.session.Protocol;
//import jakarta.xml.bind.annotation.XmlAttribute;
//import jakarta.xml.bind.annotation.XmlRootElement;
//import java.util.concurrent.atomic.AtomicInteger;
//import javafx.beans.property.BooleanProperty;
//import javafx.beans.property.IntegerProperty;
//import javafx.beans.property.ObjectProperty;
//import javafx.beans.property.SimpleBooleanProperty;
//import javafx.beans.property.SimpleIntegerProperty;
//import javafx.beans.property.SimpleObjectProperty;
//import javafx.beans.property.SimpleStringProperty;
//import javafx.beans.property.StringProperty;
//
///**
// *
// * @author Pavel Castornii
// */
//@XmlRootElement(name = "Connection")
//public class ConnectionSettings implements ConnectionBase {
//
//    private final AtomicInteger sessionCounter = new AtomicInteger(0);
//
//    private StringProperty name = new SimpleStringProperty();
//
//    private StringProperty loginName = new SimpleStringProperty();
//
//    private StringProperty loginPassword = new SimpleStringProperty();
//
//    private StringProperty host = new SimpleStringProperty();
//
//    private IntegerProperty port = new SimpleIntegerProperty();
//
//    private ObjectProperty<Protocol> protocol = new SimpleObjectProperty<>();
//
//    private BooleanProperty secure = new SimpleBooleanProperty();
//
//    public StringProperty nameProperty() {
//        return name;
//    }
//
//    @XmlAttribute
//    @Override
//    public String getName() {
//        return name.get();
//    }
//
//    public void setName(String name) {
//        this.name.set(name);
//    }
//
//    public StringProperty loginNameProperty() {
//        return this.loginName;
//    }
//
//    @XmlAttribute
//    @Override
//    public String getLoginName() {
//        return loginName.get();
//    }
//
//    public void setLoginName(String loginName) {
//        this.loginName.set(loginName);
//    }
//
//    public StringProperty loginPasswordProperty() {
//        return this.loginPassword;
//    }
//
//    @XmlAttribute
//    public String getLoginPassword() {
//        return loginPassword.get();
//    }
//
//    public void setLoginPassword(String loginPassword) {
//        this.loginPassword.set(loginPassword);
//    }
//
//    public StringProperty hostProperty() {
//        return this.host;
//    }
//
//    @XmlAttribute
//    @Override
//    public String getHost() {
//        return host.get();
//    }
//
//    public void setHost(String host) {
//        this.host.set(host);
//    }
//
//    public IntegerProperty portProperty() {
//        return this.port;
//    }
//
//    @XmlAttribute
//    @Override
//    public int getPort() {
//        return port.get();
//    }
//
//    public void setPort(int port) {
//        this.port.set(port);
//    }
//
//    public ObjectProperty<Protocol> protocolProperty() {
//        return this.protocol;
//    }
//
//    @XmlAttribute
//    @Override
//    public Protocol getProtocol() {
//        return protocol.get();
//    }
//
//    public void setProtocol(Protocol protocol) {
//        this.protocol.set(protocol);
//    }
//
//    public BooleanProperty secureProperty() {
//        return this.secure;
//    }
//
//    @XmlAttribute
//    @Override
//    public boolean isSecure() {
//        return secure.get();
//    }
//
//    public void setSecure(boolean secure) {
//        this.secure.set(secure);
//    }
//
//    public AtomicInteger getSessionCounter() {
//        return sessionCounter;
//    }
//}
