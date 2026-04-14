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

package com.techsenger.weaverbird.core.impl;

import com.techsenger.toolkit.core.jpms.ServiceUtils;
import com.techsenger.toolkit.core.version.Version;
import com.techsenger.weaverbird.core.api.ComponentManager;
import com.techsenger.weaverbird.core.api.Constants;
import com.techsenger.weaverbird.core.api.DefaultPathManager;
import com.techsenger.weaverbird.core.api.Framework;
import com.techsenger.weaverbird.core.api.FrameworkSettings;
import com.techsenger.weaverbird.core.api.JvmInspector;
import com.techsenger.weaverbird.core.api.LogManager;
import com.techsenger.weaverbird.core.api.PathManager;
import com.techsenger.weaverbird.core.api.ServiceTracker;
import com.techsenger.weaverbird.core.api.component.ComponentDescriptor;
import com.techsenger.weaverbird.core.api.registry.Registry;
import com.techsenger.weaverbird.core.impl.project.ProjectInfo;
import com.techsenger.weaverbird.core.impl.registry.DefaultRegistry;
import com.techsenger.weaverbird.core.spi.repo.RepoService;
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

    private static final String LAYER_NAME = "weaverbird-framework";

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
    private final DefaultServiceTracker serviceTracker = new DefaultServiceTracker();

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
    private final DefaultComponentManager componentManager = new DefaultComponentManager(this);

    /**
     * Flag indicating that framework is shutting down.
     */
    private volatile boolean shuttingDown = false;

    private final ServiceTracker.TrackingRegistration repoRegistration;

    private volatile RepoService repoService;

    public DefaultFramework(FrameworkSettings settings, Path rootPath) {
        this(settings, rootPath, null);
    }

    public DefaultFramework(FrameworkSettings settings, PathManager pathManager) {
        this(settings, null, pathManager);
    }

    private DefaultFramework(FrameworkSettings settings, Path rootPath, PathManager pathManager) {
        if (pathManager == null) {
            this.pathManager = new DefaultPathManager(rootPath, componentManager);
        } else {
            this.pathManager = pathManager;
        }
        this.settings = settings;
        serviceTracker.initialize(this.componentManager);
        this.repoRegistration = this.serviceTracker.registerTracking(RepoService.class);
        this.registry = new DefaultRegistry(this.pathManager);

        logger.info("Weaverbird Framework {} created on {}", getVersion().getFull(),
                this.getClass().getModule().getLayer());
    }

    @Override
    public FrameworkSettings getSettings() {
        return settings;
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
        List<ComponentDescriptor> descriptors = new ArrayList<>(componentManager.getDescriptors());
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
        logger.info("Weaverbird Framework was shut down");
    }

    @Override
    public boolean isShuttingDown() {
        return shuttingDown;
    }
}
