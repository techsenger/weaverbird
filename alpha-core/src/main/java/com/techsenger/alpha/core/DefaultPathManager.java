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

package com.techsenger.alpha.core;

import com.techsenger.alpha.api.Constants;
import com.techsenger.alpha.api.Framework;
import com.techsenger.alpha.api.FrameworkMode;
import com.techsenger.alpha.api.PathManager;
import com.techsenger.alpha.api.SystemProperties;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    /**
     * Logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(DefaultPathManager.class);

    /**
     * Root path.
     */
    private final Path rootPath;

    /**
     * Constructor.
     */
    public DefaultPathManager() {
        rootPath = Paths.get(System.getProperty(SystemProperties.ROOT_PATH));
        logger.info("Framework root path: " + rootPath);
    }

    @Override
    public Path getRootDirectoryPath() {
        return rootPath;
    }

    @Override
    public Path getBinDirectoryPath() {
        var binPath = rootPath.resolve(BINARY_DIRECTORY_NAME);
        return binPath;
    }

    @Override
    public Path getCacheDirectoryPath() {
        var cachePath = rootPath.resolve(CACHE_DIRECTORY_NAME);
        return cachePath;
    }

    @Override
    public Path getConfigDirectoryPath() {
        var configPath = rootPath.resolve(CONFIG_DIRECTORY_NAME);
        return configPath;
    }

    @Override
    public Path getDataDirectoryPath() {
        var dataPath = rootPath.resolve(DATA_DIRECTORY_NAME);
        return dataPath;
    }

    @Override
    public Path getDocumentDirectoryPath() {
        var docPath = rootPath.resolve(DOCUMENT_DIRECTORY_NAME);
        return docPath;
    }

    @Override
    public Path getLegalDirectoryPath() {
        var legalPath = rootPath.resolve(LEGAL_DIRECTORY_NAME);
        return legalPath;
    }

    @Override
    public Path getLogDirectoryPath() {
        var logPath = rootPath.resolve(LOG_DIRECTORY_NAME);
        return logPath;
    }

    @Override
    public Path getRepositoryDirectoryPath() {
        var repoPath = rootPath.resolve(REPOSITORY_DIRECTORY_NAME);
        return repoPath;
    }

    @Override
    public Path getScriptDirectoryPath() {
        var scriptPath = rootPath.resolve(SCRIPT_DIRECTORY_NAME);
        return scriptPath;
    }

    @Override
    public Path getTempDirectoryPath() {
        var tempPath = rootPath.resolve(TEMPORARY_DIRECTORY_NAME);
        return tempPath;
    }

    @Override
    public Path getLogFilePath() {
        return rootPath.getParent().resolve(System.getProperty(SystemProperties.LOG_PATH));
    }

    @Override
    public Path getPidFilePath() {
        var mode = Framework.getMode();
        String pidFileName = null;
        if (mode == FrameworkMode.STANDALONE) {
            pidFileName = Constants.FILE_PREFIX;
        } else {
            pidFileName = Constants.FILE_PREFIX + "-" + mode.name().toLowerCase();
        }
        pidFileName += ".pid";
        var pidFilePath = this.getTempDirectoryPath().resolve(pidFileName);
        return pidFilePath;
    }
}
