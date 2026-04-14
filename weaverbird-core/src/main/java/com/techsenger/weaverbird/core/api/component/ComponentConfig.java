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

import com.techsenger.weaverbird.core.api.LayerOwner;
import com.techsenger.weaverbird.core.api.module.ModuleConfig;
import java.util.List;
import java.util.Map;

/**
 * Provides configuration for a component.
 *
 * @author Pavel Castornii
 */
public interface ComponentConfig extends ComponentConfigDto, LayerOwner {

    static ComponentConfigBuilder builder() {
        return new ComponentConfigBuilder();
    }

    /**
     * Returns an unmodifiable map of metadata where the key is the attribute name and the value is the
     * attribute value. Arbitrary metadata can be provided, for example: author, developer, license, etc.
     *
     * @return the metadata map
     */
    Map<String, String> getMetadata();

    /**
     * Returns an unmodifiable list of repositories from which modules will be resolved.
     *
     * <p>Repositories are used in the order they are specified. A module is searched for in the first
     * repository, then in the next, and so on. By default, the central repository is not used and must
     * be explicitly added if required.
     *
     * @return the list of repository configurations
     */
    List<RepositoryConfig> getRepositories();

    /**
     * Returns an unmodifiable list of parent component configurations to which this component can be
     * added as a child. An empty list indicates that this component can be added to any component.
     *
     * @return the list of parent configurations
     */
    List<ParentConfig> getParents();

    /**
     * Returns an unmodifiable list of module configurations.
     *
     * @return the list of module configurations
     */
    List<ModuleConfig> getModules();
}
