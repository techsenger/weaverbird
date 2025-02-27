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

package com.techsenger.alpha.api.registry;

import com.techsenger.alpha.api.component.ComponentConfig;
import com.techsenger.toolkit.core.version.Version;
import java.util.Objects;

/**
 *
 * @author Pavel Castornii
 */
public class ComponentEntry {

    private final String name;

    private final Version version;

    public ComponentEntry(String name, Version version) {
        this.name = name;
        this.version = version;
    }

    public ComponentEntry(ComponentConfig config) {
        this.name = config.getName();
        this.version = config.getVersion();
    }

    public String getName() {
        return name;
    }

    public Version getVersion() {
        return version;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.name);
        hash = 83 * hash + Objects.hashCode(this.version);
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
        final ComponentEntry other = (ComponentEntry) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return Objects.equals(this.version, other.version);
    }

    @Override
    public String toString() {
        return "ComponentEntry{" + "name=" + name + ", version=" + version + '}';
    }
}
