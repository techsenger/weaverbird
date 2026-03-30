package com.techsenger.alpha.net.client.api;

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
//package com.techsenger.alpha.api.net.session;
//
//import com.techsenger.toolkit.http.ClientSession;
//import com.techsenger.toolkit.http.session.Session;
//import java.util.Objects;
//
///**
// * Session descriptor is NOT created in the client and doesn't depend on the client. So, its instances can be safely
// * used when client layer is removed.
// *
// *
// * @author Pavel Castornii
// */
//public final class SessionDescriptor implements ConnectionBase {
//
//    private final String name;
//
//    private final String host;
//
//    private final int port;
//
//    private final String loginName;
//
//    public SessionDescriptor(Session session) {
//        this.name = session.getName();
//        this.host = session.getHost();
//        this.port = session.getPort();
//        this.loginName = session.getLoginName();
//    }
//
//    @Override
//    public String getName() {
//        return name;
//    }
//
//    @Override
//    public String getHost() {
//        return host;
//    }
//
//    @Override
//    public int getPort() {
//        return port;
//    }
//
//    @Override
//    public String getLoginName() {
//        return loginName;
//    }
//
//    @Override
//    public int hashCode() {
//        int hash = 5;
//        hash = 37 * hash + Objects.hashCode(this.name);
//        hash = 37 * hash + Objects.hashCode(this.host);
//        hash = 37 * hash + this.port;
//        hash = 37 * hash + Objects.hashCode(this.loginName);
//        return hash;
//    }
//
//    @Override
//    public boolean equals(Object obj) {
//        if (this == obj) {
//            return true;
//        }
//        if (obj == null) {
//            return false;
//        }
//        if (getClass() != obj.getClass()) {
//            return false;
//        }
//        final SessionDescriptor other = (SessionDescriptor) obj;
//        if (this.port != other.port) {
//            return false;
//        }
//        if (!Objects.equals(this.name, other.name)) {
//            return false;
//        }
//        if (!Objects.equals(this.host, other.host)) {
//            return false;
//        }
//        if (!Objects.equals(this.loginName, other.loginName)) {
//            return false;
//        }
//        return true;
//    }
//}
