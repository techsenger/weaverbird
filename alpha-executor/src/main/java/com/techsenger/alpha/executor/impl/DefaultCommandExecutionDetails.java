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

import com.techsenger.alpha.executor.api.command.CommandResult;
import com.techsenger.alpha.executor.api.CommandExecutionDetails;

/**
 *
 *
 * @author Pavel Castornii
 */
class DefaultCommandExecutionDetails implements CommandExecutionDetails {

    private final int index;

    private final String name;

    private final CommandResult result;

    private String title;

    DefaultCommandExecutionDetails(int index, String name, CommandResult result) {
        this.index = index;
        this.name = name;
        this.result = result;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public CommandResult getResult() {
        return result;
    }

    void setTitle(String title) {
        this.title = title;
    }
}
