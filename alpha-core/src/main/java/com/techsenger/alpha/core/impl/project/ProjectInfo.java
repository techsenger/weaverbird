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

package com.techsenger.alpha.core.impl.project;

import com.techsenger.toolkit.core.PropertiesUtils;
import com.techsenger.toolkit.core.version.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
public final class ProjectInfo {

    /**
     * Logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(ProjectInfo.class);

    private static final String name;

    private static final Version version;

    static {
        String n = null;
        Version v = null;
        try {
            var properties = PropertiesUtils.read(ProjectInfo.class, "project-info.properties");
            n = properties.getProperty("name");
            v = Version.parse(properties.getProperty("version"));
        } catch (Exception ex) {
            logger.error("Error reading project info", ex);
        }
        name = n;
        version = v;
    }

    /**
     * Returns name.
     * @return
     */
    public static String getName() {
        return name;
    }

    /**
     * Returns version.
     * @return
     */
    public static Version getVersion() {
        return version;
    }

    private ProjectInfo() {
        //empty
    }
}
