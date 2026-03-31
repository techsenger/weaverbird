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
//import com.techsenger.tabshell.kit.text.viewer.ViewerTabHelper;
//
///**
// *
// * @author Pavel Castornii
// */
//class LogTabHelper<T extends AbstractLogTabView<?>> extends ViewerTabHelper<T> {
//
//    LogTabHelper(T shellTabView) {
//        super(shellTabView);
//    }
//
//    public void openLogEventDialog(LogEventDialogViewModel dialogViewModel) {
//        var dialogView = new LogEventDialogView(dialogViewModel);
//        dialogView.initialize();
//        getView().getDialogManager().openDialog(dialogView);
//    }
//}
