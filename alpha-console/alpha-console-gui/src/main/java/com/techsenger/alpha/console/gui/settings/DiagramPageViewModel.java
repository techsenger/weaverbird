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
//package com.techsenger.alpha.console.gui.settings;
//
//import com.techsenger.tabshell.core.page.AbstractPageViewModel;
//import com.techsenger.tabshell.core.page.PageKey;
//import javafx.beans.property.IntegerProperty;
//import javafx.beans.property.ObjectProperty;
//import javafx.beans.property.SimpleIntegerProperty;
//import javafx.beans.property.SimpleObjectProperty;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.scene.paint.Color;
//
///**
// *
// * @author Pavel Castornii
// */
//class DiagramPageViewModel extends AbstractPageViewModel {
//
//    private static final PageKey key = new PageKey("Diagram Page");
//
//    private final ObjectProperty<Color> layerColor = new SimpleObjectProperty<>();
//
//    private final ObjectProperty<Color> moduleColor = new SimpleObjectProperty<>();
//
//    private final ObservableList<LineType> lineTypes = FXCollections.observableArrayList(LineType.values());
//
//    private final ObjectProperty<LineType> lineType = new SimpleObjectProperty<>();
//
//    private final ObservableList<LayoutEngine> layoutEngines = FXCollections.observableArrayList(LayoutEngine.values());
//
//    private final ObjectProperty<LayoutEngine> layoutEngine = new SimpleObjectProperty<>();
//
//    private final IntegerProperty limitSize = new SimpleIntegerProperty();
//
//    DiagramPageViewModel(DiagramSettings settings) {
//        this.layerColor.set(settings.getLayerColor());
//        this.moduleColor.set(settings.getModuleColor());
//        this.lineType.set(settings.getLineType());
//        this.layoutEngine.set(settings.getLayoutEngine());
//        this.limitSize.set(settings.getLimitSize());
//        setTitle("Diagram");
//    }
//
//    @Override
//    public PageKey getKey() {
//        return key;
//    }
//
//    public ObjectProperty<Color> layerColorProperty() {
//        return layerColor;
//    }
//
//    public ObjectProperty<Color> moduleColorProperty() {
//        return moduleColor;
//    }
//
//    public IntegerProperty limitSizeProperty() {
//        return limitSize;
//    }
//
//    public ObservableList<LineType> getLineTypes() {
//        return lineTypes;
//    }
//
//    public ObjectProperty<LineType> lineTypeProperty() {
//        return lineType;
//    }
//
//    public ObservableList<LayoutEngine> getLayoutEngines() {
//        return layoutEngines;
//    }
//
//    public ObjectProperty<LayoutEngine> layoutEngineProperty() {
//        return layoutEngine;
//    }
//}
