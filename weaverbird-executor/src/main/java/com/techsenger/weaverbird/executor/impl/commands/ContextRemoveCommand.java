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
@CommandMeta(name = "context:remove", description = "Removes property from command context.")
public class ContextRemoveCommand extends AbstractCommand {

    /**
     * Key.
     */
    @Parameter(names = {"-k", "--key"}, required = true, description = "sets the key of the property")
    private String key;

    @Override
    public String getTitle() {
        return StringUtils.format("Removing context property [{}]", this.key);
    }

    @Override
    public void execute(final CommandContext context, MessagePrinter printer) throws Exception {
        context.getProperties().remove(key);
        printer.printlnMessage(StringUtils.format("Removed property with key [{}] from context", key));
    }
}
