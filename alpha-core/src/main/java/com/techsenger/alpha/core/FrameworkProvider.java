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
import com.techsenger.alpha.api.JvmInspector;
import com.techsenger.alpha.api.LogManager;
import com.techsenger.alpha.api.PathManager;
import com.techsenger.alpha.api.ServiceManager;
import com.techsenger.alpha.api.ShutdownReason;
import com.techsenger.alpha.api.component.ComponentDescriptor;
import com.techsenger.alpha.api.registry.Registry;
import com.techsenger.alpha.api.spi.FrameworkService;
import com.techsenger.alpha.core.launcher.ModeResolver;
import com.techsenger.alpha.core.project.ProjectInfo;
import com.techsenger.alpha.core.registry.DefaultRegistry;
import com.techsenger.toolkit.core.file.FileUtils;
import com.techsenger.toolkit.core.version.Version;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
public class FrameworkProvider implements FrameworkService {

    private static final String LAYER_NAME = "alpha-framework";

    /**
     * Logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(Framework.class);

    /**
     * Log manager.
     */
    private static final LogManager logManager = new DefaultLogManager();

    /*
     * Logger initialization. We need to initialize it before any log messages.
     */
    static {
        logManager.initialize();
    }

    /**
     * Mode.
     */
    private final FrameworkMode mode = ModeResolver.resolve();

    /**
     * Path provider. It must be created before component manager.
     */
    private final PathManager pathManager = new DefaultPathManager();

    /**
     * JVM inspector.
     */
    private final JvmInspector jvmInspector = new DefaultJvmInspector();

    /**
     * Registry.
     */
    private final DefaultRegistry registry = new DefaultRegistry(pathManager);

    /**
     * Service inspector.
     */
    private final ServiceManager serviceManager = new DefaultServiceManager();

    /**
     * Component manager.
     */
    private final ComponentManager componentManager =
            new DefaultComponentManager(registry, pathManager, serviceManager, mode);

    /**
     * Flag indicating that framework is shutting down.
     */
    private volatile boolean shuttingDown = false;

    /**
     * Variable shows the reason of shutting down.
     */
    private volatile ShutdownReason shutdownReason = null;

    public FrameworkProvider() {

    }

    @Override
    public String getLayerName() {
        return LAYER_NAME;
    }

    @Override
    public String getLayerFullName() {
        return LAYER_NAME + Constants.NAME_VERSION_SEPARATOR + getVersion();
    }

    @Override
    public Version getVersion() {
        return ProjectInfo.getVersion();
    }

    @Override
    public FrameworkMode getMode() {
        return mode;
    }

    @Override
    public LogManager getLogManager() {
        return logManager;
    }

    @Override
    public ComponentManager getComponentManager() {
        return componentManager;
    }

    @Override
    public ServiceManager getServiceManager() {
        return serviceManager;
    }

    @Override
    public JvmInspector getJvmInspector() {
        return jvmInspector;
    }

    @Override
    public PathManager getPathManager() {
        return pathManager;
    }

    @Override
    public Registry getRegistry() {
        return registry;
    }

    @Override
    public synchronized void launch() {
        //this hook will be called on System.exit(0), kill -9, kill -15, ctrl+c...
        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                if (shutdownReason == ShutdownReason.INTERNAL_CALL) {
                    //must be called at the very end because of https://issues.apache.org/jira/browse/LOG4J2-3678
                    logManager.deinitialize();
                } else {
                    shuttingDown = true;
                    shutdownReason = ShutdownReason.EXTERNAL_SIGNAL;
                    doShutdown();
                    logManager.deinitialize();
                }
            }
        });
        long pid = ProcessHandle.current().pid();
        var pidFilePath = pathManager.getPidFilePath();
        try {
            FileUtils.writeFile(pidFilePath, String.valueOf(pid), StandardCharsets.UTF_8);
            logger.debug("Wrote pid {} to file {}", pid, pidFilePath.toString());
        } catch (Exception ex) {
            logger.error("Error writing pid to file", ex);
        }
        logger.info("Alpha Framework {} was launched in {} mode",
                getVersion().getFull(), mode);
    }

    @Override
    public synchronized void shutdown() {
        shuttingDown = true;
        shutdownReason = ShutdownReason.INTERNAL_CALL;
        doShutdown();
        System.exit(0);
    }

    @Override
    public boolean isShuttingDown() {
        return shuttingDown;
    }

    @Override
    public ShutdownReason getShutdownReason() {
        return shutdownReason;
    }

    private void doShutdown() {
        logger.debug("Shutting down framework; reason: {}", shutdownReason);
        List<ComponentDescriptor> descriptors =
                new ArrayList<>(componentManager.getDescriptors());
        Collections.sort(descriptors, new Comparator<ComponentDescriptor>() {
            @Override public int compare(ComponentDescriptor d1, ComponentDescriptor d2) {
                return d2.getId() - d1.getId(); // reverse
            }
        });
        for (ComponentDescriptor descriptor : descriptors) {
            try {
                componentManager.stopComponent(descriptor.getId());
            } catch (Exception ex) {
                logger.error("There was a problem stopping component {}{}{}", descriptor.getConfig().getName(),
                        Constants.NAME_VERSION_SEPARATOR, descriptor.getConfig().getVersion(), ex);
            }
        }
        logger.info("Alpha Framework was shut down");
    }
}
