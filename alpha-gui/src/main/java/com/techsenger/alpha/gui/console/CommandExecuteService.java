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

package com.techsenger.alpha.gui.console;

import com.techsenger.alpha.executor.api.CommandExecutor;
import com.techsenger.alpha.executor.api.command.CommandResult;
import java.util.List;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 *
 * @author Pavel Castornii
 */
class CommandExecuteService extends Service<List<CommandResult>> {

    private final CommandExecutor commandExecutor;

    private final String command;

    private final int width;

    CommandExecuteService(CommandExecutor commandExecutor, String command, int width) {
        this.commandExecutor = commandExecutor;
        this.command = command;
        this.width = width;
    }

    @Override
    protected Task<List<CommandResult>> createTask() {
        return new Task<List<CommandResult>>() {
            @Override
            protected List<CommandResult> call() throws Exception {
                return commandExecutor.executeCommands(command, null, null, width);
            }
        };
    }
}
