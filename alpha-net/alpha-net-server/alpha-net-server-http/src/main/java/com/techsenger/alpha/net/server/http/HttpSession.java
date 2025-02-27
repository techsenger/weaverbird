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

package com.techsenger.alpha.net.server.http;

import com.techsenger.alpha.api.net.security.AuthenticationException;
import com.techsenger.alpha.api.net.security.CommandSecurityContext;
import com.techsenger.alpha.api.net.session.SessionStatus;
import com.techsenger.alpha.spi.net.session.AbstractSession;
import java.time.Instant;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
public class HttpSession extends AbstractSession {

    private static final Logger logger = LoggerFactory.getLogger(HttpSession.class);

    /**
     * Unix time.
     */
    private long lastAccessedTime;

    private CommandSecurityContext commandSecurityContext;

    public HttpSession(String uuid) {
        setUuid(uuid);
    }

    public void setLastAccessedTime(long lastAccessedTime) {
        this.lastAccessedTime = lastAccessedTime;
    }

    public long getLastAccessedTime() {
        return lastAccessedTime;
    }

    public void touch() {
        this.setLastAccessedTime(Instant.now().getEpochSecond());
    }

    public CommandSecurityContext getCommandSecurityContext() {
        return commandSecurityContext;
    }

    public void setCommandSecurityContext(CommandSecurityContext commandSecurityContext) {
        this.commandSecurityContext = commandSecurityContext;
    }

    public void close() throws AuthenticationException, Exception {
        setStatus(SessionStatus.CLOSED);
        setClosedAt(LocalDateTime.now());
        if (this.commandSecurityContext != null) {
            this.commandSecurityContext.close();
        }
    }

    @Override
    public String toString() {
        return "Session{" + "uuid=" + getUuid() + ", lastAccessedTime=" + lastAccessedTime + '}';
    }
}
