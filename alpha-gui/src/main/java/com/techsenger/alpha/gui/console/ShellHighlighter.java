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
//import com.techsenger.alpha.api.Framework;
//import com.techsenger.alpha.api.FrameworkMode;
//import com.techsenger.alpha.api.executor.CommandSpecialSymbols;
//import com.techsenger.alpha.spi.console.AbstractCommandInfosConsumer;
//import com.techsenger.alpha.spi.console.CommandInfosManager;
//import com.techsenger.tabshell.kit.material.textarea.TextAreaStyle;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//import java.util.stream.Collectors;
//import org.fxmisc.richtext.model.StyleSpans;
//import org.fxmisc.richtext.model.StyleSpansBuilder;
//
///**
// *
// * @author Pavel Castornii
// */
//class ShellHighlighter extends AbstractCommandInfosConsumer {
//
//    private Pattern localPattern;
//
//    private Pattern remotePattern;
//
//    ShellHighlighter(CommandInfosManager manager) {
//        super(manager);
//    }
//
//    public StyleSpans<Collection<TextAreaStyle>> computeHighlighting(String text) {
//        Pattern pattern;
//        if (!isRemote(text)) {
//            updateLocalInfos();
//            pattern = localPattern;
//        } else {
//            updateRemoteInfos();
//            pattern = remotePattern;
//        }
//
//        Matcher matcher = pattern.matcher(text);
//        int lastKwEnd = 0;
//        StyleSpansBuilder<Collection<TextAreaStyle>> spansBuilder = new StyleSpansBuilder<>();
//        while (matcher.find()) {
//            TextAreaStyle style = null;
//            if (matcher.group("COMMAND") != null) {
//                style = new TextAreaStyle("command");
//            }
//            var start = matcher.start();
//            var end = matcher.end();
//            spansBuilder.add(Collections.emptyList(), start - lastKwEnd);
//            spansBuilder.add(Collections.singleton(style), end - start);
//            lastKwEnd = matcher.end();
//        }
//        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
//        return spansBuilder.create();
//    }
//
//    @Override
//    protected void updateLocalInfos() {
//        if (shouldUpdateLocalInfos()) {
//            super.updateLocalInfos();
//            var commands = getManager().getLocalInfos().getItems().stream().map(i -> i.getName())
//                    .collect(Collectors.toList());
//            if (Framework.getMode() == FrameworkMode.CLIENT) {
//            this.localPattern = Pattern.compile("^\\s*(?<COMMAND>" + CommandSpecialSymbols.LOCAL_COMMAND
//                        + "?\\b(" + String.join("|", commands) + ")\\b)");
//            } else {
//                this.localPattern = Pattern.compile("^\\s*(?<COMMAND>\\b(" + String.join("|",
//                        commands) + ")\\b)");
//            }
//        }
//    }
//
//    @Override
//    protected void updateRemoteInfos() {
//        if (shouldUpdateRemoteInfos()) {
//            super.updateRemoteInfos();
//            var commands = getManager().getRemoteInfos().getItems().stream().map(i -> i.getName())
//                    .collect(Collectors.toList());
//            this.remotePattern = Pattern.compile("^\\s*(?<COMMAND>\\b(" + String.join("|", commands) + ")\\b)");
//        }
//    }
//}
//
