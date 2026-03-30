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

package com.techsenger.alpha.core.api.state;

/**
 *
 * @author Pavel Castornii
 */
public interface ComponentsState {

    /**
     * Returns the state ID of all components instances. Each time any component is deployed, undeployed, activated,
     * or deactivated, the ID is incremented by one. This ID can be used to determine if there have been any changes
     * to components that might affect command lists, the layer graph, and similar elements.
     *
     * @return the state ID of all components instances.
     */
    int getId();

    /**
     * Returns the total number of deployed components.
     *
     * @return the count of deployed components
     */
    int getDeployedCount();

    /**
     * Returns the total number of activated components.
     *
     * @return the count of activated components
     */
    int getActivatedCount();
}
