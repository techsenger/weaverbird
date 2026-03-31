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
//package com.techsenger.alpha.console.gui.log;
//
//import atlantafx.base.theme.Styles;
//import com.techsenger.tabshell.core.style.SizeConstants;
//import com.techsenger.tabshell.kit.dialog.AbstractSimpleDialogView;
//import javafx.geometry.Insets;
//import javafx.geometry.VPos;
//import javafx.scene.control.ComboBox;
//import javafx.scene.control.Label;
//import javafx.scene.control.TextArea;
//import javafx.scene.layout.ColumnConstraints;
//import javafx.scene.layout.GridPane;
//import javafx.scene.layout.Priority;
//import javafx.scene.layout.Region;
//import javafx.scene.layout.VBox;
//
///**
// *
// * @author Pavel Castornii
// */
//class LogEventDialogView extends AbstractSimpleDialogView<LogEventDialogViewModel> {
//
//    private final Label levelLabel = new Label("Level");
//
//    private final ComboBox<String> levelComboBox = new ComboBox<>();
//
//    private final Label messageLabel = new Label("Message");
//
//    private final TextArea messageTextArea = new TextArea();
//
//    LogEventDialogView(LogEventDialogViewModel viewModel) {
//        super(viewModel);
//    }
//
//    @Override
//    public LogEventDialogViewModel getViewModel() {
//        return (LogEventDialogViewModel) super.getViewModel();
//    }
//
//    public ComboBox<String> getLevelComboBox() {
//        return levelComboBox;
//    }
//
//    public TextArea getMessageTextArea() {
//        return messageTextArea;
//    }
//
//    @Override
//    public void requestFocus() {
//        messageTextArea.requestFocus();
//    }
//
//    @Override
//    protected void build(LogEventDialogViewModel viewModel) {
//        super.build(viewModel);
//        GridPane gridpane = new GridPane();
//        gridpane.setHgap(SizeConstants.INSET);
//        gridpane.setVgap(SizeConstants.INSET);
//        var c0 = new ColumnConstraints();
//        var c1 = new ColumnConstraints();
//        c1.setHgrow(Priority.ALWAYS);
//        gridpane.getColumnConstraints().addAll(c0, c1);
//
//        levelLabel.setMinWidth(Region.USE_PREF_SIZE);
//        gridpane.add(levelLabel, 0, 0);
//        levelComboBox.setMaxWidth(Double.MAX_VALUE);
//        GridPane.setHgrow(levelComboBox, Priority.ALWAYS);
//        gridpane.add(levelComboBox, 1, 0);
//        levelComboBox.getStyleClass().add(Styles.DENSE);
//
//        GridPane.setValignment(messageLabel, VPos.TOP);
//        messageLabel.setMinWidth(Region.USE_PREF_SIZE);
//        gridpane.add(messageLabel, 0, 1);
//        GridPane.setHgrow(messageTextArea, Priority.ALWAYS);
//        GridPane.setVgrow(messageTextArea, Priority.ALWAYS);
//        gridpane.add(messageTextArea, 1, 1);
//        VBox.setVgrow(gridpane, Priority.ALWAYS);
//
//        var content = new VBox();
//        VBox.setVgrow(content, Priority.ALWAYS);
//        content.setPadding(new Insets(SizeConstants.INSET, SizeConstants.INSET, 0, SizeConstants.INSET));
//        content.getChildren().addAll(gridpane);
//        getButtonBox().getChildren().add(getOkButton());
//        this.getContentPane().getChildren().addAll(content, getButtonBox());
//
//        this.levelComboBox.setItems(viewModel.getLevels());
//        this.levelComboBox.getSelectionModel().select(3);
//        this.levelComboBox.getStyleClass().add(Styles.DENSE);
//    }
//
//    @Override
//    protected void bind(LogEventDialogViewModel viewModel) {
//        super.bind(viewModel);
//        viewModel.textProperty().bind(this.messageTextArea.textProperty());
//        viewModel.levelProperty().bind(this.levelComboBox.getSelectionModel().selectedItemProperty());
//    }
//}
