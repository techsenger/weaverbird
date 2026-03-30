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

package com.techsenger.alpha.core.api.module;

import java.io.Serializable;

/**
 *
 * @author Pavel Castornii
 */
public interface ModuleArtifact extends Serializable {

    /**
     * Returns group id.
     * @return
     */
    String getGroupId();

    /**
     * Returns artifact id.
     * @return
     */
    String getArtifactId();

    /**
     * Returns version.
     * @return
     */
    String getVersion();

    /**
     * Sometimes modules needs classifier in repo, for example, 'linux' classifier for javafx modules.
     * @return
     */
    String getClassifier();

    /**
     * Returns type. If null, then jar is used.
     * @return
     */
    ModuleType getType();

    /**
     * This method returns artifactId-version.type, if type is null, then jar is used.
     * @return
     */
    String getFileName();
}
