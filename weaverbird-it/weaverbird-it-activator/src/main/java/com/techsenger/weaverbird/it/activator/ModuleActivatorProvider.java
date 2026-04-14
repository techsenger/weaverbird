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

package com.techsenger.weaverbird.it.activator;

import com.techsenger.weaverbird.core.spi.module.ModuleActivator;
import com.techsenger.weaverbird.core.spi.module.ModuleContext;
import com.techsenger.weaverbird.it.shared.ActivatorProbe;
import com.techsenger.toolkit.core.jpms.ServiceUtils;

/**
 *
 * @author Pavel Castornii
 */
public class ModuleActivatorProvider implements ModuleActivator {

    @Override
    public void activate(ModuleContext context) throws Exception {
        var provider = ServiceUtils.loadProvider(this.getClass().getModule().getLayer(), true, ActivatorProbe.class);
        provider.get().notifyActivated();
    }

    @Override
    public void deactivate(ModuleContext context) throws Exception {
        var provider = ServiceUtils.loadProvider(this.getClass().getModule().getLayer(), true, ActivatorProbe.class);
        provider.get().notifyDeactivated();
    }
}
