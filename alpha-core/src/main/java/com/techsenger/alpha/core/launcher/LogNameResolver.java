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

package com.techsenger.alpha.core.launcher;

import com.techsenger.alpha.api.Constants;
import com.techsenger.alpha.api.FrameworkMode;
import com.techsenger.alpha.api.SystemProperties;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class can't have a logger.
 *
 * @author Pavel Castornii
 */
final class LogNameResolver {

    public static void resolve() {
        String logFileName = null;
        var mode = ModeResolver.resolve();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
        var date = dateFormat.format(new Date());
        if (mode == FrameworkMode.STANDALONE) {
            logFileName = Constants.FILE_PREFIX + "-" + date + ".log";
        } else {
            logFileName = Constants.FILE_PREFIX + "-" + mode.toString().toLowerCase() + "-" + date + ".log";
        }
        var rootPath = Paths.get(System.getProperty(SystemProperties.ROOT_PATH));
        var logPath = rootPath.resolve("log").resolve(logFileName);
        System.setProperty(SystemProperties.LOG_PATH, logPath.toString());
    }

    private LogNameResolver() {
        //empty
    }
}
