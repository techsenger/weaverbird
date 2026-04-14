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

package com.techsenger.weaverbird.net.client.impl;

import com.google.gson.reflect.TypeToken;
import com.techsenger.weaverbird.net.client.api.ClientService;
import com.techsenger.weaverbird.net.client.api.ClientSession;
import com.techsenger.weaverbird.net.shared.Endpoints;
import com.techsenger.weaverbird.net.shared.json.GsonProvider;
import com.techsenger.toolkit.core.version.Version;
import com.techsenger.toolkit.http.exceptions.AuthenticationException;
import com.techsenger.toolkit.http.exceptions.AuthorizationException;
import com.techsenger.toolkit.http.exceptions.ServerException;
import com.techsenger.toolkit.http.exceptions.VersionMismatchException;
import com.techsenger.toolkit.http.request.LoginRequest;
import com.techsenger.toolkit.http.request.Request;
import com.techsenger.toolkit.http.request.RequestEnvelope;
import com.techsenger.toolkit.http.response.LoginResponse;
import com.techsenger.toolkit.http.response.LogoutResponse;
import com.techsenger.toolkit.http.response.Response;
import com.techsenger.toolkit.http.response.ResponseEnvelope;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
public class HttpClientService implements ClientService {

    private static final Logger logger = LoggerFactory.getLogger(HttpClientService.class);

    private final ClientSessionManager sessionManager = new ClientSessionManager();

    @Override
    public ClientSession openSession(String sessionName, String host, int port, String loginName, String loginPassword,
            Version version) throws VersionMismatchException, AuthenticationException, AuthorizationException,
            IOException, ServerException {
        checkSessionName(sessionName);
        URL url = this.buildUrl(host, port, Endpoints.LOGIN);
        HttpURLConnection httpConnection = this.buildHttpConnection(url);
        LoginRequest request = new LoginRequest(loginName, loginPassword, version);
        var requestEnvelope = new RequestEnvelope<>(null, request);
        String jsonRequest = GsonProvider.gson().toJson(requestEnvelope);
        this.writeJson(jsonRequest, httpConnection);
        String jsonResponse = this.readJson(httpConnection);
        var type = TypeToken.getParameterized(ResponseEnvelope.class, LoginResponse.class).getType();
        ResponseEnvelope<LoginResponse> responseEnvelope = GsonProvider.gson().fromJson(jsonResponse, type);
        throwException(responseEnvelope);
        var response = responseEnvelope.getResponse();

        var sessionInfo = new DefaultClientSession();
        sessionInfo.setUuid(response.getSessionUuid());
        sessionInfo.setName(sessionName);
        sessionInfo.setLoginName(loginName);
        sessionInfo.setHost(host);
        sessionInfo.setPort(port);
        sessionInfo.setOpenedAt(LocalDateTime.now());
        sessionManager.addSession(sessionInfo);
        return sessionInfo;
    }

    @Override
    public void closeSession(String sessionName) throws IllegalArgumentException, AuthenticationException,
            AuthorizationException, TimeoutException, IOException, ServerException {
        checkSessionName(sessionName);
        DefaultClientSession session = null;
        try {
            session = (DefaultClientSession) this.sessionManager.acquireSession(sessionName, true);
            this.sessionManager.removeSession(session);
            var response = sendRequest(session, Endpoints.LOGOUT, null, LogoutResponse.class);
            try {
                throwException(response);
            } catch (VersionMismatchException ex) {
                //will not happen
            }
        } finally {
            if (session != null) {
                session.setClosedAt(LocalDateTime.now());
            }
        }
    }

    @Override
    public Map<String, ClientSession> getSessionsByName() {
        return Collections.unmodifiableMap(this.sessionManager.getSessionsByName());
    }

    @Override
    public <T extends Response> T sendRequest(String sessionName, String endpoint, Request request,
            Class<T> responseClass) throws IllegalArgumentException, AuthenticationException, AuthorizationException,
            TimeoutException, IOException, ServerException {
        checkSessionName(sessionName);
        DefaultClientSession session = null;
        try {
            session = (DefaultClientSession) this.sessionManager.acquireSession(sessionName, true);
            var wrapper = sendRequest(session, endpoint, request, responseClass);
            try {
                throwException(wrapper);
            } catch (VersionMismatchException ex) {
                //will not happen
            }
            return wrapper.getResponse();
        } finally {
            if (session != null) {
                this.sessionManager.releaseSession(session);
            }
        }
    }

    private void checkSessionName(String sessionName) {
        if (sessionName == null) {
            throw new IllegalArgumentException("Session name is not provided");
        }
    }

    private <T extends Response> ResponseEnvelope<T> sendRequest(DefaultClientSession session, String endpoint,
            Request request, Class<T> responseClass) throws IllegalArgumentException, IOException {
        if (endpoint == null) {
            throw new IllegalArgumentException();
        }
        URL url = this.buildUrl(session, endpoint);
        HttpURLConnection urlConnection = this.buildHttpConnection(url);
        var requestEnvelope = new RequestEnvelope<>(session.getUuid(), request);
        String jsonRequest = GsonProvider.gson().toJson(requestEnvelope);
        this.writeJson(jsonRequest, urlConnection);
        String jsonResponse = this.readJson(urlConnection);
        if (responseClass == null) {
            responseClass = (Class<T>) Response.class;
        }
        var type = TypeToken.getParameterized(ResponseEnvelope.class, responseClass).getType();
        ResponseEnvelope<T> responseEnvelope = GsonProvider.gson().fromJson(jsonResponse, type);
        return responseEnvelope;
    }

    private HttpURLConnection buildHttpConnection(URL url) throws IOException {
        //don't call HttpURLConnection.disconnect()
        //HttpURLConnection or HttpsURLConnection is defined by url.
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);
        connection.setUseCaches(false);
        return connection;
    }

    private URL buildUrl(DefaultClientSession session, String endpoint) throws MalformedURLException {
        return buildUrl(session.getHost(), session.getPort(), endpoint);
    }

    private URL buildUrl(String host, Integer port, String endpoint) throws MalformedURLException {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append("http://");
        urlBuilder.append(host);
        urlBuilder.append(":");
        urlBuilder.append(port);
        urlBuilder.append(endpoint);
        String urlString = urlBuilder.toString();
        logger.debug("Built URL: {}", urlString);
        URL url = new URL(urlString);
        return url;
    }

    private void writeJson(String jsonRequest, HttpURLConnection connection) throws IOException {
        logger.debug("JSON request: {}", jsonRequest);
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonRequest.getBytes("utf-8");
            os.write(input, 0, input.length);
        } catch (IOException ex) {
            logger.error("Error writing request to {}", connection.getURL(), ex);
            throw ex;
        }
    }

    private String readJson(HttpURLConnection connection) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(connection.getInputStream(), "utf-8");
                BufferedReader br = new BufferedReader(reader)) {
            StringBuilder responseBuilder = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                responseBuilder.append(responseLine.trim());
            }
            String jsonResponse = responseBuilder.toString();
            logger.debug("JSON response: {}", jsonResponse);
            return jsonResponse;
        } catch (IOException ex) {
            logger.error("Error reading response from {}", connection.getURL(), ex);
            throw ex;
        }
    }

    private void throwException(ResponseEnvelope<?> envelope) throws VersionMismatchException, AuthenticationException,
            AuthorizationException, ServerException {
        if (envelope.getExceptionClass() != null) {
            var exceptionClass = envelope.getExceptionClass();
            if (exceptionClass.equals(VersionMismatchException.class.getName())) {
                throw new VersionMismatchException(envelope.getExceptionMessage());
            } else if (exceptionClass.equals(AuthenticationException.class.getName())) {
                throw new AuthenticationException(envelope.getExceptionMessage());
            } else if (exceptionClass.equals(AuthorizationException.class.getName())) {
                throw new AuthorizationException(envelope.getExceptionMessage());
            } else if (exceptionClass.equals(ServerException.class.getName())) {
                throw new ServerException(envelope.getExceptionMessage());
            } else {
                throw new AssertionError();
            }
        }
    }
}
