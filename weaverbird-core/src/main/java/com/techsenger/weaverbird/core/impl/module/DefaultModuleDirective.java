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

package com.techsenger.weaverbird.core.impl.module;

import com.techsenger.weaverbird.core.api.module.DirectiveType;
import com.techsenger.weaverbird.core.api.module.ModuleDirective;

/**
 *
 * @author Pavel Castornii
 */
public class DefaultModuleDirective implements ModuleDirective {

    private final DirectiveType type;

    private final String pkg;

    private final String module;

    private final String layer;

    public DefaultModuleDirective(DirectiveType type, String pkg, String module, String layer) {
        this.type = type;
        this.pkg = pkg;
        this.module = module;
        this.layer = layer;
    }

    @Override
    public DirectiveType getType() {
        return this.type;
    }

    @Override
    public String getPackage() {
        return this.pkg;
    }

    @Override
    public String getModule() {
        return this.module;
    }

    @Override
    public String getLayer() {
        return this.layer;
    }

}
