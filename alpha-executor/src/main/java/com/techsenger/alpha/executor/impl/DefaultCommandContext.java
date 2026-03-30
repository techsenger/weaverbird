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

package com.techsenger.alpha.executor.impl;

import com.techsenger.alpha.core.api.Framework;
import com.techsenger.alpha.executor.api.CommandContext;
import com.techsenger.alpha.executor.api.CommandExecutor;
import com.techsenger.alpha.executor.api.ParameterProvider;
import com.techsenger.alpha.executor.api.command.ExecutionTarget;
import com.techsenger.alpha.net.client.api.ClientService;
import com.techsenger.alpha.net.client.api.ClientServiceFactory;
import com.techsenger.alpha.net.client.api.ClientSession;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Pavel Castornii
 */
public class DefaultCommandContext implements CommandContext {

    private final Map<Object, Object> properties = new ConcurrentHashMap<>();

    private final Framework framework;

    private final CommandExecutor executor;

    /**
     * Provider which can be used to get certain command parameters.
     * Can be null if there is no provider for command.
     */
    private ParameterProvider parameterProvider;

    private ClientSession session;

    private ClientService client = ClientServiceFactory.create();

    private ExecutionProgress progress;

    private ExecutionTarget executionTarget;

    /**
     * Constructor.
     */
    public DefaultCommandContext(Framework framework, CommandExecutor executor) {
        this.framework = framework;
        this.executor = executor;
    }

    @Override
    public ClientSession getSession() {
        return session;
    }

    @Override
    public void setSession(ClientSession session) {
        this.session = session;
    }

    @Override
    public ClientService getClient() {
        return client;
    }

    @Override
    public Map<Object, Object> getProperties() {
        return properties;
    }

    @Override
    public ParameterProvider getParameterProvider() {
        return parameterProvider;
    }

    @Override
    public Framework getFramework() {
        return framework;
    }

    @Override
    public CommandExecutor getExecutor() {
        return executor;
    }

    @Override
    public boolean isExecutionLocal() {
        return this.executionTarget == ExecutionTarget.LOCAL;
    }

    @Override
    public boolean isExecutionRemote() {
        return this.executionTarget == ExecutionTarget.REMOTE;
    }

    void setParameterProvider(ParameterProvider parameterProvider) {
        this.parameterProvider = parameterProvider;
    }

    ExecutionProgress getProgress() {
        return progress;
    }

    void setProgress(ExecutionProgress progress) {
        this.progress = progress;
    }

    void setExecutionTarget(ExecutionTarget executionTarget) {
        this.executionTarget = executionTarget;
    }
}
