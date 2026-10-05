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
import com.techsenger.shellfx.material.slot.GroupSlot;
import com.techsenger.shellfx.material.slot.MenuBarSlot;
import com.techsenger.shellfx.material.slot.MenuSlot;
import javafx.scene.control.MenuItem;

/**
 * The slots - the menu bar, its menus, and their groups - the Weaverbird application's shell offers.
 *
 * @author Pavel Castornii
 */
public final class Slots {

    public static final class FileMenu {

        public static final MenuSlot<ShellView<?>> MENU = new MenuSlot<>(ShellView.class, "File");

        public static final GroupSlot<ShellView<?>, MenuItem> MAIN = new GroupSlot<>(ShellView.class, "Main");

        private FileMenu() {
            // empty
        }
    }

    /**
     * The menu bar of the shell; the File menu is put into it.
     */
    public static final MenuBarSlot<ShellView<?>> MAIN_MENU = new MenuBarSlot<>(ShellView.class, "MainMenu");

    private Slots() {
        // empty
    }
}
