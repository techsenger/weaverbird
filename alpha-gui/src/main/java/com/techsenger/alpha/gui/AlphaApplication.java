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

package com.techsenger.alpha.gui;

import com.techsenger.alpha.gui.console.InMemoryHistoryManager;
import com.techsenger.alpha.gui.menu.FileMenuRegistrar;
import com.techsenger.tabshell.core.DefaultShellContext;
import com.techsenger.tabshell.core.DefaultShellFxView;
import com.techsenger.tabshell.core.DefaultShellPresenter;
import com.techsenger.tabshell.core.settings.AppearanceSettings;
import com.techsenger.tabshell.core.settings.DefaultAppearanceSettings;
import com.techsenger.tabshell.core.settings.Settings;
import com.techsenger.tabshell.icons.IconStylesheetFactory;
import com.techsenger.tabshell.layout.tabhost.TabHostFxView;
import com.techsenger.tabshell.layout.tabhost.TabHostPresenter;
import com.techsenger.tabshell.material.theme.AtlantaFxTheme;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 *
 * @author Pavel Castornii
 */
public class AlphaApplication extends Application {

    public static Settings createSettings() {
        var regularFont = Font.font("System", 14);
        var monospaceFont = Font.font("Monospace", 14);
        var appearance = new DefaultAppearanceSettings(regularFont, monospaceFont);
        appearance.setTheme(AtlantaFxTheme.CUPERTINO_DARK);
        var settings = new Settings() {

            @Override
            public AppearanceSettings getAppearance() {
                return appearance;
            }

        };
        return settings;
    }

    @Override
    public void start(Stage stage) throws Exception {
        var shellView = new DefaultShellFxView<>(this, stage, IconStylesheetFactory.forAll());
        var context = new DefaultShellContext(createSettings(), new InMemoryHistoryManager(), getHostServices());
        var shellPresenter = new DefaultShellPresenter<>(shellView, context);
        shellPresenter.setOnClose(() -> Platform.exit());
        shellPresenter.initialize();
        shellView.setTitle("Console");

        var workspaceView = new TabHostFxView<>(true);
        var workspacePresenter = new TabHostPresenter<>(workspaceView);
        workspacePresenter.initialize();
        shellView.getComposer().addWorkspace(workspaceView);

        var fileRegistrar = new FileMenuRegistrar(shellView, ModuleActivatorProvider.getFramework());
        fileRegistrar.register();

        shellView.upgradeMenuBar();
    }

}
