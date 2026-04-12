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

package com.techsenger.alpha.demo.starter;

import com.techsenger.alpha.core.api.ComponentManager;
import com.techsenger.alpha.core.api.FrameworkFactory;
import com.techsenger.alpha.core.api.FrameworkSettings;
import com.techsenger.alpha.core.api.SystemProperties;
import com.techsenger.alpha.core.api.component.ComponentConfig;
import com.techsenger.alpha.core.api.component.VersionMatch;
import com.techsenger.alpha.core.api.message.SystemMessagePrinter;
import com.techsenger.toolkit.core.version.Version;
import java.nio.file.Paths;

/**
 * A simple starter that demonstrates basic operations with components.
 *
 * <p>Build and run the scripts in the {@code target/framework/bin} folder
 *
 * @author Pavel Castornii
 */
public final class StarterDemo {

    private static final String APP_NAME = "starter-demo";

    private static final Version APP_VERSION = Version.of("1.0.0");

    private static final String COMPONENT_NAME = "custom-component";

    private static final Version COMPONENT_VERSION = Version.of("2.0.0");

    private static final ComponentConfig COMPONENT_CONFIG = ComponentConfig.builder()
            .title("Custom Component")
            .name(COMPONENT_NAME)
            .version(COMPONENT_VERSION)
            .repositories(
                r -> r.name("central").url("https://repo1.maven.org/maven2")
            )
            .parents(
                p -> p.name(APP_NAME).version(APP_VERSION).versionMatch(VersionMatch.PATCH)
            )
            .modules(
                m -> m.groupId("com.google.code.gson").artifactId("gson").version(Version.of("2.10"))
            )
            .build();

    private static final String COMPONENT_CONFIG_XML =
            """
            <Configuration title="Custom Component" name="custom-component" version="2.0.0" type="base">
                <Repositories>
                    <Repository name="central" url="https://repo1.maven.org/maven2/"/>
                </Repositories>
                <Parents>
                    <Parent name="starter-demo" version="1.0.0" versionMatch="patch"/>
                </Parents>
                <Modules>
                    <Module groupId="com.google.code.gson" artifactId="gson" version="2.10"/>
                </Modules>
            </Configuration>
            """;

    public static void main(String[] args) throws Exception {
        var frameworkPath = Paths.get(System.getProperty(SystemProperties.ROOT_PATH));
        var settings = FrameworkSettings.builder()
                .repoChecksumEnabled(false)
                .application(app -> app
                    .name(APP_NAME)
                    .version(APP_VERSION))
                .build();
        var framework = FrameworkFactory.create(settings, frameworkPath);
        var messagePrinter = new SystemMessagePrinter();
        var componentManager = framework.getComponentManager();

        System.out.println("Starting repo (it is already resolved)");
        componentManager.startComponent("alpha-repo", framework.getVersion());
        listComponents(componentManager);

        System.out.println("\nInstalling and starting custom component");
        if (!framework.getRegistry().isComponentAdded(COMPONENT_NAME, COMPONENT_VERSION)) {
            // you can install a new component using java config or xml config
            var xml = componentManager.installComponent(COMPONENT_CONFIG, messagePrinter);
            // or using xml
            // var config = componentManager.installComponent(COMPONENT_CONFIG_XML, messagePrinter);

        }

        componentManager.startComponent(COMPONENT_NAME, COMPONENT_VERSION);
        listComponents(componentManager);

        framework.shutdown();
    }

    private static void listComponents(ComponentManager manager) {
        System.out.println("Current components:");
        manager.getComponents()
                .forEach(c -> System.out.println("    " + c.getDescriptor().getConfig().getFullName()));
    }

    private StarterDemo() {
        // empty
    }
}
