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

package com.techsenger.alpha.console.gui.menu;

import com.techsenger.alpha.console.gui.about.AboutDialogView;
import com.techsenger.alpha.console.gui.about.AboutDialogViewModel;
import com.techsenger.tabshell.core.TabShellKey;
import com.techsenger.tabshell.core.TabShellView;
import com.techsenger.tabshell.core.registry.AbstractControlRegistrar;
import com.techsenger.tabshell.core.registry.ControlFactory;
import com.techsenger.tabshell.core.registry.ControlRegistry;
import com.techsenger.tabshell.material.menu.KeyedMenu;
import com.techsenger.tabshell.material.menu.KeyedMenuGroup;
import com.techsenger.tabshell.material.menu.KeyedMenuItem;

/**
 *
 * @author Pavel Castornii
 */
public class HelpMenuRegistrar extends AbstractControlRegistrar {

    public HelpMenuRegistrar(ControlRegistry registry) {
        super(registry);
    }

    @Override
    public void register() {
        registerHelpMenu();
        registerDefaultGroup();
        registerAboutItem();
    }

    protected void registerHelpMenu() {
        ControlFactory<KeyedMenu> f = (v) -> {
            return new KeyedMenu(HelpMenuKeys.HELP, "_Help");
        };
        addRegistration(getRegistry().registerMenu(TabShellKey.INSTANCE, null, f, 1000));
    }

    protected void registerDefaultGroup() {
        ControlFactory<KeyedMenuGroup> f = (v) -> {
            return new KeyedMenuGroup(HelpMenuKeys.DEFAULT);
        };
        addRegistration(getRegistry().registerMenuGroup(TabShellKey.INSTANCE, HelpMenuKeys.HELP, f, 10000));
    }

    protected void registerAboutItem() {
        ControlFactory<KeyedMenuItem> f = (v) -> {
            var item = new KeyedMenuItem(HelpMenuKeys.ABOUT, false, false, false, "_About");
            var tabShellView = (TabShellView<?>) v;
            item.setOnAction((e) -> {
                var dialogModelView = new AboutDialogViewModel(tabShellView.getViewModel()
                        .getSettings().getAppearance());
                var dialogView = new AboutDialogView(dialogModelView);
                dialogView.initialize();
                tabShellView.getDialogManager().openDialog(dialogView);
            });
            return item;
        };
        addRegistration(getRegistry().registerMenuItem(TabShellKey.INSTANCE, HelpMenuKeys.DEFAULT, f, 100));
    }
}
