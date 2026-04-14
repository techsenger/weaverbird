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

package com.techsenger.weaverbird.executor.impl.commands;

import com.beust.jcommander.Parameter;
import com.techsenger.weaverbird.core.api.Constants;
import com.techsenger.weaverbird.core.api.message.MessagePrinter;
import com.techsenger.weaverbird.executor.api.CommandContext;
import com.techsenger.weaverbird.executor.api.command.Commands;
import com.techsenger.weaverbird.executor.spi.AbstractCommand;
import com.techsenger.weaverbird.executor.spi.CommandMeta;
import com.techsenger.weaverbird.executor.spi.LocalCommand;
import com.techsenger.toolkit.core.StringUtils;
import com.techsenger.toolkit.core.file.FileUtils;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 *
 * @author Pavel Castornii
 */
@LocalCommand
@CommandMeta(name = Commands.SCRIPT_EXECUTE, description = "Executes script which is on client side.")
public class ScriptExecuteCommand extends AbstractCommand {

    /**
     * File name.
     */
    @Parameter(names = {"-s", "--script"}, required = true,
            description = "sets the script file name without extention in the script folder")
    private String scriptName;

    @Override
    public String getTitle() {
        return StringUtils.format("Executing script {}", this.scriptName);
    }

    @Override
    public void execute(final CommandContext context, MessagePrinter printer) throws Exception {
        Path scriptPath = context.getFramework().getPathManager().getScriptDirectory().resolve(
                this.scriptName + "." + Constants.SCRIPT_EXTENSION);
        if (Files.exists(scriptPath)) {
            try {
                var commands = FileUtils.readFile(scriptPath, StandardCharsets.UTF_8);
                var results = context
                        .getExecutor()
                        .executeCommands(commands, scriptName, null, printer.getWidth());
                results.forEach(r -> printer.print(r.getMessages()));
            } catch (Exception ex) {
                throw new Exception("Error executing script " + scriptPath);
            }

        } else {
            throw new Exception("Script not found in " + scriptPath);
        }
        printer.printlnMessage("Script " + scriptPath + " was executed");
    }
}
