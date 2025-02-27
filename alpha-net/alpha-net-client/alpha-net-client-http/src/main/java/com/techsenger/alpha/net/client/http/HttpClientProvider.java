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

package com.techsenger.alpha.net.client.http;

import com.techsenger.alpha.api.Framework;
import com.techsenger.alpha.api.command.CommandResult;
import com.techsenger.alpha.api.executor.CommandInfos;
import com.techsenger.alpha.api.executor.CommandLine;
import com.techsenger.alpha.api.executor.CommandSkippedException;
import com.techsenger.alpha.api.executor.RemoteCommandException;
import com.techsenger.alpha.api.model.LayersInfo;
import com.techsenger.alpha.api.net.ClientService;
import com.techsenger.alpha.api.net.security.AuthenticationException;
import com.techsenger.alpha.api.net.security.AuthorizationException;
import com.techsenger.alpha.api.net.security.VersionMismatchException;
import com.techsenger.alpha.api.net.session.Protocol;
import com.techsenger.alpha.api.net.session.SessionInfo;
import com.techsenger.alpha.api.net.session.SessionStatus;
import com.techsenger.alpha.api.state.ComponentsState;
import com.techsenger.alpha.net.shared.http.AbstractRequest;
import com.techsenger.alpha.net.shared.http.AbstractResponse;
import com.techsenger.alpha.net.shared.http.CommandExecuteRequest;
import com.techsenger.alpha.net.shared.http.CommandExecuteResponse;
import com.techsenger.alpha.net.shared.http.CommandInfoRequest;
import com.techsenger.alpha.net.shared.http.CommandInfoResponse;
import com.techsenger.alpha.net.shared.http.ComponentStateRequest;
import com.techsenger.alpha.net.shared.http.ComponentStateResponse;
import com.techsenger.alpha.net.shared.http.Constants;
import com.techsenger.alpha.net.shared.http.LayerInfoRequest;
import com.techsenger.alpha.net.shared.http.LayerInfoResponse;
import com.techsenger.alpha.net.shared.http.LoginRequest;
import com.techsenger.alpha.net.shared.http.LoginResponse;
import com.techsenger.alpha.net.shared.http.LogoutRequest;
import com.techsenger.alpha.net.shared.http.LogoutResponse;
import com.techsenger.alpha.net.shared.http.UrlPaths;
import com.techsenger.alpha.net.shared.http.json.GsonProvider;
import com.techsenger.alpha.spi.net.session.AbstractSession;
import com.techsenger.alpha.spi.net.session.Session;
import com.techsenger.alpha.spi.net.session.SessionManager;
import com.techsenger.toolkit.core.SingletonFactory;
import com.techsenger.toolkit.core.ssl.SslUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.concurrent.TimeoutException;
import javax.net.ssl.HttpsURLConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
public class HttpClientProvider implements ClientService {

    private static final Logger logger = LoggerFactory.getLogger(HttpClientProvider.class);

    private static final SingletonFactory<ClientService> singletonFactory =
            new SingletonFactory<>(() -> new HttpClientProvider());

    public static ClientService provider() {
        return singletonFactory.singleton();
    }

    static {

        /*
         * TO solve CertificateException: No name matching localhost found
         *
         * we skip server name checking with the following code:
         */
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
    }

    private static final class HttpSession extends AbstractSession {

    }

    private final SessionManager<HttpSession> sessionManager = new SessionManager();

    @Override
    public Protocol getProtocol() {
        return Protocol.HTTP;
    }

    @Override
    public Collection<SessionInfo> getSessionInfos() {
        return this.sessionManager.getSessionInfos();
    }

    @Override
    public void openSession(String name, String host, int port, boolean secure, String logginName,
            String loginPassword) throws AuthenticationException, VersionMismatchException, AuthorizationException,
            IOException {
        URL url = this.buildUrl(host, port, secure, UrlPaths.LOGIN);
        HttpURLConnection httpConnection = this.buildHttpConnection(url, secure);
        LoginRequest request = new LoginRequest(logginName, loginPassword, Framework.getVersion());
        String jsonRequest = GsonProvider.gson().toJson(request);
        this.writeJson(jsonRequest, httpConnection);
        String jsonResponse = this.readJson(httpConnection);
        LoginResponse response = GsonProvider.gson().fromJson(jsonResponse, LoginResponse.class);
        try {
            throwException(response);
        } catch (RemoteCommandException | CommandSkippedException e) {

        }
        if (response.getSessionUuid() != null) {
            var session = new HttpSession();
            session.setUuid(response.getSessionUuid());
            session.setName(name);
            session.setLoginName(logginName);
            session.setHost(host);
            session.setPort(port);
            session.setSecure(secure);
            session.setProtocol(Protocol.HTTP);
            session.setStatus(SessionStatus.OPEN);
            session.setOpenedAt(LocalDateTime.now());
            sessionManager.addSession(session);
        }
    }

    @Override
    public void closeSession(String sessionName) throws TimeoutException, IOException {
        HttpSession session = null;
        try {
            session = this.sessionManager.acquireSession(sessionName, true);
            this.sessionManager.removeSession(session);
            var request = new LogoutRequest(session.getUuid());
            var response = sendRequest(session, UrlPaths.LOGOUT, request, LogoutResponse.class);
            try {
                throwException(response);
            } catch (AuthorizationException | AuthenticationException | VersionMismatchException
                    | CommandSkippedException | RemoteCommandException ex) {
                //will not happen
            }
        } finally {
            if (session != null) {
                session.setStatus(SessionStatus.CLOSED);
                session.setClosedAt(LocalDateTime.now());
            }
        }
    }

    @Override
    public CommandInfos getCommandInfos(String sessionName) throws TimeoutException, IOException {
        HttpSession session = null;
        try {
            session = this.sessionManager.acquireSession(sessionName, true);
            var request = new CommandInfoRequest(session.getUuid());
            var response = sendRequest(session, UrlPaths.COMMAND_INFO, request, CommandInfoResponse.class);
            return response.getCommandInfos();
        } finally {
            if (session != null) {
                this.sessionManager.releaseSession(session);
            }
        }
    }

    @Override
    public CommandResult sendCommand(String sessionName, CommandLine commandLine, int outputWidth)
            throws AuthorizationException, RemoteCommandException, CommandSkippedException,
            TimeoutException, IOException {
            HttpSession session = null;
        try {
            session = this.sessionManager.acquireSession(sessionName, true);
            var request = new CommandExecuteRequest(session.getUuid(), commandLine, outputWidth);
            var response = sendRequest(session, UrlPaths.COMMAND_EXECUTE, request, CommandExecuteResponse.class);
            try {
                this.throwException(response);
            } catch (VersionMismatchException | AuthenticationException ex) {
                //will not happen
            }
            return response.getCommandResult();
        } finally {
            if (session != null) {
                this.sessionManager.releaseSession(session);
            }
        }
    }

    @Override
    public LayersInfo getLayersInfo(String sessionName) throws TimeoutException, IOException {
        HttpSession session = null;
        try {
            session = this.sessionManager.acquireSession(sessionName, true);
            var request = new LayerInfoRequest(session.getUuid());
            var response = sendRequest(session, UrlPaths.LAYER_INFO, request, LayerInfoResponse.class);
            return response.getLayersInfo();
        } finally {
            if (session != null) {
                this.sessionManager.releaseSession(session);
            }
        }
    }

    @Override
    public ComponentsState getComponentsState(String sessionName) throws TimeoutException, IOException {
        HttpSession session = null;
        try {
            session = this.sessionManager.acquireSession(sessionName, true);
            var request = new ComponentStateRequest(session.getUuid());
            var response = sendRequest(session, UrlPaths.COMPONENT_STATE, request, ComponentStateResponse.class);
            return response.getState();
        } finally {
            if (session != null) {
                this.sessionManager.releaseSession(session);
            }
        }
    }

    private <T extends AbstractResponse> T sendRequest(HttpSession session, String urlPath, AbstractRequest request,
            Class<T> responseClass) throws IOException {
        URL url = this.buildUrl(session, urlPath);
        HttpURLConnection urlConnection = this.buildHttpConnection(url, session.isSecure());
        String jsonRequest = GsonProvider.gson().toJson(request);
        this.writeJson(jsonRequest, urlConnection);
        String jsonResponse = this.readJson(urlConnection);
        T response = GsonProvider.gson().fromJson(jsonResponse, responseClass);
        return response;
    }

    private HttpURLConnection buildHttpConnection(URL url, boolean secure) throws IOException {
        //don't call HttpURLConnection.disconnect()
        //HttpURLConnection or HttpsURLConnection is defined by url.
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);
        connection.setUseCaches(false);
        if (secure) {
            try {
                ((HttpsURLConnection) connection).setSSLSocketFactory(
                        SslUtils.buildSocketFactory(Constants.SSL_CERTIFICATE_ALIAS));
            } catch (Exception ex) {
                logger.error("Error setting SSLSocketFactory", ex);
            }
        }
        return connection;
    }

    private URL buildUrl(Session connection, String urlPath) throws MalformedURLException {
        return buildUrl(connection.getHost(), connection.getPort(), connection.isSecure(), urlPath);
    }

    private URL buildUrl(String host, Integer port, boolean secure, String urlPath) throws MalformedURLException {
        StringBuilder urlBuilder = new StringBuilder();
        if (secure) {
            urlBuilder.append("https://");
        } else {
            urlBuilder.append("http://");
        }
        urlBuilder.append(host);
        urlBuilder.append(":");
        urlBuilder.append(port);
        urlBuilder.append(urlPath);
        String urlString = urlBuilder.toString();
        logger.debug("Built URL: {}", urlString);
        URL url = new URL(urlString);
        return url;
    }

    private void writeJson(String jsonRequest, HttpURLConnection connection) {
        logger.debug("JSON request: {}", jsonRequest);
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonRequest.getBytes("utf-8");
            os.write(input, 0, input.length);
        } catch (Exception ex) {
            logger.error("Error wrting to output stream", ex);
        }
    }

    private String readJson(HttpURLConnection connection) {
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
        } catch (Exception ex) {
            logger.error("Error reading input stream", ex);
            return null;
        }
    }

    private void throwException(AbstractResponse response) throws VersionMismatchException, AuthenticationException,
            AuthorizationException, CommandSkippedException, RemoteCommandException {
        if (response.getExceptionClass() != null) {
            var exceptionClass = response.getExceptionClass();
            if (exceptionClass.equals(VersionMismatchException.class.getName())) {
                throw new VersionMismatchException(response.getExceptionMessage());
            } else if (exceptionClass.equals(AuthenticationException.class.getName())) {
                throw new AuthenticationException(response.getExceptionMessage());
            } else if (exceptionClass.equals(AuthorizationException.class.getName())) {
                throw new AuthorizationException(response.getExceptionMessage());
            } else if (exceptionClass.equals(CommandSkippedException.class.getName())) {
                throw new CommandSkippedException(response.getExceptionMessage());
            } else {
                throw new RemoteCommandException(response.getExceptionMessage());
            }
        }
    }
}
