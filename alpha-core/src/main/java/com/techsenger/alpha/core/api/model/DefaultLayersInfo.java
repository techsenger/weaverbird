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

package com.techsenger.alpha.core.api.model;

import com.techsenger.alpha.core.api.state.ComponentsState;
import java.util.Map;

/**
 *
 * @author Pavel Castornii
 */
public class DefaultLayersInfo implements LayersInfo {

    private Map<Integer, ComponentLayerModel> layersById;

    private ComponentsState componentsState;

    @Override
    public Map<Integer, ComponentLayerModel> getLayersById() {
        return layersById;
    }

    public void setLayersById(Map<Integer, ComponentLayerModel> layersById) {
        this.layersById = layersById;
    }

    @Override
    public ComponentsState getComponentsState() {
        return componentsState;
    }

    public void setComponentsState(ComponentsState componentsState) {
        this.componentsState = componentsState;
    }

    @Override
    public void resolveReferences() {
        ComponentLayerModel.resolveReferences(layersById);
    }
}
