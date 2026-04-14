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
//import com.techsenger.tabshell.core.TabShellView;
//import com.techsenger.tabshell.kit.material.textarea.ExtendedTextArea;
//
///**
// *
// * @author Pavel Castornii
// */
//public class FileLogTabView extends AbstractLogTabView<FileLogTabViewModel> {
//
//    public FileLogTabView(TabShellView<?> tabShell, FileLogTabViewModel viewModel) {
//        super(tabShell, viewModel, new ExtendedTextArea());
//    }
//
//    @Override
//    public FileLogTabViewModel getViewModel() {
//        return (FileLogTabViewModel) super.getViewModel();
//    }
//
//    @Override
//    protected void build(FileLogTabViewModel viewModel) {
//        super.build(viewModel);
//        getLevelFilterButton().setDisable(true);
//        getFatalButton().setDisable(true);
//        getErrorButton().setDisable(true);
//        getWarnButton().setDisable(true);
//        getInfoButton().setDisable(true);
//        getDebugButton().setDisable(true);
//        getTraceButton().setDisable(true);
//        getOtherButton().setDisable(true);
//        getClearButton().setDisable(true);
//    }
//}
