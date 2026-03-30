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

import com.beust.jcommander.Parameter;
import com.techsenger.alpha.core.api.Constants;
import com.techsenger.alpha.core.api.message.MessagePrinter;
import com.techsenger.alpha.executor.api.CommandContext;
import com.techsenger.alpha.executor.spi.CommandMeta;
import com.techsenger.alpha.executor.spi.LocalCommand;
import com.techsenger.toolkit.core.StringUtils;
import java.nio.file.Paths;

/**
 *
 * @author Pavel Castornii
 */
@LocalCommand
@CommandMeta(name = "component:build", description = "Builds the component distribution archive")
public class ComponentBuildCommand extends AbstractComponentNameVerCommand {

    /**
     * Archive path.
     */
    @Parameter(names = {"-p", "--path"}, required = true,
            description = "sets the path to the directory where the built component archive will be saved")
    private String path;

    /**
     * Built archive extension.
     */
    @Parameter(names = {"-e"}, required = false,
            description = "sets the extension of the built archive; default is 'zip'")
    private String extension = "zip";

    @Override
    public String getTitle() {
        return StringUtils.format("Building {}{}{}", getName(), Constants.NAME_VERSION_SEPARATOR, getVersion());
    }

    @Override
    public void execute(CommandContext context, MessagePrinter printer) throws Exception {
        var p = Paths.get(path);
        var archivePath = context.getFramework().getComponentManager()
                .buildComponent(getName(), getVersion(), p, extension);
        printer.printlnMessage(StringUtils.format("Saved {}{}{} archive to {}",
                getName(), Constants.NAME_VERSION_SEPARATOR, getVersion(), archivePath.toAbsolutePath()));
    }
}


