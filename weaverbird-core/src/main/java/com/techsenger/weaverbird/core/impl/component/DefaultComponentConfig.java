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

package com.techsenger.weaverbird.core.impl.component;

import com.techsenger.weaverbird.core.api.Constants;
import com.techsenger.weaverbird.core.api.component.ComponentConfig;
import com.techsenger.weaverbird.core.api.component.ParentConfig;
import com.techsenger.weaverbird.core.api.component.RepositoryConfig;
import com.techsenger.weaverbird.core.api.module.ModuleConfig;
import com.techsenger.toolkit.core.version.Version;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author Pavel Castornii
 */
public class DefaultComponentConfig implements ComponentConfig {

    private final String title;

    private final String name;

    private final Version version;

    private final String type;

    private Map<String, String> metadata;

    private List<RepositoryConfig> repositories;

    private List<ParentConfig> parents;

    private List<ModuleConfig> modules;

    public DefaultComponentConfig(String title, String name, Version version, String type) {
        this.title = title;
        this.name = name;
        this.version = version;
        this.type = type;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getFullName() {
        return name + Constants.NAME_VERSION_SEPARATOR + version;
    }

    @Override
    public Version getVersion() {
        return version;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + Objects.hashCode(this.name);
        hash = 23 * hash + Objects.hashCode(this.version);
        hash = 23 * hash + Objects.hashCode(this.type);
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
        final DefaultComponentConfig other = (DefaultComponentConfig) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.version, other.version)) {
            return false;
        }
        return this.type.equals(other.type);
    }

    @Override
    public Map<String, String> getMetadata() {
        return metadata;
    }

    @Override
    public List<RepositoryConfig> getRepositories() {
        return repositories;
    }

    @Override
    public List<ParentConfig> getParents() {
        return parents;
    }

    @Override
    public List<ModuleConfig> getModules() {
        return modules;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = Collections.unmodifiableMap(metadata);
    }

    public void setRepositories(List<RepositoryConfig> repositories) {
        this.repositories = Collections.unmodifiableList(repositories);
    }

    public void setParents(List<ParentConfig> parents) {
        this.parents = parents;
    }

    public void setModules(List<ModuleConfig> moduleConfigs) {
        this.modules = Collections.unmodifiableList(moduleConfigs);
    }
}
