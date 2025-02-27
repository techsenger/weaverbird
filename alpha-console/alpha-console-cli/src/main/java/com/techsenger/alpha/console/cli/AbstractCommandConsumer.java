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

package com.techsenger.alpha.console.cli;

import com.techsenger.alpha.api.command.CommandInfo;
import com.techsenger.alpha.spi.console.AbstractCommandInfosConsumer;
import com.techsenger.alpha.spi.console.CommandInfosManager;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author Pavel Castornii
 */
abstract class AbstractCommandConsumer extends AbstractCommandInfosConsumer {

    private Map<String, CommandInfo> localCommandsByName;

    private Map<String, CommandInfo> remoteCommandsByName;

    AbstractCommandConsumer(CommandInfosManager manager) {
        super(manager);
    }

    protected Map<String, CommandInfo> getLocalCommandsByName() {
        return localCommandsByName;
    }

    protected Map<String, CommandInfo> getRemoteCommandsByName() {
        return remoteCommandsByName;
    }

    @Override
    protected void updateLocalInfos() {
        if (shouldUpdateLocalInfos()) {
            super.updateLocalInfos();
            localCommandsByName = getManager().getLocalInfos().getItems().stream()
                    .collect(Collectors.toMap(i -> i.getName(), i -> i));
        }
    }

    @Override
    protected void updateRemoteInfos() {
        if (shouldUpdateRemoteInfos()) {
            super.updateRemoteInfos();
            remoteCommandsByName = getManager().getRemoteInfos().getItems().stream()
                    .collect(Collectors.toMap(i -> i.getName(), i -> i));
        }
    }
}
