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

package com.techsenger.weaverbird.core.api;

import com.techsenger.toolkit.core.version.Version;
import java.util.Objects;

/**
 *
 * @author Pavel Castornii
 */
public final class ApplicationSettings implements LayerOwner {

    private String name;

    private Version version;

    private ApplicationSettings() {
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getFullName() {
        return name + Constants.NAME_VERSION_SEPARATOR + version;
    }

    @Override
    public Version getVersion() {
        return version;
    }

    public static final class Builder {

        private String name;

        private Version version;

        Builder() {

        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder version(Version version) {
            this.version = version;
            return this;
        }

        ApplicationSettings build() {
            Objects.requireNonNull(this.name, "Application name is not set");
            Objects.requireNonNull(this.version, "Application version is not set");
            var settings = new ApplicationSettings();
            settings.name = this.name;
            settings.version = this.version;
            return settings;
        }
    }
}
