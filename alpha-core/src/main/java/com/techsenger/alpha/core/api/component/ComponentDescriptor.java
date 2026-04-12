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

package com.techsenger.alpha.core.api.component;

import com.techsenger.alpha.core.api.module.ModuleType;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

/**
 * Each component instance has a corresponding {@code ComponentDescriptor}. A descriptor is created when a
 * component is deployed and removed when the component is undeployed.
 *
 * @author Pavel Castornii
 */
public interface ComponentDescriptor extends ComponentDescriptorDto {

    /**
     * Returns an unmodifiable list of parent component descriptors. If the component has no parents,
     * returns an empty list.
     *
     * @return the list of parent descriptors
     */
    @Override
    List<? extends ComponentDescriptor> getParents();

    /**
     * Returns an unmodifiable set of all ancestor component descriptors (nodes higher in the same lineage).
     * The JPMS module layer graph is a directed acyclic graph (DAG).
     *
     * @return the set of ancestor descriptors
     */
    Set<ComponentDescriptor> findAncestors();

    /**
     * Returns an unmodifiable set of all descendant component descriptors (nodes lower in the same lineage).
     * The JPMS module layer graph is a directed acyclic graph (DAG).
     *
     * @return the set of descendant descriptors
     */
    Set<ComponentDescriptor> findDescendants();

    /**
     * Returns the component configuration.
     *
     * @return the component configuration
     */
    @Override
    ComponentConfig getConfig();

    /**
     * Returns an unmodifiable list of module paths. The index in this list corresponds to the index in
     * {@link ComponentConfig#getModules()}.
     *
     * @return the list of module paths
     */
    List<Path> getModulePaths();

    /**
     * Returns {@code true} if this component contains modules of type {@link ModuleType#WAR}, otherwise {@code false}.
     * The result is computed once and cached.
     *
     * @return {@code true} if WAR modules are present, otherwise {@code false}
     */
    boolean containsWarModules();
}
