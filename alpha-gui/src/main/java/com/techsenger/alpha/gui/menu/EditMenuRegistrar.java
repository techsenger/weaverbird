package com.techsenger.alpha.gui.menu;

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
//package com.techsenger.alpha.console.gui.menu;
//
//import com.techsenger.alpha.console.gui.shell.ShellTabView;
//import com.techsenger.tabshell.core.TabShellKey;
//import com.techsenger.tabshell.core.TabShellView;
//import com.techsenger.tabshell.core.registry.ControlFactory;
//import com.techsenger.tabshell.core.registry.ControlRegistry;
//import com.techsenger.tabshell.kit.core.style.CoreIcons;
//import com.techsenger.tabshell.kit.text.menu.EditMenuKeys;
//import com.techsenger.tabshell.material.icon.FontIconView;
//import com.techsenger.tabshell.material.menu.KeyedMenuItem;
//import javafx.scene.input.KeyCode;
//import javafx.scene.input.KeyCodeCombination;
//import javafx.scene.input.KeyCombination;
//
///**
// *
// * @author Pavel Castornii
// */
//public class EditMenuRegistrar extends com.techsenger.tabshell.kit.text.menu.EditMenuRegistrar {
//
//    public EditMenuRegistrar(ControlRegistry registry) {
//        super(registry);
//    }
//
//    @Override
//    protected void registerCutItem() {
//        ControlFactory<KeyedMenuItem> f = (v) -> {
//            var item = new KeyedMenuItem(EditMenuKeys.CUT, false, true, false, "Cu_t",
//                     new FontIconView(CoreIcons.CUT));
//            item.setAccelerator(new KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_DOWN));
//            item.setOnAction(e -> {
//                var tab = ((TabShellView<?>) v).getSelectedTab();
//                if (tab != null && tab instanceof ShellTabView) {
//                    ((ShellTabView) tab).cut();
//                }
//            });
//            return item;
//        };
//        addRegistration(getRegistry().registerMenuItem(TabShellKey.INSTANCE, EditMenuKeys.CLIPBOARD, f, 100));
//    }
//
//    @Override
//    protected void registerPasteItem() {
//        ControlFactory<KeyedMenuItem> f = (v) -> {
//            var item = new KeyedMenuItem(EditMenuKeys.PASTE, false, true, false, "_Paste",
//                    new FontIconView(CoreIcons.PASTE));
//            item.setAccelerator(new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN));
//            item.setOnAction(e -> {
//                var tab = ((TabShellView<?>) v).getSelectedTab();
//                if (tab != null && tab instanceof ShellTabView) {
//                    ((ShellTabView) tab).paste();
//                }
//            });
//            return item;
//        };
//        addRegistration(getRegistry().registerMenuItem(TabShellKey.INSTANCE, EditMenuKeys.CLIPBOARD, f, 300));
//    }
//}
