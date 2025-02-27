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
import com.techsenger.alpha.api.net.security.AuthenticationException;
import com.techsenger.alpha.net.shared.http.LogoutRequest;
import com.techsenger.alpha.net.shared.http.LogoutResponse;
import java.io.IOException;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
public class LogoutHandler extends AbstractHandler {

    private static final Logger logger = LoggerFactory.getLogger(LogoutHandler.class);

    public LogoutHandler(DefaultHttpServer httpServer) {
        super(httpServer);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            var request = this.getRequest(httpExchange, LogoutRequest.class);

            LogoutResponse response;
            var result = this.call((Callable<Void>) () -> {
                var session = this.getHttpServer().stopSession(request.getSessionUuid());
                if (session != null) {
                    session.close(); //exceptions here
                } else {
                    //we need to show, that there wasn't session
                    throw new AuthenticationException();
                }
                return null;
            });
            response = new LogoutResponse(result.isSuccessful(), result.getException());

            this.sendResponse(response, httpExchange);
        } catch (Exception ex) {
            logger.error("Error processing request", ex);
        }
    }

}
