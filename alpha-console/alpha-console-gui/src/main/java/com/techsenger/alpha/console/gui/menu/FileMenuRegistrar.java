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

package com.techsenger.alpha.console.gui.menu;

import com.techsenger.alpha.console.gui.settings.ConsoleSettings;
import com.techsenger.alpha.console.gui.settings.SettingsDialogView;
import com.techsenger.alpha.console.gui.settings.SettingsDialogViewModel;
import com.techsenger.alpha.console.gui.style.ConsoleIcons;
import com.techsenger.tabshell.core.TabShellKey;
import com.techsenger.tabshell.core.TabShellView;
import com.techsenger.tabshell.core.registry.ControlFactory;
import com.techsenger.tabshell.core.registry.ControlRegistry;
import com.techsenger.tabshell.material.icon.FontIconView;
import com.techsenger.tabshell.material.menu.KeyedMenuGroup;
import com.techsenger.tabshell.material.menu.KeyedMenuItem;

/**
 *
 * @author Pavel Castornii
 */
public class FileMenuRegistrar extends com.techsenger.tabshell.kit.core.menu.FileMenuRegistrar {

    public FileMenuRegistrar(ControlRegistry registry) {
        super(registry);
    }

    @Override
    public void register() {
        super.register();
        registerMainGroup();
        registerSettingsItem();
    }

    @Override
    protected void registerSaveFileAsItem() {
        //TODO
    }

    @Override
    protected void registerSaveFileItem() {
        //TODO
    }

    @Override
    protected void registerOpenFileItem() {
        //TODO
    }

    @Override
    protected void registerBaseFileGroup() {
        //TODO
    }

    private void registerMainGroup() {
        ControlFactory<KeyedMenuGroup> f = (v) -> {
            return new KeyedMenuGroup(FileMenuKeys.MAIN);
        };
        addRegistration(getRegistry().registerMenuGroup(TabShellKey.INSTANCE, FileMenuKeys.FILE, f, 0));
    }

    private void registerSettingsItem() {
        ControlFactory<KeyedMenuItem> f = (v) -> {
            var tabShellView = (TabShellView<?>) v;
            var item = new KeyedMenuItem(FileMenuKeys.SETTINGS, false, false, false, "_Settings",
                    new FontIconView(ConsoleIcons.SETTINGS));
            item.setOnAction((e) -> {
                var viewModel =
                        new SettingsDialogViewModel((ConsoleSettings) tabShellView.getViewModel().getSettings(),
                        tabShellView.getViewModel().getHistoryManager());
                var view = new SettingsDialogView(viewModel);
                view.initialize();
                tabShellView.getDialogManager().openDialog(view);
            });
            return item;

        };
        addRegistration(getRegistry().registerMenuItem(TabShellKey.INSTANCE, FileMenuKeys.MAIN, f, 1000));
    }
}
