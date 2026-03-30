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

package com.techsenger.alpha.console.gui.diagram;

import com.techsenger.alpha.api.model.ComponentLayerModel;
import com.techsenger.alpha.console.gui.keys.ConsoleComponentKeys;
import com.techsenger.alpha.console.gui.settings.ConsoleSettings;
import com.techsenger.mvvm4fx.core.HistoryPolicy;
import com.techsenger.tabshell.core.dialog.DialogKey;
import com.techsenger.tabshell.core.dialog.DialogScope;
import com.techsenger.tabshell.core.history.HistoryManager;
import com.techsenger.tabshell.kit.dialog.page.AbstractPageDialogViewModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;

/**
 *
 * @author Pavel Castornii
 */
class LayerDialogViewModel extends AbstractPageDialogViewModel {

    private final List<LayerConfig> layerConfigs;

    private final Map<Integer, LayerConfig> previousLayerConfigsById;

    private boolean selectAllListenersEnabled = true;

    private final List<LayerPageViewModel> pages = new ArrayList<>();

    /**
     * Constructor.
     *
     * @param previousLayerConfigs can differ from current components. For example, there can be new components in
     * current configuration or even the same components but with new modules (for example, after component restart).
     */
    LayerDialogViewModel(List<ComponentLayerModel> layerModels, List<LayerConfig> previousLayerConfigs,
            HistoryManager historyManager) {
        super(DialogScope.TAB, true);
        if (previousLayerConfigs != null) {
            this.previousLayerConfigsById = previousLayerConfigs.stream()
                    .collect(Collectors.toMap(c -> c.getLayer().getId(), c -> c));
        } else {
            this.previousLayerConfigsById = null;
        }
        minWidthProperty().set(800);
        minHeightProperty().set(600);
        titleProperty().set("Layer Diagram Configuration");
        setHistoryPolicy(HistoryPolicy.APPEARANCE);
        setHistoryProvider(() -> historyManager.getHistory(LayerDialogHistory.class, LayerDialogHistory::new));
        this.layerConfigs = createLayerConfigs(layerModels);
        for (var c : this.layerConfigs) {
            pages.add(new LayerPageViewModel(c));
        }
    }

    @Override
    public DialogKey getKey() {
        return ConsoleComponentKeys.LAYER_DIALOG;
    }

    String generateUmlCode(ConsoleSettings settings) {
        var generator = new LayerDiagramGenerator(layerConfigs, settings);
        return generator.generate();
    }

    List<LayerConfig> getLayerConfigs() {
        return layerConfigs;
    }

    List<LayerPageViewModel> getPages() {
        return pages;
    }

    void reset() {
        this.selectAllListenersEnabled = false;
        for (var c : this.layerConfigs) {
            c.reset();
        }
        this.selectAllListenersEnabled = true;
    }

    private List<LayerConfig> createLayerConfigs(List<ComponentLayerModel> layers) {
        var result = new ArrayList<LayerConfig>();
        LayerConfig frameworkLayerConfig = null;
        for (var layer : layers) {
            var nonObservableModuleConfigs = new ArrayList<ModuleConfig>();
            var layerConfig = new LayerConfig(layer);
            if (layer.getId() == 0) {
                frameworkLayerConfig = layerConfig;
            } else {
                result.add(layerConfig);
            }
            for (var module: layer.getModulesByName().values()) {
                var moduleConfig = new ModuleConfig(module);
                nonObservableModuleConfigs.add(moduleConfig);
            }
            layerConfig.includedAllProperty().addListener((ov, oldV, newV) -> {
                if (this.selectAllListenersEnabled) {
                    layerConfig.getModules().forEach(m -> m.includedProperty().set(newV));
                }
            });
            layerConfig.readsAllProperty().addListener((ov, oldV, newV) -> {
                if (this.selectAllListenersEnabled) {
                    layerConfig.getModules().forEach(m -> m.readsProperty().set(newV));
                }
            });
            layerConfig.exportsAllProperty().addListener((ov, oldV, newV) -> {
                if (this.selectAllListenersEnabled) {
                    layerConfig.getModules().forEach(m -> m.exportsProperty().set(newV));
                }
            });
            layerConfig.opensAllProperty().addListener((ov, oldV, newV) -> {
                if (this.selectAllListenersEnabled) {
                    layerConfig.getModules().forEach(m -> m.opensProperty().set(newV));
                }
            });
            layerConfig.requiresAllProperty().addListener((ov, oldV, newV) -> {
                if (this.selectAllListenersEnabled) {
                    layerConfig.getModules().forEach(m -> m.requiresProperty().set(newV));
                }
            });
            layerConfig.allowsAllProperty().addListener((ov, oldV, newV) -> {
                if (this.selectAllListenersEnabled) {
                    layerConfig.getModules().forEach(m -> m.allowsProperty().set(newV));
                }
            });
            layerConfig.servicesAllProperty().addListener((ov, oldV, newV) -> {
                if (this.selectAllListenersEnabled) {
                    layerConfig.getModules().forEach(m -> m.servicesProperty().set(newV));
                }
            });
            layerConfig.coloredAllProperty().addListener((ov, oldV, newV) -> {
                if (this.selectAllListenersEnabled) {
                    layerConfig.getModules().forEach(m -> m.coloredProperty().set(newV));
                }
            });
            Collections.sort(nonObservableModuleConfigs, Comparator.comparing(ModuleConfig::getName));
            layerConfig.setModules(FXCollections.observableArrayList(nonObservableModuleConfigs));

            if (this.previousLayerConfigsById != null) {
                var previousComponent = this.previousLayerConfigsById.get(layer.getId());
                layerConfig.copyStateFrom(previousComponent);
            }
        }
        Collections.sort(result, Comparator.comparing(LayerConfig::getName));
        result.add(0, frameworkLayerConfig);
        return result;
    }
}
