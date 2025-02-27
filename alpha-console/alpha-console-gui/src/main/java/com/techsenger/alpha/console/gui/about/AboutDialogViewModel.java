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

package com.techsenger.alpha.console.gui.about;

import com.techsenger.alpha.api.Framework;
import com.techsenger.alpha.console.gui.keys.ConsoleComponentKeys;
import com.techsenger.tabshell.core.dialog.DialogKey;
import com.techsenger.tabshell.core.dialog.DialogScope;
import com.techsenger.tabshell.core.settings.AppearanceSettings;
import com.techsenger.tabshell.kit.dialog.AbstractSimpleDialogViewModel;

/**
 *
 * @author Pavel Castornii
 */
public class AboutDialogViewModel extends AbstractSimpleDialogViewModel {

    static final String TITLE =  "Techsenger Alpha\n";

    static final String VERSION = "Version " + Framework.getVersion().getFull() + "\n\n";

    static final String COPYRIGHT = "Copyright 2018-2025 Pavel Castornii. All rights reserved.\n\n";

    static final String LICENSE = "This software is licensed under the Apache License, Version 2.0."
            + " It includes third-party libraries and components that are distributed under their own licenses."
            + " To view which third-party libraries are used, refer to configuration files.\n\n";

    static final String SITE = "For more information visit ";

    static final String URL = "https://github.com/techsenger/alpha";

    private final AppearanceSettings tabShellSettings;

    public AboutDialogViewModel(AppearanceSettings tabShellSettings) {
        super(DialogScope.SHELL, false);
        this.tabShellSettings = tabShellSettings;
        setTitle("About");
        setPrefWidth(600);
    }

    @Override
    public DialogKey getKey() {
        return ConsoleComponentKeys.ABOUT_DIALOG;
    }

    public AppearanceSettings getTabShellSettings() {
        return tabShellSettings;
    }
}
