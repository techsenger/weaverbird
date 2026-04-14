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

package com.techsenger.weaverbird.net.client.api;

import com.techsenger.toolkit.core.version.Version;
import com.techsenger.toolkit.http.exceptions.AuthenticationException;
import com.techsenger.toolkit.http.exceptions.AuthorizationException;
import com.techsenger.toolkit.http.exceptions.ServerException;
import com.techsenger.toolkit.http.exceptions.VersionMismatchException;
import com.techsenger.toolkit.http.request.Request;
import com.techsenger.toolkit.http.response.Response;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author Pavel Castornii
 */
public interface ClientService {

    /**
     * Opens a new session and establishes a connection to the remote server.
     *
     * <p>The session is identified by {@code sessionName} and can be used for subsequent interactions with
     * the server. Authentication and version compatibility checks are performed during this operation.
     *
     * @param sessionName    the unique name of the session; must not be {@code null} or empty
     * @param host           the server host name or IP address
     * @param port           the server port
     * @param loginName      the user login name
     * @param loginPassword  the user password
     * @param version        the version of the framework
     *
     * @throws IllegalArgumentException   if any argument is invalid
     * @throws VersionMismatchException   if the client and server versions are incompatible
     * @throws AuthenticationException    if authentication fails
     * @throws AuthorizationException     if the user is not authorized to open a session
     * @throws IOException                if a network or I/O error occurs
     * @throws ServerException if the server returns an unexpected error or fails to process the request
     */
    ClientSession openSession(String sessionName, String host, int port, String loginName, String loginPassword,
            Version version) throws VersionMismatchException, AuthenticationException, AuthorizationException,
            IOException, ServerException;

    /**
     * Closes an existing session identified by the given name.
     *
     * <p>After the session is closed, it can no longer be used for communication with the server.
     *
     * @param sessionName the name of the session to close; must not be {@code null} or empty
     *
     * @throws IllegalArgumentException if the session name is invalid or the session does not exist
     * @throws AuthenticationException    if authentication fails
     * @throws AuthorizationException     if the user is not authorized to open a session
     * @throws TimeoutException        if the server does not respond within the expected time
     * @throws IOException             if a network or I/O error occurs
     * @throws ServerException if the server returns an unexpected error or fails to process the request
     */
    void closeSession(String sessionName) throws IllegalArgumentException, AuthenticationException,
            AuthorizationException, TimeoutException, IOException, ServerException;

    /**
     * Returns the information about all existing sessions in unmodifiable map.
     */
    Map<String, ClientSession> getSessionsByName();

    /**
     * Sends a request to a specific endpoint on the server within the specified session and returns the response.
     *
     * <p>The {@code endpoint} parameter specifies the URL on the server to which the request will be dispatched.
     * The server must support this endpoint; otherwise the behavior is implementation-dependent.
     *
     * @param sessionName the name or identifier of the session used to communicate with the server; must not be
     * {@code null} or empty
     * @param endpoint the server endpoint URL to send the request to; must not be {@code null} or empty
     * @param request the request object to send; can be {@code null}
     * @return the response returned by the server; can be {@code null}
     *
     * @throws IllegalArgumentException if {@code sessionName}, {@code endpoint}, or {@code request} is {@code null},
     * empty, or invalid
     * @throws AuthenticationException    if authentication fails
     * @throws AuthorizationException     if the user is not authorized to open a session
     * @throws TimeoutException if the server does not respond within the expected time
     * @throws IOException if a network or I/O error occurs during communication
     * @throws ServerException if the server returns an unexpected error or fails to process the request
     */
    <T extends Response> T sendRequest(String sessionName, String endpoint, Request request,
            Class<T> responseClass) throws IllegalArgumentException, AuthenticationException, AuthorizationException,
            TimeoutException, IOException, ServerException;

}
