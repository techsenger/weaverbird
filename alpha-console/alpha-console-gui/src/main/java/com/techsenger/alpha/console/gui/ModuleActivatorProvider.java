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

package com.techsenger.alpha.console.gui;

import com.techsenger.alpha.core.api.Framework;
import com.techsenger.alpha.core.spi.module.ModuleActivator;
import com.techsenger.alpha.core.spi.module.ModuleContext;
import javafx.application.Application;

/**
 *
 * @author Pavel Castornii
 */
public class ModuleActivatorProvider implements ModuleActivator {

    private static Framework framework;

    static Framework getFramework() {
        return framework;
    }

    @Override
    public void activate(ModuleContext context) throws Exception {
        framework = context.getFramework();
        // JavaFX Application.launch() blocks the calling thread until the application exits.
        // To prevent blocking the activator, we start it in a separate daemon thread.
        Thread thread = new Thread(() -> Application.launch(ConsoleApplication.class));
        thread.setDaemon(true);
        thread.start();




//        componentDescriptor = context.getComponent().getDescriptor();
//        var componentConfig = ModuleActivatorProvider.getComponentDescriptor().getConfig();
//        FxPlatform.runLaterAndWait(() -> {
//            //var settings = settingsFile.getSettings();
//            var stage = new Stage();
//            var shellView = new DefaultShellFxView<>(this, stage, IconStylesheetFactory.forAll());
//            var context = new DefaultShellContext(DemoSettings.createSettings(),
//            var tabShellViewModel = new DefaultTabShellViewModel(settings, new DefaultHistoryManager(historyFile));
//            var stylesheets = List.of(
//                StyleClasses.class.getResource("base.css").toExternalForm(),
//                CoreIcons.class.getResource("icons.css").toExternalForm(),
//                TextIcons.class.getResource("icons.css").toExternalForm(),
//                DialogIcons.class.getResource("icons.css").toExternalForm(),
//                ConsoleIcons.class.getResource("icons.css").toExternalForm()
//            );
//            this.tabShellView = new DefaultTabShellView(stylesheets, tabShellViewModel);
//            tabShellView.initialize();
//            tabShellViewModel.setTitle("Techsenger Alpha Console");
//            ValueUtils.callAndAddListener(settings.getAppearance().themeProperty(), (ov, oldV, newV) -> {
//                Font font = Font.font("Material Design Icons", 32);
//                var image = ImageUtils.createIcon(String.format("%c", 0xF002B), font,
//                        ColorUtils.toColor(newV.getPalette().getDefaultFgColor()), Color.TRANSPARENT,
//                        -6, 28, 22, 34);
//                tabShellViewModel.setIcon(new ImageIcon(image));
//            });
//            tabShellViewModel.setOnClosed(() -> {
//                try {
//                    doClose();
//                } catch (Exception ex) {
//                    logger.error("Error closing console", ex);
//                }
//            });
//            ShellTabViewModel shellViewModel = new ShellTabViewModel(tabShellView.getViewModel());
//            ShellTabView shellView = new ShellTabView(tabShellView, shellViewModel);
//            shellView.initialize();
//
//            var controlRegistry = new ControlRegistry();
//            var controlRegistrators = List.of(
//                    new FileMenuRegistrar(controlRegistry),
//                    new EditMenuRegistrar(controlRegistry),
//                    new ToolsMenuRegistrar(controlRegistry),
//                    new HelpMenuRegistrar(controlRegistry));
//            controlRegistrators.forEach(r -> r.register());
//            tabShellView.upgradeMenuBar(controlRegistry);
//            tabShellView.openTab(shellView);
//        });

    }

    @Override
    public void deactivate(ModuleContext context) throws Exception {
//        //even if the console hasn't been created it is very easy to create it as all fields initialized in open()
//        var console = (ConsoleProvider) ConsoleProvider.provider();
//        if (console.isOpen()) {
//            console.close();
//        }
    }
}
