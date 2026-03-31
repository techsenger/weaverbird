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
//import com.techsenger.alpha.api.Framework;
//import com.techsenger.alpha.api.FrameworkMode;
//import com.techsenger.alpha.api.SystemProperties;
//import com.techsenger.alpha.console.gui.keys.ToolsMenuKeyManager;
//import com.techsenger.alpha.console.gui.session.SessionDialogView;
//import com.techsenger.alpha.console.gui.session.SessionDialogViewModel;
//import com.techsenger.alpha.console.gui.settings.ConsoleSettings;
//import com.techsenger.alpha.console.gui.style.ConsoleIcons;
//import com.techsenger.alpha.console.gui.utils.TabOpener;
//import com.techsenger.tabshell.core.TabShellKey;
//import com.techsenger.tabshell.core.TabShellView;
//import com.techsenger.tabshell.core.dialog.DialogScope;
//import com.techsenger.tabshell.core.registry.AbstractControlRegistrar;
//import com.techsenger.tabshell.core.registry.ControlFactory;
//import com.techsenger.tabshell.core.registry.ControlRegistry;
//import com.techsenger.tabshell.kit.dialog.alert.AlertDialogType;
//import com.techsenger.tabshell.kit.dialog.alert.AlertDialogView;
//import com.techsenger.tabshell.kit.dialog.alert.AlertDialogViewModel;
//import com.techsenger.tabshell.material.icon.FontIconView;
//import com.techsenger.tabshell.material.menu.KeyedMenu;
//import com.techsenger.tabshell.material.menu.KeyedMenuGroup;
//import com.techsenger.tabshell.material.menu.KeyedMenuItem;
//import javafx.scene.input.KeyCode;
//import javafx.scene.input.KeyCodeCombination;
//import javafx.scene.input.KeyCombination;
//
///**
// *
// * @author Pavel Castornii
// */
//public class ToolsMenuRegistrar extends AbstractControlRegistrar implements TabOpener {
//
//    public ToolsMenuRegistrar(ControlRegistry registry) {
//        super(registry);
//    }
//
//    @Override
//    public void register() {
//        registerToolsMenu();
//        registerDefaultGroup();
//        registerMainGroup();
//        registerDiagramsItem();
//        registerMemoryLogItem();
//        registerFileLogItem();
//        if (Framework.getMode() == FrameworkMode.CLIENT) {
//            registerSessionsItem();
//        }
//    }
//
//    protected void registerToolsMenu() {
//        ControlFactory<KeyedMenu> f = (v) -> {
//            return new KeyedMenu(ToolsMenuKeyManager.getTools(), "_Tools");
//        };
//        addRegistration(getRegistry().registerMenu(TabShellKey.INSTANCE, null, f, 300));
//    }
//
//    private void registerMainGroup() {
//        ControlFactory<KeyedMenuGroup> f = (v) -> {
//            return new KeyedMenuGroup(ToolsMenuKeys.MAIN);
//        };
//        addRegistration(getRegistry().registerMenuGroup(TabShellKey.INSTANCE, ToolsMenuKeyManager.getTools(), f, 0));
//    }
//
//    private void registerDefaultGroup() {
//        ControlFactory<KeyedMenuGroup> f = (v) -> {
//            return new KeyedMenuGroup(ToolsMenuKeys.DEFAULT);
//        };
//        addRegistration(getRegistry().registerMenuGroup(TabShellKey.INSTANCE, ToolsMenuKeyManager.getTools(), f, 100));
//    }
//
//    private void registerDiagramsItem() {
//        ControlFactory<KeyedMenuItem> f = (v) -> {
//            var item = new KeyedMenuItem(ToolsMenuKeyManager.getDiagrams(), false, false, false, "_Diagrams",
//                    new FontIconView(ConsoleIcons.DIAGRAMS));
//            item.setOnAction((e) -> openDiagramsTab((TabShellView<?>) v));
//            return item;
//
//        };
//        addRegistration(getRegistry().registerMenuItem(TabShellKey.INSTANCE, ToolsMenuKeys.MAIN, f, 200));
//    }
//
//    private void registerMemoryLogItem() {
//        ControlFactory<KeyedMenuItem> f = (v) -> {
//            var tabShellView = (TabShellView<?>) v;
//            var item = new KeyedMenuItem(ToolsMenuKeyManager.getMemoryLog(), false, false, false, "_Memory Log",
//                    new FontIconView(ConsoleIcons.MEMORY_LOG));
//            item.setAccelerator(new KeyCodeCombination(KeyCode.M, KeyCombination.CONTROL_DOWN));
//            item.setOnAction((e) -> {
//                var memoryLogEnabled = System.getProperty(SystemProperties.LOG_MEMORY);
//                if (memoryLogEnabled == null || memoryLogEnabled.equalsIgnoreCase("false")) {
//                    var alertViewModel = new AlertDialogViewModel(DialogScope.SHELL, AlertDialogType.INFO,
//                            "Memory log is not enabled.");
//                    var alertView = new AlertDialogView<AlertDialogViewModel>(alertViewModel);
//                    alertView.initialize();
//                    tabShellView.getDialogManager().openDialog(alertView);
//                } else {
//                    openMemoryLogTab(tabShellView);
//                }
//            });
//            return item;
//
//        };
//        addRegistration(getRegistry().registerMenuItem(TabShellKey.INSTANCE, ToolsMenuKeys.MAIN, f, 300));
//    }
//
//    private void registerFileLogItem() {
//        ControlFactory<KeyedMenuItem> f = (v) -> {
//            var tabShellView = (TabShellView<?>) v;
//            var item = new KeyedMenuItem(ToolsMenuKeyManager.getFileLog(), false, false, false, "F_ile Log",
//                    new FontIconView(ConsoleIcons.FILE_LOG));
//            item.setOnAction((e) -> {
//                openFileLogTab(tabShellView);
//            });
//            return item;
//
//        };
//        addRegistration(getRegistry().registerMenuItem(TabShellKey.INSTANCE, ToolsMenuKeys.MAIN, f, 400));
//    }
//
//    private void registerSessionsItem() {
//        ControlFactory<KeyedMenuItem> f = (v) -> {
//            var tabShellView = (TabShellView<?>) v;
//            var item = new KeyedMenuItem(ToolsMenuKeys.SESSIONS, false, false, false, "S_essions",
//                    new FontIconView(ConsoleIcons.NETWORK));
//            item.setOnAction((e) -> {
//                var viewModel = new SessionDialogViewModel((ConsoleSettings) tabShellView.getViewModel().getSettings(),
//                        tabShellView.getViewModel().getHistoryManager());
//                var view = new SessionDialogView(viewModel, tabShellView.getDialogManager());
//                view.initialize();
//                tabShellView.getDialogManager().openDialog(view);
//            });
//            return item;
//
//        };
//        addRegistration(getRegistry().registerMenuItem(TabShellKey.INSTANCE, ToolsMenuKeys.DEFAULT, f, 0));
//    }
//
//}
