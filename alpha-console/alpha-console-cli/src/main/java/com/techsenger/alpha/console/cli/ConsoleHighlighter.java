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
import com.techsenger.alpha.spi.console.CommandInfosManager;
import java.util.Map;
import java.util.regex.Pattern;
import org.jline.reader.Highlighter;
import org.jline.reader.LineReader;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

/**
 *
 * @author Pavel Castornii
 */
class ConsoleHighlighter extends AbstractCommandConsumer implements Highlighter {

    private final AttributedStyle commandStyle = new AttributedStyle().bold(); //.foreground(0, 0, 230);

    ConsoleHighlighter(CommandInfosManager manager) {
        super(manager);
    }

    /**
     * This method is called when input line changes. The AttributedString that will be returned from this method
     * will completely replace input line. We use Jline and Jansi.
     *
     * <p>Jansi from 2.1.0 supports 16, 256 colors and truecolor (24 bits):
     * https://github.com/fusesource/jansi/commit/235b653bbb57101dca7db3ff2f880115d781bc38
     *
     * <p>Jansi automaticallu checks what colors are supported in the terminal and round unsupported colors.
     *
     * @param reader
     * @param buffer is the full input line after change.
     * @return
     */
    @Override
    public AttributedString highlight(LineReader reader, String buffer) {
        int idx = buffer.indexOf(" ");
        String command = null;
        String parameters = null;
        if (idx > 0) {
            command = buffer.substring(0,  idx);
            parameters = buffer.substring(idx);
        } else {
            command = buffer;
        }

        boolean remote = false;

        AttributedStringBuilder asb = new AttributedStringBuilder();
        Map<String, CommandInfo> commandsByName = null;
        if (isRemote(command)) {
            updateRemoteInfos();
            commandsByName = getRemoteCommandsByName();
        } else {
            updateLocalInfos();
            commandsByName = getLocalCommandsByName();
        }
        var strippedName = stripExtraSymbols(command);
        if (commandsByName.containsKey(strippedName)) {
            asb.styled(commandStyle, command);
            if (parameters != null) {
                asb.append(parameters);
            }
        } else {
            asb.append(buffer);
        }
        return asb.toAttributedString();
    }

    @Override
    public void setErrorPattern(Pattern errorPattern) { }

    @Override
    public void setErrorIndex(int errorIndex) { }
}
