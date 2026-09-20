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

package com.techsenger.weaverbird.gui;

import com.techsenger.shellfx.core.ShellView;
import com.techsenger.shellfx.material.menu.DefaultMenuGroupName;
import com.techsenger.shellfx.material.menu.DefaultMenuName;
import com.techsenger.shellfx.material.menu.MenuGroupName;
import com.techsenger.shellfx.material.menu.MenuName;

/**
 * The menus and menu groups the Weaverbird application's shell menu bar offers.
 *
 * @author Pavel Castornii
 */
public final class ShellControls {

    public static final class FileMenu {

        public static final MenuName<ShellView<?>> NAME = createName();

        public static final MenuGroupName<ShellView<?>> MAIN = createGroupName("Main");

        private FileMenu() {
            // empty
        }
    }

    /**
     * The group File menu registers into, and that {@link com.techsenger.shellfx.core.DefaultShellView} treats
     * as the top-level group of its own menu bar.
     */
    public static final MenuGroupName<ShellView<?>> MAIN_MENU_GROUP = createGroupName("MainMenuGroup");

    private static MenuName<ShellView<?>> createName() {
        return new DefaultMenuName<>(ShellView.class);
    }

    private static MenuGroupName<ShellView<?>> createGroupName(String text) {
        return new DefaultMenuGroupName<>(ShellView.class, text);
    }

    private ShellControls() {
        // empty
    }
}
