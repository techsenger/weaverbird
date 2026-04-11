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

package com.techsenger.alpha.gui.diagram;

import com.techsenger.alpha.core.api.model.ComponentModuleModel;
import com.techsenger.toolkit.core.model.ModuleModel;
import com.techsenger.toolkit.core.model.ResolvedModuleModel;

/**
 *
 * @author Pavel Castornii
 */
class ModuleConfig extends AbstractUmlComponent {

    private boolean reads;

    private boolean exports;

    private boolean opens;

    private boolean requires;

    private boolean requests;

    private boolean services;

    private final ComponentModuleModel module;

    private final ResolvedModuleModel resolvedModule;

    ModuleConfig(ModuleModel module) {
        super(module.getName());
        this.module = (ComponentModuleModel) module;
        this.resolvedModule = module.getLayer().getConfiguration().getModulesByName().get(module.getName());
    }

    public boolean isReads() {
        return reads;
    }

    public void setReads(boolean reads) {
        this.reads = reads;
    }

    public boolean isExports() {
        return exports;
    }

    public void setExports(boolean exports) {
        this.exports = exports;
    }

    public boolean isOpens() {
        return opens;
    }

    public void setOpens(boolean opens) {
        this.opens = opens;
    }

    public boolean isRequires() {
        return requires;
    }

    public void setRequires(boolean requires) {
        this.requires = requires;
    }

    public boolean isRequests() {
        return requests;
    }

    public void setRequests(boolean requests) {
        this.requests = requests;
    }

    public boolean isServices() {
        return services;
    }

    public void setServices(boolean services) {
        this.services = services;
    }

    public ComponentModuleModel getModule() {
        return module;
    }

    public ResolvedModuleModel getResolvedModule() {
        return resolvedModule;
    }
}
