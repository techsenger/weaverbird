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

import com.techsenger.alpha.core.api.component.ComponentObserver;
import java.util.Optional;
import java.util.Set;

/**
 * Tracks services provided by components in child module layers. Since the platform resides in the boot layer
 * and components are loaded into separate child layers dynamically, standard {@link java.util.ServiceLoader}
 * lookups are not sufficient — this tracker monitors component lifecycle events to maintain an up-to-date
 * view of which child layers currently provide a given service. Can be used as an alternative to
 * {@link ComponentObserver} when only service availability needs to be monitored.
 *
 * @author Pavel Castornii
 */
public interface ServiceTracker {

    /**
     * A handle for an active service tracking registration. Provides access to the current tracking state
     * and allows cancelling the registration when it is no longer needed.
     */
    interface TrackingRegistration {

        /**
         * Returns the fully qualified name of the tracked service class.
         *
         * @return fully qualified service class name
         */
        String getServiceClassName();

        /**
         * Returns the layers that currently provide the tracked service. The returned set reflects
         * the live state and changes as components are started or stopped. Returns an empty optional
         * if this service class has not been registered for tracking.
         *
         * @return set of layers providing the tracked service, empty optional if not tracked,
         *         or an optional containing an empty set if tracked but currently unavailable
         */
        Optional<Set<ModuleLayer>> getLayers();

        /**
         * Cancels this tracking registration. If no other registrations exist for the same service class,
         * the service is removed from tracking entirely.
         */
        void unregister();
    }

    /**
     * Registers a service class for tracking. The tracker will monitor component lifecycle events
     * and maintain the set of layers that provide the given service.
     *
     * @param serviceClass the service class to track
     * @return a registration handle for accessing tracking state and cancelling the registration
     */
    TrackingRegistration registerTracking(Class<?> serviceClass);
}
