package com.techsenger.alpha.gui.console;

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
//package com.techsenger.alpha.console.gui.shell;
//
//import atlantafx.base.theme.Styles;
//import javafx.geometry.Insets;
//import javafx.geometry.Orientation;
//import javafx.scene.Node;
//import javafx.scene.control.ListView;
//import javafx.scene.control.SplitPane;
//import javafx.scene.layout.Priority;
//import javafx.scene.layout.StackPane;
//import javafx.scene.layout.VBox;
//
///**
// * The main idea of modal window - it is displayed as it were with focus, but focus is always on text area.
// *
// * @author Pavel Castornii
// */
//class AttributeWindowViewOld extends AbstractWindowView<AttributeWindowViewModelOld> {
//
//
//    AttributeWindowViewOld(AttributeWindowViewModelOld viewModel,
//            ExtendedTextArea mainTextArea) {
//        super(viewModel, mainTextArea);
//    }
//
//
//    public StyleClassedTextArea getTextArea() {
//        return textArea;
//    }
//
//    @Override
//    protected void bind(AttributeWindowViewModelOld viewModel) {
//        super.bind(viewModel);
//        listView.itemsProperty().bind(viewModel.currentAttributesProperty());
//    }
//
//    @Override
//    protected void addListeners(AttributeWindowViewModelOld viewModel) {
//        super.addListeners(viewModel);
//        listView.getSelectionModel().selectedItemProperty().addListener((ov, t, t1) ->
//                viewModel.doOnAttributeChanged(t1, ov.getValue()));
//        viewModel.selectedAttributeIndexProperty().addListener((ov, oldValue, newValue) -> {
//            listView.getSelectionModel().select(newValue.intValue());
//            var value = newValue.intValue();
//            if (!FxUtils.isItemVisible(listView, value)) {
//                listView.scrollTo(value);
//            }
//            this.printDescription(value);
//        });
//        listView.getSelectionModel().selectedIndexProperty().addListener((ov, oldValue, newValue) ->
//                viewModel.selectedAttributeIndexProperty().set(newValue.intValue()));
//    }
//
//    private void printDescription(int index) {
//        var textArea = this.getTextArea();
//        textArea.clear();
//        var currentDescriptions = this.getViewModel().getCurrentDescriptions();
//        if (index < 0 || currentDescriptions == null || currentDescriptions.size() <= index) {
//            return;
//        }
//        var descriptions = currentDescriptions.get(index);
//        for (var description: descriptions) {
//            if (description.getStyle() != null) {
//                textArea.append(description.getText(), description.getStyle());
//            } else {
//                textArea.appendText(description.getText());
//            }
//        }
//    }
//}
