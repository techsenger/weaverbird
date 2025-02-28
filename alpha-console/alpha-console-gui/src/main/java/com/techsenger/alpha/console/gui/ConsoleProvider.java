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

package com.techsenger.alpha.console.gui;

import com.techsenger.alpha.api.Framework;
import com.techsenger.alpha.api.FrameworkMode;
import com.techsenger.alpha.console.gui.menu.EditMenuRegistrar;
import com.techsenger.alpha.console.gui.menu.FileMenuRegistrar;
import com.techsenger.alpha.console.gui.menu.HelpMenuRegistrar;
import com.techsenger.alpha.console.gui.menu.ToolsMenuRegistrar;
import com.techsenger.alpha.console.gui.settings.ConsoleSettings;
import com.techsenger.alpha.console.gui.shell.ShellTabView;
import com.techsenger.alpha.console.gui.shell.ShellTabViewModel;
import com.techsenger.alpha.console.gui.style.ConsoleIcons;
import com.techsenger.alpha.spi.console.ConsoleService;
import com.techsenger.tabshell.core.DefaultTabShellView;
import com.techsenger.tabshell.core.DefaultTabShellViewModel;
import com.techsenger.tabshell.core.TabShellView;
import com.techsenger.tabshell.core.history.DefaultHistoryManager;
import com.techsenger.tabshell.core.history.HistoryFile;
import com.techsenger.tabshell.core.registry.ControlRegistry;
import com.techsenger.tabshell.kit.core.settings.SettingsFile;
import com.techsenger.tabshell.kit.core.style.CoreIcons;
import com.techsenger.tabshell.kit.core.style.StyleClasses;
import com.techsenger.tabshell.kit.dialog.style.DialogIcons;
import com.techsenger.tabshell.kit.text.style.TextIcons;
import com.techsenger.tabshell.material.icon.ImageIcon;
import com.techsenger.toolkit.core.SingletonFactory;
import com.techsenger.toolkit.fx.FxPlatform;
import com.techsenger.toolkit.fx.color.ColorUtils;
import com.techsenger.toolkit.fx.utils.ImageUtils;
import com.techsenger.toolkit.fx.value.ValueUtils;
import java.io.File;
import java.util.List;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
public final class ConsoleProvider implements ConsoleService {

    private static final SingletonFactory<ConsoleService> singletonFactory =
            new SingletonFactory<>(() -> new ConsoleProvider());

    public static ConsoleService provider() {
        return singletonFactory.singleton();
    }

    private static final Logger logger = LoggerFactory.getLogger(ConsoleProvider.class);

    private SettingsFile settingsFile;

    private HistoryFile historyFile;

    private TabShellView<?> tabShellView;

    /**
     * Opens console.
     * @throws Exception
     */
    @Override
    public synchronized void open() throws Exception {
        if (tabShellView != null) {
            throw new IllegalStateException("Console is already open");
        }

        var componentConfig = ModuleActivatorProvider.getComponentDescriptor().getConfig();
        var pathResolver = Framework.getComponentManager().getPathResolver();
        var dataPath = pathResolver.resolveDataDirectoryPath(componentConfig);

        var ttfPath = dataPath.resolve("dejavu-fonts-ttf-2.37").resolve("ttf");
        for (File file : ttfPath.toFile().listFiles()) {
            Font.loadFont(file.toURI().toURL().toExternalForm(), 10);
        }

        var historyPath = dataPath.resolve("history.ser");
        this.historyFile = new HistoryFile(historyPath);
        historyFile.read();

        var settingsPath = pathResolver.resolveSettingsFilePath(componentConfig);
        settingsFile = new SettingsFile<ConsoleSettings>(ConsoleSettings.class, settingsPath);
        settingsFile.read();

        FxPlatform.runLaterAndWait(() -> {
            var settings = settingsFile.getSettings();
            var tabShellViewModel = new DefaultTabShellViewModel(settings, new DefaultHistoryManager(historyFile));
            var stylesheets = List.of(
                StyleClasses.class.getResource("base.css").toExternalForm(),
                CoreIcons.class.getResource("icons.css").toExternalForm(),
                TextIcons.class.getResource("icons.css").toExternalForm(),
                DialogIcons.class.getResource("icons.css").toExternalForm(),
                ConsoleIcons.class.getResource("icons.css").toExternalForm()
            );
            this.tabShellView = new DefaultTabShellView(stylesheets, tabShellViewModel);
            tabShellView.initialize();
            tabShellViewModel.setTitle("Techsenger Alpha Console");
            ValueUtils.callAndAddListener(settings.getAppearance().themeProperty(), (ov, oldV, newV) -> {
                Font font = Font.font("Material Design Icons", 32);
                var image = ImageUtils.createIcon(String.format("%c", 0xF002B), font,
                        ColorUtils.toColor(newV.getPalette().getDefaultFgColor()), Color.TRANSPARENT,
                        -6, 28, 22, 34);
                tabShellViewModel.setIcon(new ImageIcon(image));
            });
            tabShellViewModel.setOnClosed(() -> {
                try {
                    doClose();
                } catch (Exception ex) {
                    logger.error("Error closing console", ex);
                }
            });
            ShellTabViewModel shellViewModel = new ShellTabViewModel(tabShellView.getViewModel());
            ShellTabView shellView = new ShellTabView(tabShellView, shellViewModel);
            shellView.initialize();

            var controlRegistry = new ControlRegistry();
            var controlRegistrators = List.of(
                    new FileMenuRegistrar(controlRegistry),
                    new EditMenuRegistrar(controlRegistry),
                    new ToolsMenuRegistrar(controlRegistry),
                    new HelpMenuRegistrar(controlRegistry));
            controlRegistrators.forEach(r -> r.register());
            tabShellView.upgradeMenuBar(controlRegistry);
            tabShellView.openTab(shellView);
        });
    }

    /**
     * The most important thing about close method is that it can be closed from
     * one of the threads - console loop thread or some another thread.
     *
     * Besides console can be closed via TabShell or via this method.
     *
     * So, we have two variants of closing - via flag and via interrupt.
     */
    @Override
    public synchronized void close() throws Exception {
        if (this.tabShellView != null) {
            Platform.runLater(() -> {
                //latch is not used here because user can cancel closing.
                tabShellView.close();
            });
        } else {
            throw new IllegalStateException("Console is not open");
        }
    }

    private void doClose() throws Exception {
        this.settingsFile.write();
        this.settingsFile = null;
        this.historyFile.write();
        this.historyFile = null;
        this.tabShellView = null;
        logger.debug("Console was closed");
        if (Framework.getMode() == FrameworkMode.CLIENT) {
            Framework.shutdown();
        }
    }

    boolean isOpen() {
        return this.tabShellView != null;
    }

}
