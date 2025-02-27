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

package com.techsenger.alpha.net.client.commands;

import com.techsenger.alpha.api.Framework;
import com.techsenger.alpha.api.command.CommandContext;
import com.techsenger.alpha.api.command.Commands;
import com.techsenger.alpha.api.message.MessagePrinter;
import com.techsenger.alpha.api.net.session.SessionStatus;
import com.techsenger.alpha.spi.command.CommandMeta;
import com.techsenger.alpha.spi.command.LocalCommand;

/**
 *
 * @author Pavel Castornii
 */
@LocalCommand
@CommandMeta(name = Commands.SESSION_ATTACH, description = "Attaches to the session with the specified name.")
public class SessionAttachCommand extends AbstractSessionNameCommand {

    @Override
    public String getTitle() {
        return "Attaching to the session '" + getName() + "'";
    }

    @Override
    public void execute(CommandContext context, MessagePrinter printer) throws Exception {
        var session = Framework.getServiceManager().getSessionInfosByName().get(getName());
        if (session == null) {
            throw new Exception("The session '" + getName() + "' doesn't exist");
        } else {
            if (session.getStatus() == SessionStatus.CLOSED) {
                throw new Exception("The session '" + getName() + "' is closed");
            } else {
                attachTo(session, context, printer);
            }
        }
    }
}
