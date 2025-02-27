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

package com.techsenger.alpha.commands;

import com.beust.jcommander.Parameter;
import com.techsenger.alpha.api.Constants;
import com.techsenger.alpha.api.Framework;
import com.techsenger.alpha.api.command.CommandContext;
import com.techsenger.alpha.api.message.MessagePrinter;
import com.techsenger.alpha.spi.command.CommandMeta;
import com.techsenger.alpha.spi.command.LocalCommand;
import com.techsenger.alpha.spi.command.RemoteCommand;
import com.techsenger.toolkit.core.StringUtils;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Pavel Castornii
 */
@LocalCommand
@RemoteCommand
@CommandMeta(name = "component:deploy", description = "Deploys the component by creating its ModuleLayer")
public class ComponentDeployCommand extends AbstractComponentReferenceCommand {

    @Parameter(names = {"-a", "--alias"}, required = false, description = "sets the alias of the component")
    private String alias;

    @Parameter(names = {"--parent-ids"}, required = false, description = "sets the ids of the parent components")
    private List<Integer> parentIds = new ArrayList<>();

    @Parameter(names = {"--parent-aliases"}, required = false,
            description = "sets the aliases of the parent components")
    private List<String> parentAliases = new ArrayList<>();

    @Parameter(names = {"--use-parent-cl"}, required = false,
            description = "sets whether the class loader of the parent component should be used; "
                    + "can be used only for components with one parent having one loader")
    private boolean useParentClassLoader;

    @Override
    public String getTitle() {
        return StringUtils.format("Deploying {}{}{}", getName(), Constants.NAME_VERSION_SEPARATOR, getVersion());
    }

    @Override
    public void execute(final CommandContext context, MessagePrinter printer) throws Exception {
        Framework.getComponentManager().deployComponent(getName(), getVersion(), this.alias,
                this.parentIds, this.parentAliases, useParentClassLoader);
        printer.printlnMessage(StringUtils.format("Component {}{}{} deployed", getName(),
                Constants.NAME_VERSION_SEPARATOR, getVersion()));
    }

    String getAlias() {
        return alias;
    }

    List<Integer> getParentIds() {
        return parentIds;
    }

    List<String> getParentAliases() {
        return parentAliases;
    }

    public boolean usesParentClassLoader() {
        return useParentClassLoader;
    }
}
