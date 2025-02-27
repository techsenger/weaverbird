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

/**
 * Interface for handling progress notifications during command execution.
 *
 * <p>Implementations of this interface provide hooks to monitor the execution process of commands, allowing for
 * initialization, pre-execution, post-execution, and finalization steps.
 *
 * <p>Important. One script can execute another script. For both scripts this handler will be used (see level).
 */
public interface ProgressHandler {

    /**
     * Called before the execution of all commands begins.
     *
     * <p>This method can be used to perform setup operations or initialize resources
     * necessary for tracking progress throughout the execution process.
     *
     */
    void initialize();

    /**
     * Called before the execution of an individual command.
     *
     * <p>This method can be used to log or display the current state of progress
     * or prepare resources specific to the command being executed.
     *
     * @param scriptName the name of the parameter or null.
     * @param level the level of the execution. One script can execute another script, so, the first script
     * has level 0, the second 1 etc.
     * @param levelTotal  the total number of commands on this level.
     * @param details the details about the current command
     */
    void beforeExecution(String scriptName, int level, int levelTotal, CommandExecutionDetails details);

    /**
     * Called after the execution of an individual command.
     *
     * <p>This method can be used to log results, update progress indicators,
     * or release resources allocated for the executed command.
     *
     * @param scriptName the name of the parameter or null.
     * @param level the level of the execution. One script can execute another script, so, the first script
     * has level 0, the second 1 etc.
     * @param levelTotal  the total number of commands on this level.
     * @param details the details about the current command
     */
    void afterExecution(String scriptName, int level, int levelTotal, CommandExecutionDetails details);

    /**
     * Called if there was an error during execution and execution can't continue.
     *
     * @param scriptName the name of the parameter or null.
     * @param level the level of the execution. One script can execute another script, so, the first script
     * has level 0, the second 1 etc.
     * @param levelTotal  the total number of commands on this level.
     * @param details the details about the current command
     */
    void onError(String scriptName, int level, int levelTotal, CommandExecutionDetails details);

    /**
     * Called after the execution of all commands is complete.
     *
     * <p>This method can be used to finalize resources, summarize results,
     * or perform cleanup operations.
     */
    void deinitialize();
}
