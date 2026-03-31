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
//package com.techsenger.alpha.console.gui.about;
//
//import com.techsenger.tabshell.core.style.SizeConstants;
//import com.techsenger.tabshell.kit.dialog.AbstractSimpleDialogView;
//import com.techsenger.toolkit.fx.utils.NodeUtils;
//import java.awt.Desktop;
//import java.awt.EventQueue;
//import java.net.URI;
//import javafx.geometry.Insets;
//import javafx.scene.control.Hyperlink;
//import javafx.scene.layout.Priority;
//import javafx.scene.layout.VBox;
//import javafx.scene.text.Text;
//import javafx.scene.text.TextFlow;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
///**
// *
// * @author Pavel Castornii
// */
//public class AboutDialogView extends AbstractSimpleDialogView<AboutDialogViewModel> {
//
//    private static final Logger logger = LoggerFactory.getLogger(AboutDialogView.class);
//
//    public AboutDialogView(AboutDialogViewModel viewModel) {
//        super(viewModel);
//    }
//
//    @Override
//    public void requestFocus() {
//        NodeUtils.requestFocus(getContentPane());
//    }
//
//    @Override
//    protected void build(AboutDialogViewModel viewModel) {
//        super.build(viewModel);
//
//        var box = new VBox();
//        VBox.setVgrow(box, Priority.ALWAYS);
//
//        var title = new Text(viewModel.TITLE);
//        title.setStyle("-fx-font-size: 1.6em;");
//        var titleSpace = new Text("\n");
//        titleSpace.setStyle("-fx-font-size: 0.6em");
//        var version = new Text(viewModel.VERSION);
//
//        var copyRight = new Text(viewModel.COPYRIGHT);
//        var license = new Text(viewModel.LICENSE);
//
//        var site = new Text(viewModel.SITE);
//        var link = new Hyperlink(AboutDialogViewModel.URL);
//        link.setOnAction(e -> {
//            EventQueue.invokeLater(() -> {
//                try {
//                    Desktop.getDesktop().browse(new URI(AboutDialogViewModel.URL));
//                } catch (Exception ex) {
//                    logger.debug("Error opening the link", ex);
//                }
//            });
//        });
//
//        TextFlow textFlow = new TextFlow(title, titleSpace, version, copyRight, license, site, link);
//        textFlow.setLineSpacing(viewModel.getTabShellSettings().getRegularFont().getSize() * 0.25);
//        textFlow.setPadding(new Insets(SizeConstants.INSET));
//
//
//        VBox.setVgrow(textFlow, Priority.ALWAYS);
//        box.getChildren().addAll(textFlow);
//        getContentPane().getChildren().addAll(box, getButtonBox());
//        getButtonBox().getChildren().addAll(getOkButton());
//    }
//}
