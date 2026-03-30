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

import com.techsenger.alpha.core.api.component.Component;
import com.techsenger.alpha.core.api.component.ComponentDescriptor;
import com.techsenger.alpha.net.server.spi.FrameworkRequestContext;
import com.techsenger.alpha.net.shared.ComponentDescriptorRequest;
import com.techsenger.alpha.net.shared.ComponentDescriptorResponse;
import com.techsenger.alpha.net.shared.DefaultComponentDescriptorDto;
import com.techsenger.alpha.net.shared.Endpoints;
import com.techsenger.toolkit.http.handler.AbstractEndpointHandler;
import com.techsenger.toolkit.http.handler.Endpoint;
import com.techsenger.toolkit.http.response.Response;

/**
 *
 * @author Pavel Castornii
 */
@Endpoint(Endpoints.COMPONENT_DESCRIPTOR)
public class ComponentDescriptorHandler extends AbstractEndpointHandler<FrameworkRequestContext,
        ComponentDescriptorRequest> {

    public ComponentDescriptorHandler() {
        super(ComponentDescriptorRequest.class);
    }

    @Override
    public Response handle(FrameworkRequestContext context, ComponentDescriptorRequest request) throws Exception {
        Component component;
        ComponentDescriptor descriptor = null;
        if (request.getId() != null) {
            component = context.getFramework().getComponentManager().getComponent(request.getId());
        } else {
            component = context.getFramework().getComponentManager().findComponent(request.getAlias());
        }
        if (component != null) {
            descriptor = component.getDescriptor();
        }
        return new ComponentDescriptorResponse(DefaultComponentDescriptorDto.of(descriptor));
    }

}
