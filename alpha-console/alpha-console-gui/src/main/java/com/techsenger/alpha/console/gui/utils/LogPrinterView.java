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
//import com.techsenger.alpha.console.gui.textstyle.StyledText;
//import com.techsenger.tabshell.kit.material.textarea.ExtendedTextArea;
//import com.techsenger.tabshell.kit.material.textarea.RichTextFxUtils;
//import com.techsenger.tabshell.kit.material.textarea.TextAreaStyle;
//import java.util.Collection;
//import java.util.List;
//import java.util.Map;
//import org.fxmisc.flowless.VirtualizedScrollPane;
//import org.fxmisc.richtext.model.StyleSpansBuilder;
//
///**
// *
// * @author Pavel Castornii
// */
//public interface LogPrinterView {
//
//    default void printLogEvents(List<StyledText> texts, ExtendedTextArea textArea,
//            VirtualizedScrollPane scrollPane, Map<String, Collection<TextAreaStyle>> styleCollectionsByClassName) {
//        boolean shouldScroll = true;
//        var isScrolledToBottom = RichTextFxUtils.isScrolledToBottom(scrollPane);
//        var selectionRange = textArea.getSelection();
//        if ((!isScrolledToBottom && textArea.getText().length() != 0)
//                || selectionRange.getStart() != selectionRange.getEnd()) {
//            shouldScroll = false;
//        }
//
//        StringBuilder sb = new StringBuilder();
//        StyleSpansBuilder<Collection<TextAreaStyle>> ssb = new StyleSpansBuilder<>(texts.size());
//        int oldLength = textArea.getLength();
//        for (var styledText: texts) {
//            var style = styledText.getStyle();
//            var text = styledText.getText();
//            sb.append(text);
//            if (style == null) {
//                ssb.add(TextAreaStyle.EMPTY, text.length());
//            } else {
//                ssb.add(styleCollectionsByClassName.get(style), text.length());
//            }
//        }
//        textArea.saveSelection();
//        textArea.appendText(sb.toString());
//        textArea.setStyleSpans(oldLength, ssb.create());
//        textArea.restoreSelection();
//        if (shouldScroll) {
//            RichTextFxUtils.scrollToBottom(textArea);
//        }
//    }
//}
