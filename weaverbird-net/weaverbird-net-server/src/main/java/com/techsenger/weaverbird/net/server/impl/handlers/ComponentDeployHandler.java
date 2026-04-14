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

import com.techsenger.weaverbird.net.server.spi.FrameworkRequestContext;
import com.techsenger.weaverbird.net.shared.ComponentDeployRequest;
import com.techsenger.weaverbird.net.shared.ComponentDeployResponse;
import com.techsenger.weaverbird.net.shared.DefaultComponentDescriptorDto;
import com.techsenger.weaverbird.net.shared.Endpoints;
import com.techsenger.toolkit.http.handler.AbstractEndpointHandler;
import com.techsenger.toolkit.http.handler.Endpoint;
import com.techsenger.toolkit.http.response.Response;

/**
 *
 * @author Pavel Castornii
 */
@Endpoint(Endpoints.COMPONENT_DEPLOY)
public class ComponentDeployHandler extends AbstractEndpointHandler<FrameworkRequestContext, ComponentDeployRequest> {

    public ComponentDeployHandler() {
        super(ComponentDeployRequest.class);
    }

    @Override
    public Response handle(FrameworkRequestContext context, ComponentDeployRequest request) throws Exception {
        var descriptor =  context.getFramework().getComponentManager()
                .deployComponent(request.getName(), request.getVersion(), request.getAlias(),
                request.getParentIds(), request.getParentAliases(), request.isUseParentClassLoader());
        return new ComponentDeployResponse(DefaultComponentDescriptorDto.of(descriptor));
    }
}
