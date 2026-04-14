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

package com.techsenger.weaverbird.executor.api;

import com.techsenger.weaverbird.core.api.Framework;
import com.techsenger.weaverbird.net.client.api.ClientService;
import com.techsenger.weaverbird.net.client.api.ClientSession;
import java.util.Map;

/**
 *
 * @author Pavel Castornii
 */
public interface CommandContext {

    /**
     * Returns the client.
     */
    ClientService getClient();

    /**
     * Returns the active session.
     */
    ClientSession getSession();

    /**
     * Sets the active session.
     */
    void setSession(ClientSession session);

    /**
     * Returns {@code true} if the current command is executed locally and {@code false} otherwise.
     */
    boolean isExecutionLocal();

    /**
     * Returns {@code true} if the current command is executed remotely and {@code false} otherwise.
     */
    boolean isExecutionRemote();

    /**
     * Returns the properties.
     */
    Map<Object, Object> getProperties();

    /**
     * Returns the parameter provider.
     */
    ParameterProvider getParameterProvider();

    /**
     * Returns the framework.
     */
    Framework getFramework();

    /**
     * Returns the command executor.
     */
    CommandExecutor getExecutor();
}
