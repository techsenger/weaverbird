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

import com.techsenger.patternfx.mvvm.ChildComposer;
import com.techsenger.shellfx.core.page.AbstractPageViewModel;
import java.util.List;

/**
 * @param <C> the composer type
 * @author Pavel Castornii
 */
public class LayerPageViewModel<C extends ChildComposer> extends AbstractPageViewModel<C> implements LayerPagePort {

    private final LayerConfig layer;

    private final List<ModuleConfigAdapter> moduleAdapters;

    public LayerPageViewModel(LayerPageParams params) {
        super(params);
        this.layer = params.getLayer();
        this.moduleAdapters = layer.getModules().stream().map(ModuleConfigAdapter::new).toList();
    }

    public LayerConfig getLayer() {
        return layer;
    }

    public List<ModuleConfigAdapter> getModuleAdapters() {
        return moduleAdapters;
    }

    @Override
    public void reset() {
        for (var adapter : moduleAdapters) {
            adapter.setIncluded(false);
            adapter.setReads(false);
            adapter.setExports(false);
            adapter.setOpens(false);
            adapter.setRequires(false);
            adapter.setRequests(false);
            adapter.setColored(false);
        }
    }
}
