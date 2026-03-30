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
import com.techsenger.alpha.executor.spi.CommandMeta;
import com.techsenger.alpha.executor.spi.LocalCommand;
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
        var session = context.getSession();
        if (Objects.equals(session.getName(), getName())) {
            detachFrom(context, printer);
        }
        var sessionInfo = context.getClient().getSessionsByName().get(getName());
        if (sessionInfo == null || sessionInfo.isClosed()) {
            printer.printlnMessage("The session '" + getName() + "' is already closed.");
        } else {
            context.getClient().closeSession(getName());
            printer.printlnMessage("Closed the session '" + getName() + "'");
        }
    }

}
