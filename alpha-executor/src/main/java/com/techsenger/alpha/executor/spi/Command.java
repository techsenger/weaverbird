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

import com.techsenger.alpha.executor.api.CommandContext;
import com.techsenger.alpha.core.api.message.MessagePrinter;

/**
 *
 * @author Pavel Castornii
 */
public interface Command {

    /**
     * Checks if help is requested for the command. We want to execute command or just get help about it?
     * @return true of false.
     */
    boolean isHelpRequested();

    /**
     * Checks if it is necessary to use stopwatch to measure total command execution time. If stopwatch is used
     * then total time will be printed on the list line of the command output.
     *
     * @return
     */
    boolean isStopWatchUsed();

    /**
     * Returns true if program can continue execute next commands if this command has given an error.
     *
     * @return
     */
    boolean shouldContinueOnFail();

    /**
     * Returns a static text that describes the action being performed by the command when executed. This text provides
     * a clear description of what the command is doing, and it remains unchanged throughout the execution of the
     * command.
     *
     * @return
     */
    String getTitle();

    /**
     * Returns the descriptor of this command.
     * @return
     */
    CommandDescriptor getDescriptor();

    /**
     * Executes command.
     *
     * @param context
     * @param printer
     * @throws Exception
     */
    void execute(CommandContext context, MessagePrinter printer) throws Exception;
}
