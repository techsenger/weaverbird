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

package com.techsenger.alpha.core.impl;

import com.techsenger.alpha.core.api.ComponentManager;
import com.techsenger.alpha.core.api.Constants;
import com.techsenger.alpha.core.api.Framework;
import com.techsenger.alpha.core.api.FrameworkSettings;
import com.techsenger.alpha.core.api.JvmInspector;
import com.techsenger.alpha.core.api.LogManager;
import com.techsenger.alpha.core.api.PathManager;
import com.techsenger.alpha.core.api.ServiceTracker;
import com.techsenger.alpha.core.api.component.ComponentDescriptor;
import com.techsenger.alpha.core.api.registry.Registry;
import com.techsenger.alpha.core.impl.project.ProjectInfo;
import com.techsenger.alpha.core.impl.registry.DefaultRegistry;
import com.techsenger.alpha.core.spi.repo.RepoService;
import com.techsenger.toolkit.core.jpms.ServiceUtils;
import com.techsenger.toolkit.core.version.Version;
import java.nio.file.Path;
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
public class DefaultFramework implements Framework {

    private static final Logger logger = LoggerFactory.getLogger(Framework.class);

    private static final String LAYER_NAME = "alpha-framework";

    private final FrameworkSettings settings;

    /**
     * Log manager.
     */
    private final LogManager logManager = new DefaultLogManager();

    /**
     * JVM inspector.
     */
    private final JvmInspector jvmInspector = new DefaultJvmInspector(this);

    /**
     * Service tracker.
     */
    private final ServiceTracker serviceTracker = new DefaultServiceTracker();

    /**
     * Path provider. It must be created before component manager.
     */
    private final PathManager pathManager;

    /**
     * Registry.
     */
    private final DefaultRegistry registry;

    /**
     * Component manager.
     */
    private final ComponentManager componentManager;

    /**
     * Flag indicating that framework is shutting down.
     */
    private volatile boolean shuttingDown = false;

    private final ServiceTracker.TrackingRegistration repoRegistration;

    private volatile RepoService repoService;

    public DefaultFramework(FrameworkSettings settings, Path rootPath) {
        this(settings, new DefaultPathManager(rootPath));
    }

    public DefaultFramework(FrameworkSettings settings, PathManager pathManager) {
        this.settings = settings;
        this.pathManager = pathManager;
        this.repoRegistration = this.serviceTracker.registerTracking(RepoService.class);
        this.registry = new DefaultRegistry(pathManager);
        this.componentManager = new DefaultComponentManager(registry, pathManager, this);
        logger.info("Alpha Framework {} created on {}", getVersion().getFull(), this.getClass().getModule().getLayer());
    }

    @Override
    public FrameworkSettings getSettings() {
        return settings;
    }

    @Override
    public String getName() {
        return LAYER_NAME;
    }

    @Override
    public String getFullName() {
        return LAYER_NAME + Constants.NAME_VERSION_SEPARATOR + getVersion();
    }

    @Override
    public Version getVersion() {
        return ProjectInfo.getVersion();
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
    public ServiceTracker getServiceTracker() {
        return this.serviceTracker;
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
    public RepoService getRepoService() {
        if (this.repoService == null) {
            synchronized (this) {
                if (this.repoService == null) {
                    var trackingResult = repoRegistration.getLayers();
                    if (trackingResult.isEmpty()) {
                        return null;
                    }
                    var layers = trackingResult.get();
                    if (layers.size() != 1) {
                        throw new IllegalStateException(RepoService.class.getSimpleName() + " found in "
                                + layers.size() + " layers");
                    }
                    var optional = ServiceUtils.loadProvider(layers.iterator().next(), false, RepoService.class);
                    this.repoService = optional.get();
                }
            }
        }
        return this.repoService;
    }

    @Override
    public synchronized void shutdown() {
        shuttingDown = true;
        logger.debug("Shutting down framework");
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
                logger.error("There was an error stopping component {}{}{}", descriptor.getConfig().getName(),
                        Constants.NAME_VERSION_SEPARATOR, descriptor.getConfig().getVersion(), ex);
            }
        }
        logger.info("Alpha Framework was shut down");
    }

    @Override
    public boolean isShuttingDown() {
        return shuttingDown;
    }
}
