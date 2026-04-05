package com.techsenger.alpha.gui.diagram;

///*
// * Copyright 2018-2026 Pavel Castornii.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.techsenger.alpha.console.gui.diagram;
//
//import atlantafx.base.theme.Styles;
//import com.techsenger.tabshell.core.page.AbstractPageView;
//import com.techsenger.tabshell.core.style.SizeConstants;
//import com.techsenger.toolkit.fx.Spacer;
//import javafx.beans.binding.Bindings;
//import javafx.beans.property.BooleanProperty;
//import javafx.beans.value.ObservableValue;
//import javafx.geometry.Insets;
//import javafx.geometry.Pos;
//import javafx.scene.control.CheckBox;
//import javafx.scene.control.Label;
//import javafx.scene.control.TableColumn;
//import javafx.scene.control.TableView;
//import javafx.scene.control.cell.CheckBoxTableCell;
//import javafx.scene.layout.HBox;
//import javafx.scene.layout.Priority;
//import javafx.scene.layout.Region;
//import javafx.scene.layout.VBox;
//import javafx.util.Callback;
//
///**
// *
// * @author Pavel Castornii
// */
//class LayerPageView extends AbstractPageView<LayerPageViewModel> {
//
//    private final VBox main = new VBox();
//
//    private final HBox titleBox = new HBox();
//
//    LayerPageView(LayerPageViewModel viewModel) {
//        super(viewModel);
//    }
//
//    @Override
//    public void requestFocus() {
//
//    }
//
//    @Override
//    public Region getNode() {
//        return this.main;
//    }
//
//    @Override
//    protected void build(LayerPageViewModel viewModel) {
//        super.build(viewModel);
//        var layer = viewModel.getLayerConfig();
//
//        var includedLabel = new Label("Included");
//        //gridPane.add(includedLabel, 0, 0);
//        var includedCheckBox = new CheckBox();
//        includedCheckBox.selectedProperty().bindBidirectional(viewModel.getLayerConfig().includedProperty());
//        //gridPane.add(includedCheckBox, 1, 0);
//        //gridPane.add(new Spacer(), 2, 0);
//        var coloredLabel = new Label("Colored");
//        //gridPane.add(coloredLabel, 3, 0);
//        var coloredCheckBox = new CheckBox();
//        coloredCheckBox.selectedProperty().bindBidirectional(viewModel.getLayerConfig().coloredProperty());
//        //gridPane.add(coloredCheckBox, 4, 0);
//        titleBox.getChildren().addAll(getTitleLabel(), new Spacer(), includedLabel, includedCheckBox,
//                coloredLabel, coloredCheckBox);
//        titleBox.setSpacing(SizeConstants.INSET);
//        titleBox.setAlignment(Pos.CENTER_LEFT);
//        titleBox.setPadding(new Insets(0, 0, SizeConstants.INSET, 0));
//
//        //table
//        TableView<ModuleConfig> table = new TableView<>();
//        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
//        table.setEditable(true);
//        table.getStyleClass().add(Styles.DENSE);
//        buildTableColumns(table);
//        buildTableRows(table, layer);
//        VBox.setVgrow(table, Priority.ALWAYS);
//        //control box
//        var controlBox = createControlBox(table, layer);
//        this.main.getChildren().addAll(titleBox, table, controlBox);
//        VBox.setVgrow(this.main, Priority.ALWAYS);
//    }
//
//    private void buildTableColumns(TableView<ModuleConfig> table) {
//        //name column
//        TableColumn<ModuleConfig, String> nameColumn = new TableColumn<>("Module");
//        nameColumn.setCellValueFactory(data -> data.getValue().nameProperty());
//        nameColumn.setEditable(false);
//        //we disable column reodering because we use control pane under table
//        nameColumn.setReorderable(false);
//        nameColumn.setMinWidth(280);
//        nameColumn.setStyle("-fx-alignment: center-left;");
//        TableColumn<ModuleConfig, Boolean> includedColumn = createColumn("Included",
//                data -> data.getValue().includedProperty());
//        TableColumn<ModuleConfig, Boolean> readsColumn = createColumn("Reads",
//                data -> data.getValue().readsProperty());
//        TableColumn<ModuleConfig, Boolean> exportsColumn = createColumn("Exports",
//                data -> data.getValue().exportsProperty());
//        TableColumn<ModuleConfig, Boolean> opensColumn = createColumn("Opens",
//                data -> data.getValue().opensProperty());
//        TableColumn<ModuleConfig, Boolean> requiresColumn = createColumn("Requires",
//                data -> data.getValue().requiresProperty());
//        TableColumn<ModuleConfig, Boolean> allowsColumn = createColumn("Allows",
//                data -> data.getValue().allowsProperty());
//        TableColumn<ModuleConfig, Boolean> coloredColumn = createColumn("Colored",
//                data -> data.getValue().coloredProperty());
//        table.getColumns().addAll(nameColumn, includedColumn, readsColumn, exportsColumn, opensColumn,
//                requiresColumn, allowsColumn, coloredColumn);
//    }
//
//    private TableColumn<ModuleConfig, Boolean> createColumn(String title,
//            Callback<TableColumn.CellDataFeatures<ModuleConfig, Boolean>, ObservableValue<Boolean>> clbck) {
//        TableColumn<ModuleConfig, Boolean> column = new TableColumn<>(title);
//        column.setCellValueFactory(clbck);
//        column.setCellFactory(tc -> new CheckBoxTableCell<ModuleConfig, Boolean>());
//        column.setEditable(true);
//        column.setReorderable(false);
//        //column.setMinWidth(20);
//        column.setStyle("-fx-alignment: center;");
//        return column;
//    }
//
//    private void buildTableRows(TableView<ModuleConfig> table, LayerConfig layerComponent) {
//        table.setItems(layerComponent.getModules());
//    }
//
//    private HBox createControlBox(TableView<ModuleConfig> table, LayerConfig layer) {
//        var nameBox = new HBox();
//        this.bindWidths(nameBox, table.getColumns().get(0), -2); // table cell has borders?
//        nameBox.getChildren().add(new Label("Select/Unselect All"));
//        //included box
//        var includedBox = createHBox(table, layer.includedAllProperty(), 1);
//        var readsBox = createHBox(table, layer.readsAllProperty(), 2);
//        var exportsBox = createHBox(table, layer.exportsAllProperty(), 3);
//        var opensBox = createHBox(table, layer.opensAllProperty(), 4);
//        var requiresBox = createHBox(table, layer.requiresAllProperty(), 5);
//        var allowsBox = createHBox(table, layer.allowsAllProperty(), 6);
//        var coloredBox = createHBox(table, layer.coloredAllProperty(), 7);
//        //main box
//        var mainBox = new HBox(nameBox, includedBox, readsBox, exportsBox, opensBox, requiresBox,
//                allowsBox, coloredBox);
//        mainBox.setPadding(new Insets(SizeConstants.HALF_INSET, 0, 0, 0));
//        //mainBox.setSpacing(0.5);
//        return mainBox;
//    }
//
//    private HBox createHBox(TableView<ModuleConfig> table, BooleanProperty property, int index) {
//        var checkBox = new CheckBox();
//        checkBox.selectedProperty().bindBidirectional(property);
//        var hBox = new HBox(checkBox);
//        this.bindWidths(hBox, table.getColumns().get(index), 0);
//        hBox.setAlignment(Pos.CENTER);
//        //checkboxes in table are smaller, so we use same class as table cell
//        //see https://gist.github.com/maxd/63691840fc372f22f470#file-modena-css-L2519
//        hBox.getStyleClass().add("check-box-table-cell");
//        return hBox;
//    }
//
//    private void bindWidths(HBox box, TableColumn<?, ?> column, int correction) {
//        box.prefWidthProperty().bind(Bindings.add(column.widthProperty(), correction));
//    }
//}
