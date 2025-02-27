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
import com.techsenger.alpha.api.message.MessagePrinter;
import com.techsenger.alpha.api.net.session.SessionDescriptor;
import com.techsenger.alpha.api.net.session.SessionStatus;
import com.techsenger.alpha.spi.command.CommandMeta;
import com.techsenger.alpha.spi.command.ContextParameter;
import com.techsenger.alpha.spi.command.LocalCommand;
import java.util.Objects;

/**
 *
 * @author Pavel Castornii
 */
@LocalCommand
@CommandMeta(name = "session:close", description =
        "Detaches from the session with the specified name, if it is currently used, and closes it.")
public class SessionCloseCommand extends AbstractSessionNameCommand {

    @Override
    public String getTitle() {
        return "Closing the connecion '" + getName() + "'";
    }

    @Override
    public void execute(CommandContext context, MessagePrinter printer) throws Exception {
        var session = (SessionDescriptor) context.getProperty(ContextParameter.SESSION_DESCRIPTOR);
        if (Objects.equals(session.getName(), getName())) {
            detachFrom(context, printer);
        }
        var sessionInfo = Framework.getServiceManager().getSessionInfosByName().get(getName());
        if (sessionInfo == null || sessionInfo.getStatus() == SessionStatus.CLOSED) {
            printer.printlnMessage("The session '" + getName() + "' is already closed.");
        } else {
            Framework.getServiceManager().getClient(sessionInfo.getProtocol()).closeSession(getName());
            printer.printlnMessage("Closed the session '" + getName() + "'");
        }
    }

}
