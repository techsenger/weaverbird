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

import com.beust.jcommander.Parameter;
import com.techsenger.weaverbird.executor.spi.MainParameter;

/**
 *
 * @author Pavel Castornii
 */
public abstract class AbstractSessionNameCommand extends AbstractSessionCommand {

    @MainParameter("name")
    @Parameter(required = true, description = "sets the name of the session")
    private String name;

    protected String getName() {
        return name;
    }
}
