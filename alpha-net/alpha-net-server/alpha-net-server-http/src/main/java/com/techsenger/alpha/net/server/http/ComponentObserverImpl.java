/*
 * Copyright 2018-2025 Pavel Castornii.
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

package com.techsenger.alpha.net.server.http;

import com.techsenger.alpha.api.component.AbstractComponentObserver;
import com.techsenger.alpha.api.component.Component;
import com.techsenger.alpha.spi.net.security.SecurityContextService;
import com.techsenger.toolkit.core.jpms.ServiceUtils;
import java.util.Optional;

/**
 *
 * @author Pavel Castornii
 */
class ComponentObserverImpl extends AbstractComponentObserver {

    /**
     * We can't get layer in which service instance was created, only layer, to which service class belongs to.
     */
    private ModuleLayer securityContextServiceLayer;

    @Override
    public void onDeployed(Component component) {
        Optional<SecurityContextService> contextService =
                ServiceUtils.loadProvider(component.getLayer(), false, SecurityContextService.class);
        if (contextService.isPresent()) {
            SecurityContextServiceHolder.setInstance(contextService.get());
            securityContextServiceLayer = component.getLayer();
        }
    }

    @Override
    public void onUndeployed(Component component) {
        if (securityContextServiceLayer == component.getLayer()) {
            SecurityContextServiceHolder.setInstance(null);
            securityContextServiceLayer = null;
        }
    }
}
