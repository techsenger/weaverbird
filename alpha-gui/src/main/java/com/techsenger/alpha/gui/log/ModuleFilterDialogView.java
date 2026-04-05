package com.techsenger.alpha.gui.log;

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
//import com.techsenger.toolkit.fx.utils.ButtonUtils;
//import com.techsenger.toolkit.fx.utils.NodeUtils;
//import javafx.geometry.Insets;
//import javafx.scene.control.CheckBox;
//import javafx.scene.control.TreeCell;
//import javafx.scene.control.TreeItem;
//import javafx.scene.control.TreeView;
//import javafx.scene.layout.Priority;
//import javafx.scene.layout.VBox;
//import javafx.util.Callback;
//
///**
// *
// * @author Pavel Castornii
// */
//class ModuleFilterDialogView extends AbstractSimpleDialogView<ModuleFilterDialogViewModel> {
//
//    private final TreeView<AbstractDialogElement> treeView = new TreeView<>();
//
//    ModuleFilterDialogView(ModuleFilterDialogViewModel viewModel) {
//        super(viewModel);
//    }
//
//    @Override
//    public void requestFocus() {
//        NodeUtils.requestFocus(getOkButton());
//    }
//
//    @Override
//    protected void build(ModuleFilterDialogViewModel viewModel) {
//        super.build(viewModel);
//
//        class CheckBoxTreeItem extends TreeItem<AbstractDialogElement> {
//
//            private final CheckBox checkBox = new CheckBox();
//
//            CheckBoxTreeItem(AbstractDialogElement t) {
//                super(t);
//                if (t != null) {
//                    checkBox.selectedProperty().bindBidirectional(t.selectedProperty());
//                }
//            }
//        }
//
//        this.treeView.setCellFactory(new Callback<>() {
//            @Override
//            public TreeCell<AbstractDialogElement> call(TreeView<AbstractDialogElement> param) {
//                return new TreeCell<>() {
//
//                    @Override
//                    protected void updateItem(AbstractDialogElement item, boolean empty) {
//                        super.updateItem(item, empty);
//                        if (item == null || empty) {
//                            setGraphic(null);
//                            setText(null);
//                        } else {
//                            setGraphic(((CheckBoxTreeItem) getTreeItem()).checkBox);
//                            setText(item.getName());
//                        }
//                    }
//                };
//            }
//        });
//
//        CheckBoxTreeItem rootItem = new CheckBoxTreeItem(null);
//        rootItem.setExpanded(true);
//        for (var l : viewModel.getLayers()) {
//            var layerItem = new CheckBoxTreeItem(l);
//            layerItem.checkBox.setOnAction(e -> {
//                var value = layerItem.checkBox.isSelected();
//                l.getModules().forEach(m -> m.selectedProperty().set(value));
//            });
//            layerItem.setExpanded(l.selectedProperty().get());
//            for (var m : l.getModules()) {
//                var moduleItem = new CheckBoxTreeItem(m);
//                layerItem.getChildren().add(moduleItem);
//                moduleItem.checkBox.setOnAction(e -> {
//                    var value = moduleItem.checkBox.isSelected();
//                    if (value) {
//                        layerItem.checkBox.setSelected(true);
//                    } else {
//                        var oneIsSelected = false;
//                        for (var mm : l.getModules()) {
//                            if (mm.selectedProperty().get()) {
//                                oneIsSelected = true;
//                                break;
//                            }
//                        }
//                        if (!oneIsSelected) {
//                            layerItem.checkBox.setSelected(false);
//                        }
//                    }
//                });
//            }
//            rootItem.getChildren().add(layerItem);
//        }
//        this.treeView.setShowRoot(false);
//        this.treeView.setRoot(rootItem);
//        this.treeView.getStyleClass().add(Styles.DENSE);
//        var wrapper = new VBox(this.treeView);
//        VBox.setVgrow(wrapper, Priority.ALWAYS);
//        VBox.setVgrow(this.treeView, Priority.ALWAYS);
//        wrapper.setPadding(new Insets(SizeConstants.INSET));
//        getContentPane().getChildren().addAll(wrapper, getButtonBox());
//        getButtonBox().getChildren().addAll(getCancelButton(), getOkButton());
//        ButtonUtils.makeEqualWidth(getCancelButton(), getOkButton());
//    }
//}
