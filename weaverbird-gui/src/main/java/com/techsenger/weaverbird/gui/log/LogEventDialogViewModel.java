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
//import com.techsenger.weaverbird.console.gui.keys.ConsoleComponentKeys;
//import com.techsenger.tabshell.core.dialog.DialogKey;
//import com.techsenger.tabshell.core.dialog.DialogScope;
//import com.techsenger.tabshell.kit.dialog.AbstractSimpleDialogViewModel;
//import java.util.List;
//import javafx.beans.property.ObjectProperty;
//import javafx.beans.property.SimpleObjectProperty;
//import javafx.beans.property.SimpleStringProperty;
//import javafx.beans.property.StringProperty;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//
///**
// *
// * @author Pavel Castornii
// */
//class LogEventDialogViewModel extends AbstractSimpleDialogViewModel {
//
//    private ObservableList<String> levels =
//            FXCollections.observableArrayList(List.of("FATAL", "ERROR", "WARN", "INFO", "DEBUG", "TRACE"));
//
//    private ObjectProperty<String> level = new SimpleObjectProperty<>();
//
//    private StringProperty text = new SimpleStringProperty();
//
//    LogEventDialogViewModel() {
//        super(DialogScope.TAB, false);
//        prefWidthProperty().set(500);
//        prefHeightProperty().set(400);
//        titleProperty().set("New Log Event");
//    }
//
//    @Override
//    public DialogKey getKey() {
//        return ConsoleComponentKeys.LOG_EVENT_DIALOG;
//    }
//
//    public ObservableList<String> getLevels() {
//        return levels;
//    }
//
//    public ObjectProperty<String> levelProperty() {
//        return level;
//    }
//
//    public StringProperty textProperty() {
//        return text;
//    }
//}
