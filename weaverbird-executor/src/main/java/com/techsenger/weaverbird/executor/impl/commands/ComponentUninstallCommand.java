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

import com.techsenger.weaverbird.core.api.Constants;
import com.techsenger.weaverbird.core.api.component.ComponentConfigDto;
import com.techsenger.weaverbird.core.api.message.MessagePrinter;
import com.techsenger.weaverbird.executor.api.CommandContext;
import com.techsenger.weaverbird.executor.spi.CommandMeta;
import com.techsenger.weaverbird.executor.spi.LocalCommand;
import com.techsenger.weaverbird.executor.spi.RemoteCommand;
import com.techsenger.weaverbird.net.client.api.DomainClient;
import com.techsenger.toolkit.core.StringUtils;

/**
 *
 * @author Pavel Castornii
 */
@LocalCommand
@RemoteCommand
@CommandMeta(name = "component:uninstall", description = "Uninstalls the component (unresolves and removes it)")
public class ComponentUninstallCommand extends ComponentRemoveCommand {

    @Override
    public String getTitle() {
        return StringUtils.format("Uninstalling {}{}{}", getName(), Constants.NAME_VERSION_SEPARATOR, getVersion());
    }

    @Override
    public void execute(final CommandContext context, MessagePrinter printer) throws Exception {
        ComponentConfigDto config;
        if (context.isExecutionLocal()) {
            config = context.getFramework().getComponentManager().uninstallComponent(getName(), getVersion(), printer);
        } else {
            var client = new DomainClient(context.getClient(), context.getSession());
            config = client.uninstallComponent(getName(), getVersion());
        }

        printer.printlnMessage(StringUtils.format("Component {}{}{} uninstalled", config.getName(),
                Constants.NAME_VERSION_SEPARATOR, config.getVersion()));
    }
}
