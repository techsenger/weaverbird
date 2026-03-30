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

package com.techsenger.alpha.executor.impl.commands;

import com.techsenger.alpha.core.api.message.MessagePrinter;
import com.techsenger.alpha.executor.api.CommandContext;
import com.techsenger.alpha.executor.api.CommandSkippedException;
import com.techsenger.alpha.executor.api.command.Commands;
import com.techsenger.alpha.executor.spi.CommandMeta;
import com.techsenger.alpha.executor.spi.LocalCommand;

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
