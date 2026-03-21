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

package com.techsenger.alpha.executor.impl.commands;

import com.beust.jcommander.Parameter;
import com.techsenger.alpha.core.api.Constants;
import com.techsenger.alpha.core.api.message.MessagePrinter;
import com.techsenger.alpha.executor.api.CommandContext;
import com.techsenger.alpha.executor.spi.AbstractCommand;
import com.techsenger.alpha.executor.spi.CommandMeta;
import com.techsenger.alpha.executor.spi.LocalCommand;
import com.techsenger.toolkit.core.StringUtils;
import java.nio.file.Paths;

/**
 *
 * @author Pavel Castornii
 */
@LocalCommand
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
        var config = context.getFramework().getComponentManager().addComponent(p);
        printer.printlnMessage(StringUtils.format("Component {}{}{} added from {}",
                config.getName(), Constants.NAME_VERSION_SEPARATOR, config.getVersion(), p));
    }

    String getPath() {
        return path;
    }
}
