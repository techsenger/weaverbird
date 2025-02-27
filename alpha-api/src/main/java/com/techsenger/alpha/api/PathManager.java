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

package com.techsenger.alpha.api;

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
    Path getRootDirectoryPath();

    /**
     * Returns framework bin directory path.
     * @return bin path.
     */
    Path getBinDirectoryPath();

    /**
     * Returns framework cache directory path.
     * @return cache path.
     */
    Path getCacheDirectoryPath();

    /**
     * Returns framework config directory path.
     * @return config path.
     */
    Path getConfigDirectoryPath();

    /**
     * Returns framework data directory path.
     * @return data path.
     */
    Path getDataDirectoryPath();

    /**
     * Returns framework documents directory path.
     * @return data path.
     */
    Path getDocumentDirectoryPath();

    /**
     * Returns framework legal directory path.
     * @return log path.
     */
    Path getLegalDirectoryPath();

    /**
     * Returns framework log directory path.
     * @return log path.
     */
    Path getLogDirectoryPath();

    /**
     * Returns framework repo directory path.
     * @return jar path.
     */
    Path getRepositoryDirectoryPath();

    /**
     * Returns framework script directory path.
     * @return
     */
    Path getScriptDirectoryPath();

    /**
     * Returns framework temp directory path.
     * @return temp path.
     */
    Path getTempDirectoryPath();

    /**
     * Returns path to log file. Path is set in launcher in system property. This method only returns value from this
     * system property.
     *
     * @return
     */
    Path getLogFilePath();

    /**
     * Returns path to framework process id file.
     * @return
     */
    Path getPidFilePath();
}
