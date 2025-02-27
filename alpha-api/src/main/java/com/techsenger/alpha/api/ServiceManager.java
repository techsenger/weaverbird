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

package com.techsenger.alpha.api;

import com.techsenger.alpha.api.net.ClientService;
import com.techsenger.alpha.api.executor.CommandExecutor;
import com.techsenger.alpha.api.repo.RepoService;
import com.techsenger.alpha.api.net.ServerService;
import com.techsenger.alpha.api.net.session.Protocol;
import com.techsenger.alpha.api.net.session.SessionInfo;
import java.util.Collection;
import java.util.Map;

/**
 * A convenient way to get Alpha service providers.
 *
 * @author Pavel Castornii
 */
public interface ServiceManager {

    /**
     * Returns command executor.
     * @return
     */
    CommandExecutor getCommandExecutor();

    /**
     * Returns repo service.
     * @return
     */
    RepoService getRepoService();

    /**
     * Returns service tracker.
     * @return
     */
    ServiceTracker getServiceTracker();

    /**
     * Returns the information about all existing sessions of all clients (if the framework is in client mode) and
     * all servers (if the framework is in server mode).
     *
     * @return
     */
    Collection<SessionInfo> getSessionInfos();

    /**
     * Returns the information about all existing sessions. Use this method only for client sessions because
     * server sessions don't have names.
     *
     * @return
     */
    Map<String, SessionInfo> getSessionInfosByName();

    /**
     * Returns the client for the specified protocol. The method returns null if the framework is not in client mode.
     *
     * @param protocol
     * @return
     */
    ClientService getClient(Protocol protocol);

    /**
     * Returns the server for the specified protocol. The method returns null if the framework is not in server mode.
     *
     * @param protocol
     * @return
     */
    ServerService getServer(Protocol protocol);
}
