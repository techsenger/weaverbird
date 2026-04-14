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
import com.techsenger.weaverbird.executor.api.CommandSkippedException;
import com.techsenger.weaverbird.executor.api.command.Commands;
import com.techsenger.weaverbird.executor.spi.CommandMeta;
import com.techsenger.weaverbird.executor.spi.LocalCommand;

/**
 *
 * @author Pavel Castornii
 */
@LocalCommand
@CommandMeta(name = Commands.SESSION_DETACH, description = "Detaches from the session with the specified name.")
public class SessionDetachCommand extends AbstractSessionCommand {

    @Override
    public String getTitle() {
        return "Detaching from the current session";
    }

    @Override
    public void execute(CommandContext context, MessagePrinter printer) throws Exception {
        if (context.getSession() == null) {
            throw new CommandSkippedException("No session is used at the moment");
        } else {
            detachFrom(context, printer);
        }
    }
}
