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

package com.techsenger.weaverbird.executor.api.command;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Pavel Castornii
 */
public interface CommandInfo extends Serializable {

    /**
     * Returns command name.
     *
     * @return
     */
    String getName();

    /**
     * Returns the name of the module that contains this command.
     * @return
     */
    String getModuleName();

    /**
     * Returns the description of the command.
     * @return
     */
    String getDescription();

    /**
     * Returns true if the command has RemoteCommand annotation and false otherwise.
     * @return
     */
    boolean isRemote();

    /**
     * Returns true if the command has LocalCommand annotation and false otherwise.
     * @return
     */
    boolean isLocal();

    /**
     * Returns parameters of the command.
     *
     * @return list or null if there are no parameters.
     */
    List<ParameterDescriptor> getParameters();
}
