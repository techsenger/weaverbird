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

package com.techsenger.alpha.demo.starter;

import com.techsenger.alpha.core.api.ComponentManager;
import com.techsenger.alpha.core.api.FrameworkFactory;
import com.techsenger.alpha.core.api.FrameworkSettings;
import com.techsenger.alpha.core.api.SystemProperties;
import com.techsenger.alpha.core.api.message.MessagePrinter;
import com.techsenger.toolkit.core.version.Version;
import java.nio.file.Paths;

/**
 * A simple starter that demonstrates basic operations with components.
 *
 * <p>Two ways to run this demo:
 * <ol>
 *     <li>Compile and run scripts in {@code target/framework/bin} folder</li>
 *     <li>Run: {@code mvn clean install exec:exec}</li>
 * </ol>
 * @author Pavel Castornii
 */
public final class StarterDemo {

    private static final String CUSTOM_COMPONENT =
            """
            <Configuration title="Custom Component" name="custom-component" version="1.0.0" type="base">
                <Repositories>
                    <Repository name="central" url="https://repo1.maven.org/maven2/"/>
                </Repositories>

                <Modules>
                    <Module groupId="com.google.code.gson" artifactId="gson" version="2.10"/>
                </Modules>
            </Configuration>
            """;

    private static final class MessagePrinterImpl implements MessagePrinter {

        @Override
        public void printlnMessage(String message) {
            System.out.println(message);
        }

        @Override
        public void printlnError(String error) {
            System.err.println(error);
        }

        @Override
        public int getWidth() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    public static void main(String[] args) throws Exception {
        var frameworkPath = Paths.get(System.getProperty(SystemProperties.ROOT_PATH));
        var settings = FrameworkSettings.builder().repoChecksumEnabled(false).build();
        var framework = FrameworkFactory.create(settings, frameworkPath);
        var messagePrinter = new MessagePrinterImpl();
        var componentManager = framework.getComponentManager();

        System.out.println("Starting repo (it is already resolved)");
        componentManager.startComponent("alpha-repo", framework.getVersion());
        listComponents(componentManager);

        System.out.println("\nInstalling and starting custom component");
        componentManager.installComponent(CUSTOM_COMPONENT, messagePrinter);
        componentManager.startComponent("custom-component", Version.parse("1.0.0"));
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
