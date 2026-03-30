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

package com.techsenger.alpha.executor.spi;

import com.beust.jcommander.Parameter;

/**
 *
 * @author Pavel Castornii
 */
public abstract  class AbstractCommand implements Command {

    /**
     * We don't use here -h because it used for example as host etc.
     */
    @Parameter(names = {"-?", "--help"}, description = "displays this help", help = true)
    private boolean helpRequested = false;

    /**
     * Stopwatch.
     */
    @Parameter(names = {"--stopwatch"}, required = false,
            description = "sets a flag to use stopwatch and to print total command execution time")
    private boolean stopWatchUsed = false;

    /**
     * Continue on fail.
     */
    @Parameter(names = {"--continue-on-fail"}, required = false,
            description = "allows program to execute next command if this command has given an error,"
            + "default is false")
    private boolean continueOnFail = false;

    private CommandDescriptor descriptor;

    @Override
    public boolean isHelpRequested() {
        return helpRequested;
    }

    @Override
    public boolean isStopWatchUsed() {
        return stopWatchUsed;
    }

    @Override
    public boolean shouldContinueOnFail() {
        return this.continueOnFail;
    }

    @Override
    public CommandDescriptor getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(CommandDescriptor descriptor) {
        this.descriptor = descriptor;
    }
}
