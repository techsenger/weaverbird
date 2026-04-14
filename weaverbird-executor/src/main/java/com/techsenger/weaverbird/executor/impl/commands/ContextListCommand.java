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

import com.techsenger.weaverbird.core.api.message.MessagePrinter;
import com.techsenger.weaverbird.executor.api.CommandContext;
import com.techsenger.weaverbird.executor.spi.AbstractCommand;
import com.techsenger.weaverbird.executor.spi.CommandMeta;
import com.techsenger.weaverbird.executor.spi.LocalCommand;
import com.techsenger.weaverbird.executor.spi.RemoteCommand;
import com.techsenger.toolkit.core.StringUtils;

/**
 *
 * @author Pavel Castornii
 */
@LocalCommand
@RemoteCommand
@CommandMeta(name = "context:list", description = "Lists all the properties of command context.")
public class ContextListCommand extends AbstractCommand {

    @Override
    public String getTitle() {
        return "Listing command context properties";
    }

    @Override
    public void execute(final CommandContext context, MessagePrinter printer) throws Exception {
        var props = context.getProperties();
        if (!props.isEmpty()) {
            printer.printlnMessage("Command context properties:");
            for (var entry : context.getProperties().entrySet()) {
                printer.printlnMessage(StringUtils.format("{} => [{}]", entry.getKey(), entry.getValue()));
            }
        } else {
            printer.printlnMessage("Command context is empty");
        }

    }
}
