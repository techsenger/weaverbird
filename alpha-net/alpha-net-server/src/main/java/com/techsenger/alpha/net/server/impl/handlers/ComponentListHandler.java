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

import com.techsenger.alpha.net.server.spi.FrameworkRequestContext;
import com.techsenger.alpha.net.shared.ComponentListRequest;
import com.techsenger.alpha.net.shared.ComponentListResponse;
import static com.techsenger.alpha.net.shared.ComponentState.ACTIVATED;
import com.techsenger.alpha.net.shared.DefaultComponentConfigDto;
import com.techsenger.alpha.net.shared.DefaultComponentDescriptorDto;
import com.techsenger.alpha.net.shared.Endpoints;
import com.techsenger.toolkit.http.handler.AbstractEndpointHandler;
import com.techsenger.toolkit.http.handler.Endpoint;
import com.techsenger.toolkit.http.response.Response;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Pavel Castornii
 */
@Endpoint(Endpoints.COMPONENT_LIST)
public class ComponentListHandler extends AbstractEndpointHandler<FrameworkRequestContext, ComponentListRequest> {

    public ComponentListHandler() {
        super(ComponentListRequest.class);
    }

    @Override
    public Response handle(FrameworkRequestContext context, ComponentListRequest request) throws Exception {
        List<DefaultComponentConfigDto> componentConfigs = null;
        List<DefaultComponentDescriptorDto> componentDescriptors = null;
        switch (request.getComponentState()) {
            case ADDED -> {
                componentConfigs = new ArrayList<>();
                var framework = context.getFramework();
                for (var entry : framework.getRegistry().getAddedComponents()) {
                    var config = framework.getComponentManager().readConfig(entry.getName(), entry.getVersion());
                    componentConfigs.add(DefaultComponentConfigDto.of(config));
                }
            }
            case RESOLVED -> {
                componentConfigs = new ArrayList<>();
                var framework = context.getFramework();
                for (var entry : framework.getRegistry().getResolvedComponents()) {
                    var config = framework.getComponentManager().readConfig(entry.getName(), entry.getVersion());
                    componentConfigs.add(DefaultComponentConfigDto.of(config));
                }
            }
            case DEPLOYED -> {
                componentDescriptors = context.getFramework().getComponentManager().getDescriptors().stream()
                        .map(d -> DefaultComponentDescriptorDto.of(d)).toList();
            }
            case ACTIVATED -> {
                componentDescriptors = context.getFramework().getComponentManager().getDescriptors().stream()
                        .filter(d -> d.isActivated())
                        .map(d -> DefaultComponentDescriptorDto.of(d)).toList();
            }
            default -> throw new AssertionError();
        }
        return new ComponentListResponse(componentConfigs, componentDescriptors);
    }

}
