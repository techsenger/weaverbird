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

package com.techsenger.weaverbird.executor.impl.commands;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.Parameter;
import com.techsenger.weaverbird.core.api.Constants;
import com.techsenger.weaverbird.core.api.component.Component;
import com.techsenger.weaverbird.executor.spi.AbstractCommand;
import com.techsenger.weaverbird.executor.spi.MainParameter;
import com.techsenger.toolkit.core.Pair;
import com.techsenger.toolkit.core.version.Version;

/**
 *
 * @author Pavel Castornii
 */
abstract class AbstractComponentNameVerCommand extends AbstractCommand {

    private static final class NameVersionConverter implements IStringConverter<Pair<String, Version>> {

        @Override
        public Pair<String, Version> convert(String string) {
            return Component.resolveNameAndVersion(string);
        }

    }

    /**
     * Name and version. For example, console:2.3.0
     */
    @MainParameter("name" + Constants.NAME_VERSION_SEPARATOR + "version")
    @Parameter(required = true, description = "sets component name and version", converter = NameVersionConverter.class)
    private Pair<String, Version> nameAndVersion;

    public String getName() {
        if (this.nameAndVersion != null) {
            return this.nameAndVersion.getFirst();
        } else {
            return null;
        }
    }

    public Version getVersion() {
        if (this.nameAndVersion != null) {
            return this.nameAndVersion.getSecond();
        } else {
            return null;
        }
    }
}
