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
//package com.techsenger.alpha.console.gui.log;
//
//import com.techsenger.alpha.api.Framework;
//import com.techsenger.alpha.api.model.ComponentLayerModel;
//import com.techsenger.alpha.console.gui.keys.ConsoleComponentKeys;
//import com.techsenger.alpha.console.gui.style.ConsoleIcons;
//import com.techsenger.tabshell.core.dialog.DialogKey;
//import com.techsenger.tabshell.core.dialog.DialogScope;
//import com.techsenger.tabshell.kit.dialog.AbstractSimpleDialogViewModel;
//import com.techsenger.tabshell.material.icon.FontIcon;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Set;
//import java.util.stream.Collectors;
//
///**
// *
// * @author Pavel Castornii
// */
//class ModuleFilterDialogViewModel extends AbstractSimpleDialogViewModel {
//
//    private final List<DialogLayer> layers = new ArrayList<>();
//
//    ModuleFilterDialogViewModel(Set<Module> selectedModules) {
//        super(DialogScope.TAB, true);
//        setPrefWidth(600);
//        setIcon(new FontIcon(ConsoleIcons.FILTER_SETTINGS));
//        setTitle("Module Filter Settings");
//        DialogLayer layer = null;
//        for (var c: Framework.getComponentManager().getComponents()) {
//            layer = DialogLayer.from(c.getLayer(), ComponentLayerModel
//                    .resolveName(c.getDescriptor().getConfig().getFullName(), c.getDescriptor().getId()),
//                    selectedModules);
//            layers.add(layer);
//        }
//        layers.sort((l1, l2) -> l1.getName().compareTo(l2.getName()));
//        layer = DialogLayer.from(Framework.class.getModule().getLayer(), Framework.getLayerFullName(),
//                selectedModules);
//        layers.add(0, layer);
//    }
//
//    @Override
//    public DialogKey getKey() {
//        return ConsoleComponentKeys.MODULE_FILTER_DIALOG;
//    }
//
//    public List<DialogLayer> getLayers() {
//        return layers;
//    }
//
//    public Set<Module> getSelectedModules() {
//        Set<Module> modules = layers.stream().flatMap(l -> l.getModules().stream())
//                .filter(m -> m.selectedProperty().get()).map(m -> m.getModule()).collect(Collectors.toSet());
//        return modules;
//    }
//
//}
