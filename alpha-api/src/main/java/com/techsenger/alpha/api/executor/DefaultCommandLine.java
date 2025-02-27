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

import java.io.Serializable;
import java.util.List;

/**
 * This class allows to modify parameters and build new command lines.
 * Parameter starts with - or --
 * @author Pavel Castornii
 */
public class DefaultCommandLine implements CommandLine, Serializable {

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

    public DefaultCommandLine() {

    }

    /**
     * Constructor.
     */
    public DefaultCommandLine(final String prefix, final String name, final List<String> arguments) {
        this.prefix = prefix;
        this.name = name;
        this.arguments = arguments;
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<String> getArguments() {
        return arguments;
    }

    @Override
    public String[] getArgumentsAsArray() {
        String[] array = new String[this.arguments.size()];
        this.arguments.toArray(array); // fill the array
        return array;
    }

    /**
     * Returns next parameter following passed parameter or null if not found.
     * @param parameter
     * @return
     */
    public String getParameterValue(final String parameter) {
        int index = this.parameterIndex(parameter);
        if (index == -1 || this.arguments.size() - 1 <= index) {
            return null;
        }
        String argument = this.arguments.get(index + 1);
        //is it parameter or value?
        if (argument.startsWith("-")) {
            return null;
        } else {
            return argument;
        }

    }

    /**
     *
     * @param parameter
     * @return true if replacement was taken place otherwise false.
     */
    public boolean setParameterValue(final String parameter, final String value) {
        int index = this.parameterIndex(parameter);
        if (index == -1 || this.arguments.size() - 1 <= index) {
            return false;
        }
        String argument = this.arguments.get(index + 1);
        //is it parameter or value?
        if (argument.startsWith("-")) {
            return false;
        } else {
            this.arguments.set(index + 1, value);
            return true;
        }
    }

    /**
     * Doesn't check if parameters exists.
     * @param parameter
     */
    public void addParameter(String parameter) {
        this.arguments.add(parameter);
    }

    /**
     * Doesn't check if parameters exists.
     * @param parameter
     * @param value
     */
    public void addParameterAndValue(String parameter, String value) {
        this.arguments.add(parameter);
        this.arguments.add(value);
    }

    /**
     * Checks if parameter exists.
     * @param parameter
     * @return
     */
    public boolean parameterExists(String parameter) {
        return !(this.parameterIndex(parameter) == -1);
    }

    /**
     * Removes parameter without its value.
     * @param parameter
     * @return
     */
    public boolean removeParameter(String parameter) {
        int index = this.parameterIndex(parameter);
        if (index == -1) {
            return false;
        } else {
            this.arguments.remove(index);
            return true;
        }
    }

    /**
     * Creates command line with modified parameters/values.
     * @return
     */
    public String buildTextLine() {
        StringBuilder builder = new StringBuilder();
        builder.append(name);
        this.arguments.stream().forEach(arg -> {
            builder.append(" ");
            builder.append(arg);
            });
        builder.append(";");
        return builder.toString();
    }

    /**
     * Returns -1 if not finds.
     * @param parameter
     * @return
     */
    private int parameterIndex(String parameter) {
        int result = -1;
        for (int i = 0; i < this.arguments.size(); i++) {
            if (this.arguments.get(i).equalsIgnoreCase(parameter)) {
                result = i;
                break;
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "CommandLine{prefix=" + prefix + ", name=" + name + ", arguments=" + arguments + '}';
    }
}
