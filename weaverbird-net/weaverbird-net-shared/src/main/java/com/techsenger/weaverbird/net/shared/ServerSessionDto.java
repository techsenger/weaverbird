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

package com.techsenger.weaverbird.net.shared;

import com.techsenger.toolkit.http.security.SecurityContext;
import com.techsenger.toolkit.http.session.Session;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 *
 * @author Pavel Castornii
 */
public class ServerSessionDto implements Serializable {

    private String uuid;

    private String remoteHost;

    private int remotePort;

    private boolean closed;

    private LocalDateTime openedAt;

    private LocalDateTime closedAt;

    private String loginName;

    public static ServerSessionDto of(Session session) {
        SecurityContext securityContext = session.getSecurityContext();
        ServerSessionDto dto = new ServerSessionDto();
        dto.uuid = session.getUuid();
        dto.remoteHost = session.getRemoteHost();
        dto.remotePort = session.getRemotePort();
        dto.closed = session.isClosed();
        dto.openedAt = session.getOpenedAt();
        dto.closedAt = session.getClosedAt();
        dto.loginName = securityContext != null ? securityContext.getLoginName() : null;
        return dto;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getRemoteHost() {
        return remoteHost;
    }

    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }

    public int getRemotePort() {
        return remotePort;
    }

    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public LocalDateTime getOpenedAt() {
        return openedAt;
    }

    public void setOpenedAt(LocalDateTime openedAt) {
        this.openedAt = openedAt;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }
}
