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

package com.techsenger.weaverbird.net.server.impl.handlers;

import com.techsenger.weaverbird.core.api.message.MessageArtifactEventListener;
import com.techsenger.toolkit.http.handler.AbstractEndpointHandler;
import com.techsenger.toolkit.http.handler.Endpoint;
import com.techsenger.toolkit.http.response.Response;
import com.techsenger.weaverbird.core.api.message.InMemoryMessagePrinter;
import com.techsenger.weaverbird.net.server.spi.FrameworkRequestContext;
import com.techsenger.weaverbird.net.shared.ComponentUnresolveRequest;
import com.techsenger.weaverbird.net.shared.ComponentUnresolveResponse;
import com.techsenger.weaverbird.net.shared.DefaultComponentConfigDto;
import com.techsenger.weaverbird.net.shared.Endpoints;

/**
 *
 * @author Pavel Castornii
 */
@Endpoint(Endpoints.COMPONENT_UNRESOLVE)
public class ComponentUnresolveHandler extends AbstractEndpointHandler<FrameworkRequestContext,
        ComponentUnresolveRequest> {

    public ComponentUnresolveHandler() {
        super(ComponentUnresolveRequest.class);
    }

    @Override
    public Response handle(FrameworkRequestContext context, ComponentUnresolveRequest request) throws Exception {
        var printer = new InMemoryMessagePrinter();
        var listener = new MessageArtifactEventListener(printer, false);
        var config = context.getFramework().getComponentManager().unresolveComponent(request.getName(),
                request.getVersion(), listener);
        return new ComponentUnresolveResponse(DefaultComponentConfigDto.of(config), printer.getMessages());
    }

}
