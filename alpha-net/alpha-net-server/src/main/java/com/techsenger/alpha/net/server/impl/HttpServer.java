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

package com.techsenger.alpha.net.server.impl;

import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import com.techsenger.alpha.core.api.Framework;
import com.techsenger.alpha.net.server.api.ServerService;
import com.techsenger.alpha.net.server.impl.handlers.ComponentActivateHandler;
import com.techsenger.alpha.net.server.impl.handlers.ComponentAddHandler;
import com.techsenger.alpha.net.server.impl.handlers.ComponentDeactivateHandler;
import com.techsenger.alpha.net.server.impl.handlers.ComponentDeployHandler;
import com.techsenger.alpha.net.server.impl.handlers.ComponentDescriptorHandler;
import com.techsenger.alpha.net.server.impl.handlers.ComponentInstallHandler;
import com.techsenger.alpha.net.server.impl.handlers.ComponentListHandler;
import com.techsenger.alpha.net.server.impl.handlers.ComponentRemoveHandler;
import com.techsenger.alpha.net.server.impl.handlers.ComponentResolveHandler;
import com.techsenger.alpha.net.server.impl.handlers.ComponentStartHandler;
import com.techsenger.alpha.net.server.impl.handlers.ComponentStateHandler;
import com.techsenger.alpha.net.server.impl.handlers.ComponentStopHandler;
import com.techsenger.alpha.net.server.impl.handlers.ComponentUndeployHandler;
import com.techsenger.alpha.net.server.impl.handlers.ComponentUninstallHandler;
import com.techsenger.alpha.net.server.impl.handlers.ComponentUnresolveHandler;
import com.techsenger.alpha.net.server.impl.handlers.EndpointListHandler;
import com.techsenger.alpha.net.server.impl.handlers.LayerInfoHandler;
import com.techsenger.alpha.net.server.impl.handlers.LoginHandler;
import com.techsenger.alpha.net.server.impl.handlers.LogoutHandler;
import com.techsenger.alpha.net.server.impl.handlers.ModuleListHandler;
import com.techsenger.alpha.net.server.impl.handlers.ModuleResolveHandler;
import com.techsenger.alpha.net.server.impl.handlers.ModuleUnresolveHandler;
import com.techsenger.alpha.net.server.impl.handlers.SessionListHandler;
import com.techsenger.alpha.net.server.impl.handlers.ThreadInfoListHandler;
import com.techsenger.alpha.net.server.impl.handlers.ThreadListHandler;
import com.techsenger.alpha.net.server.spi.EndpointHandlerService;
import com.techsenger.alpha.net.server.spi.FrameworkRequestContext;
import com.techsenger.alpha.net.shared.json.GsonProvider;
import com.techsenger.toolkit.core.jpms.ServiceUtils;
import com.techsenger.toolkit.http.DispatcherHttpHandler;
import com.techsenger.toolkit.http.JsonConverter;
import com.techsenger.toolkit.http.Server;
import com.techsenger.toolkit.http.SimpleHttpServer;
import com.techsenger.toolkit.http.handler.EndpointHandler;
import com.techsenger.toolkit.http.request.DefaultRequestContext;
import com.techsenger.toolkit.http.request.Request;
import com.techsenger.toolkit.http.request.RequestContextFactory;
import com.techsenger.toolkit.http.request.RequestEnvelope;
import com.techsenger.toolkit.http.response.ResponseEnvelope;
import com.techsenger.toolkit.http.security.SecurityContextFactory;
import com.techsenger.toolkit.http.session.Session;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author Pavel Castornii
 */
public class HttpServer implements ServerService {

    private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);

    private static final class FrameworkRequestContextImpl extends DefaultRequestContext
            implements FrameworkRequestContext {

        private final Framework framework;

        FrameworkRequestContextImpl(Server server, HttpExchange exchange, Session session, Framework framework) {
            super(server, exchange, session);
            this.framework = framework;
        }

        @Override
        public Framework getFramework() {
            return this.framework;
        }
    }

    private volatile SimpleHttpServer httpServer;

    private final Framework framework;

    public HttpServer(Framework framework) {
        this.framework = framework;
    }

    @Override
    public synchronized void start(String host, int port) throws Exception {
        if (this.httpServer != null) {
            throw new IllegalStateException("Server is already running");
        }

        RequestContextFactory<FrameworkRequestContext> rcf =  new RequestContextFactory<FrameworkRequestContext>() {
            @Override
            public FrameworkRequestContext create(HttpExchange exchange, Session session) {
                return new FrameworkRequestContextImpl(httpServer, exchange, session, framework);
            }
        };

        JsonConverter converter = new JsonConverter() {
            @Override
            public <T extends Request> RequestEnvelope<T> fromJson(String str, Class<T> requestClass) {
                if (requestClass == null) {
                    requestClass = (Class<T>) Request.class;
                }
                var type = TypeToken.getParameterized(RequestEnvelope.class, requestClass).getType();
                RequestEnvelope<T> envelope = GsonProvider.gson().fromJson(str, type);
                return envelope;
            }

            @Override
            public String toJson(ResponseEnvelope<?> envelope) {
                var str = GsonProvider.gson().toJson(envelope);
                return str;
            }
        };

        // parent layers included because without it it is not possible to run framework for tests using plugin
        var scf = ServiceUtils.loadProvider(HttpServer.class.getModule().getLayer(), true,
                    SecurityContextFactory.class);
        if (scf.isEmpty()) {
            throw new IllegalStateException("Security context factory not found");
        }

        var handlerProviders = ServiceUtils.loadProviders(HttpServer.class.getModule().getLayer(), false,
                EndpointHandlerService.class);
        var customHandlers = handlerProviders.stream()
                .flatMap(service -> service.getHandlers().stream())
                .collect(Collectors.toList());
        var defaultHandlers = createDefaultHandlers();
        var allHandlers = Stream.concat(customHandlers.stream(), defaultHandlers.stream()).collect(Collectors.toList());

        var dispatcherHandler = new DispatcherHttpHandler(rcf, converter, allHandlers, scf.get());
        this.httpServer = new SimpleHttpServer(dispatcherHandler);
        this.httpServer.start(host, port, null);
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
            throw new IllegalStateException("Server is not running");
        }
    }

    private List<Class<? extends EndpointHandler<FrameworkRequestContext, ?>>> createDefaultHandlers() {
        return List.of(
                ComponentActivateHandler.class,
                ComponentAddHandler.class,
                ComponentDeactivateHandler.class,
                ComponentDeployHandler.class,
                ComponentDescriptorHandler.class,
                ComponentInstallHandler.class,
                ComponentListHandler.class,
                ComponentRemoveHandler.class,
                ComponentResolveHandler.class,
                ComponentStartHandler.class,
                ComponentStateHandler.class,
                ComponentStopHandler.class,
                ComponentUndeployHandler.class,
                ComponentUninstallHandler.class,
                ComponentUnresolveHandler.class,
                EndpointListHandler.class,
                LayerInfoHandler.class,
                LoginHandler.class,
                LogoutHandler.class,
                ModuleListHandler.class,
                ModuleResolveHandler.class,
                ModuleUnresolveHandler.class,
                SessionListHandler.class,
                ThreadInfoListHandler.class,
                ThreadListHandler.class);
    }
}
