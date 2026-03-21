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

package com.techsenger.alpha.core.api.module;

/**
 *
 * @author Pavel Castornii
 */
public enum DirectiveType {

    /**
     * Indicates that this module will open its package to another module.
     */
    OPENS,

    /**
     * Indicates that this directive if this module will read a package from another module.
     */
    READS,

    /**
     * Indicates that this module will export its package to another module.
     */
    EXPORTS,

    /**
     * Indicates that another module will open its package to this module.
     */
    ALLOWS_OPEN,

    /**
     * Indicates that another module will read a package from this module.
     */
    ALLOWS_READ,

    /**
     * Indicates that another module will export its package to this module.
     */
    ALLOWS_EXPORT;

    static {
        OPENS.opposite = ALLOWS_OPEN;
        READS.opposite = ALLOWS_READ;
        EXPORTS.opposite = ALLOWS_EXPORT;
        ALLOWS_OPEN.opposite = OPENS;
        ALLOWS_READ.opposite = READS;
        ALLOWS_EXPORT.opposite = EXPORTS;
    }

    private DirectiveType opposite;

    /**
     * Returns opposite value. For example, ALLOWS_OPEN -> OPEN, OPEN -> ALLOWS_OPEN etc.
     *
     * @return
     */
    public DirectiveType getOpposite() {
        return opposite;
    }
}
