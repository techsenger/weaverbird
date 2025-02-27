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

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpsExchange;
import com.techsenger.alpha.api.net.session.Protocol;
import com.techsenger.alpha.api.net.session.SessionStatus;
import com.techsenger.alpha.net.shared.http.LoginRequest;
import com.techsenger.alpha.net.shared.http.LoginResponse;
import com.techsenger.alpha.spi.net.security.SecurityContextService;
import com.techsenger.toolkit.core.jpms.ServiceUtils;
import java.io.IOException;
import java.time.LocalDateTime;
import javax.net.ssl.SSLSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
public class LoginHandler extends AbstractHandler {

    private static final Logger logger = LoggerFactory.getLogger(LoginHandler.class);

    public LoginHandler(DefaultHttpServer httpServer) {
        super(httpServer);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            var request = this.getRequest(httpExchange, LoginRequest.class);
            if (httpExchange instanceof HttpsExchange) {
                SSLSession sess = ((HttpsExchange) httpExchange).getSSLSession();
                logger.debug("There is an attempt to login using SSL from {}", sess.getPeerHost());
            }
            SecurityContextService contextService = SecurityContextServiceHolder.getInstance();
            if (contextService == null) {
                var op = ServiceUtils.loadProvider(LoginHandler.class.getModule().getLayer(), false,
                        SecurityContextService.class);
                if (op.isPresent()) {
                    contextService = op.get();
                }
            }
            if (contextService == null) {
                throw new IllegalStateException("Security context service wasn't provided");
            }
            var finalContextService = contextService;
            var result = this.call(() -> {
                var context = finalContextService.getFactory(true).create(
                        request.getLoginName(),
                        request.getLoginPassword(),
                        request.getVersion());
                return context;
            });
            LoginResponse response = null;
            if (result.isSuccessful()) {
                HttpSession session = this.getHttpServer().startSession();
                session.setLoginName(request.getLoginName());
                session.setProtocol(Protocol.HTTP);
                session.setSecure(getHttpServer().isSecure());
                session.setStatus(SessionStatus.OPEN);
                session.setHost(httpExchange.getRemoteAddress().getHostString());
                session.setPort(httpExchange.getRemoteAddress().getPort());
                session.setCommandSecurityContext(result.getResult());
                session.setOpenedAt(LocalDateTime.now());
                response = new LoginResponse(session.getUuid(), null);
            } else {
                response = new LoginResponse(null, result.getException());
            }

            this.sendResponse(response, httpExchange);
        } catch (Exception ex) {
            logger.error("Error processing request", ex);
        }
    }
}
