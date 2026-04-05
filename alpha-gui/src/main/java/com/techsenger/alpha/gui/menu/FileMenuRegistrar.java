/*
 * Copyright 2018-2026 Pavel Castornii.
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

package com.techsenger.alpha.gui.menu;

import com.techsenger.alpha.gui.console.ConsoleTabFxView;
import com.techsenger.alpha.gui.console.ConsoleTabPresenter;
import com.techsenger.alpha.gui.style.ConsoleIcons;
import com.techsenger.alpha.core.api.Framework;
import com.techsenger.tabshell.core.CoreComponents;
import com.techsenger.tabshell.core.ShellFxView;
import com.techsenger.tabshell.core.registry.AbstractControlRegistrar;
import com.techsenger.tabshell.core.registry.ControlFactory;
import com.techsenger.tabshell.layout.tabhost.TabHostFxView;
import com.techsenger.tabshell.material.icon.FontIconView;
import com.techsenger.tabshell.material.menu.NamedMenu;
import com.techsenger.tabshell.material.menu.NamedMenuGroup;
import com.techsenger.tabshell.material.menu.NamedMenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

/**
 *
 * @author Pavel Castornii
 */
public class FileMenuRegistrar extends AbstractControlRegistrar {

    private final ShellFxView<?> shell;

    private final Framework framework;

    public FileMenuRegistrar(ShellFxView<?> shell, Framework framework) {
        super(shell.getControlRegistry());
        this.shell = shell;
        this.framework = framework;
    }

    @Override
    public void register() {
        registerMenu();
        registerMainGroup();
        registerShellItem();
//        registerSettingsItem();
    }

    protected void registerMenu() {
        ControlFactory<NamedMenu> f = (v) -> {
            return new NamedMenu(FileMenu.NAME, "_File", 0);
        };
        addRegistration(getRegistry().registerMenu(CoreComponents.SHELL, null, f));
    }

    private void registerMainGroup() {
        ControlFactory<NamedMenuGroup> f = (v) -> new NamedMenuGroup(FileMenu.MAIN, 100);
        addRegistration(getRegistry().registerMenuGroup(CoreComponents.SHELL, FileMenu.NAME, f));
    }

    private void registerShellItem() {
        ControlFactory<NamedMenuItem> f = (v) -> {
            var item = new NamedMenuItem(FileMenu.SHELL, false, false, false, "S_hell", 100);
            item.setGraphic(new FontIconView(ConsoleIcons.CONSOLE));
            item.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
            item.setOnAction((e) -> {
                var shell = (ShellFxView<?>) v;
                var shellTabView = new ConsoleTabFxView<>(shell);
                var shellTabPresenter = new ConsoleTabPresenter<>(shellTabView, framework);
                shellTabPresenter.initialize();
                TabHostFxView<?> workspace = (TabHostFxView<?>) shell.getComposer().getWorkspace();
                workspace.getComposer().addTab(shellTabView);
            });
            return item;

        };
        addRegistration(getRegistry().registerMenuItem(CoreComponents.SHELL, FileMenu.MAIN, f));
    }

//    private void registerSettingsItem() {
//        ControlFactory<KeyedMenuItem> f = (v) -> {
//            var tabShellView = (TabShellView<?>) v;
//            var item = new KeyedMenuItem(FileMenuKeys.SETTINGS, false, false, false, "_Settings",
//                    new FontIconView(ConsoleIcons.SETTINGS));
//            item.setOnAction((e) -> {
//                var viewModel =
//                        new SettingsDialogViewModel((ConsoleSettings) tabShellView.getViewModel().getSettings(),
//                        tabShellView.getViewModel().getHistoryManager());
//                var view = new SettingsDialogView(viewModel);
//                view.initialize();
//                tabShellView.getDialogManager().openDialog(view);
//            });
//            return item;
//
//        };
//        addRegistration(getRegistry().registerMenuItem(TabShellKey.INSTANCE, FileMenuKeys.MAIN, f, 1000));
//    }
}
