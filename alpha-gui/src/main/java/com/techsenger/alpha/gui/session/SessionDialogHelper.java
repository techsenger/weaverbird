package com.techsenger.alpha.gui.session;

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
//import com.techsenger.mvvm4fx.core.AbstractComponentHelper;
//import com.techsenger.tabshell.core.dialog.DialogView;
//import com.techsenger.tabshell.kit.dialog.StandardDialogHelper;
//
///**
// *
// * @author Pavel Castornii
// */
//public class SessionDialogHelper extends AbstractComponentHelper<SessionDialogView<?>>
//        implements StandardDialogHelper<SessionDialogView<?>> {
//
//    public SessionDialogHelper(SessionDialogView view) {
//        super(view);
//    }
//
//    @Override
//    public void openDialog(DialogView<?> dialog) {
//        getView().openDialog(dialog);
//    }
//
//    public void openNewSessionDialog(NewSessionDialogViewModel viewModel) {
//        var view = new NewSessionDialogView(viewModel);
//        view.initialize();
//        openDialog(view);
//    }
//}
