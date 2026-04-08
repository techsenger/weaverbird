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

import com.techsenger.alpha.gui.menu.FileMenuRegistrar;
import com.techsenger.alpha.gui.settings.ConsoleSettings;
import com.techsenger.alpha.gui.settings.LayoutEngine;
import com.techsenger.alpha.gui.settings.LineType;
import com.techsenger.alpha.gui.style.ConsoleIcons;
import com.techsenger.tabshell.core.DefaultShellContext;
import com.techsenger.tabshell.core.DefaultShellFxView;
import com.techsenger.tabshell.core.DefaultShellPresenter;
import com.techsenger.tabshell.icons.IconStylesheetFactory;
import com.techsenger.tabshell.layout.tabhost.TabHostFxView;
import com.techsenger.tabshell.layout.tabhost.TabHostPresenter;
import com.techsenger.tabshell.material.style.Stylesheet;
import com.techsenger.tabshell.material.theme.AtlantaFxTheme;
import com.techsenger.toolkit.fx.color.ColorUtils;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 *
 * @author Pavel Castornii
 */
public class AlphaApplication extends Application {

    public static ConsoleSettings createSettings() {
        var settings = new ConsoleSettings();
        var appearance = settings.getAppearance();
        appearance.setRegularFont(Font.font("System", 14));
        appearance.setMonospaceFont(Font.font("Monospace", 14));
        appearance.setTheme(AtlantaFxTheme.CUPERTINO_DARK);
        var diagram = settings.getDiagram();
        diagram.setLayoutEngine(LayoutEngine.SMETANA);
        diagram.setLimitSize(10000);
        diagram.setLineType(LineType.ORTHO);
        diagram.setLayerColor(ColorUtils.toColor(appearance.getTheme().getPalette().getAccent3Color()));
        diagram.setModuleColor(ColorUtils.toColor(appearance.getTheme().getPalette().getDanger3Color()));
        return settings;
    }

    @Override
    public void start(Stage stage) throws Exception {
        var stylesheets = new ArrayList<Stylesheet>(IconStylesheetFactory.forAll());
        stylesheets.add(new Stylesheet(ConsoleIcons.class.getResource("icons.css")));

        var shellView = new DefaultShellFxView<>(this, stage, stylesheets);
        var context = new DefaultShellContext(createSettings(), new InMemoryHistoryManager(), getHostServices());
        var shellPresenter = new DefaultShellPresenter<>(shellView, context);
        shellPresenter.setOnClose(() ->
                Thread.startVirtualThread(() -> ModuleActivatorProvider.getFramework().shutdown()));

        shellPresenter.initialize();
        shellView.setTitle("Alpha Framework");

        var workspaceView = new TabHostFxView<>(true);
        var workspacePresenter = new TabHostPresenter<>(workspaceView);
        workspacePresenter.initialize();
        shellView.getComposer().addWorkspace(workspaceView);

        var fileRegistrar = new FileMenuRegistrar(shellView, ModuleActivatorProvider.getFramework());
        fileRegistrar.register();

        shellView.upgradeMenuBar();
    }

}
