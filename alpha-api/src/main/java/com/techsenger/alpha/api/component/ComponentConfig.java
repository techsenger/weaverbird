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

import com.techsenger.alpha.api.module.ModuleDescriptor;
import com.techsenger.alpha.api.module.ModuleType;
import com.techsenger.alpha.api.repo.RepositoryDescriptor;
import com.techsenger.toolkit.core.version.Version;
import java.util.List;
import java.util.Map;

/**
 * Configuration can exist without component so, component can be created on the base of the configuration.
 *
 * @author Pavel Castornii
 */
public interface ComponentConfig {

    /**
     * Returns the human-readable name of the component, for example, "Archive Plugin". Title is never used
     * in commands.
     *
     * @return
     */
    String getTitle();

    /**
     * Returns the technical name of the component. This name must be unique. This is the only name for all component
     * commands.
     *
     * @return
     */
    String getName();

    /**
     * Returns the name and the version the component.
     *
     * @return
     */
    String getFullName();

    /**
     * Returns the version of the component.
     *
     * @return
     */
    Version getVersion();

    /**
     * Returns the type of the component.
     *
     * @return
     */
    String getType();

    /**
     * Returns map where key is the attribute name, and value is the attribute value. It is possible to add any
     * metadata, for example, author, developer, license etc.
     *
     * @return
     */
    Map<String, String> getMetadata();

    /**
     * Returns repos from which module must be installed.
     *
     * <p>Important. Component modules can be resolved only from repo that proves reliable and safe way
     * to use component modules - the origin of the module is always known.
     *
     * @return
     */
    List<RepositoryDescriptor> getRepositories();

    /**
     * Returns module descriptors.
     *
     * @return
     */
    List<ModuleDescriptor> getModules();

    /**
     * Returns true if this component contains modules with type {@link ModuleType#WAR}, otherwise returns false.
     * The check is performed only once, and the result is stored.
     *
     * @return
     */
    boolean containsWarModules();
}
