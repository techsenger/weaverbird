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
//import com.techsenger.alpha.api.model.ComponentLayerModel;
//import java.util.stream.Collectors;
//import javafx.beans.property.BooleanProperty;
//import javafx.beans.property.SimpleBooleanProperty;
//import javafx.collections.ObservableList;
//
///**
// *
// * @author Pavel Castornii
// */
//class LayerConfig extends AbstractUmlComponent {
//
//    private ObservableList<ModuleConfig> modules;
//
//    private final ComponentLayerModel layer;
//
//    private final BooleanProperty includedAll = new SimpleBooleanProperty();
//
//    private final BooleanProperty readsAll = new SimpleBooleanProperty();
//
//    private final BooleanProperty exportsAll = new SimpleBooleanProperty();
//
//    private final BooleanProperty opensAll = new SimpleBooleanProperty();
//
//    private final BooleanProperty requiresAll = new SimpleBooleanProperty();
//
//    private final BooleanProperty allowsAll = new SimpleBooleanProperty();
//
//    private final BooleanProperty servicesAll = new SimpleBooleanProperty();
//
//    private final BooleanProperty coloredAll = new SimpleBooleanProperty();
//
//    LayerConfig(ComponentLayerModel layer) {
//        super(layer.getName());
//        this.layer = layer;
//        includedProperty().set(true);
//    }
//
//    public ObservableList<ModuleConfig> getModules() {
//        return modules;
//    }
//
//    public void setModules(ObservableList<ModuleConfig> modules) {
//        this.modules = modules;
//    }
//
//    public ComponentLayerModel getLayer() {
//        return layer;
//    }
//
//    public BooleanProperty includedAllProperty() {
//        return includedAll;
//    }
//
//    public BooleanProperty readsAllProperty() {
//        return readsAll;
//    }
//
//    public BooleanProperty exportsAllProperty() {
//        return exportsAll;
//    }
//
//    public BooleanProperty opensAllProperty() {
//        return opensAll;
//    }
//
//    public BooleanProperty requiresAllProperty() {
//        return requiresAll;
//    }
//
//    public BooleanProperty allowsAllProperty() {
//        return allowsAll;
//    }
//
//    public BooleanProperty servicesAllProperty() {
//        return servicesAll;
//    }
//
//    public BooleanProperty coloredAllProperty() {
//        return coloredAll;
//    }
//
//    public void copyStateFrom(LayerConfig other) {
//        if (other == null) {
//            return;
//        }
//        includedProperty().set(other.includedProperty().get());
//        this.coloredProperty().set(other.coloredProperty().get());
////        this.layerColor.set(other.layerColor.get());
////        this.moduleColor.set(other.moduleColor.get());
//
//        var otherModulesByName = other.getModules().stream().collect(Collectors.toMap(m -> m.getName(), m -> m));
//        for (var m : modules) {
//            var otherModule = otherModulesByName.get(m.getName());
//            //their modules can differ, for example, after component restart
//            if (otherModule == null) {
//                continue;
//            }
//            m.includedProperty().set(otherModule.includedProperty().get());
//            m.readsProperty().set(otherModule.readsProperty().get());
//            m.exportsProperty().set(otherModule.exportsProperty().get());
//            m.opensProperty().set(otherModule.opensProperty().get());
//            m.requiresProperty().set(otherModule.requiresProperty().get());
//            m.allowsProperty().set(otherModule.requiresProperty().get());
//            m.servicesProperty().set(otherModule.servicesProperty().get());
//            m.coloredProperty().set(otherModule.coloredProperty().get());
//        }
//    }
//
//    public void reset() {
//        includedProperty().set(true);
//        coloredProperty().set(false);
////        layerColor.set(Color.web(DEFAULT_LAYER_COLOR));
////        moduleColor.set(Color.web(DEFAULT_MODULE_COLOR));
//        for (var m : modules) {
//            m.reset();
//        }
//        includedAll.set(false);
//        readsAll.set(false);
//        exportsAll.set(false);
//        opensAll.set(false);
//        requiresAll.set(false);
//        allowsAll.set(false);
//        servicesAll.set(false);
//        coloredAll.set(false);
//    }
//}
