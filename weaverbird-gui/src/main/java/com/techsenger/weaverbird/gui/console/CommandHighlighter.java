/*
 * Copyright 2018-2026 Pavel Castornii.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.techsenger.weaverbird.gui.console;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jfx.incubator.scene.control.richtext.RichTextArea;
import jfx.incubator.scene.control.richtext.TextPos;
import jfx.incubator.scene.control.richtext.model.StyledInput;
import jfx.incubator.scene.control.richtext.model.StyledSegment;

/**
 *
 * @author Pavel Castornii
 */
public class CommandHighlighter {

    private final RichTextArea textArea;

    private Matcher matcher;

    private Set<String> commands;

    CommandHighlighter(RichTextArea textArea) {
        this.textArea = textArea;
    }

    protected void highlight(int paragraphIndex) {
        var paragraph = textArea.getModel().getParagraph(paragraphIndex).getPlainText();
        this.matcher.reset(paragraph);

        var lineStart = new TextPos(paragraphIndex, 0, 0, false);
        var lineEnd = new TextPos(paragraphIndex, paragraph.length(), 0, false);

        if (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();

            textArea.replaceText(lineStart, lineEnd, styledInput(List.of(
                StyledSegment.of(paragraph.substring(0, start), TextAreaCssStyles.DEFAULT),
                StyledSegment.of(paragraph.substring(start, end), TextAreaCssStyles.COMMAND),
                StyledSegment.of(paragraph.substring(end), TextAreaCssStyles.DEFAULT)
            )));
        } else {
            textArea.replaceText(lineStart, lineEnd, StyledInput.of(paragraph, TextAreaCssStyles.DEFAULT));
        }
    }

    /**
     * Creates a {@link StyledInput} from the given segments, applying all style changes
     * in a single replace operation so the model fires only one change event instead of multiple.
     *
     * @param segments the styled segments to apply
     * @return a StyledInput that iterates over the given segments
     */
    private StyledInput styledInput(List<StyledSegment> segments) {
        var it = segments.iterator();
        return new StyledInput() {
            @Override public StyledSegment nextSegment() {
                return it.hasNext() ? it.next() : null;
            }
            @Override public void close() { }
        };
    }

    protected void setCommands(Set<String> commands) {
        this.commands = commands;
        this.matcher = Pattern.compile("\\s*(?<COMMAND>\\b(" + String.join("|", commands) + ")\\b)").matcher("");
    }
}

