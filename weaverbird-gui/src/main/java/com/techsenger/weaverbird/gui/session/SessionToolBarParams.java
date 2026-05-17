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

package com.techsenger.weaverbird.gui.session;

import com.techsenger.tabshell.core.area.AreaParams;
import com.techsenger.weaverbird.net.client.api.ClientService;
import com.techsenger.weaverbird.net.client.api.ClientSession;

/**
 *
 * @author Pavel Castornii
 */
public class SessionToolBarParams extends AreaParams {

    private final ClientService client;

    private final ClientSession session;

    public SessionToolBarParams(ClientService client, ClientSession session) {
        this.client = client;
        this.session = session;
    }

    public ClientService getClient() {
        return client;
    }

    public ClientSession getSession() {
        return session;
    }
}
