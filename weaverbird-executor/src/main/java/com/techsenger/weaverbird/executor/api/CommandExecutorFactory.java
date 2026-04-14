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
import com.techsenger.weaverbird.executor.impl.DefaultCommandExecutor;
import com.techsenger.weaverbird.net.client.api.ClientService;
import com.techsenger.weaverbird.net.client.api.ClientServiceFactory;

/**
 *
 * @author Pavel Castornii
 */
public final class CommandExecutorFactory {

    public static CommandExecutor create(Framework framework) throws Exception {
        return new DefaultCommandExecutor(framework, ClientServiceFactory.create());
    }

    public static CommandExecutor create(Framework framework, ClientService client) throws Exception {
        return new DefaultCommandExecutor(framework, client);
    }

    private CommandExecutorFactory() {
        // empty
    }
}
