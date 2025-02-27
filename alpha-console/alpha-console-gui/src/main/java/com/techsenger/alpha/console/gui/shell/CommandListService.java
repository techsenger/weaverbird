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

package com.techsenger.alpha.console.gui.shell;

import com.techsenger.alpha.api.Framework;
import com.techsenger.alpha.api.executor.CommandInfos;
import com.techsenger.alpha.api.net.session.SessionDescriptor;
import com.techsenger.tabshell.kit.core.workertab.TabWorker;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 *
 * @author Pavel Castornii
 */
public class CommandListService extends Service<CommandInfos> implements TabWorker<CommandInfos> {

    private final SessionDescriptor descriptor;

    public CommandListService(SessionDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    @Override
    protected Task<CommandInfos> createTask() {
        return new Task<CommandInfos>() {
            @Override
            protected CommandInfos call() throws Exception {
                return Framework.getServiceManager().getClient(descriptor.getProtocol())
                        .getCommandInfos(descriptor.getName());
            }
        };
    }

    @Override
    public boolean usesProgress() {
        return false;
    }

}
