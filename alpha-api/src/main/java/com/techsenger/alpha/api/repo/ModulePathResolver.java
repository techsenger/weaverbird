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

package com.techsenger.alpha.api.repo;

import java.nio.file.Path;

/**
 * In order to load any component (including repo component) we need module path resolver, that will find modules in
 * repo. This interface is required as every repo can have its own structure.
 *
 * @author Pavel Castornii
 */
public interface ModulePathResolver {

    /**
     * Resolves module path in repo.
     *
     * @param repoPath
     * @param artifact
     * @return
     */
    Path resolveModulePath(Path repoPath, ModuleArtifact artifact);
}
