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

import java.util.function.Consumer;

/**
 *
 * @author Pavel Castornii
 */
public final class FrameworkSettings {

    private boolean repoChecksumEnabled;

    private ApplicationSettings application;

    private FrameworkSettings() {
    }

    public boolean isRepoChecksumEnabled() {
        return repoChecksumEnabled;
    }

    public ApplicationSettings getApplication() {
        return application;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private boolean repoChecksumEnabled = true;
        private final ApplicationSettings.Builder applicationBuilder = new ApplicationSettings.Builder();

        private Builder() {
        }

        public Builder repoChecksumEnabled(boolean repoChecksumEnabled) {
            this.repoChecksumEnabled = repoChecksumEnabled;
            return this;
        }

        public Builder application(Consumer<ApplicationSettings.Builder> consumer) {
            consumer.accept(applicationBuilder);
            return this;
        }

        public FrameworkSettings build() {
            var settings = new FrameworkSettings();
            settings.repoChecksumEnabled = this.repoChecksumEnabled;
            settings.application = applicationBuilder.build();
            return settings;
        }
    }
}
