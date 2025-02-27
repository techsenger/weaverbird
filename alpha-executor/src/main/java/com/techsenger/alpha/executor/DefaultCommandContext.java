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

package com.techsenger.alpha.executor;

import com.techsenger.alpha.api.command.CommandContext;
import com.techsenger.alpha.api.executor.CommandExecutor;
import com.techsenger.alpha.api.executor.ParameterProvider;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Pavel Castornii
 */
public class DefaultCommandContext implements CommandContext {

    /**
     * Executor.
     */
    private final CommandExecutor commandExecutor;

    /**
     * Properties.
     */
    private final Map<Object, Object> properties;

    /**
     * Provider which can be used to get certain command parameters.
     * Can be null if there is no provider for command.
     */
    private final ParameterProvider parameterProvider;

    private ExecutionProgress progress;

    /**
     * Constructor.
     */
    public DefaultCommandContext(CommandExecutor commandExecutor, ParameterProvider parameterProvider) {
        this.commandExecutor = commandExecutor;
        this.properties = new ConcurrentHashMap<>();
        this.parameterProvider = parameterProvider;
    }

    @Override
    public Map<Object, Object> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    @Override
    public void addProperty(final Object key, final Object value) {
        properties.put(key, value);
    }

    @Override
    public Object removeProperty(final Object key) {
        return properties.remove(key);
    }

    @Override
    public Object getProperty(final Object key) {
        return properties.get(key);
    }

    @Override
    public boolean containsProperty(final Object key) {
        return properties.containsKey(key);
    }

    @Override
    public void clearProperties() {
        this.properties.clear();
    }

    @Override
    public ParameterProvider getParameterProvider() {
        return parameterProvider;
    }

    @Override
    public CommandExecutor getCommandExecutor() {
        return this.commandExecutor;
    }

    ExecutionProgress getProgress() {
        return progress;
    }

    void setProgress(ExecutionProgress progress) {
        this.progress = progress;
    }
}
