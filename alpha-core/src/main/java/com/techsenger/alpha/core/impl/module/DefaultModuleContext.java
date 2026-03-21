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

package com.techsenger.alpha.core.impl.module;

import com.techsenger.alpha.core.api.Framework;
import com.techsenger.alpha.core.impl.component.DefaultComponent;
import com.techsenger.alpha.core.spi.module.ModuleContext;

/**
 *
 * @author Pavel Castornii
 */
public class DefaultModuleContext implements ModuleContext {

    private final Framework framework;

    /**
     * It can be ComponentDescriptor, ExtensionDescriptor etc.
     */
    private final DefaultComponent component;

    public DefaultModuleContext(Framework framework, DefaultComponent component) {
        this.framework = framework;
        this.component = component;
    }

    @Override
    public Framework getFramework() {
        return framework;
    }

    @Override
    public DefaultComponent getComponent() {
        return component;
    }
}
