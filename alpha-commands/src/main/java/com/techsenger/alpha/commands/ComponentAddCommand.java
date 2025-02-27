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
import com.techsenger.alpha.spi.command.AbstractCommand;
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
@CommandMeta(name = "component:add", description = "Adds the component from a distro archive")
public class ComponentAddCommand extends AbstractCommand {

    @Parameter(names = {"-p", "--path"}, required = true, description = "sets the path to distro archive")
    private String path;

    @Override
    public String getTitle() {
        return StringUtils.format("Adding {}", Paths.get(path).toFile().getName());
    }

    @Override
    public void execute(CommandContext context, MessagePrinter printer) throws Exception {
        var p = Paths.get(path);
        var config = Framework.getComponentManager().addComponent(p);
        printer.printlnMessage(StringUtils.format("Component {}{}{} added from {}",
                config.getName(), Constants.NAME_VERSION_SEPARATOR, config.getVersion(), p));
    }

    String getPath() {
        return path;
    }
}
