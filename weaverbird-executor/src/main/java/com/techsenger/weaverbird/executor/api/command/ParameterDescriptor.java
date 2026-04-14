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

/**
 *
 * @author Pavel Castornii
 */
public interface ParameterDescriptor extends Serializable {

    /**
     * Returns short parameter name, for example `-s`.
     *
     * @return parameter or null.
     */
    String getShortName();

    /**
     * Returns long parameter name, for example `--something`.
     *
     * @return
     */
    String getLongName();

    /**
     * True for required and false for optional.
     * @return
     */
    boolean isRequired();

    /**
     * Description of the parameter.
     * @return
     */
    String getDescription();

    /**
     * Indicates if this parameter is main. Main parameter doesn't have short name.
     *
     * @return
     */
    boolean isMain();
}
