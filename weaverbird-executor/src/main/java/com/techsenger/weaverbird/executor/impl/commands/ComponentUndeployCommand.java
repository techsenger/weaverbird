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
import com.techsenger.weaverbird.core.api.component.ComponentDescriptorDto;
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
@CommandMeta(name = "component:undeploy", description = "Undeploys the component by destroying its JPMS layer.")
public class ComponentUndeployCommand extends AbstractComponentIdAliasCommand {

    @Override
    public String getTitle() {
        String identifier = null;
        if (getId() != null) {
            identifier = "with id " + String.valueOf(getId());
        } else {
            identifier = "with alias " + getAlias();
        }
        return StringUtils.format("Undeploying {}", identifier);
    }

    @Override
    public void execute(final CommandContext context, MessagePrinter printer) throws Exception {
        ComponentDescriptorDto descriptor = null;
        if (getId() != null) {
            if (context.isExecutionLocal()) {
                descriptor = context.getFramework().getComponentManager().undeployComponent(getId());
            } else {
                var client = new DomainClient(context.getClient(), context.getSession());
                client.undeployComponent(getId());
            }
        } else if (getAlias() != null) {
            if (context.isExecutionLocal()) {
                descriptor = context.getFramework().getComponentManager().undeployComponent(getAlias());
            } else {
                var client = new DomainClient(context.getClient(), context.getSession());
                client.undeployComponent(getAlias());
            }
        } else {
            throw new Exception("Either id or alias must be set");
        }
        printer.printlnMessage(StringUtils.format("Component {}{}{} undeployed", descriptor.getConfig().getName(),
                Constants.NAME_VERSION_SEPARATOR, descriptor.getConfig().getVersion()));
    }
}
