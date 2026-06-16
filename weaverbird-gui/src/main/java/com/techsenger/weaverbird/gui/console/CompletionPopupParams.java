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

package com.techsenger.weaverbird.gui.console;

import com.techsenger.shellfx.core.popup.PopupParams;
import com.techsenger.weaverbird.executor.api.command.CommandInfo;
import com.techsenger.weaverbird.executor.api.command.ParameterDescriptor;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Pavel Castornii
 */
public class CompletionPopupParams extends PopupParams {

    private final Collection<CommandInfo> commands;

    private final boolean sessionExists;

    private final List<ParameterDescriptor> parameterDescriptors;

    private final String token;

    private final CompletionPopupAwarePort popupAware;

    public CompletionPopupParams(Collection<CommandInfo> commands, boolean sessionExists,
            List<ParameterDescriptor> parameterDescriptors, String token, CompletionPopupAwarePort popupAware) {
        super(false);
        this.commands = commands;
        this.sessionExists = sessionExists;
        this.parameterDescriptors = parameterDescriptors;
        this.token = token;
        this.popupAware = popupAware;
    }

    public Collection<CommandInfo> getCommands() {
        return commands;
    }

    public boolean isSessionExists() {
        return sessionExists;
    }

    public List<ParameterDescriptor> getParameterDescriptors() {
        return parameterDescriptors;
    }

    public String getToken() {
        return token;
    }

    public CompletionPopupAwarePort getPopupAware() {
        return popupAware;
    }
}
