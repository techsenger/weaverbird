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

package com.techsenger.alpha.net.client.api;

import com.techsenger.toolkit.http.exceptions.AuthenticationException;
import com.techsenger.toolkit.http.exceptions.AuthorizationException;
import com.techsenger.toolkit.http.exceptions.ServerException;
import com.techsenger.toolkit.http.request.Request;
import com.techsenger.toolkit.http.response.Response;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author Pavel Castornii
 */
public abstract class AbstractDomainClient {

    private final ClientService client;

    private final ClientSession session;

    public AbstractDomainClient(ClientService client, ClientSession sessionInfo) {
        this.client = client;
        this.session = sessionInfo;
    }

    protected ClientSession getSession() {
        return session;
    }

    protected ClientService getClient() {
        return client;
    }

    protected <T extends Response> T sendRequest(String endpoint, Request request, Class<T> responseClass)
            throws ClientException, ServerException {
        try {
            return this.client.sendRequest(this.session.getName(), endpoint, request, responseClass);
        } catch (IllegalArgumentException | AuthenticationException | AuthorizationException
                | TimeoutException | IOException ex) {
            throw new ClientException(ex);
        }
    }
}
