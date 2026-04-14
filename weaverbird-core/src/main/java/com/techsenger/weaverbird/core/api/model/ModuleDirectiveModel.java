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

package com.techsenger.weaverbird.core.api.model;

import com.techsenger.weaverbird.core.api.module.DirectiveType;
import java.io.Serializable;

/**
 *
 * @author Pavel Castornii
 */
public class ModuleDirectiveModel implements Serializable {

    private DirectiveType type;

    private ComponentModuleModel sourceModule;

    private String packageName;

    private ComponentModuleModel targetModule;

    public DirectiveType getType() {
        return type;
    }

    public void setType(DirectiveType type) {
        this.type = type;
    }

    public ComponentModuleModel getSourceModule() {
        return sourceModule;
    }

    public void setSourceModule(ComponentModuleModel sourceModule) {
        this.sourceModule = sourceModule;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public ComponentModuleModel getTargetModule() {
        return targetModule;
    }

    public void setTargetModule(ComponentModuleModel targetModule) {
        this.targetModule = targetModule;
    }
}
