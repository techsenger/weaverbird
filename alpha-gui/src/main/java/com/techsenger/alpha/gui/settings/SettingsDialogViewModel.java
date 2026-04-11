package com.techsenger.alpha.gui.settings;

///*
// * Copyright 2018-2026 Pavel Castornii.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.techsenger.alpha.console.gui.settings;
//
//import com.techsenger.alpha.api.Framework;
//import com.techsenger.alpha.api.FrameworkMode;
//import com.techsenger.alpha.console.gui.keys.ConsoleComponentKeys;
//import com.techsenger.alpha.console.gui.style.ConsoleIcons;
//import com.techsenger.mvvm4fx.core.HistoryPolicy;
//import com.techsenger.tabshell.core.dialog.DialogKey;
//import com.techsenger.tabshell.core.dialog.DialogScope;
//import com.techsenger.tabshell.core.history.HistoryManager;
//import com.techsenger.tabshell.kit.dialog.page.AbstractPageDialogViewModel;
//import com.techsenger.tabshell.material.icon.FontIcon;
//import javafx.scene.text.Font;
//
///**
// *
// * @author Pavel Castornii
// */
//public class SettingsDialogViewModel extends AbstractPageDialogViewModel {
//
//    private final AppearancePageViewModel appearance;
//
//    private final ConnectionsPageViewModel connections;
//
//    private final DiagramPageViewModel diagram;
//
//    private final ConsoleSettings settings;
//
//    public SettingsDialogViewModel(ConsoleSettings settings, HistoryManager historyManager) {
//        super(DialogScope.SHELL, true);
//        this.settings = settings;
//        this.appearance = new AppearancePageViewModel(settings);
//        this.diagram = new DiagramPageViewModel(settings.getDiagram());
//        if (Framework.getMode() == FrameworkMode.CLIENT) {
//            this.connections = new ConnectionsPageViewModel(settings);
//        } else {
//            this.connections = null;
//        }
//        setTitle("Settings");
//        setHistoryPolicy(HistoryPolicy.APPEARANCE);
//        setHistoryProvider(() -> historyManager.getHistory(SettingsDialogHistory.class, SettingsDialogHistory::new));
//        setIcon(new FontIcon(ConsoleIcons.SETTINGS));
//        setMinWidth(600);
//        setMinHeight(400);
//        okActionProperty().set(() -> {
//            updateShellSettings();
//            close();
//        });
//    }
//
//    @Override
//    public DialogKey getKey() {
//        return ConsoleComponentKeys.SETTINGS_DIALOG;
//    }
//
//    AppearancePageViewModel getAppearance() {
//        return appearance;
//    }
//
//    ConnectionsPageViewModel getConnections() {
//        return connections;
//    }
//
//    DiagramPageViewModel getDiagram() {
//        return diagram;
//    }
//
//    private void updateShellSettings() {
//        //we don't update settings values via listeners as changes must happen only after ok clicking
//        settings.getAppearance().setTheme(appearance.themeProperty().get());
//        settings.getAppearance()
//                .setRegularFont(Font.font(appearance.fontFamilyProperty().get(),
//                          appearance.fontSizeProperty().get()));
//        settings.getViewer().setFont(Font
//                .font(appearance.viewerFontFamilyProperty().get(), appearance.viewerFontSizeProperty().get()));
//
//        var diagramSettings = settings.getDiagram();
//        diagramSettings.setLayerColor(this.diagram.layerColorProperty().get());
//        diagramSettings.setModuleColor(this.diagram.moduleColorProperty().get());
//        diagramSettings.setLineType(this.diagram.lineTypeProperty().get());
//        diagramSettings.setLayoutEngine(this.diagram.layoutEngineProperty().get());
//        diagramSettings.setLimitSize(this.diagram.limitSizeProperty().get());
//
//        if (this.connections != null) {
//            var connectionList = settings.getConnections();
//            connectionList.clear();
//            this.connections.getConnections().forEach(c -> connectionList.add(c));
//        }
//    }
//}
