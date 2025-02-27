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

package com.techsenger.alpha.api.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * It is not ready because it doesnt know about server:get "dfd fdfd".
 * @author Pavel Castornii
 */
public final class CommandParser {

    /**
     * Logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(CommandParser.class);

    /**
     * Private constructor.
     */
    private CommandParser() {
        //empty
    }

    /**
     * Parses command string which doesn't contain ";".
     * @param textLine to be parsed.
     * @return command descriptor.
     * @throws Exception if there are errors.
     */
    public static DefaultCommandLine parseLine(final String textLine) throws Exception {
        String name = null;
        String newTextLine = textLine.trim().replaceAll(" +", " "); //multiple spaces to one
        if (newTextLine.length() == 0) {
            throw new Exception("Command can't be empty.");
        }
        String prefix = null;
        if (newTextLine.startsWith(CommandSpecialSymbols.LOCAL_COMMAND)) {
            prefix = CommandSpecialSymbols.LOCAL_COMMAND;
            newTextLine = newTextLine.substring(1);
        }
        String[] splits = newTextLine.split(" ");
        name = splits[0];
        List<String> arguments = new ArrayList<String>();
        //The regular expression simply says
        //[^"]     - token starting with something other than "
        //\S*       - followed by zero or more non-space characters
        //...or...
        //".+?"   - a "-symbol followed by whatever, until another ".
        Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(newTextLine.substring(name.length()));
        while (m.find()) {
            String arg = m.group(1).trim();
            if (arg.length() > 0) {
                arguments.add(arg);
            }
        }
        return new DefaultCommandLine(prefix, name, arguments);
    }

    /**
     * Parses command text, which contains 0..N commands separated by ";".
     * @param text to be parsed.
     * @return
     */
    public static CommandText parseText(final String text) {
        String newText = text;
        if (!newText.trim().endsWith(CommandSpecialSymbols.SEPARATOR)) {
            newText += CommandSpecialSymbols.SEPARATOR;
        }
        //firstly we remove all comments
        //(?m): Turns multi-line mode on, so that the start-of-line ^ anchor matches the start of each line.
        newText = text.replaceAll("(?m)^" + CommandSpecialSymbols.COMMENT + ".*", "");
        //now we can split
        String[] textLines = newText.split(CommandSpecialSymbols.SEPARATOR);
        List<String> lines = new ArrayList<>();
        for (String textLine : textLines) {
            textLine = textLine.replaceAll(System.lineSeparator(), "");
            textLine = textLine.trim();
            if (!textLine.isEmpty()) {
                lines.add(textLine);
            }
        }
        var commandText = new DefaultCommandText(lines);
        return commandText;
    }
}
