/*
 * Copyright 2018-2025 Pavel Castornii.
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

package com.techsenger.alpha.console.gui.file;

import com.techsenger.alpha.api.Framework;
import com.techsenger.tabshell.kit.material.textarea.TextAreaStyle;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

/**
 *
 * @author Pavel Castornii
 */
public class ScriptHighlighter {

    private static final String PAREN_PATTERN = "\\(|\\)";

    private static final String BRACE_PATTERN = "\\{|\\}";

    private static final String BRACKET_PATTERN = "\\[|\\]";

    private static final String SEMICOLON_PATTERN = "\\;";

    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";

    private static final String COMMENT_PATTERN = "#[^\n]*";

    private final String[] keywords;

    private final String keywordPattern;

    private final Pattern pattern;

    public ScriptHighlighter() {
        var executor = Framework.getServiceManager().getCommandExecutor();
        List<String> commands = executor.getCommandsByName()
                .entrySet()
                .stream()
                .map(e -> e.getKey())
                .collect(Collectors.toList());
        keywords = commands.toArray(String[]::new);
        keywordPattern = "\\b(" + String.join("|", keywords) + ")\\b";
        pattern = Pattern.compile(
            "(?<KEYWORD>" + keywordPattern + ")"
            + "|(?<PAREN>" + PAREN_PATTERN + ")"
            + "|(?<BRACE>" + BRACE_PATTERN + ")"
            + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
            + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
            + "|(?<STRING>" + STRING_PATTERN + ")"
            + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
        );
    }

    public StyleSpans<Collection<TextAreaStyle>> computeHighlighting(String text) {
        Matcher matcher = pattern.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<TextAreaStyle>> spansBuilder = new StyleSpansBuilder<>();
        while (matcher.find()) {
            TextAreaStyle style =
                    matcher.group("KEYWORD") != null ? new TextAreaStyle("keyword")
                    : matcher.group("PAREN") != null ? new TextAreaStyle("paren")
                    : matcher.group("BRACE") != null ? new TextAreaStyle("brace")
                    : matcher.group("BRACKET") != null ? new TextAreaStyle("bracket")
                    : matcher.group("SEMICOLON") != null ? new TextAreaStyle("semicolon")
                    : matcher.group("STRING") != null ? new TextAreaStyle("string")
                    : matcher.group("COMMENT") != null ? new TextAreaStyle("comment")
                    : null; /* never happens */ assert style != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(style), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }
}
