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

package com.techsenger.alpha.executor.api.command;

import java.util.List;

/**
 *
 * @author Pavel Castornii
 */
public class DefaultCommandInfo implements CommandInfo {

    private String name;

    private String moduleName;

    private String description;

    private boolean local;

    private boolean remote;

    private List<DefaultParameterDescriptor> parameters;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getModuleName() {
        return moduleName;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean isLocal() {
        return local;
    }

    @Override
    public boolean isRemote() {
        return remote;
    }

    @Override
    public List<ParameterDescriptor> getParameters() {
        return (List) parameters;
    }

    protected void setName(String name) {
        this.name = name;
    }

    protected void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    protected void setDescription(String description) {
        this.description = description;
    }

    protected void setLocal(boolean local) {
        this.local = local;
    }

    protected void setRemote(boolean remote) {
        this.remote = remote;
    }

    protected void setParameters(List<DefaultParameterDescriptor> parameters) {
        this.parameters = parameters;
    }
}
