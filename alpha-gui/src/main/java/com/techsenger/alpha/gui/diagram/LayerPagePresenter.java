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

package com.techsenger.alpha.gui.diagram;

import com.techsenger.alpha.gui.AlphaComponents;
import com.techsenger.patternfx.mvp.Descriptor;
import com.techsenger.tabshell.core.page.AbstractPagePresenter;
import com.techsenger.tabshell.core.page.PageComposer;
import com.techsenger.tabshell.core.page.PageItem;

/**
 *
 * @author Pavel Castornii
 */
public class LayerPagePresenter<V extends LayerPageView, C extends PageComposer>
        extends AbstractPagePresenter<V, C> implements LayerPagePort {

    private final LayerConfig layer;

    public LayerPagePresenter(V view, PageItem item, LayerConfig layer) {
        super(view, item);
        this.layer = layer;
    }

    @Override
    public void reset() {
        getView().deselectAll();
    }

    @Override
    protected Descriptor createDescriptor() {
        return new Descriptor(AlphaComponents.LAYER_PAGE);
    }

    @Override
    protected void postInitialize() {
        super.postInitialize();
        getView().showLayer(layer);
    }
}
