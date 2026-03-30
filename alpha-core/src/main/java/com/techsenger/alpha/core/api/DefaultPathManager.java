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

package com.techsenger.alpha.core.api;

import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
public class DefaultPathManager implements PathManager {

    public static final String BINARY_DIRECTORY_NAME = "bin";

    public static final String CACHE_DIRECTORY_NAME = "cache";

    public static final String CONFIG_DIRECTORY_NAME = "config";

    public static final String DATA_DIRECTORY_NAME = "data";

    public static final String DOCUMENT_DIRECTORY_NAME = "doc";

    public static final String LEGAL_DIRECTORY_NAME = "legal";

    public static final String LOG_DIRECTORY_NAME = "log";

    public static final String REPOSITORY_DIRECTORY_NAME = "repo";

    public static final String SCRIPT_DIRECTORY_NAME = "script";

    public static final String TEMPORARY_DIRECTORY_NAME = "temp";

    private static final Logger logger = LoggerFactory.getLogger(DefaultPathManager.class);

    private final Path rootPath;

    private final PathResolver pathResolver;

    /**
     * Constructor.
     */
    public DefaultPathManager(Path rootPath, ComponentManager componentManager) {
        this.rootPath = rootPath;
        this.pathResolver = new DefaultPathResolver(this, componentManager);
        logger.info("Framework root path: " + rootPath);
    }

    @Override
    public Path getRootDirectory() {
        return rootPath;
    }

    @Override
    public Path getBinDirectory() {
        var binPath = rootPath.resolve(BINARY_DIRECTORY_NAME);
        return binPath;
    }

    @Override
    public Path getCacheDirectory() {
        var cachePath = rootPath.resolve(CACHE_DIRECTORY_NAME);
        return cachePath;
    }

    @Override
    public Path getConfigDirectory() {
        var configPath = rootPath.resolve(CONFIG_DIRECTORY_NAME);
        return configPath;
    }

    @Override
    public Path getDataDirectory() {
        var dataPath = rootPath.resolve(DATA_DIRECTORY_NAME);
        return dataPath;
    }

    @Override
    public Path getDocumentDirectory() {
        var docPath = rootPath.resolve(DOCUMENT_DIRECTORY_NAME);
        return docPath;
    }

    @Override
    public Path getLegalDirectory() {
        var legalPath = rootPath.resolve(LEGAL_DIRECTORY_NAME);
        return legalPath;
    }

    @Override
    public Path getRepositoryDirectory() {
        var repoPath = rootPath.resolve(REPOSITORY_DIRECTORY_NAME);
        return repoPath;
    }

    @Override
    public Path getScriptDirectory() {
        var scriptPath = rootPath.resolve(SCRIPT_DIRECTORY_NAME);
        return scriptPath;
    }

    @Override
    public Path getTempDirectory() {
        var tempPath = rootPath.resolve(TEMPORARY_DIRECTORY_NAME);
        return tempPath;
    }

    @Override
    public PathResolver getPathResolver() {
        return pathResolver;
    }
}
