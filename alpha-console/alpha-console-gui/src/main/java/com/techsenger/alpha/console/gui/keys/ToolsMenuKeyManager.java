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

package com.techsenger.alpha.console.gui.keys;

import com.techsenger.alpha.console.gui.menu.FileMenuKeys;
import com.techsenger.alpha.console.gui.menu.ToolsMenuKeys;
import com.techsenger.tabshell.material.menu.MenuItemKey;
import com.techsenger.tabshell.material.menu.MenuKey;

/**
 * When console is used as a plugin then menu keys are used from other application. However, alpha components
 * must know what keys it can use, so this manager is used.
 *
 * @author Pavel Castornii
 */
public final class ToolsMenuKeyManager {

    private static MenuKey tools = ToolsMenuKeys.TOOLS;

    private static MenuItemKey shell = FileMenuKeys.SHELL;

    private static MenuItemKey diagrams = ToolsMenuKeys.DIAGRAMS;

    private static MenuItemKey memoryLog = ToolsMenuKeys.MEMORY_LOG;

    private static MenuItemKey fileLog = ToolsMenuKeys.FILE_LOG;

    public static MenuKey getTools() {
        return tools;
    }

    public static void setTools(MenuKey tools) {
        ToolsMenuKeyManager.tools = tools;
    }

    public static MenuItemKey getShell() {
        return shell;
    }

    public static void setShell(MenuItemKey shell) {
        ToolsMenuKeyManager.shell = shell;
    }

    public static MenuItemKey getDiagrams() {
        return diagrams;
    }

    public static void setDiagrams(MenuItemKey diagrams) {
        ToolsMenuKeyManager.diagrams = diagrams;
    }

    public static MenuItemKey getMemoryLog() {
        return memoryLog;
    }

    public static void setMemoryLog(MenuItemKey memoryLog) {
        ToolsMenuKeyManager.memoryLog = memoryLog;
    }

    public static MenuItemKey getFileLog() {
        return fileLog;
    }

    public static void setFileLog(MenuItemKey fileLog) {
        ToolsMenuKeyManager.fileLog = fileLog;
    }

    private ToolsMenuKeyManager() {
        //empty
    }
}
