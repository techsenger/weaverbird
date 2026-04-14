package com.techsenger.weaverbird.gui.settings;

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
//package com.techsenger.weaverbird.console.gui.settings;
//
//import atlantafx.base.theme.Styles;
//import com.techsenger.tabshell.core.page.AbstractPageView;
//import com.techsenger.tabshell.core.style.SizeConstants;
//import com.techsenger.tabshell.core.theme.TabShellTheme;
//import com.techsenger.tabshell.core.theme.ThemeStringConverter;
//import javafx.scene.control.ComboBox;
//import javafx.scene.control.Label;
//import javafx.scene.layout.ColumnConstraints;
//import javafx.scene.layout.GridPane;
//import javafx.scene.layout.Priority;
//import javafx.scene.layout.Region;
//import javafx.scene.layout.VBox;
//
///**
// *
// * @author Pavel Castornii
// */
//class AppearancePageView extends AbstractPageView<AppearancePageViewModel> {
//
//    private final Label themeLabel = new Label("Theme");
//
//    private final ComboBox<TabShellTheme> themeComboBox = new ComboBox<>();
//
//    private final Label fontLabel = new Label("Font");
//
//    private final ComboBox<String> fontFamilyComboBox = new ComboBox<>();
//
//    private final ComboBox<Integer> fontSizeComboBox = new ComboBox<>();
//
//    private final Label viewerFontLabel = new Label("Viewer Font");
//
//    private final ComboBox<String> viewerFontFamilyComboBox = new ComboBox<>();
//
//    private final ComboBox<Integer> viewerFontSizeComboBox = new ComboBox<>();
//
////    private final Label tabSizeLabel = new Label("Tab Size:");
////
////    private final ComboBox<Integer> tabSizeComboBox = new ComboBox<>();
////
////    private final Label tabUseSpacesSizeLabel = new Label("Spaces for Tab:");
////
////    private final CheckBox tabUseSpacesCheckBox = new CheckBox();
//
//    private final GridPane gridPane = new GridPane();
//
//    private final VBox main = new VBox(getTitleLabel(), gridPane);
//
//    AppearancePageView(AppearancePageViewModel viewModel) {
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
//    protected void build(AppearancePageViewModel viewModel) {
//        super.build(viewModel);
//        gridPane.setHgap(SizeConstants.INSET);
//        gridPane.setVgap(SizeConstants.INSET);
//        VBox.setVgrow(gridPane, Priority.ALWAYS);
//        for (int i = 0; i < 4; i++) {
//            var cc = new ColumnConstraints();
//            gridPane.getColumnConstraints().add(cc);
//            if (i == 0) {
//                cc.setHgrow(Priority.NEVER);
//            } else {
//                cc.setHgrow(Priority.ALWAYS);
//            }
//            if (i > 1) {
//                cc.setPercentWidth(25);
//            }
//        }
//        //row 0
//        themeComboBox.setConverter(new ThemeStringConverter());
//        themeComboBox.setMaxWidth(Double.MAX_VALUE);
//        themeComboBox.getStyleClass().add(Styles.DENSE);
//        themeLabel.setMinWidth(Region.USE_PREF_SIZE);
//        gridPane.add(themeLabel, 0, 0);
//        gridPane.add(themeComboBox, 1, 0);
//        GridPane.setColumnSpan(themeComboBox, 3);
//        this.themeComboBox.setItems(viewModel.getThemes());
//
//        //row 1
//        fontLabel.setMinWidth(Region.USE_PREF_SIZE);
//        gridPane.add(fontLabel, 0, 1);
//        fontFamilyComboBox.setMaxWidth(Double.MAX_VALUE);
//        fontFamilyComboBox.getStyleClass().add(Styles.DENSE);
//        fontFamilyComboBox.setItems(viewModel.getFontFamilies());
//        GridPane.setColumnSpan(fontFamilyComboBox, 2);
//        gridPane.add(fontFamilyComboBox, 1, 1);
//        this.fontSizeComboBox.setItems(viewModel.getFontSizes());
//        fontSizeComboBox.setMaxWidth(Double.MAX_VALUE);
//        fontSizeComboBox.getStyleClass().add(Styles.DENSE);
//        gridPane.add(fontSizeComboBox, 3, 1);
//
//        //row 2
//        viewerFontLabel.setMinWidth(Region.USE_PREF_SIZE);
//        gridPane.add(viewerFontLabel, 0, 2);
//        viewerFontFamilyComboBox.setMaxWidth(Double.MAX_VALUE);
//        viewerFontFamilyComboBox.getStyleClass().add(Styles.DENSE);
//        viewerFontFamilyComboBox.setItems(viewModel.getViewerFontFamilies());
//        GridPane.setColumnSpan(viewerFontFamilyComboBox, 2);
//        gridPane.add(viewerFontFamilyComboBox, 1, 2);
//        this.viewerFontSizeComboBox.setItems(viewModel.getViewerFontSizes());
//        viewerFontSizeComboBox.setMaxWidth(Double.MAX_VALUE);
//        viewerFontSizeComboBox.getStyleClass().add(Styles.DENSE);
//        gridPane.add(viewerFontSizeComboBox, 3, 2);
//
////        gridpane.add(tabSizeLabel, 0, 2);
////        gridpane.add(tabSizeComboBox, 1, 2);
////        gridpane.add(tabUseSpacesSizeLabel, 0, 3);
////        gridpane.add(tabUseSpacesCheckBox, 1, 3);
//        //we need to cast to Index to show that this is an object
////        this.tabSizeComboBox.setItems(viewModel.getTabSizes());
////        this.tabSizeComboBox.getSelectionModel().select((Integer) viewModel.tabSizeProperty().get());
////        this.tabUseSpacesCheckBox.setSelected(viewModel.useSpacesForTabProperty().get());
//
//        this.main.setSpacing(SizeConstants.INSET);
//    }
//
//    @Override
//    protected void bind(AppearancePageViewModel viewModel) {
//        super.bind(viewModel);
//        themeComboBox.valueProperty().bindBidirectional(viewModel.themeProperty());
//        fontFamilyComboBox.valueProperty().bindBidirectional(viewModel.fontFamilyProperty());
//        fontSizeComboBox.valueProperty().bindBidirectional(viewModel.fontSizeProperty());
//        viewerFontFamilyComboBox.valueProperty().bindBidirectional(viewModel.viewerFontFamilyProperty());
//        viewerFontSizeComboBox.valueProperty().bindBidirectional(viewModel.viewerFontSizeProperty());
//
////        viewModel.tabSizeProperty().bind(tabSizeComboBox.getSelectionModel().selectedItemProperty());
////        viewModel.useSpacesForTabProperty().bind(tabUseSpacesCheckBox.selectedProperty());
//    }
//}
