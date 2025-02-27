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

import com.techsenger.alpha.api.Constants;
import com.techsenger.alpha.api.Framework;
import com.techsenger.alpha.api.command.CommandContext;
import com.techsenger.alpha.api.message.MessagePrinter;
import com.techsenger.alpha.spi.command.CommandMeta;
import com.techsenger.alpha.spi.command.LocalCommand;
import com.techsenger.alpha.spi.command.RemoteCommand;
import com.techsenger.toolkit.core.StringUtils;
import java.nio.file.Paths;


/**
 *
 * @author Pavel Castornii
 */
@LocalCommand
@RemoteCommand
@CommandMeta(name = "component:install", description = "Installs component (adds and resolves it)")
public class ComponentInstallCommand extends ComponentAddCommand {

    @Override
    public String getTitle() {
        return StringUtils.format("Installing {}", Paths.get(getPath()).toFile().getName());
    }

    @Override
    public void execute(final CommandContext context, MessagePrinter printer) throws Exception {
        var path = Paths.get(getPath());
        var config = Framework.getComponentManager().installComponent(path, printer);
        printer.printlnMessage(StringUtils.format("Component {}{}{} installed from {}",
                config.getName(), Constants.NAME_VERSION_SEPARATOR, config.getVersion(), path));
    }
}
