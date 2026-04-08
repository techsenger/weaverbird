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

package com.techsenger.alpha.gui.settings;

import com.techsenger.tabshell.core.settings.AppearanceSettings;
import com.techsenger.tabshell.core.settings.DefaultAppearanceSettings;
import com.techsenger.tabshell.core.settings.Settings;

/**
 *
 * @author Pavel Castornii
 */
public class ConsoleSettings implements Settings {

    private final AppearanceSettings appearance = new DefaultAppearanceSettings();

    private final DiagramSettings diagram = new DiagramSettings();

    @Override
    public AppearanceSettings getAppearance() {
        return appearance;
    }

    public DiagramSettings getDiagram() {
        return diagram;
    }
}
