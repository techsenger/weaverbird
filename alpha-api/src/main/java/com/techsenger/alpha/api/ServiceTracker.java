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

import java.util.Optional;
import java.util.Set;

/**
 * Service tracker is an easy way to track component services. This tracker can be used instead of component event
 * observers.
 *
 * @author Pavel Castornii
 */
public interface ServiceTracker {

    /**
     * Registration for tracking.
     */
    interface TrackingRegistration {

        /**
         * Returns fully qualified class name.
         * @return
         */
        String getServiceClassName();
    };

    /**
     * Registers services that this tracker will track on component starting/stopping.
     *
     */
    TrackingRegistration registerTracking(Class<?> serviceClass);

    /**
     * Unregisters services that this tracker will track on component starting/stopping.
     * Unregistered services are removed from tracking results if it is the last registration for this type of service.
     *
     */
    void unregisterTracking(TrackingRegistration registration);

    /**
     * Returns layers which provide tracked service.
     *
     * @return
     */
    Optional<Set<ModuleLayer>> getTrackingResult(Class<?> clazz);
}
