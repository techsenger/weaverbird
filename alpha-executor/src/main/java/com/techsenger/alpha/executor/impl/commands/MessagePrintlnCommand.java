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

package com.techsenger.alpha.executor.impl.commands;

import com.beust.jcommander.Parameter;
import com.techsenger.alpha.executor.api.CommandContext;
import com.techsenger.alpha.core.api.message.MessagePrinter;
import com.techsenger.alpha.core.api.message.MessageType;
import com.techsenger.alpha.executor.spi.AbstractCommand;
import com.techsenger.alpha.executor.spi.CommandMeta;
import com.techsenger.alpha.executor.spi.LocalCommand;
import com.techsenger.alpha.executor.spi.MainParameter;
import com.techsenger.alpha.executor.spi.ParameterUtils;
import com.techsenger.alpha.executor.spi.RemoteCommand;
import com.techsenger.toolkit.core.StringUtils;

/**
 *
 * @author Pavel Castornii
 */
@LocalCommand
@RemoteCommand
@CommandMeta(name = "message:println", description = "Prints a message and moves the cursor to the next line.")
public class MessagePrintlnCommand extends AbstractCommand {

    /**
     * Message.
     */
    @MainParameter("message")
    @Parameter(required = true, description = "sets the message that will be printed")
    private String message;

    @Parameter(names = {"-t", "--type"}, required = false,
            description = "sets the type (output/error) of the message; default is output")
    private MessageType type = MessageType.OUTPUT;

    @Override
    public String getTitle() {
        return StringUtils.format("Printing message {}", this.message);
    }

    @Override
    public void execute(final CommandContext context, MessagePrinter printer) throws Exception {
        this.message = ParameterUtils.unquote(message);
        switch (type) {
            case OUTPUT:
                printer.printlnMessage(this.message);
                break;
            case ERROR:
                printer.printlnError(this.message);
                break;
            default:
                throw new AssertionError();
        }
    }
}
