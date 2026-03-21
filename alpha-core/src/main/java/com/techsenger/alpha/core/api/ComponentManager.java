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

import com.techsenger.alpha.core.api.component.Component;
import com.techsenger.alpha.core.api.component.ComponentConfig;
import com.techsenger.alpha.core.api.component.ComponentConfigInfo;
import com.techsenger.alpha.core.api.component.ComponentConfigUtils;
import com.techsenger.alpha.core.api.component.ComponentDescriptor;
import com.techsenger.alpha.core.api.component.ComponentException;
import com.techsenger.alpha.core.api.component.UnknownComponentException;
import com.techsenger.alpha.core.api.message.MessagePrinter;
import com.techsenger.alpha.core.api.state.ComponentsState;
import com.techsenger.toolkit.core.version.Version;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Pavel Castornii
 */
public interface ComponentManager {

    /**
     * Returns the component path manager.
     *
     * @return
     */
    PathResolver getPathResolver();

    /**
     * Builds a component distro archive. If a file with this path already exists, it will be overwritten.
     *
     * @param name the name of the component
     * @param version the version of the component
     * @param directoryPath the path of the built distro archive directory
     * @param extension the extension of the built archive
     * @throws ComponentException
     * @throws UnknownComponentException
     * @return the path to the built archive
     */
    Path buildComponent(String name, Version version, Path directoryPath, String extension)
            throws ComponentException, UnknownComponentException;

    /**
     * Builds a component distro archive. If a file with this path already exists, it will be overwritten.
     *
     * @param config the config of the component
     * @param directoryPath the path of the built distro archive directory
     * @param extension the extension of the built archive
     * @throws ComponentException
     * @return the path to the built archive
     */
    Path buildComponent(ComponentConfig config, Path directoryPath, String extension) throws ComponentException;

    /**
     * Adds a component by unzipping distro archive to framework root folder.
     *
     * @param path the path to to component distro archive
     * @throws ComponentException
     * @return the config of the component
     */
    ComponentConfig addComponent(Path path) throws ComponentException;

    /**
     * Adds a component with the specified xml configuration.
     *
     * @param xmlConfig the content of the xml configuration.
     * @throws ComponentException
     * @return the config of the component
     */
    ComponentConfig addComponent(String xmlConfig) throws ComponentException;

    /**
     * Resolves a component by providing all its modules presence in repo.
     *
     * @param name
     * @param version
     * @param printer
     * @throws ComponentException
     * @throws UnknownComponentException
     * @return the config of the component
     */
    ComponentConfig resolveComponent(String name, Version version, MessagePrinter printer)
            throws ComponentException, UnknownComponentException;

    /**
     * Resolves a component by providing all its modules presence in repo.
     *
     * @param config
     * @throws ComponentException
     */
    void resolveComponent(ComponentConfig config, MessagePrinter printer) throws ComponentException;

     /**
     * Deploys a component by creating a JPMS layer with all its modules.
     *
     * @param name is the name of the component.
     * @param version is the version of the component.
     * @throws ComponentException if there are any state problems, for example attempt to start running
     * component.
     * @throws UnknownComponentException is component configuration is not found.
     * @return the ComponentDescriptor of the running component
     */
    ComponentDescriptor deployComponent(String name, Version version)
            throws ComponentException, UnknownComponentException;

    /**
     * Deploys a component by creating a JPMS layer with all its modules.
     *
     * @param name is the name of the component.
     * @param version is the version of the component.
     * @param alias is the alias of the component, can be null.
     * @throws ComponentException if there are any state problems, for example attempt to start running
     * component.
     * @throws UnknownComponentException is component configuration is not found.
     * @return the ComponentDescriptor of the running component
     */
    ComponentDescriptor deployComponent(String name, Version version, String alias)
            throws ComponentException, UnknownComponentException;

    /**
     * Deploys a component by creating a JPMS layer with all its modules.
     *
     * @param name is the name of the component.
     * @param version is the version of the component.
     * @param alias is the alias of the component, can be null.
     * @param parentIds the ids of the parent components.
     * @param parentAliases the aliases of the parent components.
     * @param useParentClassLoader {@code true} to use the parent class loader; {@code false} otherwise.
     * @throws ComponentException if there are any state problems, for example attempt to start running
     * component.
     * @throws UnknownComponentException is component configuration is not found.
     * @return the ComponentDescriptor of the running component
     */
    ComponentDescriptor deployComponent(String name, Version version, String alias, List<Integer> parentIds,
            List<String> parentAliases, boolean useParentClassLoader) throws ComponentException,
            UnknownComponentException;

    /**
     * Deploys a component by creating a JPMS layer with all its modules.
     *
     * @param config component descriptor.
     * @param alias is the alias of the component, can be null.
     * @param parentIds the ids of the parent components.
     * @param parentAliases the aliases of the parent components.
     * @param useParentClassLoader {@code true} to use the parent class loader; {@code false} otherwise.
     * @throws ComponentException if there are any state problems, for example attempt to start running
     * component.
     * @return the ComponentDescriptor of the running component
     */
    ComponentDescriptor deployComponent(ComponentConfig config, String alias, List<Integer> parentIds,
            List<String> parentAliases, boolean useParentClassLoader) throws ComponentException,
            UnknownComponentException;

    /**
     * Activates a component by calling all its activators.
     *
     * @param id is the component id.
     * @return the ComponentDescriptor of the running component
     */
    ComponentDescriptor activateComponent(int id) throws ComponentException, UnknownComponentException;

    /**
     * Activates a component by calling all its activators.
     *
     * @param alias is the component alias.
     * @return the ComponentDescriptor of the running component
     */
    ComponentDescriptor activateComponent(String alias) throws ComponentException, UnknownComponentException;

    /**
     * Activates a component by calling all its activators.
     *
     * @param descriptor
     */
    void activateComponent(ComponentDescriptor descriptor) throws ComponentException;

    /**
     * Deactivates a component by calling all its activators.
     *
     * @param id is the component id.
     * @return the ComponentDescriptor of the running component
     */
    ComponentDescriptor deactivateComponent(int id) throws ComponentException, UnknownComponentException;

    /**
     * Deactivates a component by calling all its activators.
     *
     * @param alias is the component alias.
     * @return the ComponentDescriptor of the running component
     */
    ComponentDescriptor deactivateComponent(String alias) throws ComponentException, UnknownComponentException;

    /**
     * Deactivates a component by calling all its activators.
     *
     * @param descriptor
     */
    void deactivateComponent(ComponentDescriptor descriptor) throws ComponentException;

    /**
     * Undeploys a component by destroying its JPMS layer.
     *
     * @param id is the component id.
     * @return the ComponentDescriptor of the running component
     */
    ComponentDescriptor undeployComponent(int id) throws ComponentException, UnknownComponentException;

    /**
     * Undeploys a component by destroying its JPMS layer.
     *
     * @param alias is the component alias.
     * @return the ComponentDescriptor of the running component
     */
    ComponentDescriptor undeployComponent(String alias) throws ComponentException, UnknownComponentException;

    /**
     * Undeploys a component by destroying its JPMS layer.
     *
     * @param descriptor
     */
    void undeployComponent(ComponentDescriptor descriptor) throws ComponentException;

    /**
     * Unresolves a component by removing all its modules from repo.
     *
     * @param name
     * @param version
     * @param printer
     * @return the config of the component
     * @throws ComponentException
     * @throws UnknownComponentException
     */
    ComponentConfig unresolveComponent(String name, Version version, MessagePrinter printer)
            throws ComponentException, UnknownComponentException;

    /**
     * Unresolves a component by removing all its modules from repo.
     *
     * @param config.
     * @param printer
     * @throws ComponentException
     */
    void unresolveComponent(ComponentConfig config, MessagePrinter printer) throws ComponentException;

    /**
     * Removes a component by removing all component files from framework directory.
     *
     * @param name
     * @param version
     * @return the config of the component
     * @throws ComponentException
     * @throws UnknownComponentException
     */
    ComponentConfig removeComponent(String name, Version version) throws ComponentException, UnknownComponentException;

    /**
     * Removes a component by removing all component files from framework directory.
     *
     * @param config.
     * @throws ComponentException
     */
    void removeComponent(ComponentConfig config) throws ComponentException;

    /**
     * Installs a component (adds and resolves it).
     *
     * @param path the path to component distro archive
     * @param printer
     * @throws ComponentException
     * @return the config of the component
     */
    ComponentConfig installComponent(Path path, MessagePrinter printer) throws ComponentException;

    /**
     * Installs a component (adds and resolves it).
     *
     * @param xmlConfig the content of the xml configuration.
     * @param printer
     * @throws ComponentException
     * @return the config of the component
     */
    ComponentConfig installComponent(String xmlConfig, MessagePrinter printer) throws ComponentException;

    /**
     * Starts a component (deploys and activates it).
     *
     * @param name is the name of the component.
     * @param version is the version of the component.
     * @throws ComponentException if there are any state problems, for example attempt to start running
     * component.
     * @throws UnknownComponentException is component configuration is not found.
     * @return the ComponentDescriptor of the running component
     */
    ComponentDescriptor startComponent(String name, Version version)
            throws ComponentException, UnknownComponentException;

    /**
     * Starts a component (deploys and activates it).
     *
     * @param name is the name of the component.
     * @param version is the version of the component.
     * @param alias is the alias of the component, can be null.
     * @throws ComponentException if there are any state problems, for example attempt to start running
     * component.
     * @throws UnknownComponentException is component configuration is not found.
     * @return the ComponentDescriptor of the running component
     */
    ComponentDescriptor startComponent(String name, Version version, String alias)
            throws ComponentException, UnknownComponentException;

    /**
     * Starts a component (deploys and activates it).
     *
     * @param name is the name of the component.
     * @param version is the version of the component.
     * @param alias is the alias of the component, can be null.
     * @param parentIds the ids of the parent components.
     * @param parentAliases the aliases of the parent components.
     * @param useParentClassLoader {@code true} to use the parent class loader; {@code false} otherwise.
     * @throws ComponentException if there are any state problems, for example attempt to start running
     * component.
     * @throws UnknownComponentException is component configuration is not found.
     * @return the ComponentDescriptor of the running component
     */
    ComponentDescriptor startComponent(String name, Version version, String alias, List<Integer> parentIds,
            List<String> parentAliases, boolean useParentClassLoader) throws ComponentException,
            UnknownComponentException;

    /**
     * Starts a component (deploys and activates it).
     *
     * @param config component descriptor.
     * @param alias is the alias of the component, can be null.
     * @param parentIds the ids of the parent components.
     * @param parentAliases the aliases of the parent components.
     * @param useParentClassLoader {@code true} to use the parent class loader; {@code false} otherwise.
     * @throws ComponentException if there are any state problems, for example attempt to start running
     * component.
     * @return the the ComponentDescriptor of the running component
     */
    ComponentDescriptor startComponent(ComponentConfig config, String alias, List<Integer> parentIds,
            List<String> parentAliases, boolean useParentClassLoader) throws ComponentException,
            UnknownComponentException;

    /**
     * Stops a component (deactivates and undeploys it).
     *
     * @param id is the component id.
     * @return the ComponentDescriptor of the running component
     */
    ComponentDescriptor stopComponent(int id) throws ComponentException, UnknownComponentException;

    /**
     * Stops a component (deactivates and undeploys it).
     *
     * @param alias is the component alias.
     * @return the ComponentDescriptor of the running component
     */
    ComponentDescriptor stopComponent(String alias) throws ComponentException, UnknownComponentException;

    /**
     * Stops a component (deactivates and undeploys it).
     *
     */
    void stopComponent(ComponentDescriptor descriptor) throws ComponentException;

    /**
     * Uninstalls a component (unresolves and removes it).
     *
     * @param name
     * @param version
     * @param printer
     * @return the config of the component
     * @throws ComponentException
     * @throws UnknownComponentException
     */
    ComponentConfig uninstallComponent(String name, Version version, MessagePrinter printer)
            throws ComponentException, UnknownComponentException;

    /**
     * Uninstalls a component (unresolves and removes it).
     *
     * @param config.
     * @param printer
     * @throws ComponentException
     */
    void uninstallComponent(ComponentConfig config, MessagePrinter printer) throws ComponentException;

    /**
     * Returns references to all deployed and activated components.
     *
     * @return
     */
    Collection<Component> getComponents();

    /**
     * Returns deployed and activated component by id or null.
     *
     * @param id
     * @return
     */
    Component getComponent(int id);

    /**
     * Returns deployed and activated component descriptors.
     *
     * @return
     */
    Collection<ComponentDescriptor> getDescriptors();

    /**
     * Finds a deployed and activated component by layer. Returns null if not found.
     *
     * @param layer
     * @return
     */
    Component findComponent(ModuleLayer layer);

    /**
     * Finds a deployed and activated component by alias. Returns null if not found.
     *
     * @param alias
     * @return
     */
    Component findComponent(String alias);

    /**
     * Finds deployed and activated components by name and version. There can be N instances with the same
     * name and version.
     *
     * @param name
     * @param version
     * @return
     */
    Collection<Component> findComponents(String name, Version version);

    /**
     * Reads one configured/installed descriptor.
     *
     * @return
     */
    ComponentConfig readConfig(String name, Version version) throws ComponentException, UnknownComponentException;

    /**
     * Returns the components state.
     *
     * @return
     */
    ComponentsState getComponentsState();

    /**
     * Returns the info map that will be used for EL context in the component config xml file.
     *
     * @return
     */
    ComponentConfigInfo getConfigInfo();

    /**
     * Returns the utils map that will be used for EL context in the component config xml file.
     *
     * @return
     */
    ComponentConfigUtils getConfigUtils();
}
