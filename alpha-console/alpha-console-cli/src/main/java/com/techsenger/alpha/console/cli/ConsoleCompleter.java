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

package com.techsenger.alpha.console.cli;

import com.techsenger.alpha.executor.api.CommandContext;
import com.techsenger.alpha.executor.api.CommandSyntax;
import com.techsenger.alpha.executor.api.command.CommandInfo;
import java.util.List;
import java.util.Map;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

/**
 *
 * @author Pavel Castornii
 */
class ConsoleCompleter extends AbstractCommandConsumer implements Completer {

    private final Map<String, CommandInfo> commandsByName;

    private final CommandContext commandContext;

    ConsoleCompleter(Map<String, CommandInfo> commandsByName, CommandContext commandContext) {
        this.commandsByName = commandsByName;
        this.commandContext = commandContext;
    }

    /**
     * This method is called every time when user presses tab for autocomplete. So, it doesn't need listener.
     *
     * @param reader
     * @param line
     * @param candidates
     */
    @Override
    public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
        if (line.wordIndex() == 0) {
            var trimmedLine = line.line().trim();
            var pref = "";
            if (trimmedLine.startsWith(CommandSyntax.LOCAL_COMMAND)) {
                pref = CommandSyntax.LOCAL_COMMAND;
            }
            String finalPref = pref;
            commandsByName.values().forEach(c -> {
                boolean addCommand = false;
                if (commandContext.getSession() != null && finalPref.length() == 0) {
                    if (c.isRemote()) {
                        addCommand = true;
                    }
                } else {
                    if (c.isLocal()) {
                        addCommand = true;
                    }
                }
                if (addCommand) {
                    candidates.add(new Candidate(finalPref + c.getName()));
                }
            });
        } else {
            var commandName = line.words().get(0);
            CommandInfo command = null;
            var strippedName = stripExtraSymbols(commandName);
            command = commandsByName.get(strippedName);
            if (command != null) {
                command.getParameters().forEach(c -> {
                    if (!c.isMain()) {
                        candidates.add(new Candidate(c.getLongName()));
                    }
                });
            }
        }
    }
}
