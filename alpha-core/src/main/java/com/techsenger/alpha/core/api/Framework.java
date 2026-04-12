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

package com.techsenger.alpha.core.api;

import com.techsenger.alpha.core.api.registry.Registry;
import com.techsenger.alpha.core.spi.repo.RepoService;
import com.techsenger.toolkit.core.version.Version;

/**
 *
 * @author Pavel Castornii
 */
public interface Framework {

    /**
     * Returns the version of the framework.
     */
    Version getVersion();

    /**
     * Returns the framework settings.
     */
    FrameworkSettings getSettings();

    /**
     * Returns the component manager.
     */
    ComponentManager getComponentManager();

    /**
     * Returns the log manager.
     */
    LogManager getLogManager();

    /**
     * Returns the service tracker.
     */
    ServiceTracker getServiceTracker();

    /**
     * Returns JVM inspector that can be used to get different data about JVM.
     */
    JvmInspector getJvmInspector();

    /**
     * Returns the framework path manager.
     */
    PathManager getPathManager();

    /**
     * Returns the registry.
     */
    Registry getRegistry();

    /**
     * Returns the repo service.
     */
    RepoService getRepoService();

    /**
     * Stops all components and shuts down the framework.
     */
    void shutdown();

    /**
     * Returns true if framework is shutting down. Otherwise returns false.
     */
    boolean isShuttingDown();
}
