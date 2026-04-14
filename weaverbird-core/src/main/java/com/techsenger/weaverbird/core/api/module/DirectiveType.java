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

package com.techsenger.weaverbird.core.api.module;

/**
 * Defines the types of directives that can be applied to a module. Directives are divided into two groups.
 *
 * <p>Direct directives ({@link #OPENS}, {@link #READS}, {@link #EXPORTS}) are applied to the configured module itself.
 * <p>Indirect directives ({@link #REQUESTS_OPEN}, {@link #REQUESTS_READ}, {@link #REQUESTS_EXPORT}) are applied to
 * another module, making it open, read, or export something to the configured module.
 *
 * @author Pavel Castornii
 */
public enum DirectiveType {

    /**
     * Indicates that the configured module opens its package to another module.
     */
    OPENS,

    /**
     * Indicates that the configured module reads another module.
     */
    READS,

    /**
     * Indicates that the configured module exports its package to another module.
     */
    EXPORTS,

    /**
     * Indicates that another module is requested to open its package to the configured module.
     */
    REQUESTS_OPEN,

    /**
     * Indicates that another module is requested to read the configured module.
     */
    REQUESTS_READ,

    /**
     * Indicates that another module is requested to export its package to the configured module.
     */
    REQUESTS_EXPORT;

    static {
        OPENS.opposite = REQUESTS_OPEN;
        READS.opposite = REQUESTS_READ;
        EXPORTS.opposite = REQUESTS_EXPORT;
        REQUESTS_OPEN.opposite = OPENS;
        REQUESTS_READ.opposite = READS;
        REQUESTS_EXPORT.opposite = EXPORTS;
    }

    private DirectiveType opposite;

    /**
     * Returns {@code true} if this is a direct directive, i.e. it is applied to the configured
     * module itself.
     *
     * @return {@code true} if direct, {@code false} if indirect
     */
    public boolean isDirect() {
        return this == OPENS || this == READS || this == EXPORTS;
    }

    /**
     * Returns {@code true} if this is an indirect directive, i.e. it is applied to another
     * module.
     *
     * @return {@code true} if indirect, {@code false} if direct
     */
    public boolean isIndirect() {
        return !isDirect();
    }

    /**
     * Returns the opposite directive type. For example, {@link #OPENS} returns
     * {@link #REQUESTS_OPEN} and vice versa.
     *
     * @return the opposite directive type
     */
    public DirectiveType getOpposite() {
        return opposite;
    }
}
