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
import com.sun.net.httpserver.HttpHandler;
import com.techsenger.alpha.api.executor.CommandSkippedException;
import com.techsenger.alpha.api.executor.RemoteCommandException;
import com.techsenger.alpha.api.net.security.AuthenticationException;
import com.techsenger.alpha.api.net.security.AuthorizationException;
import com.techsenger.alpha.api.net.security.VersionMismatchException;
import com.techsenger.alpha.net.shared.http.AbstractRequest;
import com.techsenger.alpha.net.shared.http.AbstractResponse;
import com.techsenger.alpha.net.shared.http.json.GsonProvider;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
public abstract class AbstractHandler implements HttpHandler {

    private static final Logger logger = LoggerFactory.getLogger(AbstractHandler.class);

    private final DefaultHttpServer httpServer;

    public AbstractHandler(DefaultHttpServer httpServer) {
        this.httpServer = httpServer;
    }

    protected boolean isAuthenticated(AbstractRequest request) {
        HttpSession session = httpServer.getSession(request.getSessionUuid());
        if (session != null) {
            //updating last access time
            session.touch();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get request from httpExchange.
     * @param <T>
     * @param httpExchange
     * @param requestType
     * @return
     */
    protected <T extends AbstractRequest> T getRequest(HttpExchange httpExchange, Class<T> requestType) {
        String requestJson = this.readJson(httpExchange);
        //JSON to Object
        T request = GsonProvider.gson().fromJson(requestJson, requestType);
        return request;
    }

    /**
     * Send response to httpExchange.
     * @param <T>
     * @param response
     * @param httpExchange
     */
    protected <T extends AbstractResponse> void sendResponse(T response, HttpExchange httpExchange) {
        //OBJECT to JSON
        String responseJson = GsonProvider.gson().toJson(response);
        // format and return the response to the user
        this.writeJson(responseJson, httpExchange);
    }

    protected DefaultHttpServer getHttpServer() {
        return httpServer;
    }

    /**
     * This is the final point of every server command. It executes function and returns result with exception.
     * Exception is only from limited list.
     * @param <R>
     * @return
     */
    protected <R> CallableResult<R> call(Callable<R> callable) {
        Exception exception = null;
        boolean successful = false;
        R result = null;
        try {
            result = callable.call();
            successful = true;
        } catch (AuthenticationException | AuthorizationException | VersionMismatchException | CommandSkippedException
                | RemoteCommandException  ex) {
            //no logging
            exception = ex;
        } catch (Exception ex) {
            logger.error("Error in the callable", ex);
            //exception is not set to the client.
        }
        var resultAndException = new CallableResult<R>(result, successful, exception);
        return resultAndException;
    }

    private String readJson(HttpExchange httpExchange) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[2048];
            int len;
            while ((len = httpExchange.getRequestBody().read(buffer)) > 0) {
                    bos.write(buffer, 0, len);
            }
            String json = new String(bos.toByteArray(), Charset.forName("UTF-8"));
            logger.debug("JSON request: {}", json);
            return json;
        } catch (IOException ex) {
            logger.error("Error receiving data", ex);
            return null;
        }
    }

    private void writeJson(String json, HttpExchange httpExchange) {
        try {
            // format and return the response to the user
            httpExchange.sendResponseHeaders(200, json.getBytes(Charset.forName("UTF-8")).length);
            httpExchange.getResponseHeaders().set("Content-Type", "application/json, charset=UTF-8");
            OutputStream os = httpExchange.getResponseBody();
            os.write(json.getBytes(Charset.forName("UTF-8")));
            os.close();
            logger.debug("JSON response: {}", json);
        } catch (IOException ex) {
            logger.error("Error sending data", ex);
        }
    }
}
