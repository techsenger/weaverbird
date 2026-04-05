package com.techsenger.alpha.gui.file;

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
//package com.techsenger.alpha.console.gui.file;
//
//import com.techsenger.tabshell.kit.core.file.FileType;
//import javafx.stage.FileChooser;
//
///**
// *
// * @author Pavel Castornii
// */
//public enum SupportedFileType implements FileType {
//
//    XML("xml", new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml")),
//
//    SCRIPT("script", new FileChooser.ExtensionFilter("Script files (*.script)", "*.script")),
//
//    TEXT("txt", new FileChooser.ExtensionFilter("Text files (*.txt)", "*.txt")),
//
//    LOG("log", new FileChooser.ExtensionFilter("Log files (*.log)", "*.log")),
//
//    PNG("png", new FileChooser.ExtensionFilter("Text files (*.png)", "*.png"));
//
//    private final String extension;
//
//    private final FileChooser.ExtensionFilter extensionFilter;
//
//    SupportedFileType(String extension, FileChooser.ExtensionFilter extensionFilter) {
//        this.extension = extension;
//        this.extensionFilter = extensionFilter;
//    }
//
//    @Override
//    public String getExtension() {
//        return extension;
//    }
//
//    @Override
//    public FileChooser.ExtensionFilter getExtensionFilter() {
//        return extensionFilter;
//    }
//}
