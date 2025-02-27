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

package com.techsenger.alpha.api.net;

import com.techsenger.alpha.api.command.CommandResult;
import com.techsenger.alpha.api.executor.CommandInfos;
import com.techsenger.alpha.api.executor.CommandLine;
import com.techsenger.alpha.api.executor.CommandSkippedException;
import com.techsenger.alpha.api.executor.RemoteCommandException;
import com.techsenger.alpha.api.model.LayersInfo;
import com.techsenger.alpha.api.net.security.AuthenticationException;
import com.techsenger.alpha.api.net.security.AuthorizationException;
import com.techsenger.alpha.api.net.security.VersionMismatchException;
import com.techsenger.alpha.api.net.session.SessionService;
import com.techsenger.alpha.api.state.ComponentsState;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author Pavel Castornii
 */
public interface ClientService extends SessionService {

    /**
     * Opens a new session.
     *
     * @param sessionName
     * @param host
     * @param port
     * @param secure
     * @param logginName
     * @param loginPassword
     * @throws AuthenticationException
     * @throws VersionMismatchException
     * @throws AuthorizationException
     * @throws IOException
     */
    void openSession(String sessionName, String host, int port, boolean secure, String logginName,
            String loginPassword) throws VersionMismatchException, AuthenticationException, AuthorizationException,
            IOException;

    /**
     * Closes the existing session by the specified name.
     *
     * @param sessionName the name of the session.
     * @throws TimeoutException
     * @throws IOException
     */
    void closeSession(String sessionName) throws TimeoutException, IOException;

    /**
     * Returns the commands that the remote server supports.
     *
     * @param sessionName the name of the session
     * @return
     * @throws TimeoutException
     * @throws IOException
     */
    CommandInfos getCommandInfos(String sessionName) throws TimeoutException, IOException;

    /**
     * Sends remote command to server.
     *
     * @param sessionName
     * @param commandLine
     * @param outputWidth
     * @return
     * @throws AuthorizationException
     * @throws RemoteCommandException
     * @throws CommandSkippedException
     * @throws TimeoutException
     * @throws IOException
     */
    CommandResult sendCommand(String sessionName, CommandLine commandLine, int outputWidth)
            throws AuthorizationException, RemoteCommandException, CommandSkippedException, TimeoutException,
            IOException;

    /**
     * Returns the information about layers.
     *
     * @param sessionName
     * @return
     * @throws TimeoutException
     * @throws IOException
     */
    LayersInfo getLayersInfo(String sessionName) throws TimeoutException, IOException;

    /**
     * Returns the components state.
     *
     * @param sessionName
     * @return
     * @throws TimeoutException
     * @throws IOException
     */
    ComponentsState getComponentsState(String sessionName) throws TimeoutException, IOException;
}
