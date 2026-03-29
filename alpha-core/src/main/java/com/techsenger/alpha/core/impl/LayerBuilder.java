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

package com.techsenger.alpha.core.impl;

import com.techsenger.alpha.core.api.ComponentManager;
import com.techsenger.alpha.core.api.Constants;
import com.techsenger.alpha.core.api.Framework;
import com.techsenger.alpha.core.api.LayerOwner;
import com.techsenger.alpha.core.api.component.Component;
import com.techsenger.alpha.core.api.component.ComponentDescriptor;
import com.techsenger.alpha.core.api.module.DirectiveType;
import com.techsenger.alpha.core.api.module.ModuleDirective;
import com.techsenger.alpha.core.api.module.ModuleType;
import com.techsenger.alpha.core.api.module.ResolvedModuleDirective;
import com.techsenger.alpha.core.impl.component.DefaultComponent;
import com.techsenger.alpha.core.impl.module.DefaultModuleDescriptor;
import com.techsenger.alpha.core.impl.module.DefaultResolvedModuleDirective;
import com.techsenger.alpha.core.impl.war.WarModuleFinder;
import com.techsenger.toolkit.core.jpms.ModuleUtils;
import java.io.IOException;
import java.lang.module.Configuration;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
class LayerBuilder {

    private static final Logger logger = LoggerFactory.getLogger(LayerBuilder.class);

    private final ComponentManager componentManager;

    private final LayerOwner framework;

    LayerBuilder(ComponentManager componentManager, LayerOwner framework) {
        this.componentManager = componentManager;
        this.framework = framework;
    }

    /**
     * Builds layer for component.
     *
     * @param component
     * @throws IOException
     * @throws Exception
     */
    public void build(final DefaultComponent component) throws IOException, Exception {
        List<Path> jarModulePaths = new ArrayList<>();
        List<Path> warModulePaths = new ArrayList<>();
        findJarAndWarModulePaths(component, jarModulePaths, warModulePaths);
        if (logger.isDebugEnabled()) {
            List<String> jarFiles = jarModulePaths
                .stream()
                .map(p -> p.getFileName().toString())
                .collect(Collectors.toList());
            List<String> warFiles = warModulePaths
                .stream()
                .map(p -> p.getFileName().toString())
                .collect(Collectors.toList());
            logger.debug("Component has the next modules jars: {}, wars: {}",
                    jarFiles, warFiles);
        }
        //collecting data
        final ModuleFinder moduleFinder = createModuleFinder(jarModulePaths, warModulePaths);
        var moduleNames = resolveModuleNames(moduleFinder, jarModulePaths, warModulePaths);
        doBuild(component, moduleFinder, moduleNames);
    }

    private ModuleFinder createModuleFinder(final List<Path> jarModulePaths, final List<Path> warModulePaths)
            throws IOException {
        ModuleFinder commonFinder = null;
        ModuleFinder jarModuleFinder = ModuleFinder.of(jarModulePaths.toArray(new Path[jarModulePaths.size()]));
        if (!warModulePaths.isEmpty()) {
            ModuleFinder warModuleFinder = WarModuleFinder.of(warModulePaths.get(0));
            commonFinder = ModuleFinder.compose(jarModuleFinder, warModuleFinder);
        } else {
            commonFinder = jarModuleFinder;
        }
        return commonFinder;
    }

    private Set<String> resolveModuleNames(ModuleFinder moduleFinder, final List<Path> jarModulePaths,
            final List<Path> warModulePaths) throws Exception {
        final Set<ModuleReference> foundModuleReferences = moduleFinder.findAll();
        final Set<String> foundModuleNames = new HashSet<>();
        final Set<String> foundModulePaths = new HashSet<>();
        for (ModuleReference reference : foundModuleReferences) {
            var uri = reference.location().get();
            var path = Paths.get(uri).normalize().toAbsolutePath().toString();
            foundModulePaths.add(path);
            var name = reference.descriptor().name();
            foundModuleNames.add(name);
        }
        var notFoundCount = (jarModulePaths.size() + warModulePaths.size()) - foundModulePaths.size();
        if (notFoundCount != 0) {
            List<String> absentModulePaths = new ArrayList<>();
            List<Path> jarAndWarPaths = new ArrayList<>();
            jarAndWarPaths.addAll(jarModulePaths);
            jarAndWarPaths.addAll(warModulePaths);
            List<String> allModulePaths = new ArrayList<>();
            for (Path path : jarAndWarPaths) {
                var s = path.normalize().toAbsolutePath().toString();
                allModulePaths.add(s);
                if (!foundModulePaths.contains(s)) {
                    absentModulePaths.add(s);
                }
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Required modules ({}): \n" + allModulePaths
                        .stream()
                        .map(p -> "\t" + p)
                        .sorted()
                        .collect(Collectors.joining("\n")), allModulePaths.size());
                logger.debug("Found modules ({}): \n" + foundModulePaths
                        .stream()
                        .map(p -> "\t" + p)
                        .sorted()
                        .collect(Collectors.joining("\n")), foundModulePaths.size());
            }
            throw new Exception(notFoundCount + " modules weren't found: "
                    + absentModulePaths);
        }
        return foundModuleNames;
    }

    /**
     * This method finds absolute paths for both type of modules.
     *
     * @param component
     * @param jarModulePaths
     * @param warModulePaths
     */
    private void findJarAndWarModulePaths(final DefaultComponent component, final List<Path> jarModulePaths,
            final List<Path> warModulePaths) {
        component.getDescriptor().getConfig().getModules().forEach(descriptor -> {
            if (descriptor.getResolvedPath() == null) {
                ((DefaultModuleDescriptor) descriptor).setResolvedPath(
                        this.componentManager.getPathResolver().resolveModule(descriptor));
            }
            if (descriptor.getType() == null || descriptor.getType() == ModuleType.JAR) {
                jarModulePaths.add(descriptor.getResolvedPath().toAbsolutePath());
            } else if (descriptor.getType() == ModuleType.WAR) {
                warModulePaths.add(descriptor.getResolvedPath().toAbsolutePath());
            } else {
                throw new IllegalArgumentException("Unknown module type=" + descriptor.getType());
            }
        });
    }

    private void doBuild(final DefaultComponent component, final ModuleFinder moduleFinder,
            final Set<String> moduleNames) throws IOException, Exception {
        final List<ModuleLayer> parentLayers = new ArrayList<>();
        final List<Configuration> parentConfs = new ArrayList<>();
        final ComponentDescriptor descriptor = component.getDescriptor();
        if (!descriptor.getParents().isEmpty()) {
            for (ComponentDescriptor desc: descriptor.getParents()) {
                ModuleLayer layer = componentManager.getComponent(desc.getId()).getLayer();
                parentLayers.add(layer);
                parentConfs.add(layer.configuration());
            }
        } else {
            parentLayers.add(Framework.class.getModule().getLayer());
            parentConfs.add(Framework.class.getModule().getLayer().configuration());
        }
        ClassLoader parentClassLoader = null;
        if (descriptor.isParentClassLoaderUsed()) {
            if (parentLayers.size() != 1) {
                throw new IllegalArgumentException("Component uses parent classloader, but has multiple parents");
            }
            if (!descriptor.getParents().isEmpty()) {
                Component parentComponent = componentManager.getComponent(descriptor.getParents().get(0).getId());
                if (parentComponent.getClassLoaders().size() != 1) {
                    throw new IllegalArgumentException("Parent component has multiple classloaders");
                }
                parentClassLoader = parentComponent.getClassLoaders().get(0);
            } else {
                parentClassLoader = ClassLoader.getSystemClassLoader();
            }
        } else {
            parentClassLoader = ClassLoader.getSystemClassLoader();
        }
        List<ClassLoader> componentClassLoaders = new ArrayList<>();
        logger.debug("Resolved parent layers: {}", parentLayers.stream()
                .map(l -> resolveLayerName(l)).collect(Collectors.toList()));
        //creating layer
        Configuration cf =
                Configuration.resolveAndBind​(moduleFinder, parentConfs, ModuleFinder.of(), moduleNames);
        ModuleLayer.Controller controller =
                ModuleLayer.defineModulesWithOneLoader(cf, parentLayers, parentClassLoader);
        component.setLayerController(controller);
        addModuleDirectives(component);
        Set<ClassLoader> cachedClassLoaders = new HashSet<>();
        controller.layer()
                .modules()
                .stream()
                .filter(m -> m.getClassLoader() != null)
                .forEach(m -> cachedClassLoaders.add(m.getClassLoader()));
        componentClassLoaders.addAll(cachedClassLoaders);
        component.setClassLoaders(componentClassLoaders);
        if (logger.isDebugEnabled()) {
            logger.debug("Created layer; classLoaders: {}, parent classLoader: {}, parent layers: {}",
                componentClassLoaders, parentClassLoader,
                parentLayers.stream().map(l -> resolveLayerName(l)).collect(Collectors.toList()));
        }
    }

    /**
     * It is impossible to get reference to boot layer controller in JPMS, so, we can only use module and
     * JVM arguments add-*.
     *
     * @param component
     */
    private void addModuleDirectives(DefaultComponent component) {
        var descriptor = component.getDescriptor();
        var config = descriptor.getConfig();
        var resolvedDirectives = new ArrayList<ResolvedModuleDirective>();
        component.setModuleDirectives(resolvedDirectives);
        for (var m : config.getModules()) {
            var module = component.getModule(m);
            if (m.getDirectives() != null) {
                for (var d : m.getDirectives()) {
                    if (d.getType() == DirectiveType.EXPORTS || d.getType() == DirectiveType.OPENS
                            || d.getType() == DirectiveType.READS) {
                        addDirectModuleDirective(component, module, d, resolvedDirectives);
                    } else {
                        addIndirectModuleDirective(component, module, d, resolvedDirectives);
                    }
                }
            }
            if (m.isNativeAccessEnabled()) {
                component.getLayerController().enableNativeAccess(module);
                if (logger.isDebugEnabled()) {
                    logger.debug("Enabled native access for module: {} in layer: {}",
                        module.getName(), component.getDescriptor().getConfig().getFullName());
                }
            }
        }
    }

    private void addDirectModuleDirective(DefaultComponent component, Module module, ModuleDirective directive,
            List<ResolvedModuleDirective> resolvedDirectives) {
        var layers = findLayers(component, directive.getLayer());
        for (var layer : layers.values()) {
            var otherModule = ModuleUtils.findModule(directive.getModule(), layer);
            logger.debug("Processing direct directive {} for module {} in {}",
                    directive.getType(), module.getName(), component.getDescriptor().getConfig().getFullName());
            applyModuleDirective(component.getLayerController(), module, directive.getType(),
                    directive.getPackage(), otherModule);
            var resolvedDirective = new DefaultResolvedModuleDirective(directive.getType(),
                    module, directive.getPackage(), otherModule);
            resolvedDirectives.add(resolvedDirective);
        }
    }

    private void addIndirectModuleDirective(DefaultComponent component, Module module, ModuleDirective directive,
            List<ResolvedModuleDirective> resolvedDirectives) {
        var layers = findLayers(component, directive.getLayer());
        for (var layer : layers.values()) {
            var otherModule = ModuleUtils.findModule(directive.getModule(), layer);
            ModuleLayer.Controller layerController = null;
            DefaultComponent layerComponent = (DefaultComponent) this.componentManager.findComponent(layer);
            if (layerComponent != null) {
                layerController = layerComponent.getLayerController();
            }
            logger.debug("Processing indirect directive {} for module {} in {}",
                    directive.getType(), module.getName(), component.getDescriptor().getConfig().getFullName());
            applyModuleDirective(layerController, otherModule, directive.getType().getOpposite(),
                    directive.getPackage(), module);
            var resolvedDirective = new DefaultResolvedModuleDirective(directive.getType(),
                    module, directive.getPackage(), otherModule);
            resolvedDirectives.add(resolvedDirective);
        }
    }

    private void applyModuleDirective(ModuleLayer.Controller controller, Module module, DirectiveType type,
            String pkg, Module otherModule) {
        if (controller != null) {
            if (type == DirectiveType.EXPORTS) {
                controller.addExports(module, pkg, otherModule);
            } else if (type == DirectiveType.OPENS) {
                controller.addOpens(module, pkg, otherModule);
            } else if (type == DirectiveType.READS) {
                controller.addReads(module, otherModule);
            }
        } else {
            logger.debug("No layer controller available for module: {}, applying directive {} via module API",
                    module.getName(), type);
            if (type == DirectiveType.EXPORTS) {
                module.addExports(pkg, otherModule);
            } else if (type == DirectiveType.OPENS) {
                module.addOpens(pkg, otherModule);
            } else if (type == DirectiveType.READS) {
                module.addReads(otherModule);
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Applied directive: {} to module: {}, package: {}, for module: {}",
                    type, module.getName(), pkg, otherModule.getName());
        }
    }

    /**
     * Sometimes we need to get layer name, for example, for adds operation or for logging.
     * @param layer
     * @return
     */
    private String resolveLayerName(ModuleLayer layer) {
        if (layer == Framework.class.getModule().getLayer()) {
            return this.framework.getFullName();
        }
        var descriptor = componentManager.findComponent(layer).getDescriptor();
        if (descriptor != null) {
            return descriptor.getConfig().getName()
                    + Constants.NAME_VERSION_SEPARATOR + descriptor.getConfig().getVersion();
        } else {
            return null;
        }
    }

    /**
     * Finds layers by name or by name and version.
     *
     * @param layerName the name of the framework/component with or without versions. For example,
     * it can be {@code alpha-control:2.0.0} or {@code alpha-control}.
     * @return
     */
    private Map<String, ModuleLayer> findLayers(DefaultComponent component, String layerName) {
        Map<String, ModuleLayer> layersByName = new HashMap<>();
        if (layerName == null) { //current layer
            layersByName.put(component.getDescriptor().getConfig().getFullName(), component.getLayer());
        } else {
            var nameAndVersion = Component.resolveNameAndVersion(layerName);
            if (nameAndVersion.getSecond() == null) { //find by name
                if (nameAndVersion.getFirst().equals(framework.getName())) {
                    layersByName.put(framework.getFullName(), Framework.class.getModule().getLayer());
                } else {
                    Set<ComponentDescriptor> ancestors = component.getDescriptor().findAncestors();
                    for (var ancestor : ancestors) {
                        var config = ancestor.getConfig();
                        if (config.getName().equals(nameAndVersion.getFirst())) {
                            var ancestorComponent = componentManager.getComponent(ancestor.getId());
                            layersByName.put(config.getFullName(), ((DefaultComponent) ancestorComponent).getLayer());
                        }
                    }
                }
            } else { //find by name and version
                if (nameAndVersion.getFirst().equals(framework.getName())
                        && nameAndVersion.getSecond().equals(framework.getVersion())) {
                    layersByName.put(framework.getFullName(), Framework.class.getModule().getLayer());
                } else {
                    Set<ComponentDescriptor> ancestors = component.getDescriptor().findAncestors();
                    for (var ancestor : ancestors) {
                        var config = ancestor.getConfig();
                        if (config.getName().equals(nameAndVersion.getFirst())
                                && config.getVersion().equals(nameAndVersion.getSecond())) {
                            var ancestorComponent = componentManager.getComponent(ancestor.getId());
                            layersByName.put(config.getFullName(), ((DefaultComponent) ancestorComponent).getLayer());
                        }
                    }
                }
            }
        }
        logger.debug("Found layers: {}, used name: {}", layersByName.keySet(), layerName);
        return layersByName;
    }
}
