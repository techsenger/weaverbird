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

package com.techsenger.alpha.executor.api.command;

import com.techsenger.alpha.core.api.message.Message;
import java.util.List;

/**
 *
 * @author Pavel Castornii
 */
public interface CommandResult {

    /**
     * Returns command name.
     *
     * @return
     */
    String getCommandName();

    /**
     * Returns the status of the command.
     *
     * @return
     */
    CommandStatus getStatus();

    /**
     * Returns the messages of the command in the order they were printed.
     *
     * @return
     */
    List<Message> getMessages();

    /**
     * Returns true if the command was executed on server side and false otherwise.
     *
     * @return
     */
    boolean isExecutedRemotely();
}
