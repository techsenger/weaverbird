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

package com.techsenger.alpha.console.gui.window;

import static com.techsenger.alpha.console.gui.window.AbstractWindowViewModel.HEIGHT;
import static com.techsenger.alpha.console.gui.window.AbstractWindowViewModel.WIDTH;
import com.techsenger.tabshell.core.pane.AbstractPaneView;
import com.techsenger.tabshell.kit.material.textarea.ExtendedTextArea;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Modal box over text area.
 *
 * @author Pavel Castornii
 */
public abstract class AbstractWindowView<T extends AbstractWindowViewModel> extends AbstractPaneView<T> {

    private final VBox root  = new VBox();

    private final ExtendedTextArea mainTextArea;

    public AbstractWindowView(T viewModel, ExtendedTextArea mainTextArea) {
        super(viewModel);
        this.mainTextArea = mainTextArea;
    }

    /**
     * Returns node, that must be focused after adding window to stage.
     * @return
     */
    public abstract Node getFocusNode();

    @Override
    public VBox getNode() {
        return this.root;
    }

    @Override
    protected void build(T viewModel) {
        super.build(viewModel);
        StackPane.setAlignment(root, Pos.TOP_LEFT);
        root.setMaxSize(WIDTH, HEIGHT);
        root.setMinSize(WIDTH, HEIGHT);
        root.getStyleClass().add("window-box");
        var bounds = mainTextArea.getCaretBounds().get();
        bounds = mainTextArea.screenToLocal(bounds);
        viewModel.resolveWindowPositions(bounds, mainTextArea.getWidth(), mainTextArea.getHeight());
    }
}
