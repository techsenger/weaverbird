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

package com.techsenger.alpha.executor.api.command;

import com.techsenger.alpha.core.api.message.DefaultMessage;
import com.techsenger.alpha.core.api.message.Message;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Pavel Castornii
 */
public class DefaultCommandResult implements CommandResult, Serializable {

    private String commandName;

    private CommandStatus status;

    private List<DefaultMessage> messages;

    private boolean executedRemotely;

    public DefaultCommandResult() {

    }

    public DefaultCommandResult(String commandName, CommandStatus status, List<DefaultMessage> messages) {
        this.commandName = commandName;
        this.status = status;
        this.messages = messages;
    }

    public String getCommandName() {
        return commandName;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    @Override
    public CommandStatus getStatus() {
        return status;
    }

    public void setStatus(CommandStatus status) {
        this.status = status;
    }

    @Override
    public List<Message> getMessages() {
        return (List) messages;
    }

    public void setMessages(List<DefaultMessage> messages) {
        this.messages = messages;
    }

    @Override
    public boolean isExecutedRemotely() {
        return executedRemotely;
    }

    public void setExecutedRemotely(boolean executedRemotely) {
        this.executedRemotely = executedRemotely;
    }
}
