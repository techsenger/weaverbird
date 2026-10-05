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

package com.techsenger.weaverbird.gui.internal;

import com.techsenger.shellfx.core.ShellView;
import com.techsenger.shellfx.core.registry.AbstractSlotRegistrar;

/**
 * Builds the tree of the slots of the Weaverbird shell: which menus the menu bar has, and which groups each
 * menu has.
 *
 * @author Pavel Castornii
 */
public class ModuleSlotRegistrar extends AbstractSlotRegistrar {

    public ModuleSlotRegistrar(ShellView<?> shell) {
        super(shell.getContext().getSlotRegistry());
    }

    @Override
    public void register() {
        register(Slots.MAIN_MENU, 0, Slots.FileMenu.MENU);
        register(Slots.FileMenu.MENU, 0, Slots.FileMenu.MAIN);
    }
}
