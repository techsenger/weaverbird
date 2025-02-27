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

package com.techsenger.alpha.api;

import com.techsenger.alpha.api.registry.Registry;
import com.techsenger.alpha.api.spi.FrameworkService;
import com.techsenger.toolkit.core.version.Version;
import java.util.ServiceLoader;

/**
 *
 * @author Pavel Castornii
 */
public class Framework {

    /**
     * Framework instance.
     */
    private static final FrameworkService service = ServiceLoader.load(FrameworkService.class).findFirst().get();

    /**
     * Returns framework version.
     *
     * @return
     */
    public static Version getVersion() {
        return service.getVersion();
    }

    /**
     * Returns framework mode.
     *
     * @return
     */
    public static FrameworkMode getMode() {
        return service.getMode();
    }

    /**
     * Returns the name (without version) of the layer where the framework is located, as a rule it is a boot layer.
     */
    public static String getLayerName() {
        return service.getLayerName();
    }

    /**
     * Returns the name and the version of the layer where framework is located. As a rule it is a boot layer.
     *
     * @return
     */
    public static String getLayerFullName() {
        return service.getLayerFullName();
    }

    /**
     * Returns component manager.
     *
     * @return
     */
    public static ComponentManager getComponentManager() {
        return service.getComponentManager();
    }

    /**
     * Returns log manager.
     *
     * @return
     */
    public static LogManager getLogManager() {
        return service.getLogManager();
    }

    /**
     * Returns Alpha service provider manager.
     *
     * @return
     */
    public static ServiceManager getServiceManager() {
        return service.getServiceManager();
    }

    /**
     * Returns JVM inspector that can be used to get different data about JVM.
     *
     * @return
     */
    public static JvmInspector getJvmInspector() {
        return service.getJvmInspector();
    }

    /**
     * Returns framework path manager.
     *
     * @return
     */
    public static PathManager getPathManager() {
        return service.getPathManager();
    }

    /**
     * Returns registry.
     *
     * @return
     */
    public static Registry getRegistry() {
        return service.getRegistry();
    }

    /**
     * Launches framework.
     */
    public static void launch() {
        service.launch();
    }

    /**
     * Shuts down framework and makes system exit. Two possible sequences:
     *
     * Sequence 1 - Framework.shutdown(); Framework.doShutdown(); System.exit() -> shutdownHook
     * Sequence 2 - termination signal -> shutdownHook -> Framework.doShutdown()
     */
    public static void shutdown() {
        service.shutdown();
    }

    /**
     * Returns true if framework is shutting down. Otherwise returns false.
     *
     * @return
     */
    public boolean isShuttingDown() {
        return service.isShuttingDown();
    }

    /**
     * Returns shutdown reason. If framework is not shutting down then returns null.
     *
     * @return
     */
    public static ShutdownReason getShutdownReason() {
        return service.getShutdownReason();
    }
}
