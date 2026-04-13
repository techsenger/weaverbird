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

package com.techsenger.alpha.demo.jfx.boot;

import com.techsenger.alpha.core.api.FrameworkFactory;
import com.techsenger.alpha.core.api.FrameworkSettings;
import com.techsenger.alpha.core.api.SystemProperties;
import com.techsenger.alpha.core.api.component.ComponentConfig;
import com.techsenger.alpha.core.api.message.SystemMessagePrinter;
import com.techsenger.toolkit.core.PropertiesUtils;
import com.techsenger.toolkit.core.os.OperatingSystem;
import com.techsenger.toolkit.core.os.OsUtils;
import com.techsenger.toolkit.core.version.Version;
import java.nio.file.Paths;

/**
 * This demo shows how to run JavaFX.
 *
 * <p>Set the local repo, build and run the scripts in the {@code target/framework/bin} folder
 *
 * @author Pavel Castornii
 */
public final class JfxDemo {

    private static final String APP_NAME = "jfx-demo";

    private static final Version APP_VERSION = Version.of("1.0.0");

    private static final String COMPONENT_NAME = "jfx-component";

    private static final Version COMPONENT_VERSION = Version.of("1.0.0");

    private static final String CENTRAL_REPO = "https://repo1.maven.org/maven2/";

    private static final Version JFX_VERSION = Version.of("26");

    public static void main(String[] args) throws Exception {
        // Framework
        var frameworkPath = Paths.get(System.getProperty(SystemProperties.ROOT_PATH));
        var settings = FrameworkSettings.builder()
                .repoChecksumEnabled(false)
                .application(app -> app
                    .name(APP_NAME)
                    .version(APP_VERSION))
                .build();
        var framework = FrameworkFactory.create(settings, frameworkPath);

        // OS and classifier
        var os = OsUtils.getOs();
        String classifier = null;
        if (os != null) {
            if (os == OperatingSystem.WINDOWS) {
                classifier = "win";
            } else {
                classifier = os.toString().toLowerCase();
            }
        }
        String fxClsfr = classifier;

        // Project properites
        var projectProperties = PropertiesUtils.read(JfxDemo.class, "project-info.properties");

        // Component config
        var config = ComponentConfig.builder()
                .title("JFX Component")
                .name(COMPONENT_NAME)
                .version(COMPONENT_VERSION)
                .repositories(
                    r -> r.name("local").url(projectProperties.getProperty("localRepo")),
                    r -> r.name("central").url(CENTRAL_REPO)
                )
                .modules(
                    m -> m.groupId("org.openjfx").artifactId("javafx-base")
                            .version(JFX_VERSION).classifier(fxClsfr),
                    m -> m.groupId("org.openjfx").artifactId("javafx-controls")
                            .version(JFX_VERSION).classifier(fxClsfr),
                    m -> m.groupId("org.openjfx").artifactId("javafx-graphics")
                            .version(JFX_VERSION).classifier(fxClsfr),
                    m -> m.groupId("com.techsenger.alpha.demo.jfx").artifactId("alpha-demo-jfx-core")
                            .version(Version.of(projectProperties.getProperty("version")))
                            .active(true) // the module is active!
                )
                .build();

        // Starting components
        var messagePrinter = new SystemMessagePrinter();
        var componentManager = framework.getComponentManager();
        System.out.println("Starting repo (it is already resolved)");
        componentManager.startComponent("alpha-repo", framework.getVersion());

        if (!framework.getRegistry().isComponentAdded(COMPONENT_NAME, COMPONENT_VERSION)) {
            System.out.println("Installing JFX component:");
            var xml = componentManager.installComponent(config, messagePrinter);
            System.out.println(xml);
        }
        System.out.println("Starting " + COMPONENT_NAME);
        componentManager.startComponent(COMPONENT_NAME, COMPONENT_VERSION);
    }

    private JfxDemo() {
        // empty
    }
}
