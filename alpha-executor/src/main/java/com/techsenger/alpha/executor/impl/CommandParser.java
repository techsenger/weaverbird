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

package com.techsenger.alpha.executor.impl;

import com.techsenger.alpha.executor.api.CommandSyntax;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
public final class CommandParser {

    private static final Logger logger = LoggerFactory.getLogger(CommandParser.class);

    /**
     * Parses command text, which contains 0..N commands separated by {@link CommandSyntax#SEPARATOR}.
     *
     * @param text to be parsed.
     * @return list of parsed commands.
     * @throws Exception if there are errors.
     */
    public static List<ParsedCommand> parse(final String text) throws Exception {
        String newText = removeComments(text.trim());
        List<String> parts = splitBySeparator(newText);
        List<ParsedCommand> commands = new ArrayList<>();
        for (String part : parts) {
            String commandText = part.replaceAll(System.lineSeparator(), "").trim();
            if (!commandText.isEmpty()) {
                commands.add(parseCommand(commandText));
            }
        }
        return commands;
    }

    /**
     * Removes comments from text. A comment is a line starting with {@link CommandSyntax#COMMENT}.
     *
     * @param text to be processed.
     * @return text without comments.
     */
    private static String removeComments(final String text) {
        return text.replaceAll("(?m)^" + CommandSyntax.COMMENT + ".*", "");
    }

    /**
     * Splits text by {@link CommandSyntax#SEPARATOR}, ignoring separators inside quotes.
     *
     * @param text to be split.
     * @return list of parts.
     */
    private static List<String> splitBySeparator(final String text) {
        List<String> parts = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
                current.append(c);
            } else if (String.valueOf(c).equals(CommandSyntax.SEPARATOR) && !inQuotes) {
                parts.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        if (current.length() != 0) {
            parts.add(current.toString());
        }
        return parts;
    }

    /**
     * Parses a single command string which doesn't contain unquoted {@link CommandSyntax#SEPARATOR}.
     *
     * @param textLine to be parsed.
     * @return parsed command.
     * @throws Exception if there are errors.
     */
    private static ParsedCommand parseCommand(final String textLine) throws Exception {
        String newTextLine = textLine.trim().replaceAll(" +", " ");
        if (newTextLine.isEmpty()) {
            throw new Exception("Command can't be empty.");
        }
        String prefix = null;
        if (newTextLine.startsWith(CommandSyntax.LOCAL_COMMAND)) {
            prefix = CommandSyntax.LOCAL_COMMAND;
            newTextLine = newTextLine.substring(CommandSyntax.LOCAL_COMMAND.length());
        }
        String[] splits = newTextLine.split(" ");
        String name = splits[0];
        List<String> arguments = new ArrayList<>();
        // [^"]\S*   - token starting with something other than " followed by non-space characters
        // ".+?"     - a "-symbol followed by whatever, until another "
        Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*")
                .matcher(newTextLine.substring(name.length()));
        while (m.find()) {
            String arg = m.group(1).trim();
            if (!arg.isEmpty()) {
                arguments.add(arg);
            }
        }
        return new ParsedCommand(prefix, name, arguments);
    }

    /**
     * Private constructor.
     */
    private CommandParser() {
        //empty
    }
}
