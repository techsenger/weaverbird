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

package com.techsenger.weaverbird.assembly.maven.plugin;

/**
 *
 * @author Pavel Castornii
 */
public final class Goals {

    /**
     * Assembles a minimal framework runtime required for integration testing. Creates the initial directory structure
     * with the repository populated with the required modules and necessary configuration files.
     *
     * <p>If the provided path already exists, execution is skipped.
     */
    public static final String ASSEMBLE_RUNTIME = "assemble-runtime";

    /**
     * Assembles a full framework distribution including {@code .sh}/{@code .bat} scripts and the default Log4j2
     * configuration. Creates the initial directory structure with the repository populated with the required modules
     * and necessary configuration files.
     *
     * <p>If the provided path already exists, execution is skipped.
     */
    public static final String ASSEMBLE_DISTRO = "assemble-distro";

    /**
     * Updates the repository with the specified modules in an existing framework runtime or distribution. This goal is
     * intended for development purposes to avoid full reassembly on every change.
     *
     * <p>If the provided path does not exist, execution is skipped.
     */
    public static final String UPDATE = "update";

    private Goals() {
        // empty
    }
}
