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

package com.techsenger.alpha.api.model;

import com.techsenger.alpha.api.state.ComponentsState;
import com.techsenger.toolkit.core.model.ModuleModel;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Pavel Castornii
 */
public class DefaultModulesInfo implements ModulesInfo {

    private Map<String, List<ModuleModel>> modulesByLayerName;

    private ComponentsState componentsState;

    @Override
    public Map<String, List<ModuleModel>> getModulesByLayerName() {
        return modulesByLayerName;
    }

    public void setModulesByLayerName(Map<String, List<ModuleModel>> modulesByLayerName) {
        this.modulesByLayerName = modulesByLayerName;
    }

    @Override
    public ComponentsState getComponentsState() {
        return componentsState;
    }

    public void setComponentsState(ComponentsState componentsState) {
        this.componentsState = componentsState;
    }
}
