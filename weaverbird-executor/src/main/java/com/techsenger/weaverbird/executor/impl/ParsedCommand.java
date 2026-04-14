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

package com.techsenger.weaverbird.executor.impl;

import java.util.List;

/**
 * @author Pavel Castornii
 */
public class ParsedCommand {

    /**
     * The prefix of the command.
     */
    private String prefix;

    /**
     * The name of the command.
     */
    private String name;

    /**
     * The arguments of the command. Arguments are either parameters or their values
     */
    private List<String> arguments;

    public ParsedCommand() {

    }

    /**
     * Constructor.
     */
    public ParsedCommand(final String prefix, final String name, final List<String> arguments) {
        this.prefix = prefix;
        this.name = name;
        this.arguments = arguments;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getName() {
        return name;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public String[] getArgumentsAsArray() {
        String[] array = new String[this.arguments.size()];
        this.arguments.toArray(array); // fill the array
        return array;
    }

    @Override
    public String toString() {
        return "CommandLine{prefix=" + prefix + ", name=" + name + ", arguments=" + arguments + '}';
    }
}
