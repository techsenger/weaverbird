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

package com.techsenger.alpha.executor.impl;

import com.beust.jcommander.JCommander;
import com.techsenger.alpha.core.api.Framework;
import com.techsenger.alpha.core.api.message.InMemoryMessagePrinter;
import com.techsenger.alpha.core.api.message.MessagePrinter;
import com.techsenger.alpha.executor.api.CommandContext;
import com.techsenger.alpha.executor.api.CommandExecutor;
import com.techsenger.alpha.executor.api.CommandSkippedException;
import com.techsenger.alpha.executor.api.CommandSyntax;
import com.techsenger.alpha.executor.api.ParameterProvider;
import com.techsenger.alpha.executor.api.ProgressHandler;
import com.techsenger.alpha.executor.api.command.CommandInfo;
import com.techsenger.alpha.executor.api.command.CommandResult;
import com.techsenger.alpha.executor.api.command.CommandStatus;
import com.techsenger.alpha.executor.api.command.DefaultCommandResult;
import com.techsenger.alpha.executor.api.command.ExecutionTarget;
import com.techsenger.alpha.executor.api.command.ParameterDescriptor;
import com.techsenger.alpha.executor.impl.commands.CommandListCommand;
import com.techsenger.alpha.executor.impl.commands.ComponentActivateCommand;
import com.techsenger.alpha.executor.impl.commands.ComponentAddCommand;
import com.techsenger.alpha.executor.impl.commands.ComponentBuildCommand;
import com.techsenger.alpha.executor.impl.commands.ComponentDeactivateCommand;
import com.techsenger.alpha.executor.impl.commands.ComponentDeployCommand;
import com.techsenger.alpha.executor.impl.commands.ComponentInstallCommand;
import com.techsenger.alpha.executor.impl.commands.ComponentListCommand;
import com.techsenger.alpha.executor.impl.commands.ComponentRemoveCommand;
import com.techsenger.alpha.executor.impl.commands.ComponentResolveCommand;
import com.techsenger.alpha.executor.impl.commands.ComponentRestartCommand;
import com.techsenger.alpha.executor.impl.commands.ComponentStartCommand;
import com.techsenger.alpha.executor.impl.commands.ComponentStopCommand;
import com.techsenger.alpha.executor.impl.commands.ComponentUndeployCommand;
import com.techsenger.alpha.executor.impl.commands.ComponentUninstallCommand;
import com.techsenger.alpha.executor.impl.commands.ComponentUnresolveCommand;
import com.techsenger.alpha.executor.impl.commands.ContextAddCommand;
import com.techsenger.alpha.executor.impl.commands.ContextClearCommand;
import com.techsenger.alpha.executor.impl.commands.ContextListCommand;
import com.techsenger.alpha.executor.impl.commands.ContextRemoveCommand;
import com.techsenger.alpha.executor.impl.commands.FrameworkShutdownCommand;
import com.techsenger.alpha.executor.impl.commands.MessagePrintlnCommand;
import com.techsenger.alpha.executor.impl.commands.ModuleListCommand;
import com.techsenger.alpha.executor.impl.commands.ModuleUpdateCommand;
import com.techsenger.alpha.executor.impl.commands.ScriptExecuteCommand;
import com.techsenger.alpha.executor.impl.commands.ServiceListCommand;
import com.techsenger.alpha.executor.impl.commands.SessionAttachCommand;
import com.techsenger.alpha.executor.impl.commands.SessionCloseCommand;
import com.techsenger.alpha.executor.impl.commands.SessionDetachCommand;
import com.techsenger.alpha.executor.impl.commands.SessionListCommand;
import com.techsenger.alpha.executor.impl.commands.SessionOpenCommand;
import com.techsenger.alpha.executor.impl.commands.ThreadDumpCommand;
import com.techsenger.alpha.executor.impl.commands.ThreadListCommand;
import com.techsenger.alpha.executor.spi.AbstractCommand;
import com.techsenger.alpha.executor.spi.Command;
import com.techsenger.alpha.executor.spi.CommandDescriptor;
import com.techsenger.alpha.executor.spi.CommandService;
import com.techsenger.alpha.net.client.api.ClientService;
import com.techsenger.toolkit.core.StopWatch;
import com.techsenger.toolkit.core.StringUtils;
import com.techsenger.toolkit.core.jpms.ServiceUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
public final class DefaultCommandExecutor implements CommandExecutor {

    private static final Logger logger = LoggerFactory.getLogger(DefaultCommandExecutor.class);

    private final Map<String, CommandDescriptor> commandsByName = new ConcurrentHashMap<>();

    private final DefaultCommandContext commandContext;

    /**
     * One script can execute another script, so we need to know stack depth. Starts from 0.
     */
    private int executionLevel = -1;

    /**
     * Constructor.
     */
    public DefaultCommandExecutor(Framework framework, ClientService client) throws Exception {
        this.commandContext = new DefaultCommandContext(framework, this, client);
        var commandProviders = ServiceUtils.loadProviders(DefaultCommandExecutor.class.getModule().getLayer(), false,
                CommandService.class);
        var customCommands = commandProviders.stream()
                .flatMap(service -> service.getCommands().stream())
                .collect(Collectors.toList());
        var defaultCommands = createDefaultCommands();
        var allCommands = Stream.concat(customCommands.stream(), defaultCommands.stream()).collect(Collectors.toList());
        for (var command : allCommands) {
            var descriptor = new CommandDescriptorImpl(command);
            var previous = commandsByName.put(descriptor.getName(), descriptor);
            if (previous != null) {
                throw new Exception("Found multiple commands with the same name " + descriptor.getName()
                        + "; one in " + previous.getType().getModule() + " and another in "
                        + descriptor.getType().getModule());
            }
        }
    }

    @Override
    public void setParameterProvider(ParameterProvider parameterProvider) {
        this.commandContext.setParameterProvider(parameterProvider);
    }

    @Override
    public synchronized List<CommandResult> executeCommands(String text, String scriptName,
            final ProgressHandler handler, int outputWidth) throws Exception {
        try {
            if (this.executionLevel == -1) {
                ExecutionProgress progress = new ExecutionProgress(handler);
                progress.initialize();
                commandContext.setProgress(progress);
            }
            this.executionLevel++;
            var commands = CommandParser.parse(text);
            List<CommandResult> commandResults = new ArrayList<>();
            if (commands.size() == 0) {
                return commandResults;
            }
            var progress = commandContext.getProgress();
            progress.setScriptDetails(scriptName, executionLevel, commands.size());
            int commandIndex = 0;
            for (var command : commands) {
                logger.debug("Executing command: {}", command.getName());
                //it is result for both local and remote commands(because there can be no result from the remote server)
                DefaultCommandResult commandResult = new DefaultCommandResult();
                commandResults.add(commandResult);
                try {
                    commandResult.setCommandName(command.getName());
                    progress.createDetails(commandIndex, command.getName(), commandResult);
                    commandContext.setExecutionTarget(ExecutionTarget.REMOTE);
                    if (commandContext.getSession() == null || (command.getPrefix() != null && command.getPrefix()
                            .equals(CommandSyntax.LOCAL_COMMAND))) {
                        commandContext.setExecutionTarget(ExecutionTarget.LOCAL);
                    }

                    var printer = new InMemoryMessagePrinter();
                    printer.setWidth(outputWidth);
                    commandResult.setMessages(printer.getMessages());
                    this.executeCommand(command, printer);
                    commandResult.setStatus(CommandStatus.SUCCESS);
                    progress.callAfterExecution();
                } catch (CommandSkippedException ex) {
                    commandResult.setStatus(CommandStatus.SKIPPED);
                    progress.callAfterExecution();
                    logger.info("Command '{}' skipped", command.getName());
                } catch (Exception ex) {
                    commandResult.setStatus(CommandStatus.FAILURE);
                    progress.callAfterExecution();
                    logger.error("Error executing command '{}'{}{}",
                                command.getName(), System.lineSeparator(),
                                ex.getMessage());
                    if (ex instanceof ContinueOnFailException) {
                        logger.info("Command '{}' has --continue-on-fail parameter. Continuing...",
                                command.getName());
                    } else {
                        progress.callOnError();
                        throw new Exception("Error executing command '" + command.getName() + "'", ex);
                    }
                }
                commandIndex++;
            }
            return commandResults;
        } catch (Exception ex) {
            throw ex;
        } finally {
            this.executionLevel--;
            if (this.executionLevel == -1 && commandContext.getProgress() != null) {
                commandContext.getProgress().deinitialize();
                commandContext.setProgress(null);
            }
        }
    }

    @Override
    public Map<String, CommandInfo> getCommandsByName() {
        var map = (Map<String, CommandInfo>) (Map<?, ?>) this.commandsByName;
        return Collections.unmodifiableMap(map);
    }

    @Override
    public CommandContext getCommandContext() {
        return this.commandContext;
    }

    private void executeCommand(ParsedCommand parsedCommand, MessagePrinter printer) throws Exception {
        String commandName = parsedCommand.getName();
        CommandDescriptor commandDescriptor = this.commandsByName.get(commandName);
        if (commandDescriptor == null) {
            throw new Exception("Unknown command '" + commandName + "'");
        }
        if (!commandDescriptor.isLocal()) {
            throw new IllegalArgumentException("Can't execute non local command");
        }
        Class<? extends Command> commandClass = (Class<? extends Command>) commandDescriptor.getType();
        Command command = ((CommandDescriptorImpl) commandDescriptor).getType().getDeclaredConstructor().newInstance();
        //jcommander will throw exception if there is problem with options
        JCommander jCommander = new JCommander(command, parsedCommand.getArgumentsAsArray());
        ((AbstractCommand) command).setDescriptor(commandDescriptor);
        if (command.isHelpRequested()) {
            printer.printlnMessage(this.buildCommandHelp(commandDescriptor));
        } else {
            ClassLoader savedClassLoader = null;
            try {
                savedClassLoader = Thread.currentThread().getContextClassLoader();
                var progress = commandContext.getProgress();
                if (progress != null) {
                    progress.setTitle(command.getTitle());
                    progress.callBeforeExecution();
                }
                //we execute every command with its own class loader in context
                Thread.currentThread().setContextClassLoader(command.getClass().getClassLoader());
                StopWatch stopWatch = null;
                if (command.isStopWatchUsed()) {
                    stopWatch = StopWatch.create();
                }
                command.execute(commandContext, printer);
                if (stopWatch != null) {
                    stopWatch.stop();
                    printer.printlnMessage(StringUtils.format("Total command execution time: {} seconds.",
                            stopWatch.elapsedSeconds()));
                }
            } catch (CommandSkippedException ex) {
                throw ex;
            } catch (Exception ex) {
                if (command.shouldContinueOnFail()) {
                    throw new ContinueOnFailException(ex);
                } else {
                    throw ex;
                }
            } finally {
                Thread.currentThread().setContextClassLoader(savedClassLoader);
            }
        }
    }

    /**
     * Builds command help.
     * @param commandDescriptor
     * @return
     */
    private String buildCommandHelp(CommandDescriptor commandDescriptor) {
        if (commandDescriptor.getDescription() == null) {
            return "No help available for this command.";
        }
        StringBuilder mainBuilder = new StringBuilder();
        mainBuilder.append(commandDescriptor.getDescription() + "\n");
        mainBuilder.append("Usage: ");
        mainBuilder.append(commandDescriptor.getName());
        if (commandDescriptor.getParameters() == null || commandDescriptor.getParameters().isEmpty()) {
            mainBuilder.append(System.lineSeparator());
            return mainBuilder.toString();
        }

        mainBuilder.append(" [parameters] ");
        mainBuilder.append(System.lineSeparator());

        StringBuilder parameterBuilder = new StringBuilder();
        String newLine = "";
        var parameters = new ArrayList<ParameterDescriptor>(commandDescriptor.getParameters());
        parameters.sort((p1, p2) -> Boolean.compare(p2.isMain(), p1.isMain())); //main parameter is the first one
        for (final var parameter : parameters) {
            parameterBuilder.append(newLine);
            parameterBuilder.append("    ");
            if (parameter.isRequired()) {
                parameterBuilder.append(" * ");
            } else {
                parameterBuilder.append("   ");
            }
            //sometime we can have only one or even no parameter
            if (parameter.getShortName() != null) {
                parameterBuilder.append(parameter.getShortName());
            }
            if (parameter.getLongName() != null) {
                if (parameter.getShortName() != null) {
                    parameterBuilder.append(", ");
                }
                parameterBuilder.append(parameter.getLongName());
            }
            parameterBuilder.append(" ");
            parameterBuilder.append(parameter.getDescription());
            newLine = System.lineSeparator();
        }
        if (parameterBuilder.length() != 0) {
            mainBuilder.append("The following parameters can be used:\n");
            mainBuilder.append(parameterBuilder.toString());
        }
        return mainBuilder.toString();
    }


    private List<Class<? extends Command>> createDefaultCommands() {
        return List.of(
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
//                LoggerConfigureCommand.class,
//                LogPrintCommand.class,
                MessagePrintlnCommand.class,
                ModuleListCommand.class,
                ModuleUpdateCommand.class,
                ScriptExecuteCommand.class,
//                ServerStartCommand.class,
//                ServerStopCommand.class,
                ServiceListCommand.class,
                SessionAttachCommand.class,
                SessionCloseCommand.class,
                SessionDetachCommand.class,
                SessionListCommand.class,
                SessionOpenCommand.class,
                ThreadDumpCommand.class,
                ThreadListCommand.class);
    }
}
