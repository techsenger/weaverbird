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

import com.techsenger.toolkit.core.StringUtils;
import com.techsenger.weaverbird.core.api.Constants;
import com.techsenger.weaverbird.core.api.message.MessageArtifactEventListener;
import com.techsenger.weaverbird.core.api.message.MessagePrinter;
import com.techsenger.weaverbird.executor.api.CommandContext;
import com.techsenger.weaverbird.executor.spi.CommandMeta;
import com.techsenger.weaverbird.executor.spi.LocalCommand;
import com.techsenger.weaverbird.executor.spi.RemoteCommand;
import com.techsenger.weaverbird.net.client.api.DomainClient;

/**
 *
 * @author Pavel Castornii
 */
@LocalCommand
@RemoteCommand
@CommandMeta(name = "component:resolve", description = "Resolves the component by adding all its modules to repo")
public class ComponentResolveCommand extends AbstractComponentNameVerCommand {

    @Override
    public String getTitle() {
        return StringUtils.format("Resolving {}{}{}", getName(), Constants.NAME_VERSION_SEPARATOR, getVersion());
    }

    @Override
    public void execute(CommandContext context, MessagePrinter printer) throws Exception {
        if (context.isExecutionLocal()) {
            var listener = new MessageArtifactEventListener(printer, true);
            context.getFramework().getComponentManager().resolveComponent(getName(), getVersion(), listener);
        } else {
            var client = new DomainClient(context.getClient(), context.getSession());
            client.resolveComponent(getName(), getVersion());
        }
        printer.printlnMessage(StringUtils.format("Component {}{}{} resolved", getName(),
                Constants.NAME_VERSION_SEPARATOR, getVersion()));
    }
}
