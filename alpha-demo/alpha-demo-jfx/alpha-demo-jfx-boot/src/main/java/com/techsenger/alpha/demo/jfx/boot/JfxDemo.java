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
import com.techsenger.alpha.core.api.message.SystemMessagePrinter;
import com.techsenger.toolkit.core.os.OperatingSystem;
import com.techsenger.toolkit.core.os.OsUtils;
import com.techsenger.toolkit.core.version.Version;
import java.nio.file.Paths;

/**
 * This demo shows how to run JavaFX.
 *
 * <p>Two run this demo: {@code mvn clean install exec:exec}
 *
 * @author Pavel Castornii
 */
public final class JfxDemo {

    private static final String JFX_COMPONENT_XML =
            """
            <Configuration title="JFX Component" name="jfx-component" version="1.0.0" type="base">
                <Repositories>
                    <Repository name="local" url="${sys['mvn.localrepo']}"/>
                    <Repository name="central" url="https://repo1.maven.org/maven2/"/>
                </Repositories>

                <Modules>
                    <Module groupId="org.openjfx" artifactId="javafx-base" classifier="${info.os}" version="25"/>
                    <Module groupId="org.openjfx" artifactId="javafx-controls" classifier="${info.os}" version="25"/>
                    <Module groupId="org.openjfx" artifactId="javafx-graphics" classifier="${info.os}" version="25"/>

                    <Module groupId="com.techsenger.alpha.demo.jfx" artifactId="alpha-demo-jfx-core"
                            version="${sys['project.version']}" active="true"/>
                </Modules>
            </Configuration>
            """;

    private static final String JFX_COMPONENT_NAME = "jfx-component";

    private static final Version JFX_COMPONENT_VERSION = Version.parse("1.0.0");

    public static void main(String[] args) throws Exception {
        var frameworkPath = Paths.get(System.getProperty(SystemProperties.ROOT_PATH));
        var settings = FrameworkSettings.builder().repoChecksumEnabled(false).build();
        var framework = FrameworkFactory.create(settings, frameworkPath);
        var messagePrinter = new SystemMessagePrinter();
        var componentManager = framework.getComponentManager();

        // setting OS
        var os = OsUtils.getOs();
        if (os != null) {
            if (os == OperatingSystem.WINDOWS) {
                componentManager.getConfigInfo().put("os", "win");
            } else {
                componentManager.getConfigInfo().put("os", os.toString().toLowerCase());
            }
        }

        System.out.println("Starting repo (it is already resolved)");
        componentManager.startComponent("alpha-repo", framework.getVersion());

        if (!framework.getRegistry().isComponentAdded(JFX_COMPONENT_NAME, JFX_COMPONENT_VERSION)) {
            System.out.println("\nInstalling and starting JFX component");
            componentManager.installComponent(JFX_COMPONENT_XML, messagePrinter);
        }
        componentManager.startComponent(JFX_COMPONENT_NAME, JFX_COMPONENT_VERSION);
    }

    private JfxDemo() {
        // empty
    }
}
