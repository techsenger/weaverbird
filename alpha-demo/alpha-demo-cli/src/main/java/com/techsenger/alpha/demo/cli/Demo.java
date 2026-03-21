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

package com.techsenger.alpha.demo.cli;

import com.techsenger.alpha.core.api.Framework;
import com.techsenger.alpha.core.api.FrameworkFactory;
import com.techsenger.alpha.core.api.FrameworkSettings;
import com.techsenger.alpha.core.api.SystemProperties;
import com.techsenger.toolkit.core.version.Version;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
public final class Demo {

    private static final Logger logger = LoggerFactory.getLogger(Demo.class);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        var rootPath = Paths.get(System.getProperty(SystemProperties.ROOT_PATH));

        // Checksum is disabled because artifacts resolved from the local Maven repository
        // do not have checksum files when installed via "mvn install"
        var settings = FrameworkSettings.builder().repoChecksumEnabled(false).build();

        var framework = FrameworkFactory.create(settings, rootPath);
        resolveAndStartComponent(framework, "alpha-repo");
        resolveAndStartComponent(framework, "alpha-server");
        resolveAndStartComponent(framework, "alpha-console-cli");
    }

    private static void resolveAndStartComponent(Framework framework, String componentName) throws Exception {
        Version componentVersion = framework.getVersion();
        var compManager = framework.getComponentManager();
        if (!framework.getRegistry().isComponentResolved(componentName, componentVersion)) {
            compManager.resolveComponent(componentName, componentVersion, null);
        }
        compManager.startComponent(componentName, componentVersion);
    }

    private Demo() {
        // empty
    }
}
