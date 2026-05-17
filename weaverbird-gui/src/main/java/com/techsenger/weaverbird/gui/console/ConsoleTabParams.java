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

package com.techsenger.weaverbird.gui.console;

import com.techsenger.tabshell.core.tab.TabParams;
import com.techsenger.weaverbird.core.api.Framework;
import com.techsenger.weaverbird.net.client.api.ClientService;
import com.techsenger.weaverbird.net.client.api.ClientSession;
import java.util.Objects;

/**
 *
 * @author Pavel Castornii
 */
public class ConsoleTabParams extends TabParams {

    private final Framework framework;

    private final ClientService client;

    private final ClientSession session;

    public ConsoleTabParams(Framework framework, ClientService client, ClientSession session) {
        this.framework = framework;
        this.client = client;
        this.session = session;
    }

    public Framework getFramework() {
        return framework;
    }

    public ClientService getClient() {
        return client;
    }

    public ClientSession getSession() {
        return session;
    }

    @Override
    protected void validate() {
        super.validate();
        Objects.requireNonNull(framework);
    }
}
