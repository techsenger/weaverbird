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

import com.techsenger.alpha.api.net.ServerService;
import com.techsenger.alpha.api.net.session.Protocol;
import com.techsenger.alpha.api.net.session.SessionInfo;
import com.techsenger.toolkit.core.SingletonFactory;
import java.util.Collection;


/**
 *
 * @author Pavel Castornii
 */
public class HttpServerProvider implements ServerService {

    private static final SingletonFactory<ServerService> singletonFactory =
            new SingletonFactory<>(() -> new HttpServerProvider());

    public static final ServerService provider() {
        return singletonFactory.singleton();
    }

    private volatile DefaultHttpServer httpServer;

    @Override
    public Protocol getProtocol() {
        return Protocol.HTTP;
    }

    @Override
    public synchronized void start(String host, int port, boolean secure) throws Exception {
        if (this.httpServer != null) {
            throw new IllegalStateException(getProtocol() + " server is already running");
        } else {
            this.httpServer = new DefaultHttpServer();
            this.httpServer.start(host, port, secure);
        }
    }

    @Override
    public boolean isRunning() {
        return this.httpServer != null;
    }

    @Override
    public synchronized void stop() throws Exception {
        if (this.httpServer != null) {
            this.httpServer.stop();
            this.httpServer = null;
        } else {
            throw new IllegalStateException(getProtocol() + " server not running");
        }
    }

    @Override
    public Collection<SessionInfo> getSessionInfos() {
        if (this.httpServer == null) {
            throw new IllegalStateException("Server is not running");
        } else {
            return this.httpServer.getSessionInfos();
        }
    }
}
