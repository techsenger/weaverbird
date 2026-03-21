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

import com.techsenger.alpha.core.api.ServiceTracker;
import com.techsenger.alpha.core.api.component.Component;
import com.techsenger.toolkit.core.jpms.ServiceUtils;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
public class DefaultServiceTracker implements ServiceTracker {

    /**
     * Logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(DefaultServiceTracker.class);

    /**
     * Registration implementation.
     */
    private class DefaultTrackingRegistration implements ServiceTracker.TrackingRegistration {

        private final String serviceClassName;

        DefaultTrackingRegistration(String serviceClassName) {
            this.serviceClassName = serviceClassName;
        }

        @Override
        public String getServiceClassName() {
            return this.serviceClassName;
        }

        @Override
        public Optional<Set<ModuleLayer>> getLayers() {
            var result = layersByServiceClassName.get(serviceClassName);
            if (result != null) {
                result = new HashSet<>(result);
                return Optional.of(result);
            } else {
                return Optional.empty();
            }
        }

        @Override
        public void unregister() {
            unregisterTracking(this);
        }
    }

    /**
     * Registrations. Every client has its own registration so we can know what registration were removed and which
     * are still present.
     *
     * We use string, not classes because 1) we don't keep reference to class 2) we need strings to work with
     * ServiceUtils.services().
     */
    private final Map<String, Set<TrackingRegistration>> registrationsByServiceClassName = new ConcurrentHashMap<>();

    /**
     * Layers that provide corresponding services.
     *
     * We use string, not classes because 1) we don't keep reference to class 2) we need strings to work with
     * ServiceUtils.services().     *
     */
    private final Map<String, Set<ModuleLayer>> layersByServiceClassName = new ConcurrentHashMap<>();

    @Override
    public synchronized TrackingRegistration registerTracking(final Class<?> serviceClass) {
        var className = serviceClass.getName();
        Set<TrackingRegistration> registrations = registrationsByServiceClassName.get(className);
        if (registrations == null) {
            //non concurrent hash set
            registrations = new HashSet<>();
            this.registrationsByServiceClassName.put(className, registrations);
        }
        var registration = new DefaultTrackingRegistration(className);
        registrations.add(registration);
        logger.debug("Registered tracking for service: {}. Currently {} registrations for this service",
                className, registrations.size());
        return registration;
    }

    void initialize(DefaultComponentManager componentManager) {
        componentManager.addListener(new ComponentListener() {
            @Override
            public void onComponentDeployed(Component component) {
                doOnComponentDeployed(component);
            }

            @Override
            public void onComponentUndeployed(Component component) {
                doOnComponentUndeployed(component);
            }
        });
    }

    private synchronized void unregisterTracking(TrackingRegistration registration) {
        var serviceClassName = registration.getServiceClassName();
        Set<TrackingRegistration> registrations = registrationsByServiceClassName.get(serviceClassName);
        if (registrations == null) {
            return;
        }
        registrations.remove(registration);
        if (registrations.isEmpty()) {
            registrationsByServiceClassName.remove(serviceClassName);
            //besides we clear all results
            this.layersByServiceClassName.remove(serviceClassName);
            logger.debug("Cleared tracking results for service: {}; currently {} service clases in results",
                    serviceClassName, this.layersByServiceClassName.size());
        }
        logger.debug("Unregistered tracking for service: {}; currently {} registrations for this service",
                serviceClassName, registrations.size());

    }

    /**
     * This method is called by ComponentManager.
     *
     * @param component
     */
    private synchronized void doOnComponentDeployed(Component component) {
        final var discoveredServices = new HashSet<String>();
        var layerServices = ServiceUtils.services(component.getLayer());
        for (var entry: this.registrationsByServiceClassName.entrySet()) {
            var serviceClassName = entry.getKey();
            if (layerServices.contains(serviceClassName)) {
                discoveredServices.add(serviceClassName);
                var layers = this.layersByServiceClassName.get(serviceClassName);
                if (layers == null) {
                    layers = ConcurrentHashMap.newKeySet();
                    this.layersByServiceClassName.put(serviceClassName, layers);
                }
                layers.add(component.getLayer());
            }
        }
        if (!discoveredServices.isEmpty()) {
            logger.debug("Added layer {} to tracking results for services: {}; currently {} service classes in results",
                component.getLayer(), discoveredServices, this.layersByServiceClassName.size());
        }
    }

    /**
     * This method is called by ComponentManager.
     *
     * @param component
     */
    private synchronized void doOnComponentUndeployed(Component component) {
        final var discoveredServices = new HashSet<String>();
        this.layersByServiceClassName.entrySet().removeIf(entry -> {
            var layers  = entry.getValue();
            if (layers.remove(component.getLayer())) {
                discoveredServices.add(entry.getKey());
            }
            return layers.isEmpty();
        });
        if (!discoveredServices.isEmpty()) {
            logger.debug("Removed layer {} from tracking results for services: {}; currently {} service classes "
                    + "in results", component.getLayer(), discoveredServices, this.layersByServiceClassName.size());
        }
    }
}
