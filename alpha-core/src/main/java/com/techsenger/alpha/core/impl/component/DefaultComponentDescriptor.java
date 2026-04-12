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

import com.techsenger.alpha.core.api.ComponentManager;
import com.techsenger.alpha.core.api.component.ComponentConfig;
import com.techsenger.alpha.core.api.component.ComponentDescriptor;
import com.techsenger.alpha.core.api.module.ModuleType;
import com.techsenger.toolkit.core.Recursive;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 *
 * @author Pavel Castornii
 */
public class DefaultComponentDescriptor implements Serializable, ComponentDescriptor {

    private final ComponentManager componentManager;

    private final ComponentConfig config;

    private final String alias;

    private final Integer id;

    private boolean activated;

    private final List<DefaultComponentDescriptor> parents;

    private final boolean parentClassLoaderUsed;

    private Boolean containsWarModules = null;

    private List<Path> modulePaths;

    public DefaultComponentDescriptor(ComponentManager componentManager, ComponentConfig config, String alias,
            Integer id, List<DefaultComponentDescriptor> parents, boolean parentClassLoaderUsed) {
        this.componentManager = componentManager;
        this.config = config;
        this.alias = alias;
        this.id = id;
        this.parents = parents;
        this.parentClassLoaderUsed = parentClassLoaderUsed;
    }

    @Override
    public ComponentConfig getConfig() {
        return config;
    }

    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DefaultComponentDescriptor other = (DefaultComponentDescriptor) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public List<ComponentDescriptor> getParents() {
        return (List) parents;
    }

    @Override
    public boolean isParentClassLoaderUsed() {
        return this.parentClassLoaderUsed;
    }

    @Override
    public boolean isActivated() {
        return activated;
    }

    @Override
    public Set<ComponentDescriptor> findAncestors() {
        //TODO this implementation is not optimal
        ComponentDescriptor descriptor = this;
        var ancestorDescriptors = new HashSet<ComponentDescriptor>();
        Recursive<Consumer<List<? extends ComponentDescriptor>>> recursive = new Recursive<>();
        recursive.setFunction(parents -> {
            for (var parent : parents) {
                if (!ancestorDescriptors.contains(parent)) {
                    ancestorDescriptors.add(parent);
                    recursive.getFunction().accept(parent.getParents());
                }
            }
        });
        recursive.getFunction().accept(descriptor.getParents());
        return ancestorDescriptors;
    }

    @Override
    public Set<ComponentDescriptor> findDescendants() {
        //TODO this implementation is not optimal
        var possibleDescendants = componentManager.getDescriptors().stream()
                .filter(d -> d.getId() > this.id).collect(Collectors.toSet());
        var result = possibleDescendants.stream().filter(d -> d.findAncestors().contains(this))
                .collect(Collectors.toSet());
        return result;
    }

    @Override
    public String toString() {
        return "DefaultComponentDescriptor{" + "id=" + id + ", alias=" + alias + ", activated=" + activated + '}';
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    @Override
    public boolean containsWarModules() {
        if (this.containsWarModules != null) {
            return this.containsWarModules;
        }
        this.containsWarModules = false;
        for (var m : this.config.getModules()) {
            if (m.getType() == ModuleType.WAR) {
                this.containsWarModules = true;
                break;
            }
        }
        return this.containsWarModules;
    }

    public List<Path> getModulePaths() {
        return modulePaths;
    }

    public void setModulePaths(List<Path> modulePaths) {
        this.modulePaths = modulePaths;
    }
}
