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
import com.techsenger.weaverbird.core.api.module.ResolvedModuleDirective;

/**
 *
 * @author Pavel Castornii
 */
public class DefaultResolvedModuleDirective implements ResolvedModuleDirective {

    private final DirectiveType type;

    private final Module sourceModule;

    private final Module targetModule;

    private final String packageName;

    public DefaultResolvedModuleDirective(DirectiveType type, Module sourceModule, String packageName,
            Module targetModule) {
        this.type = type;
        this.sourceModule = sourceModule;
        this.packageName = packageName;
        this.targetModule = targetModule;
    }

    @Override
    public DirectiveType getType() {
        return type;
    }

    @Override
    public Module getSourceModule() {
        return sourceModule;
    }

    @Override
    public Module getTargetModule() {
        return targetModule;
    }

    @Override
    public String getPackage() {
        return packageName;
    }
}
