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

package com.techsenger.alpha.console.gui.shell;

import com.techsenger.alpha.api.command.CommandContext;
import com.techsenger.alpha.api.command.CommandResult;
import com.techsenger.alpha.api.executor.CommandExecutor;
import com.techsenger.tabshell.kit.core.workertab.TabWorker;
import java.util.List;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
class CommandExecuteService extends Service<List<CommandResult>> implements TabWorker<List<CommandResult>> {

    private static final Logger logger = LoggerFactory.getLogger(CommandExecuteService.class);

    private final CommandExecutor commandExecutor;

    private final String command;

    private final CommandContext context;

    private final int width;

    CommandExecuteService(CommandExecutor commandExecutor, CommandContext context, String command, int width) {
        this.commandExecutor = commandExecutor;
        this.context = context;
        this.command = command;
        this.width = width;
    }

    @Override
    public boolean usesProgress() {
        return false;
    }

    @Override
    protected Task<List<CommandResult>> createTask() {
        return new Task<List<CommandResult>>() {
            @Override
            protected List<CommandResult> call() throws Exception {
                return commandExecutor.executeCommands(command, context, null, null, width);
            }
        };
    }
}
