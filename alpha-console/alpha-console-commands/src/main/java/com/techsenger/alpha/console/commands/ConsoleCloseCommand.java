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

package com.techsenger.alpha.console.commands;

import com.techsenger.alpha.api.command.CommandContext;
import com.techsenger.alpha.api.message.MessagePrinter;
import com.techsenger.alpha.spi.command.AbstractCommand;
import com.techsenger.alpha.spi.command.CommandMeta;
import com.techsenger.alpha.spi.command.LocalCommand;
import com.techsenger.alpha.spi.console.ConsoleService;
import com.techsenger.toolkit.core.jpms.ServiceType;
import com.techsenger.toolkit.core.jpms.ServiceUtils;

/**
 *
 * @author Pavel Castornii
 */
@LocalCommand
@CommandMeta(name = "console:close", description = "Closes console.")
public class ConsoleCloseCommand extends AbstractCommand {

    @Override
    public String getTitle() {
        return "Closing console";
    }

    @Override
    public void execute(final CommandContext context, MessagePrinter printer) throws Exception {
        ConsoleService service = ServiceUtils
                .loadProvider(this.getClass().getModule().getLayer(), false, new ServiceType<ConsoleService>() { })
                .orElseThrow();
        printer.printlnMessage("Ready to close console");
        service.close();
        printer.printlnMessage("Console was closed");
    }
}
