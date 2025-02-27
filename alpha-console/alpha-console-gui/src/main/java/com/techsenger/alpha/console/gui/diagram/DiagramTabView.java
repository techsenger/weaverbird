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

import atlantafx.base.theme.Styles;
import com.techsenger.alpha.console.gui.session.SessionsToolBarView;
import com.techsenger.alpha.console.gui.style.ConsoleIcons;
import com.techsenger.mvvm4fx.core.ComponentHelper;
import com.techsenger.tabshell.core.TabShellView;
import com.techsenger.tabshell.kit.core.style.StyleClasses;
import com.techsenger.tabshell.kit.core.workertab.AbstractWorkerTabView;
import com.techsenger.tabshell.material.icon.FontIconView;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 *
 * @author Pavel Castornii
 */
public class DiagramTabView extends AbstractWorkerTabView<DiagramTabViewModel> {

    private final Button layerDiagramButton = new Button(null, new FontIconView(ConsoleIcons.LAYER_DIAGRAMS));

    private final ComboBox<String> zoomLevelComboBox = new ComboBox<>();

    private final Button zoomInButton = new Button(null, new FontIconView(ConsoleIcons.ADD));

    private final Button zoomOutButton = new Button(null, new FontIconView(ConsoleIcons.REMOVE));

    private final ToolBar toolBar = new ToolBar(layerDiagramButton, new Separator(Orientation.VERTICAL),
            zoomOutButton, zoomLevelComboBox, zoomInButton);

    private final ImageView imageView = new ImageView();

    private final ScrollPane scrollPane = new ScrollPane(imageView);

    private final SessionsToolBarView sessions;

    public DiagramTabView(TabShellView<?> tabShell, DiagramTabViewModel viewModel) {
        super(tabShell, viewModel);
        if (viewModel.getSessions() != null) {
            sessions = new SessionsToolBarView(viewModel.getSessions());
            sessions.initialize();
        } else {
            sessions = null;
        }
    }

    @Override
    public DiagramTabViewModel getViewModel() {
        return (DiagramTabViewModel) super.getViewModel();
    }

    @Override
    public void requestFocus() {

    }

    @Override
    protected void build(DiagramTabViewModel viewModel) {
        super.build(viewModel);
        layerDiagramButton.setTooltip(new Tooltip("Layer Diagram"));
        layerDiagramButton.getStyleClass().add(StyleClasses.ICONED_BUTTON);
        zoomLevelComboBox.getStyleClass().add(Styles.DENSE);
        zoomLevelComboBox.setItems(viewModel.getZoomLevels());
        zoomInButton.setTooltip(new Tooltip("Zoom In"));
        zoomInButton.getStyleClass().add(StyleClasses.ICONED_BUTTON);
        zoomOutButton.setTooltip(new Tooltip("Zoom Out"));
        zoomOutButton.getStyleClass().add(StyleClasses.ICONED_BUTTON);

        imageView.setPickOnBounds(true); //to get scroll events even on transparent image section
        imageView.setPreserveRatio(true);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        scrollPane.getStyleClass().add("edge-to-edge");

        HBox toolBars = new HBox(toolBar);
        HBox.setHgrow(toolBar, Priority.ALWAYS);
        if (sessions != null) {
            toolBars.getChildren().add(sessions.getNode());
        }
        getTopPane().getChildren().addAll(toolBars, scrollPane);
    }

    @Override
    protected void bind(DiagramTabViewModel viewModel) {
        super.bind(viewModel);
        imageView.imageProperty().bind(viewModel.diagramProperty());
        zoomLevelComboBox.valueProperty().bindBidirectional(viewModel.zoomLevelProperty());
        viewModel.zoomLevelIndexProperty().bind(zoomLevelComboBox.getSelectionModel().selectedIndexProperty());
        this.imageView.fitWidthProperty().bind(viewModel.fitWidthProperty());
        this.imageView.fitHeightProperty().bind(viewModel.fitHeightProperty());
    }

    @Override
    protected void addHandlers(DiagramTabViewModel viewModel) {
        super.addHandlers(viewModel);
        layerDiagramButton.setOnAction((e) -> viewModel.openLayerDialog());
        scrollPane.setOnScroll((ScrollEvent event) -> {
            if (event.isControlDown()) {
                //there can be several events non zero and zero deltas
                if (event.getDeltaY() < 0) {
                    viewModel.zoomOut();
                } else if (event.getDeltaY() > 0) {
                    viewModel.zoomIn();
                }
                event.consume();
            }
        });
        this.zoomInButton.setOnAction(e -> viewModel.zoomIn());
        this.zoomOutButton.setOnAction(e -> viewModel.zoomOut());
    }

    @Override
    protected ComponentHelper<?> createComponentHelper() {
        return new DiagramTabHelper(this);
    }
}
