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

package com.techsenger.alpha.api.executor;

import com.techsenger.alpha.api.command.CommandContext;
import com.techsenger.alpha.api.command.CommandDescriptor;
import com.techsenger.alpha.api.command.CommandResult;
import com.techsenger.alpha.api.command.ComponentsStateInfo;
import com.techsenger.alpha.api.message.MessagePrinter;
import com.techsenger.alpha.api.net.security.AuthorizationException;
import com.techsenger.alpha.api.net.security.CommandSecurityContext;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Pavel Castornii
 */
public interface CommandExecutor {

    /**
     * Creates a context that can be used for executing commands.
     * @param parameterProvider or null
     * @return created command context.
     */
    CommandContext createContext(ParameterProvider parameterProvider);

    /**
     * Executes multiple commands separated with ";". This is main entry point. This method executes local commands
     * and dispatches remote commands.
     *
     * @param commandText
     * @param context
     * @param scriptName the name of the script or null.
     * @param handler or null
     * @param outputWidth
     * @return
     * @throws Exception
     */
    List<CommandResult> executeCommands(String commandText, CommandContext context, String scriptName,
            ProgressHandler handler, int outputWidth) throws Exception;

    /**
     * Executes one remote command locally. This method as a rule is called by http/rmi server.
     *
     * @param commandLine
     * @param securityContext
     * @param messagePrinter
     * @return the information about the states of the components.
     * @throws AuthorizationException
     * @throws CommandSkippedException
     * @throws RemoteCommandException
     */
    ComponentsStateInfo executeRemoteCommand(CommandLine commandLine, CommandSecurityContext securityContext,
            MessagePrinter messagePrinter) throws AuthorizationException, CommandSkippedException,
            RemoteCommandException;

    /**
     * Returns the unmodifiable map of command descriptors that this executor have found and can execute.
     *
     * @return
     */
    Map<String, CommandDescriptor> getCommandsByName();

    /**
     * Returns the information about all existing at the moment commands.
     *
     * @return
     */
    CommandInfos getCommandInfos();

    /**
     * Adds listener that will be called on adding/removing commands.
     *
     * @param listener
     */
    void addListener(CommandListener listener);

    /**
     * Removes listener that was called on adding/removing commands.
     *
     * @param listener
     */
    void removeListener(CommandListener listener);
}
