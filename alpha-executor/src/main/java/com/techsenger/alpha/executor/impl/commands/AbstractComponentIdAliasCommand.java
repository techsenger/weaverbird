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

package com.techsenger.alpha.executor.impl.commands;

import com.beust.jcommander.Parameter;
import com.techsenger.alpha.executor.spi.AbstractCommand;

/**
 *
 * @author Pavel Castornii
 */
abstract class AbstractComponentIdAliasCommand extends AbstractCommand {

    /**
     * Id.
     */
    @Parameter(names = {"-i", "--id"}, required = false, description = "sets the id of the component")
    private Integer id;

    /**
     * Alias.
     */
    @Parameter(names = {"-a", "--alias"}, required = false, description = "sets the alias of the component")
    private String alias;

    AbstractComponentIdAliasCommand() {

    }

    Integer getId() {
        return id;
    }

    String getAlias() {
        return alias;
    }
}
