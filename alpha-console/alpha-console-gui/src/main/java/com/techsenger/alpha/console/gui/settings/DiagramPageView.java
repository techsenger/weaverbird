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

package com.techsenger.alpha.console.gui.settings;

import com.techsenger.tabshell.core.page.AbstractPageView;
import com.techsenger.tabshell.core.style.SizeConstants;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.converter.NumberStringConverter;

/**
 *
 * @author Pavel Castornii
 */
class DiagramPageView extends AbstractPageView<DiagramPageViewModel> {

    private final GridPane gridPane = new GridPane();

    private final Label layerLabel = new Label("Layer Color");

    private final ColorPicker layerPicker = new ColorPicker();

    private final Label moduleLabel = new Label("Module Color");

    private final ColorPicker modulePicker = new ColorPicker();

    private final Label lineLabel = new Label("Line Type");

    private final ComboBox<LineType> lineComboBox = new ComboBox<>();

    private final Label engineLabel = new Label("Layout Engine");

    private final ComboBox<LayoutEngine> engineComboBox = new ComboBox<>();

    private final Label limitLabel = new Label("Limit Size");

    private final TextField limitTextField = new TextField();

    private final VBox main = new VBox(getTitleLabel(), gridPane);

    DiagramPageView(DiagramPageViewModel viewModel) {
        super(viewModel);
    }

    @Override
    public void requestFocus() {

    }

    @Override
    public Region getNode() {
        return this.main;
    }

    @Override
    protected void build(DiagramPageViewModel viewModel) {
        super.build(viewModel);

        gridPane.setHgap(SizeConstants.INSET);
        gridPane.setVgap(SizeConstants.INSET);
        VBox.setVgrow(gridPane, Priority.ALWAYS);
//        var c0 = new ColumnConstraints();
//        var c1 = new ColumnConstraints();
//        gridPane.getColumnConstraints().addAll(c0, c1);

        layerLabel.setMinWidth(Region.USE_PREF_SIZE);
        gridPane.add(layerLabel, 0, 0);
        gridPane.add(layerPicker, 1, 0);

        moduleLabel.setMinWidth(Region.USE_PREF_SIZE);
        gridPane.add(moduleLabel, 0, 1);
        GridPane.setHgrow(modulePicker, Priority.ALWAYS);
        gridPane.add(modulePicker, 1, 1);

        lineLabel.setMinWidth(Region.USE_PREF_SIZE);
        gridPane.add(lineLabel, 0, 2);
        gridPane.add(lineComboBox, 1, 2);
        lineComboBox.setItems(viewModel.getLineTypes());

        engineLabel.setMinWidth(Region.USE_PREF_SIZE);
        gridPane.add(engineLabel, 0, 3);
        gridPane.add(engineComboBox, 1, 3);
        engineComboBox.setItems(viewModel.getLayoutEngines());

        limitLabel.setMinWidth(Region.USE_PREF_SIZE);
        gridPane.add(limitLabel, 0, 4);
        gridPane.add(limitTextField, 1, 4);

        this.main.setSpacing(SizeConstants.INSET);
    }

    @Override
    protected void bind(DiagramPageViewModel viewModel) {
        super.bind(viewModel);
        layerPicker.valueProperty().bindBidirectional(viewModel.layerColorProperty());
        modulePicker.valueProperty().bindBidirectional(viewModel.moduleColorProperty());
        lineComboBox.valueProperty().bindBidirectional(viewModel.lineTypeProperty());
        engineComboBox.valueProperty().bindBidirectional(viewModel.layoutEngineProperty());
        limitTextField.textProperty().bindBidirectional(viewModel.limitSizeProperty(), new NumberStringConverter());

        lineComboBox.maxWidthProperty().bind(modulePicker.widthProperty());
        engineComboBox.maxWidthProperty().bind(modulePicker.widthProperty());
        limitTextField.maxWidthProperty().bind(modulePicker.widthProperty());
    }


}
