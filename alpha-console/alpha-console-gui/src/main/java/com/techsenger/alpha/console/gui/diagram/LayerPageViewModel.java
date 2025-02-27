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

package com.techsenger.alpha.console.gui.diagram;

import com.techsenger.tabshell.core.page.AbstractPageViewModel;
import com.techsenger.tabshell.core.page.PageKey;

/**
 *
 * @author Pavel Castornii
 */
class LayerPageViewModel extends AbstractPageViewModel {

    private static final PageKey key = new PageKey("Layer Page");

    private final LayerConfig layerConfig;

    LayerPageViewModel(LayerConfig layerConfig) {
        this.layerConfig = layerConfig;
        setTitle(layerConfig.getName());
    }

    @Override
    public PageKey getKey() {
        return key;
    }

    public LayerConfig getLayerConfig() {
        return layerConfig;
    }

}
