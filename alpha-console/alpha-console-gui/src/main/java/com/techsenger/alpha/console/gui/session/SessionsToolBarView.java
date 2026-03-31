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
//package com.techsenger.alpha.console.gui.session;
//
//import atlantafx.base.theme.Styles;
//import com.techsenger.alpha.api.net.session.SessionDescriptor;
//import com.techsenger.alpha.console.gui.style.ConsoleIcons;
//import com.techsenger.tabshell.core.node.AbstractNodeView;
//import com.techsenger.tabshell.kit.core.style.StyleClasses;
//import com.techsenger.tabshell.material.icon.FontIconView;
//import javafx.scene.control.Button;
//import javafx.scene.control.ComboBox;
//import javafx.scene.control.Label;
//import javafx.scene.control.ToolBar;
//
///**
// *
// * @author Pavel Castornii
// */
//public class SessionsToolBarView extends AbstractNodeView<SessionsToolBarViewModel> {
//
//    private final Label sessionLabel = new Label("Session");
//
//    private final ComboBox<SessionDescriptor> sessionComboBox = new ComboBox<>();
//
//    private final Button refreshButton = new Button(null, new FontIconView(ConsoleIcons.REFRESH));
//
//    private final Button detachButton = new Button(null, new FontIconView(ConsoleIcons.DETACH_SESSION));
//
//    private final ToolBar toolBar = new ToolBar(sessionLabel, sessionComboBox, refreshButton, detachButton);
//
//    public SessionsToolBarView(SessionsToolBarViewModel viewModel) {
//        super(viewModel);
//    }
//
//    @Override
//    public void requestFocus() {
//
//    }
//
//    @Override
//    public ToolBar getNode() {
//        return toolBar;
//    }
//
//    @Override
//    protected void build(SessionsToolBarViewModel viewModel) {
//        super.build(viewModel);
//        toolBar.getStylesheets().add(SessionsToolBarView.class.getResource("tool-bar.css").toExternalForm());
//
//        sessionComboBox.getStyleClass().add(Styles.DENSE);
//        sessionComboBox.setItems(viewModel.getSessions());
//        sessionComboBox.setCellFactory(lv -> new ConnectionListCell<SessionDescriptor>());
//        sessionComboBox.setButtonCell(new ConnectionListCell<SessionDescriptor>());
//
//        refreshButton.getStyleClass().add(StyleClasses.ICONED_BUTTON);
//        detachButton.getStyleClass().add(StyleClasses.ICONED_BUTTON);
//    }
//
//    @Override
//    protected void bind(SessionsToolBarViewModel viewModel) {
//        super.bind(viewModel);
//        sessionComboBox.valueProperty().bindBidirectional(viewModel.internalSessionProperty());
//    }
//
//    @Override
//    protected void addHandlers(SessionsToolBarViewModel viewModel) {
//        super.addHandlers(viewModel);
//        refreshButton.setOnAction(e -> viewModel.refreshSessions());
//        detachButton.setOnAction(e -> viewModel.detachSession());
//    }
//}
