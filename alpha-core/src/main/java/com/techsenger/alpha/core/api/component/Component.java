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

package com.techsenger.alpha.core.api.component;

import com.techsenger.alpha.core.api.Constants;
import com.techsenger.alpha.core.api.module.ModuleConfig;
import com.techsenger.alpha.core.api.module.ResolvedModuleDirective;
import com.techsenger.toolkit.core.Pair;
import com.techsenger.toolkit.core.version.Version;
import java.util.List;

/**
 *
 * @author Pavel Castornii
 */
public interface Component {

    /**
     * Resolves name with or without version.
     *
     * @param nameAndVersion the name of the component or the name with the version of the component.
     * @return
     */
    static Pair<String, Version> resolveNameAndVersion(String nameAndVersion) {
        var index = nameAndVersion.lastIndexOf(Constants.NAME_VERSION_SEPARATOR);
        if (index < 0) {
            return new Pair<>(nameAndVersion.trim(), null);
        } else {
            String name = nameAndVersion.substring(0, index).trim();
            Version version = Version.of(nameAndVersion.substring(index + 1).trim());
            return new Pair<>(name, version);
        }
    }

    /**
     * Returns the descriptor of this component. We need the descriptor because its instance can be serialized,
     * while the instance of the component itself cannot.
     *
     * @return
     */
    ComponentDescriptor getDescriptor();

    /**
     * Adds observer.
     * @param observer
     */
    void addObserver(ComponentObserver observer);

    /**
     * Removes observer.
     * @param observer
     */
    void removeObserver(ComponentObserver observer);

    /**
     * Returns module layer.
     * @return module layer.
     */
    ModuleLayer getLayer();

    /**
     * Returns module by config.
     *
     * @param config
     */
    Module getModule(ModuleConfig config);

    /**
     * Returns resolved modules directives. It is important to clarify that these will only be the directives
     * specified in the configuration file. Also note, that one of the modules (source or target) can be from different
     * component layer.
     *
     * @return
     */
    List<ResolvedModuleDirective> getModuleDirectives();

    /**
     * Returns class loaders of this component.
     * @return
     */
    List<ClassLoader> getClassLoaders();
}
