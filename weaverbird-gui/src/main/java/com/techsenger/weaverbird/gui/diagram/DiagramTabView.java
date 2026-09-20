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

import com.techsenger.shellfx.core.ShellView;
import com.techsenger.shellfx.core.tab.AbstractHostTabView;
import com.techsenger.shellfx.material.style.StyleClasses;
import com.techsenger.weaverbird.net.client.api.ClientService;
import com.techsenger.weaverbird.net.client.api.ClientSession;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * @param <VM> the ViewModel type
 * @author Pavel Castornii
 */
public class DiagramTabView<VM extends DiagramTabViewModel<?>> extends AbstractHostTabView<VM> {

    protected class Composer extends AbstractHostTabView<VM>.Composer implements DiagramTabComposer {

        private final DiagramTabView<VM> view = DiagramTabView.this;

        @Override
        public LayerDialogPort openLayerDialog(LayerDialogParams params) {
            var dialog = createDialog(params);
            addDialog(dialog);
            return dialog.getViewModel();
        }

        @Override
        public void compose() {
            super.compose();
            var toolBar = createToolBar(getViewModel().getClient(), getViewModel().getInitialSession());
            getModifiableChildren().add(toolBar);
            view.getContentBox().getChildren().add(0, toolBar.getNode());
        }

        protected DiagramToolBarView<?> createToolBar(ClientService client, ClientSession session) {
            var params = new DiagramToolBarParams(client, session, getViewModel());
            var viewModel = new DiagramToolBarViewModel<>(params);
            var view = new DiagramToolBarView<>(viewModel);
            view.initialize();
            return view;
        }

        protected LayerDialogView<?> createDialog(LayerDialogParams params) {
            var viewModel = new LayerDialogViewModel<>(params);
            var view = new LayerDialogView<>(viewModel);
            view.initialize();
            return view;
        }
    }

    private final ImageView imageView = new ImageView();

    private final StackPane imageContainer = new StackPane(imageView);

    private final ScrollPane scrollPane = new ScrollPane(imageContainer);

    public DiagramTabView(VM viewModel, ShellView<?> shell) {
        super(viewModel, shell);
    }

    @Override
    public void requestFocus() {
        imageView.requestFocus();
    }

    @Override
    public Composer getComposer() {
        return (Composer) super.getComposer();
    }

    @Override
    protected Composer createComposer() {
        return new DiagramTabView.Composer();
    }

    @Override
    protected void build() {
        super.build();
        imageView.setPickOnBounds(true); //to get scroll events even on transparent image section
        imageView.setPreserveRatio(true);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        scrollPane.getStyleClass().add(StyleClasses.NO_BORDER);
        getContentBox().getChildren().add(scrollPane);
    }

    @Override
    protected void bind() {
        super.bind();
        var viewModel = getViewModel();
        imageContainer.minWidthProperty().bind(scrollPane.viewportBoundsProperty().map(b -> b.getWidth()));
        imageContainer.minHeightProperty().bind(scrollPane.viewportBoundsProperty().map(b -> b.getHeight()));
        imageView.imageProperty().bind(viewModel.diagramProperty());
        imageView.fitWidthProperty().bind(viewModel.diagramWidthProperty());
        imageView.fitHeightProperty().bind(viewModel.diagramHeightProperty());
    }
}
