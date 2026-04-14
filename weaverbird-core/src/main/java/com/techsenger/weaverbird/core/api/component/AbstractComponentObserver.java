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

package com.techsenger.weaverbird.core.api.component;

/**
 * This is an empty implementation of ComponentObserver, all its methods are empty.
 *
 * @author Pavel Castornii
 */
public abstract class AbstractComponentObserver implements ComponentObserver {

    @Override
    public void onBuilding(ComponentConfig config) { }

    @Override
    public void onBuilt(ComponentConfig config) { }

    @Override
    public void onAdding(ComponentConfig config) { }

    @Override
    public void onAdded(ComponentConfig config) { }

    @Override
    public void onResolving(ComponentConfig config) { }

    @Override
    public void onResolved(ComponentConfig config) { }

    @Override
    public void onDeploying(ComponentConfig config) { }

    @Override
    public void onDeployed(Component component) { }

    @Override
    public void onActivating(Component component) { }

    @Override
    public void onActivated(Component component) { }

    @Override
    public void onDeactivating(Component component) { }

    @Override
    public void onDeactivated(Component component) { }

    @Override
    public void onUndeploying(Component component) { }

    @Override
    public void onUndeployed(Component component) { }

    @Override
    public void onUnresolving(ComponentConfig config) { }

    @Override
    public void onUnresolved(ComponentConfig config) { }

    @Override
    public void onRemoving(ComponentConfig config) { }

    @Override
    public void onRemoved(ComponentConfig config) { }
}
