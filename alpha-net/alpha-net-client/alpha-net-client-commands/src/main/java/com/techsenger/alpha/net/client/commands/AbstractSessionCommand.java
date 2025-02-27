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

import com.techsenger.alpha.api.command.CommandContext;
import com.techsenger.alpha.api.message.MessagePrinter;
import com.techsenger.alpha.api.net.session.SessionDescriptor;
import com.techsenger.alpha.api.net.session.SessionInfo;
import com.techsenger.alpha.spi.command.AbstractCommand;
import com.techsenger.alpha.spi.command.ContextParameter;

/**
 *
 * @author Pavel Castornii
 */
public abstract class AbstractSessionCommand extends AbstractCommand {

    protected void attachTo(SessionInfo info, CommandContext context, MessagePrinter printer) {
        context.addProperty(ContextParameter.SESSION_DESCRIPTOR, new SessionDescriptor(info));
        printer.printlnMessage("Attached to the session '" + info.getName() + "'");
    }

    protected void detachFrom(CommandContext context, MessagePrinter printer) {
        var descriptor = (SessionDescriptor) context.removeProperty(ContextParameter.SESSION_DESCRIPTOR);
        printer.printlnMessage("Detached from the session '" + descriptor.getName() + "'");
    }
}
