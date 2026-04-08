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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 *
 * @author Pavel Castornii
 */
class ModuleConfigAdapter extends UmlComponentAdapter {

    private final ModuleConfig config;

    private final BooleanProperty reads = new SimpleBooleanProperty();

    private final BooleanProperty exports = new SimpleBooleanProperty();

    private final BooleanProperty opens = new SimpleBooleanProperty();

    private final BooleanProperty requires = new SimpleBooleanProperty();

    private final BooleanProperty requests = new SimpleBooleanProperty();

    private final BooleanProperty services = new SimpleBooleanProperty();

    ModuleConfigAdapter(ModuleConfig config) {
        super(config);
        this.config = config;

        reads.set(config.isReads());
        exports.set(config.isExports());
        opens.set(config.isOpens());
        requires.set(config.isRequires());
        requests.set(config.isRequests());
        services.set(config.isServices());

        reads.addListener(getAutoIncludeListener());
        exports.addListener(getAutoIncludeListener());
        opens.addListener(getAutoIncludeListener());
        requires.addListener(getAutoIncludeListener());
        requests.addListener(getAutoIncludeListener());
        services.addListener(getAutoIncludeListener());

        reads.addListener((obs, o, v) -> config.setReads(v));
        exports.addListener((obs, o, v) -> config.setExports(v));
        opens.addListener((obs, o, v) -> config.setOpens(v));
        requires.addListener((obs, o, v) -> config.setRequires(v));
        requests.addListener((obs, o, v) -> config.setRequests(v));
        services.addListener((obs, o, v) -> config.setServices(v));
    }

    public ModuleConfig getConfig() {
        return config;
    }

    public String getName() {
        return config.getName();
    }

    public BooleanProperty readsProperty() {
        return reads;
    }

    public boolean isReads() {
        return reads.get();
    }

    public void setReads(boolean v) {
        reads.set(v);
    }

    public BooleanProperty exportsProperty() {
        return exports;
    }

    public boolean isExports() {
        return exports.get();
    }

    public void setExports(boolean v) {
        exports.set(v);
    }

    public BooleanProperty opensProperty() {
        return opens;
    }

    public boolean isOpens() {
        return opens.get();
    }

    public void setOpens(boolean v) {
        opens.set(v);
    }

    public BooleanProperty requiresProperty() {
        return requires;
    }

    public boolean isRequires() {
        return requires.get();
    }

    public void setRequires(boolean v) {
        requires.set(v);
    }

    public BooleanProperty requestsProperty() {
        return requests;
    }

    public boolean isRequests() {
        return requests.get();
    }

    public void setRequests(boolean v) {
        requests.set(v);
    }

    public BooleanProperty servicesProperty() {
        return services;
    }

    public boolean isServices() {
        return services.get();
    }

    public void setServices(boolean v) {
        services.set(v);
    }

    @Override
    public void reset() {
        super.reset();
        reads.set(false);
        exports.set(false);
        opens.set(false);
        requires.set(false);
        requests.set(false);
        services.set(false);
    }
}