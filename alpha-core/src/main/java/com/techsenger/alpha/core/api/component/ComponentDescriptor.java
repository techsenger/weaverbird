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

package com.techsenger.alpha.core.api.component;

import java.util.List;
import java.util.Set;

/**
 * Every component instance has one ComponentDescriptor instance. A descriptor is created for every component
 * and is deleted when component is undeployed.
 *
 * @author Pavel Castornii
 */
public interface ComponentDescriptor extends ComponentDescriptorDto {

    /**
     * Returns the descriptors of the parent components. If there is no parent returns empty list.
     * @return
     */
    @Override
    List<? extends ComponentDescriptor> getParents();

    /**
     * Returns the descriptors of all ancestor components (nodes higher than a given node in the same lineage),
     * i.e. higher than the component of the passed  descriptor. JPMS module layer graph is a directed acyclic
     * graph (DAG).
     *
     * @return
     */
    Set<ComponentDescriptor> findAncestors();

    /**
     * Returns the descriptors of all descendant components (nodes below than a given node in the same lineage),
     * i.e. below than the component of the passed  descriptor. JPMS module layer graph is a directed acyclic
     * graph (DAG).
     *
     * @return
     */
    Set<ComponentDescriptor> findDescendants();

    /**
     * Returns component config.
     *
     * @return
     */
    @Override
    ComponentConfig getConfig();
}
