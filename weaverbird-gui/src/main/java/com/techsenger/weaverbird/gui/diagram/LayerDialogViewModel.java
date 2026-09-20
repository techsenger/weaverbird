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

import com.techsenger.shellfx.core.CloseCheckResult;
import com.techsenger.shellfx.core.ClosePreparationResult;
import com.techsenger.shellfx.core.dialog.AbstractDialogViewModel;
import com.techsenger.weaverbird.core.api.model.ComponentLayerModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @param <C> the composer type
 * @author Pavel Castornii
 */
public class LayerDialogViewModel<C extends LayerDialogComposer> extends AbstractDialogViewModel<C>
        implements LayerDialogPort {

    private final List<LayerConfig> layerConfigs;

    public LayerDialogViewModel(LayerDialogParams params) {
        super(params);
        this.layerConfigs = createLayerConfigs(params.getLayerModels(), params.getPreviousLayerConfigs());
    }

    @Override
    public List<LayerConfig> getLayerConfigs() {
        return this.layerConfigs;
    }

    @Override
    public CloseCheckResult isReadyToClose() {
        return CloseCheckResult.READY;
    }

    @Override
    public void prepareToClose(Consumer<ClosePreparationResult> resultCallback) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void postInitialize() {
        super.postInitialize();
        setWidth(1000);
        setHeight(600);
        setTitle("Layer Diagram Configuration");
        setResizable(true);
        setRightButtons(LayerDialogButtons.CANCEL, LayerDialogButtons.OK);
        setButtonDefault(LayerDialogButtons.OK, true);
    }

    protected void onReset() {
        // some pages can be non initialized, so we update all layers here
        for (var layer : this.layerConfigs) {
            for (var m : layer.getModules()) {
                m.setIncluded(false);
                m.setReads(false);
                m.setExports(false);
                m.setOpens(false);
                m.setRequires(false);
                m.setRequests(false);
                m.setColored(false);
            }
        }
        // and only now update all initialized pages
        for (var p : getComposer().getPageHostPort().getComposerAccess().getPagePorts()) {
            LayerPagePort pagePort = (LayerPagePort) p;
            pagePort.reset();
        }
    }

    private List<LayerConfig> createLayerConfigs(List<ComponentLayerModel> layers,
            List<LayerConfig> previousLayerConfigs) {
        var previousLayerConfigsById = previousLayerConfigs != null
                ? previousLayerConfigs.stream().collect(Collectors.toMap(c -> c.getLayer().getId(), c -> c))
                : null;
        var result = new ArrayList<LayerConfig>();
        LayerConfig frameworkLayerConfig = null;
        for (var layer : layers) {
            LayerConfig layerConfig = null;
            if (previousLayerConfigsById != null) {
                var previousConfig = previousLayerConfigsById.get(layer.getId());
                if (previousConfig != null) {
                    layerConfig = previousConfig;
                }
            }
            if (layerConfig == null) {
                var moduleConfigs = new ArrayList<ModuleConfig>();
                layerConfig = new LayerConfig(layer);
                for (var module: layer.getModulesByName().values()) {
                    var moduleConfig = new ModuleConfig(module);
                    moduleConfigs.add(moduleConfig);
                }
                Collections.sort(moduleConfigs, Comparator.comparing(ModuleConfig::getName));
                layerConfig.setModules(moduleConfigs);
            }
            if (layer.getId() == 0) {
                frameworkLayerConfig = layerConfig;
            } else {
                result.add(layerConfig);
            }
        }
        Collections.sort(result, Comparator.comparing(LayerConfig::getName));
        result.add(0, frameworkLayerConfig);
        return result;
    }
}
