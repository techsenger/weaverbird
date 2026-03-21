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

package com.techsenger.alpha.core.api;

import com.techsenger.alpha.core.api.component.ComponentConfig;
import com.techsenger.alpha.core.api.module.ModuleArtifact;
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
    Path resolveCacheDirectory(ComponentConfig componentConfig);

    /**
     * Resolves component cache directory path.
     *
     * @param componentName the name of the component
     * @param componentVersion the version of the component
     * @return
     */
    Path resolveCacheDirectory(String componentName, Version componentVersion);

    /**
     * Resolves component cache directory path.
     *
     * @param componentLayer
     * @return
     */
    Path resolveCacheDirectory(ModuleLayer componentLayer);

    /**
     * Resolves component config directory path.
     *
     * @param componentConfig
     * @return
     */
    Path resolveConfigDirectory(ComponentConfig componentConfig);

    /**
     * Resolves component config directory path.
     *
     * @return
     */
    Path resolveConfigDirectory(String componentName, Version componentVersion);

    /**
     * Resolves component config directory path.
     *
     * @param componentLayer
     * @return
     */
    Path resolveConfigDirectory(ModuleLayer componentLayer);

    /**
     * Resolves component data directory path.
     *
     * @param componentConfig
     * @return
     */
    Path resolveDataDirectory(ComponentConfig componentConfig);

    /**
     * Resolves component data directory path.
     *
     * @param componentName the name of the component
     * @param componentVersion the version of the component
     * @return
     */
    Path resolveDataDirectory(String componentName, Version componentVersion);

    /**
     * Resolves component data directory path.
     *
     * @param componentLayer
     * @return
     */
    Path resolveDataDirectory(ModuleLayer componentLayer);

    /**
     * Resolves component document directory path.
     *
     * @param componentConfig
     * @return
     */
    Path resolveDocumentDirectory(ComponentConfig componentConfig);

    /**
     * Resolves component document directory path.
     *
     * @param componentName the name of the component
     * @param componentVersion the version of the component
     * @return
     */
    Path resolveDocumentDirectory(String componentName, Version componentVersion);

    /**
     * Resolves component document directory path.
     *
     * @param componentLayer
     * @return
     */
    Path resolveDocumentDirectory(ModuleLayer componentLayer);

    /**
     * Resolves component legal directory path.
     *
     * @param componentConfig
     * @return
     */
    Path resolveLegalDirectory(ComponentConfig componentConfig);

    /**
     * Resolves component legal directory path.
     *
     * @param componentName the name of the component
     * @param componentVersion the version of the component
     * @return
     */
    Path resolveLegalDirectory(String componentName, Version componentVersion);

    /**
     * Resolves component legal directory path.
     *
     * @param componentLayer
     * @return
     */
    Path resolveLegalDirectory(ModuleLayer componentLayer);

    /**
     * Resolves component temp directory path.
     *
     * @param componentConfig
     * @return
     */
    Path resolveTempDirectory(ComponentConfig componentConfig);

    /**
     * Resolves component temp directory path.
     *
     * @param componentName the name of the component
     * @param componentVersion the version of the component
     * @return
     */
    Path resolveTempDirectory(String componentName, Version componentVersion);

    /**
     * Resolves component temp directory path.
     *
     * @param componentLayer
     * @return
     */
    Path resolveTempDirectory(ModuleLayer componentLayer);

    /**
     * Resolves component "component.xml" path.
     *
     * @param componentConfig of the component.
     * @return aplication config path.
     */
    Path resolveConfigFile(ComponentConfig componentConfig);

    /**
     * Resolves component "component.xml" path.
     *
     * @param componentName
     * @param componentVersion
     * @return
     */
    Path resolveConfigFile(String componentName, Version componentVersion);

    /**
     * Resolves component "component.xml" path.
     *
     * @param componentLayer
     * @return
     */
    Path resolveConfigFile(ModuleLayer componentLayer);

    /**
     * Resolves component settings "settings.xml" path.
     *
     * @param componentConfig is some descriptor.
     * @return settings path.
     */
    Path resolveSettingsFile(ComponentConfig componentConfig);

    /**
     * Resolves component settings "settings.xml" path.
     *
     * @param componentName
     * @param componentVersion
     * @return
     */
    Path resolveSettingsFile(String componentName, Version componentVersion);

    /**
     * Resolves component settings "settings.xml" path.
     *
     * @return
     */
    Path resolveSettingsFile(ModuleLayer componentLayer);

    /**
     * Resolves module path in repo.
     *
     * @param artifact
     * @return
     */
    Path resolveModule(ModuleArtifact artifact);
}
