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

package com.techsenger.alpha.spi.console;

import com.techsenger.alpha.api.command.CommandDescriptor;
import com.techsenger.alpha.api.executor.CommandExecutor;
import com.techsenger.alpha.api.executor.CommandInfos;
import com.techsenger.alpha.api.executor.CommandListener;
import com.techsenger.alpha.api.net.session.SessionDescriptor;
import java.util.Collection;

/**
 * Holds both standalone/client and server command infos. It is used by highlighters and completers.
 *
 * @author Pavel Castornii
 */
public class DefaultCommandInfosManager implements CommandInfosManager {

    private final CommandExecutor executor;

    private volatile CommandInfos localInfos;

    private volatile CommandInfos remoteInfos;

    private volatile SessionDescriptor sessionDescriptor;

    private final CommandListener listener = new CommandListener() {

        @Override
        public void onAdded(Collection<CommandDescriptor> commands) {
            localInfos = executor.getCommandInfos();
        }

        @Override
        public void onRemoved(Collection<CommandDescriptor> commands) {
            localInfos = executor.getCommandInfos();
        }
    };

    public DefaultCommandInfosManager(CommandExecutor executor) {
        this.executor = executor;
        localInfos = executor.getCommandInfos();
        this.executor.addListener(listener);

    }

    public void deinitialize() {
        this.executor.removeListener(listener);
    }

    public final void setRemoteInfos(CommandInfos infos) {
        this.remoteInfos = infos;
    }

    @Override
    public SessionDescriptor getSessionDescriptor() {
        return sessionDescriptor;
    }

    public void setSessionDescriptor(SessionDescriptor sessionDescriptor) {
        this.sessionDescriptor = sessionDescriptor;
    }

    @Override
    public CommandInfos getLocalInfos() {
        return localInfos;
    }

    public void setLocalInfos(CommandInfos localInfos) {
        this.localInfos = localInfos;
    }

    @Override
    public CommandInfos getRemoteInfos() {
        return remoteInfos;
    }

}
