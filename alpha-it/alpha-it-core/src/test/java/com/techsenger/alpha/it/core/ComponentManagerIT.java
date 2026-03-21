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

package com.techsenger.alpha.it.core;

import com.techsenger.alpha.core.api.ComponentManager;
import com.techsenger.alpha.core.api.Framework;
import com.techsenger.alpha.core.api.FrameworkFactory;
import com.techsenger.alpha.core.api.FrameworkSettings;
import com.techsenger.alpha.core.api.component.UnknownComponentException;
import com.techsenger.alpha.core.api.message.LoggerMessagePrinter;
import com.techsenger.alpha.it.shared.TestUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author Pavel Castornii
 */
public class ComponentManagerIT {

    private static final Logger logger = LoggerFactory.getLogger(ComponentManagerIT.class);

    private static final String SERVER_COMPONENT = "alpha-server";

    private static final String UNKNOWN_COMPONENT = "unknown-component";

    private static List<String> readFile(String fileName) throws IOException {
        try (InputStream is = ComponentManagerIT.class.getResourceAsStream(fileName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.toList());
        }
    }

    private Framework framework;

    private ComponentManager componentManager;

    @TempDir
    private Path tempFwPath;

    @BeforeEach
    public void startFramework() throws Exception {
        var frameworkPath = Paths.get(System.getProperty("basedir"), "target", "framework");
        TestUtils.copyDirectory(frameworkPath, tempFwPath);
        framework = FrameworkFactory.create(FrameworkSettings.builder().repoChecksumEnabled(false).build(), tempFwPath);
        componentManager = framework.getComponentManager();
        componentManager.startComponent("alpha-repo", framework.getVersion());
    }

    @AfterEach
    public void stopFramework() throws Exception {
        framework.shutdown();
    }

    @Test
    public void resolveComponent_existingComponent_componentResolvedSuccessfully() throws Exception {
        var resolvedCount = framework.getRegistry().getResolvedComponents().size();
        componentManager.resolveComponent(SERVER_COMPONENT, framework.getVersion(), new LoggerMessagePrinter(logger));
        assertThat(framework.getRegistry().getResolvedComponents()).hasSize(resolvedCount + 1);
    }

    @Test
    public void resolveComponent_unknownComponent_exceptionThrown() throws Exception {
        assertThatThrownBy(() ->
            componentManager.resolveComponent(UNKNOWN_COMPONENT, framework.getVersion(),
                    new LoggerMessagePrinter(logger)))
            .isInstanceOf(UnknownComponentException.class);
    }

    @Test
    public void unresolveComponent_existingComponent_componentResolvedSuccessfully() throws Exception {
        componentManager.resolveComponent(SERVER_COMPONENT, framework.getVersion(), new LoggerMessagePrinter(logger));
        var resolvedCount = framework.getRegistry().getResolvedComponents().size();
        componentManager.unresolveComponent(SERVER_COMPONENT, framework.getVersion(), new LoggerMessagePrinter(logger));
        assertThat(framework.getRegistry().getResolvedComponents()).hasSize(resolvedCount - 1);
    }

    @Test
    public void unresolveComponent_unknownComponent_exceptionThrown() throws Exception {
        assertThatThrownBy(() ->
            componentManager.resolveComponent(UNKNOWN_COMPONENT, framework.getVersion(),
                    new LoggerMessagePrinter(logger)))
            .isInstanceOf(UnknownComponentException.class);
    }

    @Test
    public void deployComponent_existingComponent_componentDeployedSuccessfully() throws Exception {
        componentManager.resolveComponent(SERVER_COMPONENT, framework.getVersion(),
                new LoggerMessagePrinter(logger));
        var deployedComponentCount = componentManager.getComponents().size();
        componentManager.deployComponent(SERVER_COMPONENT, framework.getVersion());
        assertThat(componentManager.getComponents()).hasSize(deployedComponentCount + 1);
    }

    @Test
    public void deployComponent_unknownComponent_exceptionThrown() throws Exception {
        assertThatThrownBy(() ->
                componentManager.deployComponent(UNKNOWN_COMPONENT, framework.getVersion()))
                .isInstanceOf(UnknownComponentException.class);
    }

    @Test
    public void undeployComponent_existingComponent_componentDeployedSuccessfully() throws Exception {
        componentManager.resolveComponent(SERVER_COMPONENT, framework.getVersion(),
                new LoggerMessagePrinter(logger));
        var descriptor = componentManager.deployComponent(SERVER_COMPONENT, framework.getVersion());
        var deployedComponentCount = componentManager.getComponents().size();
        componentManager.undeployComponent(descriptor);
        assertThat(componentManager.getComponents()).hasSize(deployedComponentCount - 1);
    }

    @Test
    public void undeployComponent_unknownComponent_exceptionThrown() throws Exception {
        assertThatThrownBy(() ->
                componentManager.undeployComponent(100))
                .isInstanceOf(UnknownComponentException.class);
    }

    @Test
    public void activateComponent_activatorCall_activatorsAreCalledProperly() throws Exception {
        var xmlConfigLines = readFile("activator-config.xml");
        var xmlConfig = String.join("\n", xmlConfigLines);
        var config = componentManager.addComponent(xmlConfig);
        componentManager.resolveComponent(config, new LoggerMessagePrinter(logger));
        var probe = ActivatorProbeProvider.provider();
        probe.reset();
        var descriptor = componentManager.startComponent(config.getName(), config.getVersion());
        assertThat(probe.getActivatedCount()).isEqualTo(1);
        assertThat(probe.getDeactivatedCount()).isEqualTo(0);
        probe.reset();
        componentManager.stopComponent(descriptor);
        assertThat(probe.getActivatedCount()).isEqualTo(0);
        assertThat(probe.getDeactivatedCount()).isEqualTo(1);
    }
}
