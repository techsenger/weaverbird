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
import com.techsenger.alpha.api.Constants;
import com.techsenger.alpha.api.Framework;
import com.techsenger.alpha.api.FrameworkMode;
import com.techsenger.alpha.api.PathManager;
import com.techsenger.alpha.api.PathResolver;
import com.techsenger.alpha.api.ServiceManager;
import com.techsenger.alpha.api.component.Component;
import com.techsenger.alpha.api.component.ComponentConfig;
import com.techsenger.alpha.api.component.ComponentConfigInfo;
import com.techsenger.alpha.api.component.ComponentConfigUtils;
import com.techsenger.alpha.api.component.ComponentDescriptor;
import com.techsenger.alpha.api.component.ComponentException;
import com.techsenger.alpha.api.component.ComponentObserver;
import com.techsenger.alpha.api.component.UnknownComponentException;
import com.techsenger.alpha.api.message.MessagePrinter;
import com.techsenger.alpha.api.module.ModuleDescriptor;
import com.techsenger.alpha.api.registry.ComponentEntry;
import com.techsenger.alpha.api.state.ComponentsState;
import com.techsenger.alpha.api.state.DefaultComponentsState;
import com.techsenger.alpha.core.component.ConfigXmlReader;
import com.techsenger.alpha.core.component.DefaultComponent;
import com.techsenger.alpha.core.component.DefaultComponentDescriptor;
import com.techsenger.alpha.core.registry.DefaultRegistry;
import com.techsenger.toolkit.core.StringUtils;
import com.techsenger.toolkit.core.version.Version;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.zip.ZipFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
public class DefaultComponentManager implements ComponentManager {

    private static final Logger logger = LoggerFactory.getLogger(DefaultComponentManager.class);

    private final DefaultRegistry registry;

    private final PathManager pathManager;

    private final DefaultPathResolver pathResolver;

    private final ServiceManager serviceManager;

    private final LayerBuilder layerBuilder;

    private final ModuleActivatorManager activatorManager;

    private final List<ComponentListener> listeners = new ArrayList();

    private volatile int componentIdCounter = 0;

    private final DefaultComponentsState componentsState = new DefaultComponentsState();

    /**
     * Running components by component id.
     */
    private final Map<Integer, Component> componentsById = new ConcurrentHashMap<>();

    private final ComponentConfigInfo configInfo = new ComponentConfigInfo();

    private final ComponentConfigUtils configUtils = new ComponentConfigUtils();

    public DefaultComponentManager(DefaultRegistry registry, PathManager pathManager,
            ServiceManager serviceManager, FrameworkMode mode) {
        this.registry = registry;
        this.pathManager = pathManager;
        this.pathResolver = new DefaultPathResolver(pathManager, this);
        this.serviceManager = serviceManager;
        this.layerBuilder = new LayerBuilder(this);
        this.activatorManager = new ModuleActivatorManager(this);
        var serviceTracker = (DefaultServiceTracker) serviceManager.getServiceTracker();
        serviceTracker.initiailize(this);

        if (mode != FrameworkMode.STANDALONE) {
            addListener(((DefaultServiceManager) serviceManager).createComponentListener());
        }
    }

    @Override
    public synchronized Path buildComponent(String name, Version version, Path path, String extension)
            throws ComponentException, UnknownComponentException {
        ComponentConfig config = readConfig(name, version);
        return buildComponent(config, path, extension);
    }

    @Override
    public synchronized Path buildComponent(ComponentConfig config, Path path, String extension)
            throws ComponentException {
        notifyObservers((o) -> o.onBuilding(config), null);
        var fileManager = new ComponentFileManager(pathManager, pathResolver, this.configInfo, this.configUtils);
        var archivePath = fileManager.build(config, path, extension);
        notifyObservers((o) -> o.onBuilt(config), null);
        logger.info("Built component {}{}{} at {}", config.getName(),
                Constants.NAME_VERSION_SEPARATOR, config.getVersion(), archivePath);
        return archivePath;
    }

    @Override
    public synchronized ComponentConfig addComponent(Path zipPath) throws ComponentException {
        var fileManager = new ComponentFileManager(pathManager, pathResolver, this.configInfo, this.configUtils);
        try (ZipFile zipFile = new ZipFile(zipPath.toFile())) {
            var config = fileManager.readConfig(zipFile);
            if (registry.getModifiableAddedComponents().contains(new ComponentEntry(config.getName(),
                    config.getVersion()))) {
                throw new ComponentException(StringUtils.format("Component {}{}{} is already added",
                        config.getName(), Constants.NAME_VERSION_SEPARATOR, config.getVersion()));
            }
            notifyObservers((o) -> o.onAdding(config), null);
            fileManager.addComponent(config, zipPath, zipFile);
            registry.getModifiableAddedComponents().add(new ComponentEntry(config));
            registry.save();
            notifyObservers((o) -> o.onAdded(config), null);
            logger.info("Added component {}{}{} from {}; total added components: {}", config.getName(),
                    Constants.NAME_VERSION_SEPARATOR, config.getVersion(), zipPath,
                    registry.getModifiableAddedComponents().size());
            return config;
        } catch (IOException ex) {
            throw new ComponentException(StringUtils.format("Error adding component from {}", zipPath), ex);
        }
    }

    @Override
    public synchronized ComponentConfig resolveComponent(String name, Version version, MessagePrinter printer)
            throws ComponentException, UnknownComponentException {
        var config = readConfig(name, version);
        resolveComponent(config, printer);
        return config;
    }

    @Override
    public synchronized void resolveComponent(ComponentConfig config, MessagePrinter printer)
            throws ComponentException {
        //check if component is not resolved yet.
        if (registry.getModifiableResolvedComponents().contains(new ComponentEntry(config))) {
            throw new ComponentException(StringUtils.format("Component {}{}{} is already resolved",
                    config.getName(), Constants.NAME_VERSION_SEPARATOR, config.getVersion()));
        }
        //there can be situations when all files are provided in archive
        boolean resolved = false;
        if (config.getRepositories().isEmpty()) {
            resolved = true;
        } else {
            var repoService = serviceManager.getRepoService();
            if (repoService == null) {
                throw new IllegalStateException("No repo service provided");
            }
            notifyObservers((o) -> o.onResolving(config), null);
            Map<String, String> reposByName = new LinkedHashMap<>(); //keeping order!
            config.getRepositories().forEach(r -> reposByName.put(r.getName(), r.getUrl()));
            resolved = repoService.resolve(Framework.getPathManager().getRepositoryDirectoryPath(),
                    reposByName, (List) config.getModules(), printer);
        }
        if (resolved) {
            registry.getModifiableResolvedComponents().add(new ComponentEntry(config));
            registry.save();
            notifyObservers((o) -> o.onResolved(config), null);
            logger.info("Resolved component {}{}{}; total resolved components: {}", config.getName(),
                    Constants.NAME_VERSION_SEPARATOR, config.getVersion(),
                    registry.getModifiableResolvedComponents().size());
        } else {
            throw new ComponentException(StringUtils.format("Error resolving {}{}{}",
                    config.getName(), Constants.NAME_VERSION_SEPARATOR, config.getVersion()));
        }
    }

    @Override
    public synchronized ComponentDescriptor deployComponent(final String name, final Version version)
            throws ComponentException, UnknownComponentException {
        return deployComponent(name, version, null, null, null, false);
    }

    @Override
    public synchronized ComponentDescriptor deployComponent(final String name, final Version version,
            final String alias) throws ComponentException, UnknownComponentException {
        return deployComponent(name, version, alias, null, null, false);
    }

    @Override
    public synchronized ComponentDescriptor deployComponent(final String name, final Version version,
            final String alias, final List<Integer> parentIds, final List<String> parentAliases,
            boolean useParentClassLoader) throws ComponentException, UnknownComponentException {
        var config = readConfig(name, version);
        return deployComponent(config, alias, parentIds, parentAliases, useParentClassLoader);
    }

    @Override
    public synchronized ComponentDescriptor deployComponent(ComponentConfig config, final String alias,
            final List<Integer> parentIds, final List<String> parentAliases, boolean useParentClassLoader) throws
            ComponentException, UnknownComponentException {
        List<DefaultComponentDescriptor> parentDescriptors = new ArrayList<>();
        if (parentIds != null) {
            for (Integer parentId : parentIds) {
                parentDescriptors.add(provideDescriptor(parentId));
            }
        }
        if (parentAliases != null) {
            for (String parentAlias : parentAliases) {
                parentDescriptors.add(provideDescriptor(parentAlias));
            }
        }
        final DefaultComponentDescriptor descriptor = new DefaultComponentDescriptor(config, alias,
                ++componentIdCounter, parentDescriptors, useParentClassLoader);
        DefaultComponent component = new DefaultComponent(descriptor);
        //here we need to check if component modules are not loaded in boot layer...
        try {
            notifyObservers((o) -> o.onDeploying(config), null); //doesn't throw exceptions
            layerBuilder.build(component);
            componentsById.put(component.getDescriptor().getId(), component);
            for (var l : listeners) {
                l.onComponentDeployed(component); //doesn't throw exceptions
            }
            notifyObservers((o) -> o.onDeployed(component), component.getDescriptor().getId()); //no exceptions
            this.componentsState.setDeployedCount(this.componentsById.size());
            this.componentsState.setId(this.componentsState.getId() + 1);
            logger.info("Deployed component {}{}{} with id: {}; total deployed components: {}; componentsStateId: {}",
                config.getName(), Constants.NAME_VERSION_SEPARATOR, config.getVersion(),
                component.getDescriptor().getId(), this.componentsById.size(), this.componentsState.getId());
            return descriptor;
        } catch (Exception ex) {
            logger.error("Error deploying component {}{}{}", config.getName(), Constants.NAME_VERSION_SEPARATOR,
                    config.getVersion(), ex);
            return null;
        }
    }

    @Override
    public synchronized ComponentDescriptor activateComponent(final int id)
            throws ComponentException, UnknownComponentException {
        var desriptor = provideDescriptor(id);
        activateComponent(desriptor);
        return desriptor;
    }

    @Override
    public synchronized ComponentDescriptor activateComponent(final String alias)
            throws ComponentException, UnknownComponentException {
        var desriptor = provideDescriptor(alias);
        activateComponent(desriptor);
        return desriptor;
    }

    @Override
    public synchronized void activateComponent(ComponentDescriptor descriptor) throws ComponentException {
        DefaultComponent component = (DefaultComponent) getComponent(descriptor.getId());
        var config = descriptor.getConfig();
        if (descriptor.isActivated()) {
            throw new ComponentException(StringUtils.format("Component {}{}{} with id: {}, is already activated",
                    config.getName(), Constants.NAME_VERSION_SEPARATOR, config.getVersion(), descriptor.getId()));
        }
        //we need to activate activators with their layer classloader, but not of the alpha
        //saving classloader
        ClassLoader initialClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            notifyObservers((o) -> o.onActivating(component), component.getDescriptor().getId());
            activatorManager.activateActivators(component); //throws exceptions
            component.getDescriptor().setActivated(true);
            notifyObservers((o) -> o.onActivated(component), component.getDescriptor().getId());
            this.componentsState.setActivatedCount(this.componentsState.getActivatedCount() + 1);
            this.componentsState.setId(this.componentsState.getId() + 1);
            logger.info("Activated component {}{}{} with id: {}; total activated components: {}; componentsStateId: {}",
                config.getName(), Constants.NAME_VERSION_SEPARATOR, config.getVersion(), descriptor.getId(),
                this.componentsState.getActivatedCount(), this.componentsState.getId());
        } catch (Exception ex) {
            throw new ComponentException("Some modules couldn't be activated", ex);
        } finally {
            //restoring here if we had exceptions in activators.
            Thread.currentThread().setContextClassLoader(initialClassLoader);
        }
    }

    @Override
    public synchronized ComponentDescriptor deactivateComponent(int id)
            throws ComponentException, UnknownComponentException {
        var descriptor = provideDescriptor(id);
        deactivateComponent(descriptor);
        return descriptor;
    }

    @Override
    public synchronized ComponentDescriptor deactivateComponent(String alias)
            throws ComponentException, UnknownComponentException {
        var descriptor = provideDescriptor(alias);
        deactivateComponent(descriptor);
        return descriptor;
    }

    @Override
    public synchronized void deactivateComponent(ComponentDescriptor descriptor)
            throws ComponentException {
        DefaultComponent component = (DefaultComponent) getComponent(descriptor.getId());
        var config = descriptor.getConfig();
        if (!descriptor.isActivated()) {
            throw new ComponentException(StringUtils.format("Component {}{}{} with id: {} not activated",
                    config.getName(), Constants.NAME_VERSION_SEPARATOR, config.getVersion(), descriptor.getId()));
        }
        //we need to deactivate activators with their layer classloader, but not of the alpha
        //saving classloader
        ClassLoader initialClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            notifyObservers((o) -> o.onDeactivating(component), descriptor.getId());
            this.activatorManager.deactivateActivators(component);
            component.getDescriptor().setActivated(false);
            notifyObservers((o) -> o.onDeactivated(component), descriptor.getId());
            this.componentsState.setActivatedCount(this.componentsState.getActivatedCount() - 1);
            this.componentsState.setId(this.componentsState.getId() + 1);
            logger.info("Deactivated component {}{}{} with id: {}; total deployed components: {};"
                    + " componentsStateId: {}", config.getName(), Constants.NAME_VERSION_SEPARATOR, config.getVersion(),
                    descriptor.getId(), this.componentsById.size(), this.componentsState.getId());
        } catch (Exception ex) {
            throw new ComponentException("Some modules couldn't be stopped", ex);
        } finally {
            //restoring here if we had exceptions in activators.
            Thread.currentThread().setContextClassLoader(initialClassLoader);
        }
    }

    @Override
    public synchronized ComponentDescriptor undeployComponent(final int id)
            throws ComponentException, UnknownComponentException {
        var descriptor = provideDescriptor(id);
        undeployComponent(descriptor);
        return descriptor;
    }

    @Override
    public synchronized ComponentDescriptor undeployComponent(final String alias)
            throws ComponentException, UnknownComponentException {
        var descriptor = provideDescriptor(alias);
        undeployComponent(descriptor);
        return descriptor;
    }

    @Override
    public synchronized void undeployComponent(ComponentDescriptor descriptor) throws ComponentException {
        var config = descriptor.getConfig();
        try {
            DefaultComponent component = (DefaultComponent) getComponent(descriptor.getId());
            notifyObservers((o) -> o.onUndeploying(component), component.getDescriptor().getId());
            for (var l : listeners) {
                l.onComponentUndeployed(component);
            }
            componentsById.remove(component.getDescriptor().getId());
            notifyObservers((o) -> o.onUndeployed(component), component.getDescriptor().getId());
            this.componentsState.setDeployedCount(this.componentsById.size());
            this.componentsState.setId(this.componentsState.getId() + 1);
            logger.info("Undeployed component {}{}{} with id: {}; total deployed components: {}; componentsStateId: {}",
                config.getName(), Constants.NAME_VERSION_SEPARATOR, config.getVersion(), descriptor.getId(),
                this.componentsById.size(), this.componentsState.getId());
        } catch (Exception ex) {
            logger.error("Error undeploying component {}{}{}", config.getName(), Constants.NAME_VERSION_SEPARATOR,
                    config.getVersion(), ex);
        }
    }

    @Override
    public synchronized ComponentConfig unresolveComponent(String name, Version version, MessagePrinter printer)
            throws ComponentException, UnknownComponentException {
        var config = readConfig(name, version);
        unresolveComponent(config, printer);
        return config;
    }

    @Override
    public synchronized void unresolveComponent(ComponentConfig config, MessagePrinter printer)
            throws ComponentException {
        //check if component is not resolved yet.
        var regEntryIndex = registry.getModifiableResolvedComponents().indexOf(new ComponentEntry(config));
        if (regEntryIndex == -1) {
            throw new ComponentException(StringUtils.format("Component {}{}{} is not resolved",
                    config.getName(), Constants.NAME_VERSION_SEPARATOR, config.getVersion()));
        }
        var repoService = serviceManager.getRepoService();
        if (repoService == null) {
            throw new IllegalStateException("No repo service provided");
        }
        //now we need all installed configs
        List<ComponentConfig> otherResolvedConfigs = new ArrayList<>();
        for (var entry : registry.getModifiableResolvedComponents()) {
            try {
                ComponentConfig someConfig = readConfig(entry.getName(), entry.getVersion());
                if (!config.equals(someConfig)) {
                    otherResolvedConfigs.add(someConfig);
                }
            } catch (Exception e) {
                logger.error("Component {}{}{} is in registry, but its config not found", entry.getName(),
                        Constants.NAME_VERSION_SEPARATOR, entry.getVersion(), e);
            }
        }
        //now we need to find the modules that are used by other installed components
        Set<ModuleDescriptor> otherConfigModules = new HashSet<>();
        for (var otherConfig : otherResolvedConfigs) {
            for (var module : otherConfig.getModules()) {
                otherConfigModules.add(module);
            }
        }
        List<ModuleDescriptor> unresolvedModules = new ArrayList<>();
        //now we can find modules that must be uninstalled
        for (var module : config.getModules()) {
            if (!otherConfigModules.contains(module)) {
                unresolvedModules.add(module);
            }
        }
        boolean result;
        if (unresolvedModules.isEmpty()) {
            result = true;
        } else {
            notifyObservers((o) -> o.onUnresolving(config), null);
            result = repoService.unresolve(Framework.getPathManager().getRepositoryDirectoryPath(),
                    (List) unresolvedModules, printer);
            notifyObservers((o) -> o.onUnresolved(config), null);
        }
        if (result) {
            registry.getModifiableResolvedComponents().remove(regEntryIndex);
            registry.save();
            logger.info("Unresolved component {}{}{}; total resolved components: {}",
                    config.getName(), Constants.NAME_VERSION_SEPARATOR, config.getVersion(),
                    registry.getModifiableResolvedComponents().size());
        }
    }

    @Override
    public synchronized ComponentConfig removeComponent(String name, Version version)
            throws ComponentException, UnknownComponentException {
        var config = readConfig(name, version);
        removeComponent(config);
        return config;
    }

    @Override
    public synchronized void removeComponent(ComponentConfig config) throws ComponentException {
        var regEntryIndex = registry.getModifiableAddedComponents().indexOf(new ComponentEntry(config));
        if (regEntryIndex == -1) {
            throw new ComponentException(StringUtils.format("Component {}{}{} is not added",
                    config.getName(), Constants.NAME_VERSION_SEPARATOR, config.getVersion()));
        }
        notifyObservers((o) -> o.onRemoving(config), null);
        var fileManager = new ComponentFileManager(pathManager, pathResolver, this.configInfo, this.configUtils);
        fileManager.removeComponent(config);
        registry.getModifiableAddedComponents().remove(regEntryIndex);
        registry.save();
        notifyObservers((o) -> o.onRemoved(config), null);
        logger.info("Removed component {}{}{}; total added components: {}",
                config.getName(), Constants.NAME_VERSION_SEPARATOR, config.getVersion(),
                registry.getModifiableAddedComponents().size());
    }

    @Override
    public synchronized ComponentConfig installComponent(Path path, MessagePrinter printer) throws ComponentException {
        var config = addComponent(path);
        resolveComponent(config, printer);
        return config;
    }

    @Override
    public synchronized ComponentDescriptor startComponent(final String name, final Version version)
            throws ComponentException, UnknownComponentException {
        var component = deployComponent(name, version);
        activateComponent(component);
        return component;
    }

    @Override
    public synchronized ComponentDescriptor startComponent(final String name, final Version version,
            final String alias) throws ComponentException, UnknownComponentException {
        return startComponent(name, version, alias, null, null, false);
    }

    @Override
    public synchronized ComponentDescriptor startComponent(final String name, final Version version,
            final String alias, final List<Integer> parentIds, final List<String> parentAliases,
            boolean useParentClassLoader) throws ComponentException, UnknownComponentException {
        var config = readConfig(name, version);
        return startComponent(config, alias, parentIds, parentAliases, useParentClassLoader);
    }

    @Override
    public synchronized ComponentDescriptor startComponent(ComponentConfig config, String alias,
            List<Integer> parentIds, List<String> parentAliases, boolean useParentClassLoader)
            throws ComponentException, UnknownComponentException {
        var descriptor = deployComponent(config, alias, parentIds, parentAliases, useParentClassLoader);
        activateComponent(descriptor);
        return descriptor;
    }

    @Override
    public synchronized ComponentDescriptor stopComponent(final int id)
            throws ComponentException, UnknownComponentException {
        var descriptor = provideDescriptor(id);
        stopComponent(descriptor);
        return descriptor;
    }

    @Override
    public synchronized ComponentDescriptor stopComponent(final String alias)
            throws ComponentException, UnknownComponentException {
        var descriptor = provideDescriptor(alias);
        stopComponent(descriptor);
        return descriptor;
    }

    @Override
    public synchronized void stopComponent(ComponentDescriptor descriptor) throws ComponentException {
        deactivateComponent(descriptor);
        undeployComponent(descriptor);
    }

    @Override
    public synchronized ComponentConfig uninstallComponent(final String name, final Version version,
            MessagePrinter printer) throws ComponentException, UnknownComponentException  {
        var config = readConfig(name, version);
        uninstallComponent(config, printer);
        return config;
    }

    @Override
    public synchronized void uninstallComponent(final ComponentConfig config, MessagePrinter printer)
            throws ComponentException  {
        unresolveComponent(config, printer);
        removeComponent(config);
    }

    @Override
    public Component getComponent(int id) {
        return componentsById.get(id);
    }

    @Override
    public Collection<Component> getComponents() {
        return Collections.unmodifiableCollection(componentsById.values());
    }

    @Override
    public Collection<ComponentDescriptor> getDescriptors() {
        return componentsById.values().stream().map(c -> c.getDescriptor()).collect(Collectors.toList());
    }

    @Override
    public Component findComponent(final ModuleLayer layer) {
        Component component = null;
        for (var current : componentsById.values()) {
            if (current.getLayer() == layer) {
                component = current;
                break;
            }
        }
        return component;
    }

    @Override
    public Component findComponent(String alias) {
        Component component = null;
        for (var c : this.componentsById.values()) {
            var d = c.getDescriptor();
            if (d.getAlias() != null && d.getAlias().equals(alias)) {
                component = c;
                break;
            }
        }
        return component;
    }

    @Override
    public Collection<Component> findComponents(String name, Version version) {
        return this.componentsById.values().stream()
                .filter(e -> e.getDescriptor().getConfig().getName().equals(name)
                        && e.getDescriptor().getConfig().getVersion().equals(version))
                .collect(Collectors.toList());
    }

    @Override
    public ComponentConfig readConfig(String name, Version version)
            throws ComponentException, UnknownComponentException {
        Path componentConfigPath = this.pathResolver.resolveConfigFilePath(name, version);
        if (!registry.getModifiableAddedComponents().contains(new ComponentEntry(name, version))) {
            throw new UnknownComponentException(StringUtils.format("Component {}{}{} not found", name,
                    Constants.NAME_VERSION_SEPARATOR, version));
        }
        if (!Files.exists(componentConfigPath)) {
            throw new UnknownComponentException(StringUtils.format("Not found config file for {}{}{} at {}",
                name, Constants.NAME_VERSION_SEPARATOR, version, componentConfigPath));
        }
        ConfigXmlReader xmlReader = new ConfigXmlReader();
        try {
            var config = xmlReader.read(componentConfigPath, this.configInfo, this.configUtils);
            return config;
        } catch (Exception ex) {
            throw new ComponentException(StringUtils.format("Error reading configuration for {}{}{}",
                    name, Constants.NAME_VERSION_SEPARATOR, version), ex);
        }
    }

    @Override
    public PathResolver getPathResolver() {
        return pathResolver;
    }

    @Override
    public synchronized ComponentsState getComponentsState() {
        return new DefaultComponentsState(this.componentsState.getId(), this.componentsState.getDeployedCount(),
                this.componentsState.getActivatedCount());
    }

    @Override
    public ComponentConfigInfo getConfigInfo() {
        return configInfo;
    }

    @Override
    public ComponentConfigUtils getConfigUtils() {
        return configUtils;
    }

    public synchronized void addListener(ComponentListener listener) {
        this.listeners.add(listener);
    }

    public synchronized void removeListener(ComponentListener listener) {
        this.listeners.remove(listener);
    }

    /**
     * Notifies component observers.
     *
     * @param consumer
     * @param eventComponentId the id of the component on which the event was invoked, or null if there is no component
     */
    private void notifyObservers(final Consumer<ComponentObserver> consumer, Integer eventComponentId) {
        ClassLoader initialClassLoader = Thread.currentThread().getContextClassLoader();
        logger.trace("ClassLoader before observers: {}", initialClassLoader);
        //call component observers
        for (Component c : this.componentsById.values()) {
            DefaultComponent component = (DefaultComponent) c;
            //started component knows in activators
            if (eventComponentId != null && component.getDescriptor().getId() == eventComponentId) {
                continue;
            }
            if (component.getObservers() == null || component.getObservers().isEmpty()) {
                continue;
            }
            try {
                component.getObservers().forEach(e -> {
                    Thread.currentThread().setContextClassLoader(e.getClass().getModule().getClassLoader());
                    consumer.accept(e);
                });
            } catch (Exception ex) {
                logger.error("Error calling component observer", ex);
            } finally {
                Thread.currentThread().setContextClassLoader(initialClassLoader);
            }
        }
    }

    private DefaultComponentDescriptor provideDescriptor(final int id) throws UnknownComponentException {
        var component = componentsById.get(id);
        if (component == null) {
            throw new UnknownComponentException(StringUtils.format("Component with id:{} wasn't found", id));
        }
        return (DefaultComponentDescriptor) component.getDescriptor();
    }

    private DefaultComponentDescriptor provideDescriptor(final String alias) throws UnknownComponentException {
        var component = findComponent(alias);
        if (component == null) {
            throw new UnknownComponentException(StringUtils.format("Component with alias:{} wasn't found", alias));
        }
        return (DefaultComponentDescriptor) component.getDescriptor();
    }
}
