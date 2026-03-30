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

import com.techsenger.alpha.core.api.Constants;
import com.techsenger.alpha.core.api.component.ComponentDescriptorDto;
import com.techsenger.alpha.core.api.message.MessagePrinter;
import com.techsenger.alpha.executor.api.CommandContext;
import com.techsenger.alpha.executor.spi.CommandMeta;
import com.techsenger.alpha.executor.spi.LocalCommand;
import com.techsenger.alpha.executor.spi.RemoteCommand;
import com.techsenger.alpha.net.client.api.DomainClient;
import com.techsenger.toolkit.core.StringUtils;
import java.util.List;

/**
 *
 * @author Pavel Castornii
 */
@LocalCommand
@RemoteCommand
@CommandMeta(name = "component:restart", description = "Restarts the component.")
public class ComponentRestartCommand extends AbstractComponentIdAliasCommand {

    @Override
    public String getTitle() {
        String identifier = null;
        if (getId() != null) {
            identifier = "with id " + String.valueOf(getId());
        } else {
            identifier = "with alias " + getAlias();
        }
        return StringUtils.format("Restarting {}", identifier);
    }

    @Override
    public void execute(final CommandContext context, MessagePrinter printer) throws Exception {
        ComponentDescriptorDto descriptor = null;
        var framework = context.getFramework();
        if (getId() != null) {
            if (context.isExecutionLocal()) {
                descriptor = framework.getComponentManager().getComponent(getId()).getDescriptor();
            } else {
                var client = new DomainClient(context.getClient(), context.getSession());
                descriptor = client.getComponentDescriptor(getId());
            }

        } else if (getAlias() != null) {
            if (context.isExecutionLocal()) {
                descriptor = framework.getComponentManager().findComponent(getAlias()).getDescriptor();
            } else {
                var client = new DomainClient(context.getClient(), context.getSession());
                descriptor = client.getComponentDescriptor(getAlias());
            }
        } else {
            throw new Exception("Either id or alias must be set");
        }
        final List<Integer> parentIds = null;
        final List<String> parentAliases = null;
        if (!descriptor.getParents().isEmpty()) {
            descriptor.getParents().stream().forEach(d -> {
                        parentIds.add(d.getId());
                        parentAliases.add(d.getAlias());
                });
        }
        if (context.isExecutionLocal()) {
            //now we stop component
            framework.getComponentManager().stopComponent(descriptor.getId());
            //now we start component
            framework.getComponentManager().startComponent(descriptor.getConfig().getName(),
                    descriptor.getConfig().getVersion(), descriptor.getAlias(), parentIds, parentAliases,
                    descriptor.isParentClassLoaderUsed());
        } else {
            var client = new DomainClient(context.getClient(), context.getSession());
            client.stopComponent(descriptor.getId());
            client.startComponent(descriptor.getConfig().getName(),
                    descriptor.getConfig().getVersion(), descriptor.getAlias(), parentIds, parentAliases,
                    descriptor.isParentClassLoaderUsed());
        }

        printer.printlnMessage(StringUtils.format("Component {}{}{} restarted", descriptor.getConfig().getName(),
                Constants.NAME_VERSION_SEPARATOR, descriptor.getConfig().getVersion()));
    }
}
