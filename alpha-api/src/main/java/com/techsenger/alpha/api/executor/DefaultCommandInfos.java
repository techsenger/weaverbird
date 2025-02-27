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

package com.techsenger.alpha.api.executor;

import com.techsenger.alpha.api.command.CommandInfo;
import com.techsenger.alpha.api.state.ComponentsState;
import java.util.List;

/**
 *
 * @author Pavel Castornii
 */
public class DefaultCommandInfos implements CommandInfos {

    private List<CommandInfo> items;

    private ComponentsState componentsState;

    public DefaultCommandInfos() {

    }

    @Override
    public List<CommandInfo> getItems() {
        return items;
    }

    public void setItems(List<CommandInfo> items) {
        this.items = items;
    }

    @Override
    public ComponentsState getComponentsState() {
        return componentsState;
    }

    public void setComponentsState(ComponentsState componentsState) {
        this.componentsState = componentsState;
    }
}
