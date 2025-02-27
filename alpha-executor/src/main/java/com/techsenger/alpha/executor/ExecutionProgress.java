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

package com.techsenger.alpha.executor;

import com.techsenger.alpha.api.command.CommandResult;
import com.techsenger.alpha.api.executor.ProgressHandler;

/**
 *
 * @author Pavel Castornii
 */
class ExecutionProgress {

    private final ProgressHandler handler;

    private DefaultCommandExecutionDetails commandDetails;

    private String scriptName;

    private int level;

    private int levelTotal;

    ExecutionProgress(ProgressHandler handler) {
        this.handler = handler;
    }

    void initialize() {
        if (this.handler != null) {
            this.handler.initialize();
        }
    }

    void setScriptDetails(String scriptName, int level, int levelTotal) {
        if (this.handler != null) {
            this.scriptName = scriptName;
            this.level = level;
            this.levelTotal = levelTotal;
        }
    }

    void createDetails(int index, String name, CommandResult result) {
        if (this.handler != null) {
            this.commandDetails = new DefaultCommandExecutionDetails(index, name, result);
        }
    }

    void setTitle(String title) {
        if (this.handler != null) {
            this.commandDetails.setTitle(title);
        }
    }

    void callBeforeExecution() {
        if (this.handler != null) {
            this.handler.beforeExecution(this.scriptName, this.level, this.levelTotal, this.commandDetails);
        }
    }

    void callAfterExecution() {
        if (this.handler != null) {
            this.handler.afterExecution(this.scriptName, this.level, this.levelTotal, this.commandDetails);
        }
    }

    void callOnError() {
        if (this.handler != null) {
            this.handler.onError(this.scriptName, this.level, this.levelTotal, this.commandDetails);
        }
    }

    void deinitialize() {
        if (this.handler != null) {
            this.handler.deinitialize();
        }
    }
}
