package com.techsenger.alpha.console.gui;

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
//import com.techsenger.alpha.console.gui.textstyle.StyledText;
//import com.techsenger.alpha.core.api.message.AbstractMessagePrinter;
//import com.techsenger.tabshell.material.textarea.ExtendedTextArea;
//import com.techsenger.tabshell.material.textarea.TextAreaStyle;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//import org.fxmisc.flowless.VirtualizedScrollPane;
//import org.slf4j.event.Level;
//
///**
// *
// * @author Pavel Castornii
// */
//class LogMessagePrinter extends AbstractMessagePrinter implements LogPrinterView {
//
//    private final FileLogParser parser = new FileLogParser();
//
//    private final Map<Level, String> styleClassesByLevel = LogStyleUtils.createStyleClassesByLevel();
//
//    private final Map<String, Collection<TextAreaStyle>> styleCollectionsByClassName =
//            styleClassesByLevel.values().stream()
//                    .collect(Collectors.toMap(v -> v, v -> Collections.singleton(new TextAreaStyle(v))));
//
//    private final ExtendedTextArea textArea;
//
//    private final VirtualizedScrollPane scrollPane;
//
//    LogMessagePrinter(ExtendedTextArea textArea, VirtualizedScrollPane scrollPane) {
//        this.textArea = textArea;
//        this.scrollPane = scrollPane;
//    }
//
//    @Override
//    public void printlnMessage(String message) {
//        //message is one line from file
//        var fragments = parser.parse(message);
//        if (!fragments.isEmpty()) {
//            processLogFragments(fragments);
//            textArea.appendText("\n");
//        }
//    }
//
//    @Override
//    public void printlnError(String error) {
//        textArea.append(error + "\n", new TextAreaStyle("error"));
//    }
//
//    private void processLogFragments(List<FileLogFragment> fragments) {
//        List<StyledText> currentTexts = new ArrayList<>();
//        for (var fragment: fragments) {
//            if (fragment.getLevel() != null) {
//                var cssStyle = styleClassesByLevel.get(fragment.getLevel());
//                var styledText = new StyledText(cssStyle, fragment.getText());
//                currentTexts.add(styledText);
//            } else {
//                var styledText = new StyledText(null, fragment.getText());
//                currentTexts.add(styledText);
//            }
//        }
//        printLogEvents(currentTexts, textArea, scrollPane, styleCollectionsByClassName);
//    }
//}
