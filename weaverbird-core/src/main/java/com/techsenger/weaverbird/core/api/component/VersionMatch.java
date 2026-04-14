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

package com.techsenger.weaverbird.core.api.component;

/**
 * Defines the level of version matching based on SemVer (Semantic Versioning).
 *
 * The level specifies how many segments of the version must match, starting from the given version and up to the
 * next increment of the matched segment.
 *
 * <ul>
 *   <li>{@link #ANY} - matches any version, e.g. {@code 1.2.0} matches {@code [0.0.0, +∞)}</li>
 *   <li>{@link #MAJOR} - matches any version with the same major segment, e.g. {@code 1.2.0} matches
 *       {@code [1.2.0, 2.0.0)}</li>
 *   <li>{@link #MINOR} - matches any version with the same major and minor segments, e.g. {@code 1.2.0} matches
 *       {@code [1.2.0, 1.3.0)}</li>
 *   <li>{@link #PATCH} - matches any version with the same major, minor and patch segments, e.g. {@code 1.2.0} matches
 *       {@code [1.2.0, 1.2.0]}</li>
 * </ul>
 *
 * @author Pavel Castornii
 */
public enum VersionMatch {

    /**
     * Matches any version.
     */
    ANY,

    /**
     * Matches any version with the same major segment. For example, {@code 1.2.0} matches {@code [1.2.0, 2.0.0)}.
     */
    MAJOR,

    /**
     * Matches any version with the same major and minor segments. For example, {@code 1.2.0} matches
     * {@code [1.2.0, 1.3.0)}.
     */
    MINOR,

    /**
     * Matches any version with the same major, minor and patch segments. For example, {@code 1.2.0} matches
     * {@code [1.2.0, 1.2.0]}.
     */
    PATCH
}
