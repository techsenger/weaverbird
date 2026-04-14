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

package com.techsenger.weaverbird.core.api;

import com.techsenger.weaverbird.core.api.component.ComponentConfig;
import com.techsenger.weaverbird.core.api.module.ModuleArtifact;
import com.techsenger.toolkit.core.os.OsUtils;
import com.techsenger.toolkit.core.version.Version;
import java.io.File;
import java.nio.file.Path;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
public class DefaultPathResolver implements PathResolver {

    public static final String CONFIG_FILE_NAME = "configuration.xml";

    public static final String SETTINGS_FILE_NAME = "settings.xml";

    private static final Logger logger = LoggerFactory.getLogger(DefaultPathResolver.class);

    private final PathManager pathManager;

    private final ComponentManager componentManager;

    public DefaultPathResolver(PathManager pathManager, ComponentManager componentManager) {
        this.pathManager = pathManager;
        this.componentManager = componentManager;
    }

    @Override
    public Path resolveCacheDirectory(ComponentConfig componentConfig) {
        return resolveCacheDirectory(componentConfig.getName(), componentConfig.getVersion());
    }

    @Override
    public Path resolveCacheDirectory(String componentName, Version componentVersion) {
        Path path = this.pathManager.getCacheDirectory()
                .resolve(componentName + File.separator + componentVersion.getFull());
        return path;
    }

    @Override
    public Path resolveCacheDirectory(ModuleLayer componentLayer) {
        var descriptor = this.componentManager.findComponent(componentLayer).getDescriptor();
        return resolveCacheDirectory(descriptor.getConfig());
    }

    @Override
    public Path resolveConfigDirectory(ComponentConfig componentConfig) {
        return resolveConfigDirectory(componentConfig.getName(), componentConfig.getVersion());
    }

    @Override
    public Path resolveConfigDirectory(String componentName, Version componentVersion) {
        Path path = this.pathManager.getConfigDirectory()
                .resolve(componentName + File.separator + componentVersion.getFull());
        return path;
    }

    @Override
    public Path resolveConfigDirectory(ModuleLayer componentLayer) {
        var descriptor = this.componentManager.findComponent(componentLayer).getDescriptor();
        return resolveConfigDirectory(descriptor.getConfig());
    }

    @Override
    public Path resolveDataDirectory(ComponentConfig config) {
        return resolveDataDirectory(config.getName(), config.getVersion());
    }

    @Override
    public Path resolveDataDirectory(final String name, final Version version) {
        Path path = this.pathManager.getDataDirectory().resolve(name + File.separator + version.getFull());
        return path;
    }

    @Override
    public Path resolveDataDirectory(ModuleLayer layer) {
        var descriptor = this.componentManager.findComponent(layer).getDescriptor();
        return resolveDataDirectory(descriptor.getConfig());
    }

    @Override
    public Path resolveDocumentDirectory(ComponentConfig componentConfig) {
        return resolveDocumentDirectory(componentConfig.getName(), componentConfig.getVersion());
    }

    @Override
    public Path resolveDocumentDirectory(String componentName, Version componentVersion) {
        Path path = this.pathManager.getDocumentDirectory()
                .resolve(componentName + File.separator + componentVersion.getFull());
        return path;
    }

    @Override
    public Path resolveDocumentDirectory(ModuleLayer componentLayer) {
        var descriptor = this.componentManager.findComponent(componentLayer).getDescriptor();
        return resolveDocumentDirectory(descriptor.getConfig());
    }

    @Override
    public Path resolveLegalDirectory(ComponentConfig componentConfig) {
        return resolveLegalDirectory(componentConfig.getName(), componentConfig.getVersion());
    }

    @Override
    public Path resolveLegalDirectory(String componentName, Version componentVersion) {
        Path path = this.pathManager.getLegalDirectory()
                .resolve(componentName + File.separator + componentVersion.getFull());
        return path;
    }

    @Override
    public Path resolveLegalDirectory(ModuleLayer componentLayer) {
        var descriptor = this.componentManager.findComponent(componentLayer).getDescriptor();
        return resolveLegalDirectory(descriptor.getConfig());
    }

    @Override
    public Path resolveTempDirectory(ComponentConfig componentConfig) {
        return resolveTempDirectory(componentConfig.getName(), componentConfig.getVersion());
    }

    @Override
    public Path resolveTempDirectory(String componentName, Version componentVersion) {
        Path path = this.pathManager.getTempDirectory()
                .resolve(componentName + File.separator + componentVersion.getFull());
        return path;
    }

    @Override
    public Path resolveTempDirectory(ModuleLayer componentLayer) {
        var descriptor = this.componentManager.findComponent(componentLayer).getDescriptor();
        return resolveTempDirectory(descriptor.getConfig());
    }

    @Override
    public Path resolveConfigFile(final ComponentConfig config) {
        return resolveConfigFile(config.getName(), config.getVersion());
    }

    @Override
    public Path resolveConfigFile(final String name, final Version version) {
        String configName = name + File.separator + version.getFull() + File.separator + CONFIG_FILE_NAME;
        return this.pathManager.getConfigDirectory().resolve(configName);
    }

    @Override
    public Path resolveConfigFile(ModuleLayer layer) {
        var descriptor = this.componentManager.findComponent(layer).getDescriptor();
        return this.resolveConfigFile(descriptor.getConfig());
    }

    @Override
    public Path resolveSettingsFile(final ComponentConfig config) {
        return resolveSettingsFile(config.getName(), config.getVersion());
    }

    @Override
    public Path resolveSettingsFile(final String name, final Version version) {
        String configName = name + File.separator + version.getFull() + File.separator + SETTINGS_FILE_NAME;
        return this.pathManager.getConfigDirectory().resolve(configName);
    }

    @Override
    public Path resolveSettingsFile(ModuleLayer layer) {
        var descriptor = this.componentManager.findComponent(layer).getDescriptor();
        return this.resolveSettingsFile(descriptor.getConfig());
    }

    @Override
    public Path resolveModule(ModuleArtifact artifact) {
        String modulePath = null;
        if (!OsUtils.isWindows()) {
            modulePath = artifact.getGroupId().replaceAll(Pattern.quote("."), File.separator);
        } else {
            modulePath = artifact.getGroupId().replaceAll(Pattern.quote("."), "\\\\");
        }
        modulePath = modulePath
                + File.separator
                + artifact.getArtifactId()
                + File.separator
                + artifact.getVersion()
                + File.separator
                + ModuleArtifact.resolveFileName(artifact);
        var resolvedPath = this.pathManager.getRepositoryDirectory().resolve(modulePath);
        logger.trace("Module {} has path {}", artifact.getArtifactId(), resolvedPath);
        return resolvedPath;
    }
}
