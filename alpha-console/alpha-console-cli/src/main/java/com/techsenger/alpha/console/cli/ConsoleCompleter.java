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

package com.techsenger.alpha.console.cli;

import com.techsenger.alpha.api.command.CommandInfo;
import com.techsenger.alpha.api.executor.CommandSpecialSymbols;
import com.techsenger.alpha.spi.console.CommandInfosManager;
import java.util.List;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

/**
 *
 * @author Pavel Castornii
 */
class ConsoleCompleter extends AbstractCommandConsumer implements Completer {

    ConsoleCompleter(CommandInfosManager manager) {
        super(manager);
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
            if (isRemote(trimmedLine)) {
                updateRemoteInfos();
                getRemoteCommandsByName().keySet().forEach(k -> candidates.add(new Candidate(k)));
            } else {
                updateLocalInfos();
                var pref = "";
                if (trimmedLine.startsWith(CommandSpecialSymbols.LOCAL_COMMAND)) {
                    pref = CommandSpecialSymbols.LOCAL_COMMAND;
                }
                String finalPref = pref;
                getLocalCommandsByName().keySet().forEach(k -> candidates.add(new Candidate(finalPref + k)));
            }
        } else {
            var commandName = line.words().get(0);
            CommandInfo command = null;
            if (isRemote(commandName)) {
                updateRemoteInfos();
                command = getRemoteCommandsByName().get(commandName);
            } else {
                updateLocalInfos();
                var strippedName = stripExtraSymbols(commandName);
                command = getLocalCommandsByName().get(strippedName);
            }
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
