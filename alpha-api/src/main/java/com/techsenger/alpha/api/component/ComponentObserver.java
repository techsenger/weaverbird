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

package com.techsenger.alpha.api.component;

/**
 * This observer allows some component to detect state changes in other components and react to them. Thus, a component
 * observer will never be triggered by events related to the component instance itself.
 *
 * @author Pavel Castornii
 */
public interface ComponentObserver {

    /**
     * This method is called before a component is built.
     *
     * @param config
     */
    void onBuilding(ComponentConfig config);

    /**
     * This method is called after a component is built.
     *
     * @param config
     */
    void onBuilt(ComponentConfig config);

    /**
     * This method is called before a component is added.
     *
     * @param config
     */
    void onAdding(ComponentConfig config);

    /**
     * This method is called after a component is added.
     *
     * @param config
     */
    void onAdded(ComponentConfig config);

    /**
     * This method is called before a component is resolved.
     *
     * @param config
     */
    void onResolving(ComponentConfig config);

    /**
     * This method is called after a component is resolved.
     *
     * @param config
     */
    void onResolved(ComponentConfig config);

    /**
     * This method is called before a component is deployed.
     *
     * @param config
     */
    void onDeploying(ComponentConfig config);

    /**
     * This method is called after a component is deployed.
     *
     * @param component
     */
    void onDeployed(Component component);

    /**
     * This method is called before a component is activated.
     *
     * @param component
     */
    void onActivating(Component component);

    /**
     * This method is called after a component is activated.
     *
     * @param component
     */
    void onActivated(Component component);

    /**
     * This method is called before a component is deactivated.
     *
     * @param component
     */
    void onDeactivating(Component component);

    /**
     * This method is called after a component is deactivated.
     *
     * @param component
     */
    void onDeactivated(Component component);

    /**
     * This method is called before a component is undeployed.
     *
     * @param component
     */
    void onUndeploying(Component component);

    /**
     * This method is called after a component is undeployed.
     *
     * @param component
     */
    void onUndeployed(Component component);

    /**
     * This method is called before a component is unresolved.
     *
     * @param config
     */
    void onUnresolving(ComponentConfig config);

    /**
     * This method is called after a component is unresolved.
     *
     * @param config
     */
    void onUnresolved(ComponentConfig config);

    /**
     * This method is called before a component is removed.
     *
     * @param config
     */
    void onRemoving(ComponentConfig config);

    /**
     * This method is called after a component is removed.
     *
     * @param config
     */
    void onRemoved(ComponentConfig config);
}
