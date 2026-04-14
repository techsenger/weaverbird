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

package com.techsenger.weaverbird.core.api.state;

/**
 *
 * @author Pavel Castornii
 */
public class DefaultComponentsState implements ComponentsState {

    private volatile int id;

    private volatile int deployedCount;

    private volatile int activatedCount;

    public DefaultComponentsState() {
    }

    public DefaultComponentsState(int id, int deployedCount, int activatedCount) {
        this.id = id;
        this.deployedCount = deployedCount;
        this.activatedCount = activatedCount;
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int getDeployedCount() {
        return deployedCount;
    }

    public void setDeployedCount(int deployedCount) {
        this.deployedCount = deployedCount;
    }

    @Override
    public int getActivatedCount() {
        return activatedCount;
    }

    public void setActivatedCount(int activatedCount) {
        this.activatedCount = activatedCount;
    }
}
