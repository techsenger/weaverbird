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

import com.techsenger.alpha.spi.command.Command;
import com.techsenger.alpha.spi.command.CommandFactory;
import com.techsenger.toolkit.core.SingletonFactory;
import com.techsenger.toolkit.core.collection.SetUtils;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
public class CommandFactoryProvider implements CommandFactory {

    private static final Logger logger = LoggerFactory.getLogger(CommandFactoryProvider.class);

    private static final SingletonFactory<CommandFactory> singletonFactory =
            new SingletonFactory<>(() -> new CommandFactoryProvider());

    public static final CommandFactory provider() {
        return singletonFactory.singleton();
    }

    @Override
    public Set<Class<? extends Command>> getCommandClasses() {
        return SetUtils.newHashSet(
                CommandListCommand.class,
                ComponentActivateCommand.class,
                ComponentAddCommand.class,
                ComponentBuildCommand.class,
                ComponentDeactivateCommand.class,
                ComponentDeployCommand.class,
                ComponentInstallCommand.class,
                ComponentListCommand.class,
                ComponentRemoveCommand.class,
                ComponentResolveCommand.class,
                ComponentRestartCommand.class,
                ComponentStartCommand.class,
                ComponentStopCommand.class,
                ComponentUndeployCommand.class,
                ComponentUninstallCommand.class,
                ComponentUnresolveCommand.class,
                ContextAddCommand.class,
                ContextClearCommand.class,
                ContextListCommand.class,
                ContextRemoveCommand.class,
                FrameworkShutdownCommand.class,
                LogPrintCommand.class,
                LoggerConfigureCommand.class,
                MessagePrintlnCommand.class,
                ModuleListCommand.class,
                ModuleUpdateCommand.class,
                ScriptExecuteCommand.class,
                ServiceListCommand.class,
                ThreadDumpCommand.class,
                ThreadListCommand.class
        );
    }

    @Override
    public <T extends Command> T createCommand(Class<T> klass) {
        try {
            return klass.getConstructor().newInstance();
        } catch (Exception ex) {
            logger.error("Error creating instance of command", ex);
            return null;
        }
    }

}
