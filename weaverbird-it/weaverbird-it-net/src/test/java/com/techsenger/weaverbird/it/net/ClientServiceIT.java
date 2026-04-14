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

package com.techsenger.weaverbird.it.net;

import com.techsenger.weaverbird.core.api.Framework;
import com.techsenger.weaverbird.core.api.FrameworkFactory;
import com.techsenger.weaverbird.core.api.FrameworkSettings;
import com.techsenger.weaverbird.it.shared.ServerSettings;
import com.techsenger.weaverbird.net.client.api.ClientService;
import com.techsenger.weaverbird.net.client.api.ClientServiceFactory;
import com.techsenger.weaverbird.net.server.api.ServerService;
import com.techsenger.weaverbird.net.server.api.ServerServiceFactory;
import com.techsenger.weaverbird.net.shared.Endpoints;
import com.techsenger.toolkit.core.version.Version;
import com.techsenger.toolkit.http.exceptions.AuthenticationException;
import com.techsenger.toolkit.http.exceptions.ServerException;
import com.techsenger.toolkit.http.exceptions.VersionMismatchException;
import com.techsenger.toolkit.http.request.LoginRequest;
import com.techsenger.toolkit.http.response.LoginResponse;
import java.io.IOException;
import java.nio.file.Paths;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Because of this bug - https://bugs.openjdk.org/browse/JDK-8322302 it is not possible to run tests with
 * deployed components. So, in this configuration both client and server modules are located in the boot layer.
 *
 * @author Pavel Castornii
 */
public class ClientServiceIT {

    private static final Logger logger = LoggerFactory.getLogger(ClientServiceIT.class);

    private static final String HOST = "127.0.0.1";

    private static final int PORT = 1200;

    private static final String VALID_LOGIN = ServerSettings.LOGIN;

    private static final String VALID_PASSWORD = ServerSettings.PASSWORD;

    private static final String WRONG_PASSWORD = "wrong_password";

    private static final String WRONG_LOGIN = "wrong_login";

    private static final Version VALID_VERSION = new Version(1, 0, 0, true);

    private static final Version WRONG_VERSION = new Version(0, 0, 0, true);

    private static Framework framework;

    private static ServerService server;

    private static ClientService client;

    @BeforeAll
    public static void startFramework() throws Exception {
        var fwPath = Paths.get(System.getProperty("basedir"), "target", "framework");
        var settings = FrameworkSettings.builder()
                .repoChecksumEnabled(false)
                .application(app -> app
                    .name("client-test")
                    .version(Version.of("1.0.0")))
                .build();
        framework = FrameworkFactory.create(settings, fwPath);
        server = ServerServiceFactory.create(framework);
        server.start(HOST, PORT);
        client = ClientServiceFactory.create();
    }

    @AfterAll
    public static void stopFramework() throws Exception {
        server.stop();
        framework.shutdown();
    }

    // -------------------------------------------------------------------------
    // openSession
    // -------------------------------------------------------------------------

    @Test
    public void openSession_validCredentials_sessionOpenedSuccessfully() throws Exception {
        String sessionName = "session-open-valid";
        client.openSession(sessionName, HOST, PORT, VALID_LOGIN, VALID_PASSWORD, VALID_VERSION);
        client.closeSession(sessionName);
    }

    @Test
    public void openSession_wrongPassword_throwsAuthenticationException() {
        assertThrows(AuthenticationException.class, () ->
                client.openSession("session-open-wrong-pass", HOST, PORT, VALID_LOGIN, WRONG_PASSWORD, VALID_VERSION));
    }

    @Test
    public void openSession_wrongLogin_throwsAuthenticationException() {
        assertThrows(AuthenticationException.class, () ->
                client.openSession("session-open-wrong-login", HOST, PORT, WRONG_LOGIN, VALID_PASSWORD, VALID_VERSION));
    }

    @Test
    public void openSession_incompatibleVersion_throwsVersionMismatchException() {
        assertThrows(VersionMismatchException.class, () -> client.openSession("session-open-wrong-version", HOST, PORT,
                VALID_LOGIN, VALID_PASSWORD, WRONG_VERSION));
    }

    @Test
    public void openSession_nullSessionName_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                client.openSession(null, HOST, PORT, VALID_LOGIN, VALID_PASSWORD, VALID_VERSION));
    }

    @Test
    public void openSession_wrongPort_throwsIOException() {
        assertThrows(IOException.class, () ->
                client.openSession("session-open-wrong-port", HOST, 9999, VALID_LOGIN, VALID_PASSWORD, VALID_VERSION));
    }

    // -------------------------------------------------------------------------
    // closeSession
    // -------------------------------------------------------------------------

    @Test
    public void closeSession_existingSession_closedSuccessfully() throws Exception {
        String sessionName = "session-close-valid";
        client.openSession(sessionName, HOST, PORT, VALID_LOGIN, VALID_PASSWORD, VALID_VERSION);
        client.closeSession(sessionName);
    }

    @Test
    public void closeSession_nonExistingSession_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                client.closeSession("session-close-nonexistent"));
    }

    @Test
    public void closeSession_nullSessionName_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                client.closeSession(null));
    }


    @Test
    public void closeSession_alreadyClosedSession_throwsIllegalArgumentException() throws Exception {
        String sessionName = "session-close-twice";
        client.openSession(sessionName, HOST, PORT, VALID_LOGIN, VALID_PASSWORD, VALID_VERSION);
        client.closeSession(sessionName);
        assertThrows(IllegalArgumentException.class, () ->
                client.closeSession(sessionName));
    }

    // -------------------------------------------------------------------------
    // sendRequest
    // -------------------------------------------------------------------------

    @Test
    public void sendRequest_validLoginRequest_returnsResponseWithSessionUuid() throws Exception {
        String sessionName = "session-send-login";
        client.openSession(sessionName, HOST, PORT, VALID_LOGIN, VALID_PASSWORD, VALID_VERSION);
        try {
            LoginResponse response = client.sendRequest(
                    sessionName,
                    Endpoints.LOGIN,
                    new LoginRequest(VALID_LOGIN, VALID_PASSWORD, VALID_VERSION),
                    LoginResponse.class);
            assertThat(response).isNotNull();
            assertThat(response.getSessionUuid()).isNotNull().isNotEmpty();
        } finally {
            client.closeSession(sessionName);
        }
    }

    @Test
    public void sendRequest_nonExistingSession_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                client.sendRequest(
                        "session-send-nonexistent",
                        Endpoints.LOGIN,
                        new LoginRequest(VALID_LOGIN, VALID_PASSWORD, VALID_VERSION),
                        LoginResponse.class));
    }

    @Test
    public void sendRequest_nullSessionName_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                client.sendRequest(
                        null,
                        Endpoints.LOGIN,
                        new LoginRequest(VALID_LOGIN, VALID_PASSWORD, VALID_VERSION),
                        LoginResponse.class));
    }

    @Test
    public void sendRequest_emptySessionName_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                client.sendRequest(
                        "",
                        Endpoints.LOGIN,
                        new LoginRequest(VALID_LOGIN, VALID_PASSWORD, VALID_VERSION),
                        LoginResponse.class));
    }

    @Test
    public void sendRequest_nullEndpoint_throwsIllegalArgumentException() throws Exception {
        String sessionName = "session-send-null-endpoint";
        client.openSession(sessionName, HOST, PORT, VALID_LOGIN, VALID_PASSWORD, VALID_VERSION);
        try {
            assertThrows(IllegalArgumentException.class, () ->
                    client.sendRequest(
                            sessionName,
                            null,
                            new LoginRequest(VALID_LOGIN, VALID_PASSWORD, VALID_VERSION),
                            LoginResponse.class));
        } finally {
            client.closeSession(sessionName);
        }
    }

    @Test
    public void sendRequest_unknownEndpoint_throwsServerException() throws Exception {
        String sessionName = "session-send-unknown-endpoint";
        client.openSession(sessionName, HOST, PORT, VALID_LOGIN, VALID_PASSWORD, VALID_VERSION);
        try {
            assertThrows(ServerException.class, () ->
                    client.sendRequest(
                            sessionName,
                            "/rest/nonexistent/endpoint",
                            new LoginRequest(VALID_LOGIN, VALID_PASSWORD, VALID_VERSION),
                            LoginResponse.class));
        } finally {
            client.closeSession(sessionName);
        }
    }
}
