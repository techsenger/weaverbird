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

package com.techsenger.alpha.console.gui.keys;

import com.techsenger.tabshell.core.dialog.DialogKey;
import com.techsenger.tabshell.core.node.NodeKey;
import com.techsenger.tabshell.core.pane.PaneKey;
import com.techsenger.tabshell.core.tab.ShellTabKey;
import com.techsenger.tabshell.core.tab.TabKey;

/**
 *
 * @author Pavel Castornii
 */
public interface ConsoleComponentKeys {

    DialogKey ABOUT_DIALOG = new DialogKey("About Dialog");

    DialogKey LAYER_DIALOG = new DialogKey("Layer Dialog");

    DialogKey MODULE_FILTER_DIALOG = new DialogKey("Module Filter Dialog");

    DialogKey LOG_EVENT_DIALOG = new DialogKey("Log Event Dialog");

    DialogKey SETTINGS_DIALOG = new DialogKey("Settings Dialog");

    DialogKey SESSIONS_DIALOG = new DialogKey("Network Dialog");

    DialogKey NEW_SESSION_DIALOG = new DialogKey("New Session Dialog");

    PaneKey SHELL_WINDOW = new PaneKey("Shell Window");

    ShellTabKey SHELL_TAB = new ShellTabKey("Shell Tab");

    ShellTabKey FILE_TAB = new ShellTabKey("File Tab");

    TabKey CONVERTED_XML_TAB = new TabKey("Converted XML Tab");

    ShellTabKey MEMORY_LOG_TAB = new ShellTabKey("Memory Log Tab");

    ShellTabKey FILE_LOG_TAB = new ShellTabKey("File Log Tab");

    ShellTabKey DIAGRAM_TAB = new ShellTabKey("Diagram Tab");

    NodeKey SESSIONS_TOOL_BAR = new NodeKey("Sessions Tool Bar");
}
