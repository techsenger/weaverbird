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

import com.techsenger.shellfx.core.DefaultShellContext;
import com.techsenger.shellfx.core.DefaultShellParams;
import com.techsenger.shellfx.core.DefaultShellView;
import com.techsenger.shellfx.core.DefaultShellViewModel;
import com.techsenger.shellfx.core.history.InMemoryHistoryManager;
import com.techsenger.shellfx.core.registry.ControlRegistry;
import com.techsenger.shellfx.icons.Fonts;
import com.techsenger.shellfx.icons.IconStylesheetFactory;
import com.techsenger.shellfx.layout.tabhost.ProminentTabHostParams;
import com.techsenger.shellfx.layout.tabhost.ProminentTabHostView;
import com.techsenger.shellfx.layout.tabhost.ProminentTabHostViewModel;
import com.techsenger.shellfx.material.icon.FontIconView;
import com.techsenger.shellfx.material.style.Density;
import com.techsenger.shellfx.material.style.IconStylesheets;
import com.techsenger.shellfx.material.style.StyleClasses;
import com.techsenger.shellfx.material.theme.AtlantaFxTheme;
import com.techsenger.toolkit.fx.color.ColorUtils;
import com.techsenger.weaverbird.gui.controls.ModuleControlRegistrar;
import com.techsenger.weaverbird.gui.settings.ConsoleSettings;
import com.techsenger.weaverbird.gui.settings.LayoutEngine;
import com.techsenger.weaverbird.gui.settings.LineType;
import com.techsenger.weaverbird.gui.style.WeaverbirdIconStylesheets;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 *
 * @author Pavel Castornii
 */
public class WeaverbirdApplication extends Application {

    public static ConsoleSettings createSettings() {
        var settings = new ConsoleSettings();
        var appearance = settings.getAppearance();
        appearance.setRegularFont(Font.font("System", 14));
        appearance.setMonospaceFont(Font.font("Monospace", 14));
        appearance.setTheme(AtlantaFxTheme.CUPERTINO_DARK);
        appearance.setDensity(Density.S);
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
        FontIconView.setDefaultIconFont(Fonts.MATERIAL_DESIGN_ICONS.getFamily());
        IconStylesheets.addAll(IconStylesheetFactory.forAll());
        IconStylesheets.addAll(WeaverbirdIconStylesheets.getAll());

        var controlRegistry = new ControlRegistry();
        var context = new DefaultShellContext(createSettings(), new InMemoryHistoryManager(), getHostServices());
        var shellParams = new DefaultShellParams(context);
        var shellViewModel = new DefaultShellViewModel<>(shellParams);
        var shellView = new DefaultShellView<>(shellViewModel, this, stage, null, ShellControls.MAIN_MENU_GROUP,
                controlRegistry);
        shellView.initialize();
        shellViewModel.setOnClosed(() -> Platform.exit());
        shellViewModel.setTitle("Weaverbird Framework");
        shellView.getStage().getScene().getRoot().getStyleClass().add(StyleClasses.DENSITY_S);

        var workspaceParams = new ProminentTabHostParams(context.getSettings().getAppearance());
        var workspaceViewModel = new ProminentTabHostViewModel<>(workspaceParams);
        var workspaceView = new ProminentTabHostView<>(workspaceViewModel);
        workspaceView.initialize();
        shellView.getComposer().addWorkspace(workspaceView);

        var registrar = new ModuleControlRegistrar(shellView, ModuleActivatorProvider.getFramework());
        registrar.register();

        shellView.upgradeMenuBar();
        stage.show();
    }

}
