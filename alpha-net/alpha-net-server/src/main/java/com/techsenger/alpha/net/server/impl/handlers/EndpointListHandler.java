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

package com.techsenger.alpha.net.server.impl.handlers;

import com.techsenger.alpha.net.shared.Endpoints;
import com.techsenger.alpha.net.shared.EndpointListResponse;
import com.techsenger.alpha.net.server.spi.FrameworkRequestContext;
import com.techsenger.toolkit.http.handler.AbstractEndpointHandler;
import com.techsenger.toolkit.http.handler.Endpoint;
import com.techsenger.toolkit.http.request.Request;
import com.techsenger.toolkit.http.response.Response;
import java.util.ArrayList;

/**
 *
 * @author Pavel Castornii
 */
@Endpoint(Endpoints.ENDPOINT_LIST)
public class EndpointListHandler extends AbstractEndpointHandler<FrameworkRequestContext, Request> {

    public EndpointListHandler() {
        super(Request.class);
    }

    @Override
    public Response handle(FrameworkRequestContext context, Request request) throws Exception {
        var endpoints = context.getServer().getDispatcherHandler().getEndpoints();
        return new EndpointListResponse(new ArrayList<>(endpoints));
    }

}
