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

package com.techsenger.weaverbird.executor.api;

import com.techsenger.weaverbird.executor.api.command.CommandResult;

/**
 * This interface provides the details about the command that is being executed.
 *
 * @author Pavel Castornii
 */
public interface CommandExecutionDetails {

    /**
     * Returns the command index (in total commands).
     * @return
     */
    int getIndex();

    /**
     * Returns the command title.
     * @return
     */
    String getTitle();

    /**
     * Returns the command name.
     * @return
     */
    String getName();

    /**
     * Returns the command result.
     * @return result or null.
     */
    CommandResult getResult();
}
