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

package com.techsenger.alpha.net.server.http;

import com.sun.net.httpserver.HttpExchange;
import com.techsenger.alpha.api.Framework;
import com.techsenger.alpha.api.command.CommandStatus;
import com.techsenger.alpha.api.command.DefaultCommandResult;
import com.techsenger.alpha.api.executor.CommandSkippedException;
import com.techsenger.alpha.api.message.InMemoryMessagePrinter;
import com.techsenger.alpha.api.net.security.AuthenticationException;
import com.techsenger.alpha.net.shared.http.CommandExecuteRequest;
import com.techsenger.alpha.net.shared.http.CommandExecuteResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
public class CommandExecuteHandler extends AbstractHandler {

    private static final Logger logger = LoggerFactory.getLogger(CommandExecuteHandler.class);

    public CommandExecuteHandler(DefaultHttpServer httpServer) {
        super(httpServer);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            var request = getRequest(httpExchange, CommandExecuteRequest.class);
            CommandExecuteResponse response = null;
            if (isAuthenticated(request)) {
                HttpSession session = getHttpServer().getSession(request.getSessionUuid());
                InMemoryMessagePrinter messagePrinter = new InMemoryMessagePrinter();
                messagePrinter.setWidth(request.getOutputWidth());
                var callResult = call(() -> {
                    var statesInfo = Framework.getServiceManager()
                            .getCommandExecutor()
                            .executeRemoteCommand(request.getCommandLine(), session.getCommandSecurityContext(),
                                messagePrinter);
                    return statesInfo;
                });
                CommandStatus status = CommandStatus.SUCCESS;
                if (!callResult.isSuccessful()) {
                    status = CommandStatus.FAILURE;
                    if (callResult.getException() != null
                            &&  callResult.getException().getClass() == CommandSkippedException.class) {
                        status = CommandStatus.SKIPPED;
                    }
                }
                var remoteCommandResult = new DefaultCommandResult(request.getCommandLine().getName(),
                        status, messagePrinter.getMessages(), callResult.getResult());
                remoteCommandResult.setExecutedRemotely(true);
                response = new CommandExecuteResponse(remoteCommandResult, callResult.getException());
            } else {
                response = new CommandExecuteResponse(null, new AuthenticationException("Not allowed"));
            }

            sendResponse(response, httpExchange);
        } catch (Exception ex) {
            logger.error("Error processing request", ex);
        }
    }
}
