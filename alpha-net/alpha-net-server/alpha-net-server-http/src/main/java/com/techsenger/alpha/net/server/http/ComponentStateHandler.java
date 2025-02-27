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
import com.techsenger.alpha.api.Framework;
import com.techsenger.alpha.api.net.security.AuthenticationException;
import com.techsenger.alpha.net.shared.http.ComponentStateRequest;
import com.techsenger.alpha.net.shared.http.ComponentStateResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
public class ComponentStateHandler extends AbstractHandler {

    private static final Logger logger = LoggerFactory.getLogger(ComponentStateHandler.class);

    public ComponentStateHandler(DefaultHttpServer httpServer) {
        super(httpServer);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
         try {
            var request = getRequest(exchange, ComponentStateRequest.class);
             ComponentStateResponse response;
            if (isAuthenticated(request)) {
                response = new ComponentStateResponse(Framework.getComponentManager().getComponentsState(), null);
            } else {
                response = new ComponentStateResponse(null, new AuthenticationException("Not allowed"));
            }
            sendResponse(response, exchange);
        } catch (Exception ex) {
            logger.error("Error processing request", ex);
        }
    }

}
