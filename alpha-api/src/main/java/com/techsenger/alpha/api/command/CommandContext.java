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

package com.techsenger.alpha.api.command;

import com.techsenger.alpha.api.executor.CommandExecutor;
import com.techsenger.alpha.api.executor.ParameterProvider;
import java.util.Map;

/**
 *
 * @author Pavel Castornii
 */
public interface CommandContext {

    /**
     * Returns properties in unmodifiable map.
     * @return properties.
     */
    Map<Object, Object> getProperties();

    /**
     * Adds properties.
     * @param key of the property.
     * @param value of the property.
     */
    void addProperty(Object key, Object value);

    /**
     * Removes property and returns the previous value (if it exists).
     * @param key of the property.
     */
    Object removeProperty(Object key);

    /**
     * Returns the property.
     * @param key of the property.
     * @return property.
     */
    Object getProperty(Object key);

    /**
     * Checks if property exists.
     * @param key of the property.
     * @return true of false.
     */
    boolean containsProperty(Object key);

    /**
     * Clears all context properties.
     */
    void clearProperties();

    /**
     * Returns the command manager that uses this context and that calls command services.
     * @return
     */
    CommandExecutor getCommandExecutor();

    /**
     * Returns parameter provider.
     * @return
     */
    ParameterProvider getParameterProvider();
}
