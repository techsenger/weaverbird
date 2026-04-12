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

package com.techsenger.alpha.core.impl.component;

import com.techsenger.alpha.core.api.component.Component;
import com.techsenger.alpha.core.api.component.ComponentObserver;
import com.techsenger.alpha.core.api.module.ModuleConfig;
import com.techsenger.alpha.core.api.module.ResolvedModuleDirective;
import com.techsenger.alpha.core.spi.module.ModuleActivator;
import com.techsenger.alpha.core.spi.module.ModuleContext;
import com.techsenger.toolkit.core.jpms.ModuleUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author Pavel Castornii
 */
public class DefaultComponent implements Component {

    /**
     * The descriptor of this component.
     */
    private final DefaultComponentDescriptor descriptor;

    /**
     * Module layer controller. ModuleLayer.Controller  can be used to export or open any package of any module in the
     * layer to any other module. It's the equivalent of using the --add-exports or --add-opens on the
     * command line to export or open packages of modules in the boot layer.
     *
     * This field is visible only to framework, and not visible to interface.
     */
    private ModuleLayer.Controller layerController;

    /**
     * Class loader for this component not to resolve it every time.
     */
    private List<ClassLoader> classLoaders;

    /**
     * Activator. We saved it because it is always singleton.
     */
    private List<ModuleActivator> activators;

    private Map<Module, ModuleContext> moduleContextsByModule;

    private final Map<ModuleConfig, Module> modulesByConfig = new HashMap<>();

     /**
     * Observers that were created BY this component. We bind them to component as components come and go.
     */
    private List<ComponentObserver> observers = new CopyOnWriteArrayList<>();

    private List<ResolvedModuleDirective> moduleDirectives;

    /**
     * Constructor.
     */
    public DefaultComponent(final DefaultComponentDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    @Override
    public DefaultComponentDescriptor getDescriptor() {
        return this.descriptor;
    }

    @Override
    public ModuleLayer getLayer() {
        return layerController.layer();
    }

    @Override
    public Module getModule(ModuleConfig config) {
        return this.modulesByConfig.get(config);
    }

    /**
     * Returns layer controller.
     * @return
     */
    public ModuleLayer.Controller getLayerController() {
        return layerController;
    }

    /**
     * Sets layer controller.
     * @param layerController
     */
    public void setLayerController(ModuleLayer.Controller layerController) {
        this.layerController = layerController;
        buildDescriptorModuleMap();
    }

    /**
     * Returns activator.
     * @return activator.
     */
    public List<ModuleActivator> getActivators() {
        return activators;
    }

    /**
     * Sets activator.
     * @param activator to be set.
     */
    public void setActivators(final List<ModuleActivator> activators) {
        this.activators = activators;
    }

    /**
     * Returns module context by module.
     * @return module context by module map.
     */
    public Map<Module, ModuleContext> getModuleContextsByModule() {
        return moduleContextsByModule;
    }

    /**
     * Sets Module context by module.
     * @param moduleContextsByModule is map.
     */
    public void setModuleContextsByModule(final Map<Module, ModuleContext> moduleContextsByModule) {
        this.moduleContextsByModule = moduleContextsByModule;
    }

    @Override
    public void addObserver(final ComponentObserver observer) {
        this.observers.add(observer);
    }

    @Override
    public void removeObserver(final ComponentObserver observer) {
        this.observers.remove(observer);
    }

    public List<ComponentObserver> getObservers() {
        return this.observers;
    }

    public List<ClassLoader> getClassLoaders() {
        return classLoaders;
    }

    public void setClassLoaders(List<ClassLoader> classLoaders) {
        this.classLoaders = classLoaders;
    }

    @Override
    public String toString() {
        return "DefaultComponent{" + "descriptor=" + descriptor + ", moduleLayer=" + layerController.layer() + '}';
    }

    @Override
    public List<ResolvedModuleDirective> getModuleDirectives() {
        return moduleDirectives;
    }

    public void setModuleDirectives(List<ResolvedModuleDirective> moduleDirectives) {
        this.moduleDirectives = moduleDirectives;
    }

    private void buildDescriptorModuleMap() {
        var configsByPath = new HashMap<String, ModuleConfig>();
        for (var i = 0; i < descriptor.getConfig().getModules().size(); i++) {
            var config = descriptor.getConfig().getModules().get(i);
            var path = descriptor.getModulePaths().get(i).toAbsolutePath().normalize().toString();
            configsByPath.put(path, config);
        }
        var modulesByPath = new HashMap<String, Module>();
        for (var module : getLayer().modules()) {
            var path = ModuleUtils.getPath(module).toAbsolutePath().normalize().toString();
            modulesByPath.put(path, module);
        }
        for (var entry : configsByPath.entrySet()) {
            var module = modulesByPath.get(entry.getKey());
            if (module != null) {
                this.modulesByConfig.put(entry.getValue(), module);
            } else {
                throw new RuntimeException("Couldn't resolve module by path = " + entry.getKey());
            }
        }
    }
}
