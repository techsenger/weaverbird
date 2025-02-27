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

package com.techsenger.alpha.commands;

import com.beust.jcommander.Parameter;
import com.techsenger.alpha.api.command.CommandContext;
import com.techsenger.alpha.api.message.MessagePrinter;
import com.techsenger.alpha.spi.command.AbstractCommand;
import com.techsenger.alpha.spi.command.CommandMeta;
import com.techsenger.alpha.spi.command.LocalCommand;
import com.techsenger.alpha.spi.command.RemoteCommand;
import com.techsenger.toolkit.core.StringUtils;

/**
 *
 * @author Pavel Castornii
 */
@LocalCommand
@RemoteCommand
@CommandMeta(name = "context:add", description = "Adds property in command context.")
public class ContextAddCommand extends AbstractCommand {

    /**
     * Key.
     */
    @Parameter(names = {"-k", "--key"}, required = true, description = "sets the key of the property")
    private String key;

    /**
     * Value.
     */
    @Parameter(names = {"-v", "--value"}, required = true, description = "sets the value of the property")
    private String value;

    @Override
    public String getTitle() {
        return StringUtils.format("Adding context property [{}]", this.key);
    }

    @Override
    public void execute(final CommandContext context, MessagePrinter printer) throws Exception {
        context.addProperty(key, value);
        printer.printlnMessage(StringUtils
                .format("Added property with key [{}] and value [{}] to context", key, value));
    }
}
