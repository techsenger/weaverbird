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
//import com.techsenger.alpha.api.model.ComponentModuleModel;
//import com.techsenger.toolkit.core.model.ModuleModel;
//import com.techsenger.toolkit.core.model.ResolvedModuleModel;
//import javafx.beans.property.BooleanProperty;
//import javafx.beans.property.SimpleBooleanProperty;
//
///**
// *
// * @author Pavel Castornii
// */
//class ModuleConfig extends AbstractUmlComponent {
//
//    private final BooleanProperty reads = new SimpleBooleanProperty();
//
//    private final BooleanProperty exports = new SimpleBooleanProperty();
//
//    private final BooleanProperty opens = new SimpleBooleanProperty();
//
//    private final BooleanProperty requires = new SimpleBooleanProperty();
//
//    private final BooleanProperty allows = new SimpleBooleanProperty();
//
//    private final BooleanProperty services = new SimpleBooleanProperty();
//
//    private final ComponentModuleModel module;
//
//    private final ResolvedModuleModel resolvedModule;
//
//    ModuleConfig(ModuleModel module) {
//        super(module.getName());
//        this.module = (ComponentModuleModel) module;
//        this.resolvedModule = module.getLayer().getConfiguration().getModulesByName().get(module.getName());
//        addListenerToUpdateIncluded(reads, exports, opens, requires, allows, services, coloredProperty());
//    }
//
//    private void addListenerToUpdateIncluded(BooleanProperty... properties) {
//        for (var p : properties) {
//            p.addListener((ov, oldV, newV) -> {
//                if (newV) {
//                    includedProperty().set(true);
//                }
//            });
//        }
//    }
//
//    public BooleanProperty readsProperty() {
//        return reads;
//    }
//
//    public BooleanProperty exportsProperty() {
//        return exports;
//    }
//
//    public BooleanProperty opensProperty() {
//        return opens;
//    }
//
//    public BooleanProperty requiresProperty() {
//        return requires;
//    }
//
//    public BooleanProperty allowsProperty() {
//        return allows;
//    }
//
//    public BooleanProperty servicesProperty() {
//        return services;
//    }
//
//    public ComponentModuleModel getModule() {
//        return module;
//    }
//
//    public ResolvedModuleModel getResolvedModule() {
//        return resolvedModule;
//    }
//
//    public void reset() {
//        includedProperty().set(false);
//        coloredProperty().set(false);
//        this.reads.set(false);
//        this.exports.set(false);
//        this.opens.set(false);
//        this.requires.set(false);
//        this.allows.set(false);
//        this.services.set(false);
//    }
//}
