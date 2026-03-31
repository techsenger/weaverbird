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
//import com.techsenger.tabshell.kit.material.textarea.TextAreaStyle;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//import org.fxmisc.richtext.model.StyleSpans;
//import org.fxmisc.richtext.model.StyleSpansBuilder;
//
///**
// *
// * @author Pavel Castornii
// */
//public class XmlHighlighter {
//
//    private static final Pattern XML_TAG =
//            Pattern.compile("(?<ELEMENT>(</?\\h*)(\\w+)([^<>]*)(\\h*/?>))|(?<COMMENT><!--[\\s\\S\\n]*?-->)");
//
//    private static final Pattern ATTRIBUTES = Pattern.compile("(\\w+\\h*)(=)(\\h*\"[^\"]+\")");
//
//    private static final int GROUP_OPEN_BRACKET = 2;
//
//    private static final int GROUP_ELEMENT_NAME = 3;
//
//    private static final int GROUP_ATTRIBUTES_SECTION = 4;
//
//    private static final int GROUP_CLOSE_BRACKET = 5;
//
//    private static final int GROUP_ATTRIBUTE_NAME = 1;
//
//    private static final int GROUP_EQUAL_SYMBOL = 2;
//
//    private static final int GROUP_ATTRIBUTE_VALUE = 3;
//
//    public StyleSpans<Collection<TextAreaStyle>> computeHighlighting(String text) {
//        Matcher matcher = XML_TAG.matcher(text);
//        int lastKwEnd = 0;
//        StyleSpansBuilder<Collection<TextAreaStyle>> spansBuilder = new StyleSpansBuilder<>();
//        while (matcher.find()) {
//            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
//            if (matcher.group("COMMENT") != null) {
//                    spansBuilder.add(Collections.singleton(new TextAreaStyle("comment")),
//                            matcher.end() - matcher.start());
//            } else {
//                if (matcher.group("ELEMENT") != null) {
//                    String attributesText = matcher.group(GROUP_ATTRIBUTES_SECTION);
//                    spansBuilder.add(Collections.singleton(new TextAreaStyle("tagmark")),
//                            matcher.end(GROUP_OPEN_BRACKET) - matcher.start(GROUP_OPEN_BRACKET));
//                    spansBuilder.add(Collections.singleton(new TextAreaStyle("anytag")),
//                            matcher.end(GROUP_ELEMENT_NAME) - matcher.end(GROUP_OPEN_BRACKET));
//                    if (!attributesText.isEmpty()) {
//                        lastKwEnd = 0;
//                        Matcher amatcher = ATTRIBUTES.matcher(attributesText);
//                        while (amatcher.find()) {
//                            spansBuilder.add(Collections.emptyList(), amatcher.start() - lastKwEnd);
//                            spansBuilder.add(Collections.singleton(new TextAreaStyle("attribute")),
//                                    amatcher.end(GROUP_ATTRIBUTE_NAME) - amatcher.start(GROUP_ATTRIBUTE_NAME));
//                            spansBuilder.add(Collections.singleton(new TextAreaStyle("tagmark")),
//                                    amatcher.end(GROUP_EQUAL_SYMBOL) - amatcher.end(GROUP_ATTRIBUTE_NAME));
//                            spansBuilder.add(Collections.singleton(new TextAreaStyle("avalue")),
//                                    amatcher.end(GROUP_ATTRIBUTE_VALUE) - amatcher.end(GROUP_EQUAL_SYMBOL));
//                            lastKwEnd = amatcher.end();
//                        }
//                        if (attributesText.length() > lastKwEnd) {
//                            spansBuilder.add(Collections.emptyList(), attributesText.length() - lastKwEnd);
//                        }
//                    }
//                    lastKwEnd = matcher.end(GROUP_ATTRIBUTES_SECTION);
//                    spansBuilder.add(Collections.singleton(new TextAreaStyle("tagmark")),
//                            matcher.end(GROUP_CLOSE_BRACKET) - lastKwEnd);
//                }
//            }
//            lastKwEnd = matcher.end();
//        }
//        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
//        return spansBuilder.create();
//    }
//
//}
