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

package com.techsenger.alpha.core;

import com.techsenger.alpha.api.ComponentManager;
import com.techsenger.alpha.api.PathManager;
import com.techsenger.alpha.api.PathResolver;
import com.techsenger.alpha.api.component.ComponentConfig;
import com.techsenger.alpha.api.module.ModuleDescriptor;
import com.techsenger.alpha.api.repo.ModulePathResolver;
import com.techsenger.toolkit.core.jpms.ServiceUtils;
import com.techsenger.toolkit.core.version.Version;
import java.io.File;
import java.nio.file.Path;

/**
 *
 * @author Pavel Castornii
 */
class DefaultPathResolver implements PathResolver {

    public static final String CONFIG_FILE_NAME = "configuration.xml";

    public static final String SETTINGS_FILE_NAME = "settings.xml";

    private final PathManager pathManager;

    private final ComponentManager componentManager;

    private final ModulePathResolver modulePathResolver;

    DefaultPathResolver(PathManager pathManager, ComponentManager componentManager) {
        this.pathManager = pathManager;
        this.componentManager = componentManager;
        modulePathResolver = ServiceUtils.loadProvider(this.getClass().getModule().getLayer(), false,
                ModulePathResolver.class).get();
    }

    @Override
    public Path resolveCacheDirectoryPath(ComponentConfig componentConfig) {
        return resolveCacheDirectoryPath(componentConfig.getName(), componentConfig.getVersion());
    }

    @Override
    public Path resolveCacheDirectoryPath(String componentName, Version componentVersion) {
        Path path = this.pathManager.getCacheDirectoryPath()
                .resolve(componentName + File.separator + componentVersion.getFull());
        return path;
    }

    @Override
    public Path resolveCacheDirectoryPath(ModuleLayer componentLayer) {
        var descriptor = this.componentManager.findComponent(componentLayer).getDescriptor();
        return resolveCacheDirectoryPath(descriptor.getConfig());
    }

    @Override
    public Path resolveConfigDirectoryPath(ComponentConfig componentConfig) {
        return resolveConfigDirectoryPath(componentConfig.getName(), componentConfig.getVersion());
    }

    @Override
    public Path resolveConfigDirectoryPath(String componentName, Version componentVersion) {
        Path path = this.pathManager.getConfigDirectoryPath()
                .resolve(componentName + File.separator + componentVersion.getFull());
        return path;
    }

    @Override
    public Path resolveConfigDirectoryPath(ModuleLayer componentLayer) {
        var descriptor = this.componentManager.findComponent(componentLayer).getDescriptor();
        return resolveConfigDirectoryPath(descriptor.getConfig());
    }

    @Override
    public Path resolveDataDirectoryPath(ComponentConfig config) {
        return resolveDataDirectoryPath(config.getName(), config.getVersion());
    }

    @Override
    public Path resolveDataDirectoryPath(final String name, final Version version) {
        Path path = this.pathManager.getDataDirectoryPath().resolve(name + File.separator + version.getFull());
        return path;
    }

    @Override
    public Path resolveDataDirectoryPath(ModuleLayer layer) {
        var descriptor = this.componentManager.findComponent(layer).getDescriptor();
        return resolveDataDirectoryPath(descriptor.getConfig());
    }

    @Override
    public Path resolveDocumentDirectoryPath(ComponentConfig componentConfig) {
        return resolveDocumentDirectoryPath(componentConfig.getName(), componentConfig.getVersion());
    }

    @Override
    public Path resolveDocumentDirectoryPath(String componentName, Version componentVersion) {
        Path path = this.pathManager.getDocumentDirectoryPath()
                .resolve(componentName + File.separator + componentVersion.getFull());
        return path;
    }

    @Override
    public Path resolveDocumentDirectoryPath(ModuleLayer componentLayer) {
        var descriptor = this.componentManager.findComponent(componentLayer).getDescriptor();
        return resolveDocumentDirectoryPath(descriptor.getConfig());
    }

    @Override
    public Path resolveLegalDirectoryPath(ComponentConfig componentConfig) {
        return resolveLegalDirectoryPath(componentConfig.getName(), componentConfig.getVersion());
    }

    @Override
    public Path resolveLegalDirectoryPath(String componentName, Version componentVersion) {
        Path path = this.pathManager.getLegalDirectoryPath()
                .resolve(componentName + File.separator + componentVersion.getFull());
        return path;
    }

    @Override
    public Path resolveLegalDirectoryPath(ModuleLayer componentLayer) {
        var descriptor = this.componentManager.findComponent(componentLayer).getDescriptor();
        return resolveLegalDirectoryPath(descriptor.getConfig());
    }

    @Override
    public Path resolveTempDirectoryPath(ComponentConfig componentConfig) {
        return resolveTempDirectoryPath(componentConfig.getName(), componentConfig.getVersion());
    }

    @Override
    public Path resolveTempDirectoryPath(String componentName, Version componentVersion) {
        Path path = this.pathManager.getTempDirectoryPath()
                .resolve(componentName + File.separator + componentVersion.getFull());
        return path;
    }

    @Override
    public Path resolveTempDirectoryPath(ModuleLayer componentLayer) {
        var descriptor = this.componentManager.findComponent(componentLayer).getDescriptor();
        return resolveTempDirectoryPath(descriptor.getConfig());
    }

    @Override
    public Path resolveConfigFilePath(final ComponentConfig config) {
        return resolveConfigFilePath(config.getName(), config.getVersion());
    }

    @Override
    public Path resolveConfigFilePath(final String name, final Version version) {
        String configName = name + File.separator + version.getFull() + File.separator + CONFIG_FILE_NAME;
        return this.pathManager.getConfigDirectoryPath().resolve(configName);
    }

    @Override
    public Path resolveConfigFilePath(ModuleLayer layer) {
        var descriptor = this.componentManager.findComponent(layer).getDescriptor();
        return this.resolveConfigFilePath(descriptor.getConfig());
    }

    @Override
    public Path resolveSettingsFilePath(final ComponentConfig config) {
        return resolveSettingsFilePath(config.getName(), config.getVersion());
    }

    @Override
    public Path resolveSettingsFilePath(final String name, final Version version) {
        String configName = name + File.separator + version.getFull() + File.separator + SETTINGS_FILE_NAME;
        return this.pathManager.getConfigDirectoryPath().resolve(configName);
    }

    @Override
    public Path resolveSettingsFilePath(ModuleLayer layer) {
        var descriptor = this.componentManager.findComponent(layer).getDescriptor();
        return this.resolveSettingsFilePath(descriptor.getConfig());
    }

    @Override
    public Path resolveModuleFilePath(ModuleDescriptor moduleDescriptor) {
        return this.modulePathResolver
                .resolveModulePath(this.pathManager.getRepositoryDirectoryPath(), moduleDescriptor);
    }

    @Override
    public ModulePathResolver getModulePathResolver() {
        return modulePathResolver;
    }
}
