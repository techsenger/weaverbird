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

import com.techsenger.alpha.api.executor.CommandSpecialSymbols;
import com.techsenger.alpha.api.net.session.SessionDescriptor;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
public abstract class AbstractCommandInfosConsumer {

    private static final Logger logger = LoggerFactory.getLogger(AbstractCommandInfosConsumer.class);

    private final CommandInfosManager manager;

    private int remoteComponentsStateId;

    private int localComponentsStateId;

    private SessionDescriptor sessionDescriptor;

    public AbstractCommandInfosConsumer(CommandInfosManager manager) {
        this.manager = manager;
    }

    protected CommandInfosManager getManager() {
        return manager;
    }

    protected boolean isRemote(String command) {
        if (manager.getSessionDescriptor() != null) {
            if (!command.startsWith(CommandSpecialSymbols.LOCAL_COMMAND)) {
                return true;
            }
        }
        return false;
    }

    protected String stripExtraSymbols(String commandName) {
        if (commandName.startsWith(CommandSpecialSymbols.LOCAL_COMMAND)) {
            return commandName.substring(1);
        } else {
            return commandName;
        }
    }

    protected boolean shouldUpdateLocalInfos() {
        return localComponentsStateId != manager.getLocalInfos().getComponentsState().getId();
    }

    protected void updateLocalInfos() {
        localComponentsStateId = manager.getLocalInfos().getComponentsState().getId();
        logger.debug("Local command infos updated");
    }

    protected boolean shouldUpdateRemoteInfos() {
        if (!Objects.equals(sessionDescriptor, manager.getSessionDescriptor())) {
            return true;
        }
        return remoteComponentsStateId != manager.getRemoteInfos().getComponentsState().getId();
    }

    protected void updateRemoteInfos() {
        remoteComponentsStateId = manager.getRemoteInfos().getComponentsState().getId();
        sessionDescriptor = manager.getSessionDescriptor();
        logger.debug("Remote command infos updated");
    }
}
