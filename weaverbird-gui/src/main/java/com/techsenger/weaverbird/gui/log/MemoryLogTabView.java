package com.techsenger.weaverbird.gui.log;

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
//package com.techsenger.weaverbird.console.gui.log;
//
//import com.techsenger.weaverbird.console.gui.style.ConsoleIcons;
//import com.techsenger.mvvm4fx.core.ComponentHelper;
//import com.techsenger.tabshell.core.TabShellView;
//import com.techsenger.tabshell.kit.material.textarea.ExtendedTextArea;
//import com.techsenger.tabshell.material.icon.FontIconView;
//import javafx.scene.control.Button;
//import javafx.scene.control.ToggleButton;
//
///**
// *
// * @author Pavel Castornii
// */
//public class MemoryLogTabView extends AbstractLogTabView<MemoryLogTabViewModel> {
//
//    private final ToggleButton moduleFilterButton = new ToggleButton("Module", new FontIconView(ConsoleIcons.FILTER));
//
//    private final Button moduleFilterSettingsButton = new Button(null,
//              new FontIconView(ConsoleIcons.FILTER_SETTINGS));
//
//    public MemoryLogTabView(TabShellView<?> tabShell, MemoryLogTabViewModel viewModel) {
//        super(tabShell, viewModel, new ExtendedTextArea());
//    }
//
//    @Override
//    public MemoryLogTabViewModel getViewModel() {
//        return (MemoryLogTabViewModel) super.getViewModel();
//    }
//
//    @Override
//    protected void build(MemoryLogTabViewModel viewModel) {
//        super.build(viewModel);
////        moduleFilterButton.setTooltip(new Tooltip("Filter by Layers and/or Modules"));
////        moduleFilterSettingsButton.setTooltip(new Tooltip("Select Layers and/or Modules"));
////        moduleFilterSettingsButton.getStyleClass().add(StyleClasses.ICONED_BUTTON);
////        getToolBar().getItems().addAll(new Separator(Orientation.VERTICAL), moduleFilterButton,
////                moduleFilterSettingsButton);
//    }
//
//    @Override
//    protected void addListeners(MemoryLogTabViewModel viewModel) {
//        super.addListeners(viewModel);
//        viewModel.getTextAreaClear().addListener((value) ->  {
//            if (value) {
//                this.getTextArea().clear();
//            }
//        });
//    }
//
//    @Override
//    protected void addHandlers(MemoryLogTabViewModel viewModel) {
//        super.addHandlers(viewModel);
//        getClearButton().setOnAction((e) -> viewModel.clearEvents());
//        this.moduleFilterSettingsButton.setOnAction(e -> viewModel.updateModuleFilterSettings());
//    }
//
//    @Override
//    protected void bind(MemoryLogTabViewModel viewModel) {
//        super.bind(viewModel);
//        moduleFilterButton.selectedProperty().bindBidirectional(viewModel.moduleFilterButtonSelectedProperty());
//    }
//
//    @Override
//    protected ComponentHelper<?> createComponentHelper() {
//        return new MemoryLogTabHelper(this);
//    }
//}
