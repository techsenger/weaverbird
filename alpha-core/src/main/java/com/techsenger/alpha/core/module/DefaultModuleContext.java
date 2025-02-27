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

package com.techsenger.alpha.core.module;

import com.techsenger.alpha.core.component.DefaultComponent;
import com.techsenger.alpha.spi.module.ModuleContext;

/**
 *
 * @author Pavel Castornii
 */
public class DefaultModuleContext implements ModuleContext {

    /**
     * It can be ComponentDescriptor, ExtensionDescriptor etc.
     */
    private final DefaultComponent component;

    public DefaultModuleContext(DefaultComponent component) {
        this.component = component;
    }

    public DefaultComponent getComponent() {
        return component;
    }
}
