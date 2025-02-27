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

package com.techsenger.alpha.executor;

import com.beust.jcommander.JCommander;
import com.techsenger.alpha.api.Constants;
import com.techsenger.alpha.api.Framework;
import com.techsenger.alpha.api.command.CommandContext;
import com.techsenger.alpha.api.command.CommandDescriptor;
import com.techsenger.alpha.api.command.CommandResult;
import com.techsenger.alpha.api.command.CommandStatus;
import com.techsenger.alpha.api.command.ComponentsStateInfo;
import com.techsenger.alpha.api.command.DefaultCommandResult;
import com.techsenger.alpha.api.command.DefaultComponentsStateInfo;
import com.techsenger.alpha.api.command.ParameterDescriptor;
import com.techsenger.alpha.api.component.AbstractComponentObserver;
import com.techsenger.alpha.api.component.Component;
import com.techsenger.alpha.api.component.ComponentObserver;
import com.techsenger.alpha.api.executor.CommandExecutor;
import com.techsenger.alpha.api.executor.CommandInfos;
import com.techsenger.alpha.api.executor.CommandLine;
import com.techsenger.alpha.api.executor.CommandListener;
import com.techsenger.alpha.api.executor.CommandParser;
import com.techsenger.alpha.api.executor.CommandSkippedException;
import com.techsenger.alpha.api.executor.CommandSpecialSymbols;
import com.techsenger.alpha.api.executor.CommandText;
import com.techsenger.alpha.api.executor.DefaultCommandInfos;
import com.techsenger.alpha.api.executor.DefaultCommandLine;
import com.techsenger.alpha.api.executor.ParameterProvider;
import com.techsenger.alpha.api.executor.ProgressHandler;
import com.techsenger.alpha.api.executor.RemoteCommandException;
import com.techsenger.alpha.api.message.InMemoryMessagePrinter;
import com.techsenger.alpha.api.message.MessagePrinter;
import com.techsenger.alpha.api.net.security.AuthorizationException;
import com.techsenger.alpha.api.net.security.CommandSecurityContext;
import com.techsenger.alpha.api.net.session.Protocol;
import com.techsenger.alpha.api.net.session.SessionDescriptor;
import com.techsenger.alpha.spi.command.AbstractCommand;
import com.techsenger.alpha.spi.command.Command;
import com.techsenger.alpha.spi.command.CommandFactory;
import com.techsenger.alpha.spi.command.ContextParameter;
import com.techsenger.toolkit.core.SingletonFactory;
import com.techsenger.toolkit.core.StopWatch;
import com.techsenger.toolkit.core.StringUtils;
import com.techsenger.toolkit.core.jpms.ServiceUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
public final class CommandExecutorProvider implements CommandExecutor {

    /**
     * Singleton factory.
     */
    private static final SingletonFactory<CommandExecutor> singletonFactory =
            new SingletonFactory<>(() -> new CommandExecutorProvider());

    /**
     * JPMS provider.
     * @return
     */
    public static CommandExecutor provider() {
        return singletonFactory.singleton();
    }

    /**
     * Logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(CommandExecutorProvider.class);

    /**
     * This observer is used to find commands in other layers. DEAD LOCK BE CAREFUL.
     */
    private final class Observer extends AbstractComponentObserver {

        @Override
        public void onDeployed(Component component) {
            CommandExecutorProvider.this.initializeFactories(component);
        }

        @Override
        public void onUndeployed(Component component) {
            CommandExecutorProvider.this.deinitializeFactories(component);
        }
    };

    /**
     * commandClass:list - classAndService.
     */
    private final Map<String, CommandDescriptor> commandsByName = new ConcurrentHashMap<>();

    /**
     * Components can be either in separate or child layers. Using child layer we can get parent layer services.
     * So, to avoid duplicates we save worked factories here.
     */
    private final Set<CommandFactory> factories = ConcurrentHashMap.newKeySet();

    /**
     * Observer that will observer adding/removing commandClass factories.
     */
    private final ComponentObserver observer = new Observer();

    /**
     * Listeners.
     */
    private final Set<CommandListener> listeners = ConcurrentHashMap.newKeySet();

    /**
     * One script can execute another script, so we need to know stack depth. Starts from 0.
     */
    private int executionLevel = -1;

    /**
     * Constructor.
     */
    CommandExecutorProvider() {

    }

    @Override
    public CommandContext createContext(ParameterProvider parameterProvider) {
        var context = new DefaultCommandContext(this, parameterProvider);
        return context;
    }

    @Override
    public synchronized List<CommandResult> executeCommands(String text, CommandContext c, String scriptName,
            final ProgressHandler handler, int outputWidth) throws Exception {
        var context = (DefaultCommandContext) c;
        try {
            if (this.executionLevel == -1) {
                ExecutionProgress progress = new ExecutionProgress(handler);
                progress.initialize();
                context.setProgress(progress);
            }
            this.executionLevel++;
            CommandText commandText = CommandParser.parseText(text);
            List<CommandResult> commandResults = new ArrayList<>();
            if (commandText.getLines().size() == 0) {
                return commandResults;
            }
            var progress = context.getProgress();
            progress.setScriptDetails(scriptName, executionLevel, commandText.getLines().size());
            int commandIndex = 0;
            for (String textLine : commandText.getLines()) {
                logger.debug("Executing command: {}", textLine);
                DefaultCommandLine commandLine = null;
                //it is result for both local and remote commands(because there can be no result from the remote server)
                DefaultCommandResult commandResult = new DefaultCommandResult();
                commandResults.add(commandResult);
                try {
                    //parameters
                    commandLine = CommandParser.parseLine(textLine);
                    commandResult.setCommandName(commandLine.getName());
                    logger.trace("Parsed CommandLine: {}", commandLine);
                    progress.createDetails(commandIndex, commandLine.getName(), commandResult);
                    boolean commandRemote = false;
                    if (context.getProperty(ContextParameter.SESSION_DESCRIPTOR) != null) {
                        commandRemote = true;
                    }
                    if (commandLine.getPrefix() != null && commandLine.getPrefix()
                            .equals(CommandSpecialSymbols.LOCAL_COMMAND)) {
                        commandRemote = false;
                    }
                    if (!commandRemote) {
                        var printer = new InMemoryMessagePrinter();
                        printer.setWidth(outputWidth);
                        commandResult.setMessages(printer.getMessages());
                        var componentsStatusInfo = new DefaultComponentsStateInfo();
                        commandResult.setComponentsStateInfo(componentsStatusInfo);
                        componentsStatusInfo.setInitialState(Framework.getComponentManager().getComponentsState());
                        this.executeLocalCommand(commandLine, context, printer);
                        componentsStatusInfo.setFinalState(Framework.getComponentManager().getComponentsState());
                    } else {
                        progress.setTitle(StringUtils.format("Sending the command {} to the remote framework",
                                commandLine.getName()));
                        progress.callBeforeExecution();
                        //we don't remove AbstractCommand.REMOTE_PARAMETER from commandClass string. So,
                        //using commandClass.isRemote we can find type of the commandClass after creating
                        //commandClass instance.
                        commandResult.setExecutedRemotely(true);
                        var sessionDescriptor = (SessionDescriptor) context
                                .getProperty(ContextParameter.SESSION_DESCRIPTOR);
                        if (sessionDescriptor == null) {
                            throw new Exception("No active session found");
                        }
                        var remoteResult = this.sendRemoteCommand(commandLine, outputWidth,
                                sessionDescriptor.getProtocol(), sessionDescriptor.getName());
                        commandResult.setMessages((List) remoteResult.getMessages());
                        commandResult.setComponentsStateInfo(remoteResult.getComponentsStateInfo());
                    }
                    commandResult.setStatus(CommandStatus.SUCCESS);
                    progress.callAfterExecution();
                } catch (CommandSkippedException ex) {
                    commandResult.setStatus(CommandStatus.SKIPPED);
                    progress.callAfterExecution();
                    logger.info("Command '{}' skipped", commandLine.getName());
                } catch (Exception ex) {
                    commandResult.setStatus(CommandStatus.FAILURE);
                    progress.callAfterExecution();
                    logger.error("Error executing command '{}'{}{}",
                                commandLine.getName(), System.lineSeparator(),
                                ex.getMessage());
                    if (ex instanceof ContinueOnFailException) {
                        logger.info("Command '{}' has --continue-on-fail parameter. Continuing...",
                                commandLine.getName());
                    } else {
                        progress.callOnError();
                        throw new Exception("Error executing command '" + commandLine.getName() + "'", ex);
                    }
                }
                commandIndex++;
            }
            return commandResults;
        } catch (Exception ex) {
            throw ex;
        } finally {
            this.executionLevel--;
            if (this.executionLevel == -1 && context.getProgress() != null) {
                context.getProgress().deinitialize();
                context.setProgress(null);
            }
        }
    }

    @Override
    public synchronized ComponentsStateInfo executeRemoteCommand(CommandLine commandLine,
            CommandSecurityContext securityContext, MessagePrinter printer) throws AuthorizationException,
            CommandSkippedException, RemoteCommandException {
        if (securityContext == null) {
            throw new IllegalArgumentException("SecurityContext can't be null");
        }
        try {
            DefaultCommandContext context = (DefaultCommandContext) securityContext.getCommandContext();
            String commandName = commandLine.getName();
            CommandDescriptor commandDescriptor = this.commandsByName.get(commandName);
            if (commandDescriptor == null) {
                throw new RemoteCommandException("Unknown remote command '" + commandName + "'");
            }
            if (!commandDescriptor.isRemote()) {
                throw new IllegalArgumentException("Can't execute non remote command");
            }
            var finalContext = context;
            var componentsStateInfo = new DefaultComponentsStateInfo();
            componentsStateInfo.setInitialState(Framework.getComponentManager().getComponentsState());
            securityContext.execute(() -> {
                this.doExecuteCommand(commandDescriptor, commandLine, false, finalContext, printer);
            });
            componentsStateInfo.setFinalState(Framework.getComponentManager().getComponentsState());
            return componentsStateInfo;
        } catch (AuthorizationException | CommandSkippedException | RemoteCommandException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Error executing remote command", ex);
            //we don't send to the client information about server problems.
            throw new RemoteCommandException();
        }
    }

    @Override
    public Map<String, CommandDescriptor> getCommandsByName() {
        return Collections.unmodifiableMap(this.commandsByName);
    }

    @Override
    public CommandInfos getCommandInfos() {
        var infos = new DefaultCommandInfos();
        synchronized (Framework.getComponentManager()) {
            infos.setItems(this.commandsByName.values().stream().collect(Collectors.toList()));
            infos.setComponentsState(Framework.getComponentManager().getComponentsState());
        }
        return infos;
    }

    @Override
    public void addListener(CommandListener listener) {
        this.listeners.add(listener);
        logger.debug("Added listener {}; now {} listeners", listener.getClass().getName(), this.listeners.size());
    }

    @Override
    public void removeListener(CommandListener listener) {
        this.listeners.remove(listener);
        logger.debug("Removed listener {}; now {} listeners", listener.getClass().getName(), this.listeners.size());
    }

    /**
     * Initializes executor.
     * @param component
     */
    synchronized void initialize(final Component component) {
        //initializing current factories in the current layer.
        this.initializeFactories(component);
        //for all future layers
        component.addObserver(observer);
    }

    /**
     * Deinitializes executor.
     * @param component
     */
    synchronized void deinitialize(final Component component) {
        component.removeObserver(observer);
        this.deinitializeFactories(component);
    }

    /**
     * Executes one local commandClass locally.
     * @param commandLine
     * @param printer
     * @param provider
     * @throws Exception
     */
    private void executeLocalCommand(CommandLine commandLine, DefaultCommandContext context, MessagePrinter printer)
            throws Exception {
        String commandName = commandLine.getName();
        CommandDescriptor commandDescriptor = this.commandsByName.get(commandName);
        if (commandDescriptor == null) {
            throw new Exception("Unknown local command '" + commandName + "'");
        }
        if (!commandDescriptor.isLocal()) {
            throw new IllegalArgumentException("Can't execute non local command");
        }
        this.doExecuteCommand(commandDescriptor, commandLine, true, context, printer);
    }

    /**
     * Sends the command to to server using client.
     *
     * @param commandLine
     * @param outputWidth
     * @param sessionProtocol
     * @param sessionName
     * @return
     * @throws Exception
     */
    private CommandResult sendRemoteCommand(CommandLine commandLine, int outputWidth, Protocol sessionProtocol,
            String sessionName) throws Exception {
        logger.debug("Looking for RemoteFrameworkDispatcher with context classloader: {}",
                            Thread.currentThread().getContextClassLoader());
        CommandResult remoteCommandResult = null;
        remoteCommandResult = Framework.getServiceManager()
                    .getClient(sessionProtocol).sendCommand(sessionName, commandLine, outputWidth);
        return remoteCommandResult;
    }

    /**
     * Execute local and remote commands. This is the final point of the command execution.
     * @param commandDescriptor
     * @param commandLine
     * @param printer
     * @param provider
     * @throws Exception
     */
    private void doExecuteCommand(CommandDescriptor commandDescriptor, CommandLine commandLine, boolean commandLocal,
            DefaultCommandContext context, MessagePrinter printer) throws Exception {
        Class<? extends Command> commandClass = (Class<? extends Command>) commandDescriptor.getCommandClass();
        Command command = ((CommandDescriptorImpl) commandDescriptor).getFactory().createCommand(commandClass);
        //jcommander will throw exception if there is problem with options
        JCommander jCommander = new JCommander(command, commandLine.getArgumentsAsArray());
        ((AbstractCommand) command).setDescriptor(commandDescriptor);
        ((AbstractCommand) command).setRemote(!commandLocal);
        if (command.isHelpRequested()) {
            printer.printlnMessage(this.buildCommandHelp(commandDescriptor));
        } else {
            ClassLoader savedClassLoader = null;
            try {
                savedClassLoader = Thread.currentThread().getContextClassLoader();
                var progress = context.getProgress();
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
                command.execute(context, printer);
                if (stopWatch != null) {
                    stopWatch.stop();
                    printer.printlnMessage(StringUtils.format("Total command execution time: {} seconds.",
                            stopWatch.elapsedSeconds()));
                }
            } catch (CommandSkippedException ex) {
                throw ex;
            } catch (Exception ex) {
                if (commandLocal && command.shouldContinueOnFail()) {
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
     * Initializes command factories of the component.
     *
     * @param component
     */
    private synchronized void initializeFactories(final Component component) {
        List<CommandFactory> factories =
                ServiceUtils.loadProviders(component.getLayer(), false, CommandFactory.class);
        for (CommandFactory factory : factories) {
            this.initializeFactory(factory);
        }
        logger.debug("For component {}{}{} initialized {} command factories",
                component.getDescriptor().getConfig().getName(), Constants.NAME_VERSION_SEPARATOR,
                component.getDescriptor().getConfig().getVersion(),
                factories.size());
    }

    /**
     * Deinitializes command factories of the component.
     * @param component
     */
    private synchronized void deinitializeFactories(final Component component) {
        //deinitializing current services in the current layer.
        List<CommandFactory> factories =
                ServiceUtils.loadProviders(component.getLayer(), false, CommandFactory.class);
        for (CommandFactory factory : factories) {
            this.deinitializeFactory(factory);
        }
        logger.debug("For component {}{}{} deinitialized {} command factories",
                component.getDescriptor().getConfig().getName(), Constants.NAME_VERSION_SEPARATOR,
                component.getDescriptor().getConfig().getVersion(),
                factories.size());
    }

    /**
     * Initializes one factory.
     *
     * @param factory
     */
    private synchronized void initializeFactory(final CommandFactory factory) {
        List<CommandDescriptor> addedCommands = new ArrayList<CommandDescriptor>();
        for (Class<? extends Command> klass : factory.getCommandClasses()) {
            CommandDescriptor commandDescriptor = new CommandDescriptorImpl(klass, factory);
            if (commandDescriptor.getName() == null) {
                continue;
            }
            if (!commandDescriptor.isLocal() && !commandDescriptor.isRemote()) {
                logger.error("CommandClass {} has no type (local/remote)", klass.getName());
                continue;
            }
            if (!this.commandsByName.containsKey(commandDescriptor.getName())) {
                this.commandsByName.put(commandDescriptor.getName(), commandDescriptor);
                addedCommands.add(commandDescriptor);
            } else {
                logger.error("Descriptor for {} already exists", commandDescriptor.getName());
            }
            logger.trace("CommandClass {} was initialized", klass.getName());
        }
        this.factories.add(factory);
        if (!addedCommands.isEmpty()) {
            addedCommands = Collections.unmodifiableList(addedCommands);
            for (var l : listeners) {
                try {
                    l.onAdded(addedCommands);
                } catch (Exception ex) {
                    logger.error("Error calling listener {}", l.getClass().getName(), ex);
                }
            }
        }
        logger.debug("Initialized factory {}, now {} factories", factory.getClass().getName(), this.factories.size());
    }

    /**
     * Deinitializes one factory.
     *
     * @param factory
     */
    private synchronized void deinitializeFactory(final CommandFactory factory) {
        List<CommandDescriptor> removedCommands = new ArrayList<CommandDescriptor>();
        for (Class<? extends Command> klass : factory.getCommandClasses()) {
            CommandDescriptor commandDescriptor = new CommandDescriptorImpl(klass, factory);
            if (commandDescriptor.getName() == null) {
                continue;
            }
            this.commandsByName.remove(commandDescriptor.getName());
            removedCommands.add(commandDescriptor);
            logger.trace("CommandClass {} was deinitialized", klass.getName());
        }
        this.factories.remove(factory);
        if (!removedCommands.isEmpty()) {
            removedCommands = Collections.unmodifiableList(removedCommands);
            for (var l : listeners) {
                try {
                    l.onRemoved(removedCommands);
                } catch (Exception ex) {
                    logger.error("Error calling listener {}", l.getClass().getName(), ex);
                }
            }
        }
        logger.debug("Deinitialized factory {}, now {} factories", factory.getClass().getName(), this.factories.size());
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
}
