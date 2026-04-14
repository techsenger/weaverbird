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

package com.techsenger.weaverbird.core.api;

import java.nio.file.Path;

/**
 *
 * @author Pavel Castornii
 */
public interface PathManager {

    /**
     * Returns framework root path.
     * @return root path.
     */
    Path getRootDirectory();

    /**
     * Returns framework bin directory path.
     * @return bin path.
     */
    Path getBinDirectory();

    /**
     * Returns framework cache directory path.
     * @return cache path.
     */
    Path getCacheDirectory();

    /**
     * Returns framework config directory path.
     * @return config path.
     */
    Path getConfigDirectory();

    /**
     * Returns framework data directory path.
     * @return data path.
     */
    Path getDataDirectory();

    /**
     * Returns framework documents directory path.
     * @return data path.
     */
    Path getDocumentDirectory();

    /**
     * Returns framework legal directory path.
     * @return log path.
     */
    Path getLegalDirectory();

    /**
     * Returns framework repo directory path.
     * @return jar path.
     */
    Path getRepositoryDirectory();

    /**
     * Returns framework script directory path.
     * @return
     */
    Path getScriptDirectory();

    /**
     * Returns framework temp directory path.
     * @return temp path.
     */
    Path getTempDirectory();

    /**
     * Returns the component path resolver.
     *
     * @return
     */
    PathResolver getPathResolver();
}
