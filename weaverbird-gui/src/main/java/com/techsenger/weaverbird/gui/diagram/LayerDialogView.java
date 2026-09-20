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

import com.techsenger.shellfx.core.dialog.AbstractDialogView;
import com.techsenger.shellfx.core.page.DefaultPageDescriptor;
import com.techsenger.shellfx.core.page.PageDescriptor;
import com.techsenger.shellfx.core.page.PageItem;
import com.techsenger.shellfx.core.window.AbstractWindowView;
import com.techsenger.shellfx.layout.pagehost.PageHostParams;
import com.techsenger.shellfx.layout.pagehost.PageHostPort;
import com.techsenger.shellfx.layout.pagehost.PageHostView;
import com.techsenger.shellfx.layout.pagehost.PageHostViewModel;
import com.techsenger.shellfx.material.button.ResultButton;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * @param <VM> the ViewModel type
 * @author Pavel Castornii
 */
public class LayerDialogView<VM extends LayerDialogViewModel<?>> extends AbstractDialogView<VM> {

    protected class Composer extends AbstractWindowView<VM>.Composer implements LayerDialogComposer {

        private PageHostView<?> pageHost;

        @Override
        public void compose() {
            super.compose();

            pageHost = createPageHost();
            pageHost.getViewModel().setDividerPosition(0.275);
            getModifiableChildren().add(pageHost);
            getContentBox().getChildren().add(pageHost.getNode());
            VBox.setVgrow(pageHost.getNode(), Priority.ALWAYS);

            var pages = getViewModel().getLayerConfigs().stream().map(c ->
                    (PageDescriptor) new DefaultPageDescriptor(c.getName(), (item) -> createPage(item, c))).toList();
            pageHost.getComposer().setPages(pages);
            pageHost.getViewModel().selectPage(0);
        }

        @Override
        public PageHostPort getPageHostPort() {
            return this.pageHost == null ? null : this.pageHost.getViewModel();
        }

        protected PageHostView<?> createPageHost() {
            var params = new PageHostParams(null);
            var viewModel = new PageHostViewModel<>(params);
            var view = new PageHostView<>(viewModel);
            view.initialize();
            return view;
        }

        protected LayerPageView<?> createPage(PageItem item, LayerConfig layer) {
            var params = new LayerPageParams(item, layer);
            var viewModel = new LayerPageViewModel<>(params);
            var view = new LayerPageView<>(viewModel);
            view.initialize();
            return view;
        }
    }

    private final Button resetButton = new Button("Reset");

    private final ResultButton cancelButton = new ResultButton(LayerDialogButtons.CANCEL, "Cancel");

    private final ResultButton okButton = new ResultButton(LayerDialogButtons.OK, "OK");

    public LayerDialogView(VM viewModel) {
        super(viewModel);
    }

    @Override
    public void requestFocus() {
        // the layer dialog itself never takes focus
    }

    @Override
    public Composer getComposer() {
        return (Composer) super.getComposer();
    }

    @Override
    protected Composer createComposer() {
        return new LayerDialogView<VM>.Composer();
    }

    @Override
    protected void build() {
        super.build();
        registerButtons(cancelButton, okButton);
        getButtonWidthGroup().add(cancelButton, okButton);
        getRightBottomBox().getChildren().addAll(resetButton);
        getContentBox().setPadding(Insets.EMPTY);
    }

    @Override
    protected void addHandlers() {
        super.addHandlers();
        resetButton.setOnAction(e -> getViewModel().onReset());
    }
}
