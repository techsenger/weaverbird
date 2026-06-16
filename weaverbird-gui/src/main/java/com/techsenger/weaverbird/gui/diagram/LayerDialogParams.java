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

package com.techsenger.weaverbird.gui.diagram;

import com.techsenger.shellfx.core.dialog.DialogParams;
import com.techsenger.shellfx.core.settings.AppearanceSettings;
import com.techsenger.shellfx.core.window.WindowType;
import com.techsenger.weaverbird.core.api.model.ComponentLayerModel;
import java.util.List;

/**
 *
 * @author Pavel Castornii
 */
public class LayerDialogParams extends DialogParams {

    private final List<ComponentLayerModel> layerModels;

    private final List<LayerConfig> previousLayerConfigs;

    /**
     * Constructor.
     *
     * @param layerModels
     * @param previousLayerConfigs can differ from current components. For example, there can be new components in the
     * current configuration or even the same components but with new modules (for example, after component restart).
     */
    public LayerDialogParams(AppearanceSettings settings, List<ComponentLayerModel> layerModels,
            List<LayerConfig> previousLayerConfigs) {
        super(WindowType.NESTED, settings);
        this.layerModels = layerModels;
        this.previousLayerConfigs = previousLayerConfigs;
    }

    public List<ComponentLayerModel> getLayerModels() {
        return layerModels;
    }

    public List<LayerConfig> getPreviousLayerConfigs() {
        return previousLayerConfigs;
    }
}
