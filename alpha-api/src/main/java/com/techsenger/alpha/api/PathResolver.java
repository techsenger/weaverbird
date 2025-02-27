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

package com.techsenger.alpha.api;

import com.techsenger.alpha.api.component.ComponentConfig;
import com.techsenger.alpha.api.module.ModuleDescriptor;
import com.techsenger.alpha.api.repo.ModulePathResolver;
import com.techsenger.toolkit.core.version.Version;
import java.nio.file.Path;

/**
 *
 * @author Pavel Castornii
 */
public interface PathResolver {

    /**
     * Resolves component cache directory path.
     *
     * @param componentConfig
     * @return
     */
    Path resolveCacheDirectoryPath(ComponentConfig componentConfig);

    /**
     * Resolves component cache directory path.
     *
     * @param componentName the name of the component
     * @param componentVersion the version of the component
     * @return
     */
    Path resolveCacheDirectoryPath(String componentName, Version componentVersion);

    /**
     * Resolves component cache directory path.
     *
     * @param componentLayer
     * @return
     */
    Path resolveCacheDirectoryPath(ModuleLayer componentLayer);

    /**
     * Resolves component config directory path.
     *
     * @param componentConfig
     * @return
     */
    Path resolveConfigDirectoryPath(ComponentConfig componentConfig);

    /**
     * Resolves component config directory path.
     *
     * @return
     */
    Path resolveConfigDirectoryPath(String componentName, Version componentVersion);

    /**
     * Resolves component config directory path.
     *
     * @param componentLayer
     * @return
     */
    Path resolveConfigDirectoryPath(ModuleLayer componentLayer);

    /**
     * Resolves component data directory path.
     *
     * @param componentConfig
     * @return
     */
    Path resolveDataDirectoryPath(ComponentConfig componentConfig);

    /**
     * Resolves component data directory path.
     *
     * @param componentName the name of the component
     * @param componentVersion the version of the component
     * @return
     */
    Path resolveDataDirectoryPath(String componentName, Version componentVersion);

    /**
     * Resolves component data directory path.
     *
     * @param componentLayer
     * @return
     */
    Path resolveDataDirectoryPath(ModuleLayer componentLayer);

    /**
     * Resolves component document directory path.
     *
     * @param componentConfig
     * @return
     */
    Path resolveDocumentDirectoryPath(ComponentConfig componentConfig);

    /**
     * Resolves component document directory path.
     *
     * @param componentName the name of the component
     * @param componentVersion the version of the component
     * @return
     */
    Path resolveDocumentDirectoryPath(String componentName, Version componentVersion);

    /**
     * Resolves component document directory path.
     *
     * @param componentLayer
     * @return
     */
    Path resolveDocumentDirectoryPath(ModuleLayer componentLayer);

    /**
     * Resolves component legal directory path.
     *
     * @param componentConfig
     * @return
     */
    Path resolveLegalDirectoryPath(ComponentConfig componentConfig);

    /**
     * Resolves component legal directory path.
     *
     * @param componentName the name of the component
     * @param componentVersion the version of the component
     * @return
     */
    Path resolveLegalDirectoryPath(String componentName, Version componentVersion);

    /**
     * Resolves component legal directory path.
     *
     * @param componentLayer
     * @return
     */
    Path resolveLegalDirectoryPath(ModuleLayer componentLayer);

    /**
     * Resolves component temp directory path.
     *
     * @param componentConfig
     * @return
     */
    Path resolveTempDirectoryPath(ComponentConfig componentConfig);

    /**
     * Resolves component temp directory path.
     *
     * @param componentName the name of the component
     * @param componentVersion the version of the component
     * @return
     */
    Path resolveTempDirectoryPath(String componentName, Version componentVersion);

    /**
     * Resolves component temp directory path.
     *
     * @param componentLayer
     * @return
     */
    Path resolveTempDirectoryPath(ModuleLayer componentLayer);

    /**
     * Resolves component "component.xml" path.
     *
     * @param componentConfig of the component.
     * @return aplication config path.
     */
    Path resolveConfigFilePath(ComponentConfig componentConfig);

    /**
     * Resolves component "component.xml" path.
     *
     * @param componentName
     * @param componentVersion
     * @return
     */
    Path resolveConfigFilePath(String componentName, Version componentVersion);

    /**
     * Resolves component "component.xml" path.
     *
     * @param componentLayer
     * @return
     */
    Path resolveConfigFilePath(ModuleLayer componentLayer);

    /**
     * Resolves component settings "settings.xml" path.
     *
     * @param componentConfig is some descriptor.
     * @return settings path.
     */
    Path resolveSettingsFilePath(ComponentConfig componentConfig);

    /**
     * Resolves component settings "settings.xml" path.
     *
     * @param componentName
     * @param componentVersion
     * @return
     */
    Path resolveSettingsFilePath(String componentName, Version componentVersion);

    /**
     * Resolves component settings "settings.xml" path.
     *
     * @return
     */
    Path resolveSettingsFilePath(ModuleLayer componentLayer);

    /**
     * Resolves module path in repo.
     *
     * @param moduleDescriptor
     * @return
     */
    Path resolveModuleFilePath(ModuleDescriptor moduleDescriptor);

    /**
     * Returns module path resolver that is used.
     *
     * @return
     */
    ModulePathResolver getModulePathResolver();
}
