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

import com.techsenger.tabshell.core.dialog.AbstractDialogFxView;
import com.techsenger.tabshell.core.page.DefaultPageDescriptor;
import com.techsenger.tabshell.core.page.PageDescriptor;
import com.techsenger.tabshell.core.page.PageItem;
import com.techsenger.tabshell.layout.pagehost.PageHostFxView;
import com.techsenger.tabshell.layout.pagehost.PageHostPort;
import com.techsenger.tabshell.layout.pagehost.PageHostPresenter;
import com.techsenger.tabshell.material.button.ResultButton;
import java.util.List;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 *
 * @author Pavel Castornii
 */
public class LayerDialogFxView<P extends LayerDialogPresenter<?, ?>> extends AbstractDialogFxView<P>
        implements LayerDialogView {

    private final Button resetButton = new Button("Reset");

    private final ResultButton cancelButton = new ResultButton(LayerDialogButtons.CANCEL, "Cancel");

    private final ResultButton okButton = new ResultButton(LayerDialogButtons.OK, "OK");

    protected class Composer extends AbstractDialogFxView<P>.Composer implements LayerDialogComposer {

        private List<LayerConfig> layerConfgs;

        private PageHostFxView<?> pageHost;

        @Override
        public void setLayerConfigs(List<LayerConfig> layers) {
            this.layerConfgs = layers;
        }

        @Override
        public void compose() {
            super.compose();

            pageHost = createPageHost();
            pageHost.getPresenter().initialize();
            pageHost.setDividerPosition(0.275);
            getModifiableChildren().add(pageHost);
            getContentBox().getChildren().add(pageHost.getNode());
            VBox.setVgrow(pageHost.getNode(), Priority.ALWAYS);

            var pages = layerConfgs.stream().map(c ->
                    (PageDescriptor) new DefaultPageDescriptor(c.getName(), (item) -> {
                        var page = createPage(item, c);
                        page.getPresenter().initialize();
                        return page;
                    })).toList();
            pageHost.getComposer().setPages(pages);
            pageHost.getPresenter().selectPage(0);
        }

        @Override
        public PageHostPort getPageHost() {
            return this.pageHost == null ? null : this.pageHost.getPresenter();
        }

        protected PageHostFxView<?> createPageHost() {
            var view = new PageHostFxView<>();
            var presenter = new PageHostPresenter<>(view, () -> null);
            return view;
        }

        protected LayerPageFxView<?> createPage(PageItem item, LayerConfig layer) {
            var view = new LayerPageFxView<>();
            var presenter = new LayerPagePresenter<>(view, item, layer);
            return view;
        }
    }

    public LayerDialogFxView() {
        super();
    }

    @Override
    public void requestFocus() {
//        NodeUtils.requestFocus(getContentPane());
    }

    @Override
    public Composer getComposer() {
        return (Composer) super.getComposer();
    }

    @Override
    protected Composer createComposer() {
        return new LayerDialogFxView<P>.Composer();
    }

    @Override
    protected void build() {
        super.build();
        registerButtons(cancelButton, okButton);
        getLeftButtonBox().getChildren().add(resetButton);
        getContentBox().setPadding(Insets.EMPTY);
    }

    @Override
    protected void addHandlers() {
        super.addHandlers();
        resetButton.setOnAction(e -> getPresenter().onReset());
    }


}
