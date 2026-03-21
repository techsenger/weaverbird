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

package com.techsenger.alpha.core.impl.registry;

import com.techsenger.alpha.core.api.PathManager;
import com.techsenger.alpha.core.api.registry.ComponentEntry;
import com.techsenger.alpha.core.api.registry.Registry;
import com.techsenger.toolkit.core.version.Version;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
public class DefaultRegistry implements Registry {

    private static final Logger logger = LoggerFactory.getLogger(DefaultRegistry.class);

    private static final String REGISTRY_FILE = "alpha-registry.xml";

    private final List<ComponentEntry> addedComponents  = new CopyOnWriteArrayList<>();

    private final List<ComponentEntry> resolvedComponents  = new CopyOnWriteArrayList<>();

    private final PathManager pathProvider;

    public DefaultRegistry(PathManager pathProvider) {
        this.pathProvider = pathProvider;
        this.read();
    }

    @Override
    public List<ComponentEntry> getAddedComponents() {
        return Collections.unmodifiableList(addedComponents);
    }

    @Override
    public List<ComponentEntry> getResolvedComponents() {
        return Collections.unmodifiableList(resolvedComponents);
    }

    @Override
    public boolean isComponentAdded(String name, Version version) {
        return this.addedComponents.contains(new ComponentEntry(name, version));
    }

    @Override
    public boolean isComponentResolved(String name, Version version) {
        return this.resolvedComponents.contains(new ComponentEntry(name, version));
    }

    public List<ComponentEntry> getModifiableAddedComponents() {
        return this.addedComponents;
    }

    public List<ComponentEntry> getModifiableResolvedComponents() {
        return this.resolvedComponents;
    }

    public synchronized void save() {
        try {
            var writer = new RegistryXmlWriter(this);
            var path = this.pathProvider.getDataDirectory().resolve(REGISTRY_FILE);
            writer.write(path);
            logger.debug("Registry was written to {}", path);
        } catch (Exception ex) {
            logger.error("Error writing registry file", ex);
        }
    }

    private void read() {
        try {
            var reader = new RegistryXmlReader(this);
            var dataPath = this.pathProvider.getDataDirectory();
            var path = dataPath.resolve(Paths.get(REGISTRY_FILE));
            reader.read(path.toUri().toURL());
            logger.debug("Registry was read from file {}", path);
        } catch (Exception ex) {
            logger.error("Error reading registry file", ex);
        }
    }
}
