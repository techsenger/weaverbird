package com.techsenger.alpha.gui.utils;

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
//package com.techsenger.alpha.console.gui.utils;
//
//import com.techsenger.alpha.api.Framework;
//import com.techsenger.alpha.console.gui.diagram.DiagramTabView;
//import com.techsenger.alpha.console.gui.diagram.DiagramTabViewModel;
//import com.techsenger.alpha.console.gui.log.FileLogTabView;
//import com.techsenger.alpha.console.gui.log.FileLogTabViewModel;
//import com.techsenger.alpha.console.gui.log.MemoryLogTabView;
//import com.techsenger.alpha.console.gui.log.MemoryLogTabViewModel;
//import com.techsenger.alpha.console.gui.shell.ShellTabView;
//import com.techsenger.alpha.console.gui.shell.ShellTabViewModel;
//import com.techsenger.tabshell.core.TabShellView;
//
///**
// *
// * @author Pavel Castornii
// */
//public interface TabOpener {
//
//    default void openShellTab(TabShellView<?> tabShellView) {
//        var tabShellViewModel = tabShellView.getViewModel();
//        ShellTabViewModel viewModel = new ShellTabViewModel(tabShellViewModel);
//        ShellTabView view = new ShellTabView(tabShellView, viewModel);
//        view.initialize();
//        tabShellView.openTab(view);
//    }
//
//    default void openDiagramsTab(TabShellView<?> tabShellView) {
//        var tabShellViewModel = tabShellView.getViewModel();
//        DiagramTabViewModel viewModel = new DiagramTabViewModel(tabShellViewModel);
//        DiagramTabView view = new DiagramTabView(tabShellView, viewModel);
//        view.initialize();
//        tabShellView.openTab(view);
//    }
//
//    default void openMemoryLogTab(TabShellView<?> tabShellView) {
//        var tabShellViewModel = tabShellView.getViewModel();
//        MemoryLogTabViewModel viewModel = new MemoryLogTabViewModel(tabShellViewModel);
//        MemoryLogTabView view = new MemoryLogTabView(tabShellView, viewModel);
//        view.initialize();
//        tabShellView.openTab(view);
//        viewModel.startService();
//    }
//
//    default void openFileLogTab(TabShellView<?> tabShellView) {
////        FileChooser fileChooser = new FileChooser();
////        fileChooser.setTitle("Open Log File");
////        fileChooser.setInitialDirectory(Framework.getPathProvider().getLogDirectoryPath().toFile());
////        var file = fileChooser.showOpenDialog(tabShellView.getStage());
////        if (file != null) {
//            var tabShellViewModel = tabShellView.getViewModel();
//            FileLogTabViewModel viewModel = new FileLogTabViewModel(tabShellViewModel,
//                    Framework.getPathManager().getLogFilePath(), true);
//            FileLogTabView view = new FileLogTabView(tabShellView, viewModel);
//            view.initialize();
//            tabShellView.openTab(view);
//            viewModel.startService();
////        }
//    }
//}
