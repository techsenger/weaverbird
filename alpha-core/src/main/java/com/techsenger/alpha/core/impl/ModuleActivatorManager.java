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

package com.techsenger.alpha.core.impl;

import com.techsenger.alpha.core.api.Framework;
import com.techsenger.alpha.core.api.component.ComponentConfig;
import com.techsenger.alpha.core.impl.component.DefaultComponent;
import com.techsenger.alpha.core.impl.module.DefaultModuleContext;
import com.techsenger.alpha.core.impl.module.DefaultModuleDescriptor;
import com.techsenger.alpha.core.spi.module.ModuleActivator;
import com.techsenger.alpha.core.spi.module.ModuleContext;
import com.techsenger.toolkit.core.jpms.ModuleUtils;
import com.techsenger.toolkit.core.jpms.ServiceUtils;
import java.io.IOException;
import java.lang.module.ResolvedModule;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
class ModuleActivatorManager {

    private static final Logger logger = LoggerFactory.getLogger(ModuleActivatorManager.class);

    private final Framework framework;

    ModuleActivatorManager(Framework framework) {
        this.framework = framework;
    }

    public void activateActivators(final DefaultComponent component) throws Exception {
        ModuleLayer layer = component.getLayer();
        List<ModuleActivator> unorderedActivators = new ArrayList<>();
        List<ModuleActivator> orderedActivators;
        Map<Module, ModuleContext> moduleContextsByModule = new HashMap<>();
        var activatorsByModule = findActivators(layer);
        for (var moduleDescriptor : component.getDescriptor().getConfig().getModules()) {
            //module context we create only for those modules which activator is enabled.
            if (moduleDescriptor.isActive()) {
                var module = component.getModule(moduleDescriptor);
                moduleContextsByModule.put(module, new DefaultModuleContext(framework, component));
                ModuleActivator activator = activatorsByModule.get(module);
                if (activator != null) {
                    unorderedActivators.add(activator);
                } else {
                    throw new Exception("Module " + moduleDescriptor.getFileName()
                            + " has no activator but it is active in component configuration");
                }
            }
        }
        orderedActivators = sortActivatorsByModuleOrder(unorderedActivators,
                component.getDescriptor().getConfig(), layer);
        //we need to start activator with their layer classloader, but not of the alpha
        for (ModuleActivator activator : orderedActivators) {
            Module activatorModule = activator.getClass().getModule();
            ClassLoader activatorClassLoader = activatorModule.getClassLoader();
            Thread.currentThread().setContextClassLoader(activatorClassLoader);
            activator.activate(moduleContextsByModule.get(activatorModule));
            logger.debug("Activated {} of module {} with context classLoader {}",
                    activator.getClass().getName(), activatorModule.getName(), activatorClassLoader);
        }
        component.setActivators(orderedActivators);
        component.setModuleContextsByModule(moduleContextsByModule);
    }

    public void deactivateActivators(DefaultComponent component) throws Exception {
        //we need to stop in reverse order.
        Map<Module, ModuleContext> moduleContextsByModule = component.getModuleContextsByModule();
        List<ModuleActivator> activators = component.getActivators();
        //Generate an iterator. Start just after the last element.
        ListIterator<ModuleActivator> iterator = activators.listIterator(activators.size());
        while (iterator.hasPrevious()) {
            ModuleActivator activator = iterator.previous();
            Module activatorModule = activator.getClass().getModule();
            ClassLoader activatorClassLoader = activatorModule.getClassLoader();
            Thread.currentThread().setContextClassLoader(activatorClassLoader);
            activator.deactivate(moduleContextsByModule.get(activatorModule));
            logger.debug("Deactivated {} of module {} with context classLoader {}",
                    activator.getClass().getName(), activatorModule.getName(), activatorClassLoader);
        }
    }

    /**
     * Sorts activators by module order in configuration using two maps: moduleName -> activator, path -> moduleName.
     *
     * @param activators to be sorted.
     * @param modulePaths list of module paths.
     * @param componentLayer layer of the component.
     * @return ordered activators or empty list
     */
    private List<ModuleActivator> sortActivatorsByModuleOrder(final List<ModuleActivator> activators,
            final ComponentConfig config, final ModuleLayer layer) throws IOException {
        //we use here module name.
        List<ModuleActivator> result = new ArrayList<>();
        Map<String, ModuleActivator> moduleActivatorsByModuleName = new HashMap<>();
        for (ModuleActivator activator : activators) {
            moduleActivatorsByModuleName.put(activator.getClass().getModule().getName(), activator);
        }
        Map<String, Path> modulePathsByName = new HashMap<>();
        for (ResolvedModule module : layer.configuration().modules()) {
            modulePathsByName.put(module.name(), ModuleUtils.getPath(module));
        }
        for (var m : config.getModules()) {
            if (m.isActive()) {
                //module path using module descriptor
                if (m.getResolvedPath() == null) {
                    ((DefaultModuleDescriptor) m).setResolvedPath(framework.getPathManager()
                            .getPathResolver().resolveModule(m));
                }
                for (var entry : modulePathsByName.entrySet()) {
                    if (Files.isSameFile(m.getResolvedPath(), entry.getValue())) {
                        //if same path, we can match module descriptor to jpms module.
                        ModuleActivator activator = moduleActivatorsByModuleName.get(entry.getKey());
                        if (activator != null) {
                            result.add(activator);
                        }
                        break;
                    }
                }
            }
        }
        return result;
    }

    /**
     * Returns activators by module.
     * To
     * Currently I use only new one.
     * @param module for which activator will be built.
     * @return activator or null if module doesn't have activator.
     */
    private Map<Module, ModuleActivator> findActivators(final ModuleLayer layer) {
        var activators = ServiceUtils.loadProviders(layer, false, ModuleActivator.class);
        Map<Module, ModuleActivator> result = new HashMap<>();
        for (var activator : activators) {
            result.put(activator.getClass().getModule(), activator);
        }
        return result;
    }
}
