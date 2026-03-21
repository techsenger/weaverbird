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
public interface LayersInfo {

    /**
     * Returns all layers. 0 for framework layer. Call {@link #resolveReferences() } before using them.
     *
     * @return
     */
    Map<Integer, ComponentLayerModel> getLayersById();

    /**
     * The state of the components when layers info was created.
     */
    ComponentsState getComponentsState();

    /**
     * Resolves the references of the models. This method should be called only once after getting the info.
     */
    void resolveReferences();
}
