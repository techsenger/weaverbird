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

package com.techsenger.weaverbird.gui.registrars;

import com.techsenger.shellfx.core.ShellView;
import com.techsenger.shellfx.core.registrars.AbstractControlRegistrar;
import com.techsenger.shellfx.core.registries.SimpleControlProvider;
import com.techsenger.shellfx.core.registries.SimpleGroupProvider;
import com.techsenger.shellfx.layout.tabhost.TabHostView;
import com.techsenger.shellfx.material.icon.FontIconView;
import com.techsenger.weaverbird.core.api.Framework;
import com.techsenger.weaverbird.gui.console.ConsoleTabConfig;
import com.techsenger.weaverbird.gui.console.ConsoleTabParams;
import com.techsenger.weaverbird.gui.console.ConsoleTabView;
import com.techsenger.weaverbird.gui.console.ConsoleTabViewModel;
import com.techsenger.weaverbird.gui.diagram.DiagramTabConfig;
import com.techsenger.weaverbird.gui.diagram.DiagramTabParams;
import com.techsenger.weaverbird.gui.diagram.DiagramTabView;
import com.techsenger.weaverbird.gui.diagram.DiagramTabViewModel;
import com.techsenger.weaverbird.gui.settings.ConsoleSettings;
import com.techsenger.weaverbird.gui.slots.ShellSlots;
import com.techsenger.weaverbird.gui.style.WeaverbirdIcons;
import com.techsenger.weaverbird.net.client.api.ClientService;
import com.techsenger.weaverbird.net.client.api.ClientServiceFactory;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

/**
 * Registers every menu, group, and item the Weaverbird application contributes.
 *
 * @author Pavel Castornii
 */
public class ModuleControlRegistrar extends AbstractControlRegistrar {

    private final ShellView<?> shell;

    private final Framework framework;

    private final ClientService client = ClientServiceFactory.create();

    public ModuleControlRegistrar(ShellView<?> shell, Framework framework) {
        super(shell.getContext().getControlRegistry());
        this.shell = shell;
        this.framework = framework;
    }

    @Override
    public void register() {
        registerMainMenu();
        registerFileMenu();
        registerConsoleItem();
        registerDiagramItem();
    }

    private void registerMainMenu() {
        register(ShellSlots.MAIN_MENU, () -> new SimpleControlProvider<>(new MenuBar()));
    }

    private void registerFileMenu() {
        register(ShellSlots.FileMenu.MENU, () -> new SimpleControlProvider<>(new Menu("_File")));
        register(ShellSlots.FileMenu.MAIN, SimpleGroupProvider::new);
    }

    private void registerConsoleItem() {
        register(ShellSlots.FileMenu.MAIN, 100, () -> new SimpleControlProvider<>(new MenuItem("C_onsole")) {

            @Override
            public void initialize(ShellView<?> v) {
                super.initialize(v);
                getControl().setGraphic(new FontIconView(WeaverbirdIcons.CONSOLE));
                getControl().setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
                getControl().setOnAction(e -> {
                    var config = shell.getViewModel().getContext().getConfigManager()
                            .getOrCreateConfig(ConsoleTabConfig.class, ConsoleTabConfig::new);
                    var params = new ConsoleTabParams(config, framework, client, null);
                    var consoleViewModel = new ConsoleTabViewModel<>(params);
                    var consoleView = new ConsoleTabView<>(consoleViewModel, shell);
                    consoleView.initialize();
                    TabHostView<?> workspace = (TabHostView<?>) shell.getComposer().getWorkspace();
                    workspace.getComposer().addTab(consoleView);
                });
            }
        });
    }

    private void registerDiagramItem() {
        register(ShellSlots.FileMenu.MAIN, 200, () -> new SimpleControlProvider<>(new MenuItem("D_iagrams")) {

            @Override
            public void initialize(ShellView<?> v) {
                super.initialize(v);
                getControl().setGraphic(new FontIconView(WeaverbirdIcons.DIAGRAMS));
                getControl().setAccelerator(new KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_DOWN));
                getControl().setOnAction(e -> {
                    var consoleSettings = (ConsoleSettings) shell.getViewModel().getContext().getSettings();
                    var config = shell.getViewModel().getContext().getConfigManager()
                            .getOrCreateConfig(DiagramTabConfig.class, DiagramTabConfig::new);
                    var params = new DiagramTabParams(config, framework, client, null,
                            consoleSettings.getDiagram());
                    var diagramViewModel = new DiagramTabViewModel<>(params);
                    var diagramView = new DiagramTabView<>(diagramViewModel, shell);
                    diagramView.initialize();
                    TabHostView<?> workspace = (TabHostView<?>) shell.getComposer().getWorkspace();
                    workspace.getComposer().addTab(diagramView);
                });
            }
        });
    }
}
