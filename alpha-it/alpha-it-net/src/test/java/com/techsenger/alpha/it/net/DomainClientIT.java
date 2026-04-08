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

package com.techsenger.alpha.it.net;

import com.techsenger.alpha.core.api.Framework;
import com.techsenger.alpha.core.api.FrameworkFactory;
import com.techsenger.alpha.core.api.FrameworkSettings;
import com.techsenger.alpha.core.api.message.LoggerMessagePrinter;
import com.techsenger.alpha.it.shared.ServerSettings;
import com.techsenger.alpha.it.shared.TestUtils;
import com.techsenger.alpha.net.client.api.ClientService;
import com.techsenger.alpha.net.client.api.ClientServiceFactory;
import com.techsenger.alpha.net.client.api.ClientSession;
import com.techsenger.alpha.net.client.api.DomainClient;
import com.techsenger.toolkit.core.version.Version;
import com.techsenger.toolkit.http.exceptions.ServerException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
public class DomainClientIT {

    private static final Logger logger = LoggerFactory.getLogger(DomainClientIT.class);

    private static final String TEST_COMPONENT_XML = """
            <?xml version="1.0" encoding="UTF-8" ?>
            <Configuration title="Test Component" name="test-comp" version="1.0.0" type="test">
                <Repositories>
                    <Repository name="central" url="https://repo1.maven.org/maven2/"/>
                </Repositories>
                <Modules>
                    <Module groupId="com.google.code.gson" artifactId="gson" version="2.12.1"/>
                </Modules>
            </Configuration>
            """;

    private static final String TEST_COMP_NAME = "test-comp";

    private static final Version TEST_COMP_VERSION = Version.parse("1.0.0");

    private static final String TEST_COMP_ALIAS = "test-alias";

    private Framework framework;

    @TempDir
    private Path tempFwPath;

    private ClientService clientService;

    private ClientSession sessionInfo;

    private DomainClient client;

    @BeforeEach
    public void startFramework() throws Exception {
        var frameworkPath = Paths.get(System.getProperty("basedir"), "target", "framework");
        TestUtils.copyDirectory(frameworkPath, tempFwPath);
        framework = FrameworkFactory.create(FrameworkSettings.builder().repoChecksumEnabled(false).build(), tempFwPath);
        var componentManager = framework.getComponentManager();
        componentManager.startComponent("alpha-repo", framework.getVersion());
        componentManager.resolveComponent("alpha-server", framework.getVersion(), new LoggerMessagePrinter(logger));
        componentManager.startComponent("alpha-server", framework.getVersion());
        clientService = ClientServiceFactory.create();
        sessionInfo = clientService.openSession("test", ServerSettings.HOST, ServerSettings.PORT,
                ServerSettings.LOGIN, ServerSettings.PASSWORD, framework.getVersion());
        client = new DomainClient(clientService, sessionInfo);
    }

    @AfterEach
    public void stopFramework() throws Exception {
        clientService.closeSession(sessionInfo.getName());
        framework.shutdown();
    }

    // -------------------------------------------------------------------------
    // getComponentDescriptor
    // -------------------------------------------------------------------------

    @Test
    public void getComponentDescriptor_validId_descriptorReturnedSuccessfully() throws Exception {
        var descriptor = client.getComponentDescriptor(1);
        assertThat(descriptor.getId()).isEqualTo(1);
    }

    @Test
    public void getComponentDescriptor_validAlias_descriptorReturnedSuccessfully() throws Exception {
        // Deploy with a known alias first so we can look it up
        client.addComponent(TEST_COMPONENT_XML);
        client.resolveComponent(TEST_COMP_NAME, TEST_COMP_VERSION);
        client.deployComponent(TEST_COMP_NAME, TEST_COMP_VERSION, TEST_COMP_ALIAS,
                null, null, false);

        var descriptor = client.getComponentDescriptor(TEST_COMP_ALIAS);
        assertThat(descriptor.getAlias()).isEqualTo(TEST_COMP_ALIAS);
    }

    // -------------------------------------------------------------------------
    // addComponent / removeComponent
    // -------------------------------------------------------------------------

    @Test
    public void addComponent_validXml_configReturnedSuccessfully() throws Exception {
        var config = client.addComponent(TEST_COMPONENT_XML);
        assertThat(config.getName()).isEqualTo(TEST_COMP_NAME);
        assertThat(config.getVersion()).isEqualTo(TEST_COMP_VERSION);
    }

    @Test
    public void addComponent_invalidXml_serverExceptionThrown() {
        assertThatThrownBy(() -> client.addComponent("<invalid>xml"))
                .isInstanceOf(ServerException.class);
    }

    @Test
    public void removeComponent_addedComponent_configReturnedSuccessfully() throws Exception {
        client.addComponent(TEST_COMPONENT_XML);

        var config = client.removeComponent(TEST_COMP_NAME, TEST_COMP_VERSION);
        assertThat(config.getName()).isEqualTo(TEST_COMP_NAME);
        assertThat(config.getVersion()).isEqualTo(TEST_COMP_VERSION);
    }

    @Test
    public void removeComponent_nonExistentComponent_serverExceptionThrown() {
        assertThatThrownBy(() -> client.removeComponent("ghost-comp", Version.parse("9.9.9")))
                .isInstanceOf(ServerException.class);
    }

    // -------------------------------------------------------------------------
    // resolveComponent / unresolveComponent
    // -------------------------------------------------------------------------

    @Test
    public void resolveComponent_addedComponent_configReturnedSuccessfully() throws Exception {
        client.addComponent(TEST_COMPONENT_XML);

        var config = client.resolveComponent(TEST_COMP_NAME, TEST_COMP_VERSION);
        assertThat(config.getName()).isEqualTo(TEST_COMP_NAME);
    }

    @Test
    public void resolveComponent_nonExistentComponent_serverExceptionThrown() {
        assertThatThrownBy(() -> client.resolveComponent("ghost-comp", Version.parse("9.9.9")))
                .isInstanceOf(ServerException.class);
    }

    @Test
    public void unresolveComponent_resolvedComponent_configReturnedSuccessfully() throws Exception {
        client.addComponent(TEST_COMPONENT_XML);
        client.resolveComponent(TEST_COMP_NAME, TEST_COMP_VERSION);

        var config = client.unresolveComponent(TEST_COMP_NAME, TEST_COMP_VERSION);
        assertThat(config.getName()).isEqualTo(TEST_COMP_NAME);
    }

    @Test
    public void unresolveComponent_nonExistentComponent_serverExceptionThrown() {
        assertThatThrownBy(() -> client.unresolveComponent("ghost-comp", Version.parse("9.9.9")))
                .isInstanceOf(ServerException.class);
    }

    // -------------------------------------------------------------------------
    // deployComponent / undeployComponent
    // -------------------------------------------------------------------------

    @Test
    public void deployComponent_resolvedComponent_descriptorReturnedSuccessfully() throws Exception {
        client.addComponent(TEST_COMPONENT_XML);
        client.resolveComponent(TEST_COMP_NAME, TEST_COMP_VERSION);

        var descriptor = client.deployComponent(TEST_COMP_NAME, TEST_COMP_VERSION,
                null, null, null, false);
        assertThat(descriptor.getId()).isNotNull();
        assertThat(descriptor.isActivated()).isFalse();
        assertThat(descriptor.getConfig().getName()).isEqualTo(TEST_COMP_NAME);
    }

    @Test
    public void deployComponent_withAlias_descriptorHasAlias() throws Exception {
        client.addComponent(TEST_COMPONENT_XML);
        client.resolveComponent(TEST_COMP_NAME, TEST_COMP_VERSION);

        var descriptor = client.deployComponent(TEST_COMP_NAME, TEST_COMP_VERSION,
                TEST_COMP_ALIAS, null, null, false);
        assertThat(descriptor.getAlias()).isEqualTo(TEST_COMP_ALIAS);
    }

    @Test
    public void deployComponent_nonExistentComponent_serverExceptionThrown() {
        assertThatThrownBy(() -> client.deployComponent("ghost-comp", Version.parse("9.9.9"),
                null, null, null, false))
                .isInstanceOf(ServerException.class);
    }

    @Test
    public void undeployComponent_byId_descriptorReturnedSuccessfully() throws Exception {
        client.addComponent(TEST_COMPONENT_XML);
        client.resolveComponent(TEST_COMP_NAME, TEST_COMP_VERSION);
        var deployed = client.deployComponent(TEST_COMP_NAME, TEST_COMP_VERSION,
                null, null, null, false);

        var descriptor = client.undeployComponent(deployed.getId());
        assertThat(descriptor.getId()).isEqualTo(deployed.getId());
    }

    @Test
    public void undeployComponent_byAlias_descriptorReturnedSuccessfully() throws Exception {
        client.addComponent(TEST_COMPONENT_XML);
        client.resolveComponent(TEST_COMP_NAME, TEST_COMP_VERSION);
        client.deployComponent(TEST_COMP_NAME, TEST_COMP_VERSION,
                TEST_COMP_ALIAS, null, null, false);

        var descriptor = client.undeployComponent(TEST_COMP_ALIAS);
        assertThat(descriptor.getAlias()).isEqualTo(TEST_COMP_ALIAS);
    }

    @Test
    public void undeployComponent_nonExistentId_serverExceptionThrown() {
        assertThatThrownBy(() -> client.undeployComponent(Integer.MAX_VALUE))
                .isInstanceOf(ServerException.class);
    }

    @Test
    public void undeployComponent_nonExistentAlias_serverExceptionThrown() {
        assertThatThrownBy(() -> client.undeployComponent("ghost-alias"))
                .isInstanceOf(ServerException.class);
    }

    // -------------------------------------------------------------------------
    // activateComponent / deactivateComponent
    // -------------------------------------------------------------------------

    @Test
    public void activateComponent_byId_descriptorIsActivated() throws Exception {
        client.addComponent(TEST_COMPONENT_XML);
        client.resolveComponent(TEST_COMP_NAME, TEST_COMP_VERSION);
        var deployed = client.deployComponent(TEST_COMP_NAME, TEST_COMP_VERSION,
                null, null, null, false);

        var descriptor = client.activateComponent(deployed.getId());
        assertThat(descriptor.isActivated()).isTrue();
    }

    @Test
    public void activateComponent_byAlias_descriptorIsActivated() throws Exception {
        client.addComponent(TEST_COMPONENT_XML);
        client.resolveComponent(TEST_COMP_NAME, TEST_COMP_VERSION);
        client.deployComponent(TEST_COMP_NAME, TEST_COMP_VERSION,
                TEST_COMP_ALIAS, null, null, false);

        var descriptor = client.activateComponent(TEST_COMP_ALIAS);
        assertThat(descriptor.isActivated()).isTrue();
    }

    @Test
    public void activateComponent_nonExistentId_serverExceptionThrown() {
        assertThatThrownBy(() -> client.activateComponent(Integer.MAX_VALUE))
                .isInstanceOf(ServerException.class);
    }

    @Test
    public void activateComponent_nonExistentAlias_serverExceptionThrown() {
        assertThatThrownBy(() -> client.activateComponent("ghost-alias"))
                .isInstanceOf(ServerException.class);
    }

    @Test
    public void deactivateComponent_byId_descriptorIsNotActivated() throws Exception {
        client.addComponent(TEST_COMPONENT_XML);
        client.resolveComponent(TEST_COMP_NAME, TEST_COMP_VERSION);
        var deployed = client.deployComponent(TEST_COMP_NAME, TEST_COMP_VERSION,
                null, null, null, false);
        client.activateComponent(deployed.getId());

        var descriptor = client.deactivateComponent(deployed.getId());
        assertThat(descriptor.isActivated()).isFalse();
    }

    @Test
    public void deactivateComponent_byAlias_descriptorIsNotActivated() throws Exception {
        client.addComponent(TEST_COMPONENT_XML);
        client.resolveComponent(TEST_COMP_NAME, TEST_COMP_VERSION);
        client.deployComponent(TEST_COMP_NAME, TEST_COMP_VERSION,
                TEST_COMP_ALIAS, null, null, false);
        client.activateComponent(TEST_COMP_ALIAS);

        var descriptor = client.deactivateComponent(TEST_COMP_ALIAS);
        assertThat(descriptor.isActivated()).isFalse();
    }

    @Test
    public void deactivateComponent_nonExistentId_serverExceptionThrown() {
        assertThatThrownBy(() -> client.deactivateComponent(Integer.MAX_VALUE))
                .isInstanceOf(ServerException.class);
    }

    @Test
    public void deactivateComponent_nonExistentAlias_serverExceptionThrown() {
        assertThatThrownBy(() -> client.deactivateComponent("ghost-alias"))
                .isInstanceOf(ServerException.class);
    }

    // -------------------------------------------------------------------------
    // installComponent / uninstallComponent
    // -------------------------------------------------------------------------

    @Test
    public void installComponent_validXml_configReturnedSuccessfully() throws Exception {
        var config = client.installComponent(TEST_COMPONENT_XML);
        assertThat(config.getName()).isEqualTo(TEST_COMP_NAME);
        assertThat(config.getVersion()).isEqualTo(TEST_COMP_VERSION);
    }

    @Test
    public void installComponent_invalidXml_serverExceptionThrown() {
        assertThatThrownBy(() -> client.installComponent("<bad>xml"))
                .isInstanceOf(ServerException.class);
    }

    @Test
    public void uninstallComponent_installedComponent_configReturnedSuccessfully() throws Exception {
        client.installComponent(TEST_COMPONENT_XML);

        var config = client.uninstallComponent(TEST_COMP_NAME, TEST_COMP_VERSION);
        assertThat(config.getName()).isEqualTo(TEST_COMP_NAME);
    }

    @Test
    public void uninstallComponent_nonExistentComponent_serverExceptionThrown() {
        assertThatThrownBy(() -> client.uninstallComponent("ghost-comp", Version.parse("9.9.9")))
                .isInstanceOf(ServerException.class);
    }

    // -------------------------------------------------------------------------
    // startComponent / stopComponent
    // -------------------------------------------------------------------------

    @Test
    public void startComponent_installedComponent_descriptorIsActivated() throws Exception {
        client.installComponent(TEST_COMPONENT_XML);

        var descriptor = client.startComponent(TEST_COMP_NAME, TEST_COMP_VERSION,
                null, null, null, false);
        assertThat(descriptor.isActivated()).isTrue();
        assertThat(descriptor.getConfig().getName()).isEqualTo(TEST_COMP_NAME);
    }

    @Test
    public void startComponent_withAlias_descriptorHasAlias() throws Exception {
        client.installComponent(TEST_COMPONENT_XML);

        var descriptor = client.startComponent(TEST_COMP_NAME, TEST_COMP_VERSION,
                TEST_COMP_ALIAS, null, null, false);
        assertThat(descriptor.getAlias()).isEqualTo(TEST_COMP_ALIAS);
    }

    @Test
    public void startComponent_nonExistentComponent_serverExceptionThrown() {
        assertThatThrownBy(() -> client.startComponent("ghost-comp", Version.parse("9.9.9"),
                null, null, null, false))
                .isInstanceOf(ServerException.class);
    }

    @Test
    public void stopComponent_byId_descriptorReturnedSuccessfully() throws Exception {
        client.installComponent(TEST_COMPONENT_XML);
        var started = client.startComponent(TEST_COMP_NAME, TEST_COMP_VERSION,
                null, null, null, false);

        var descriptor = client.stopComponent(started.getId());
        assertThat(descriptor.getId()).isEqualTo(started.getId());
        assertThat(descriptor.isActivated()).isFalse();
    }

    @Test
    public void stopComponent_byAlias_descriptorReturnedSuccessfully() throws Exception {
        client.installComponent(TEST_COMPONENT_XML);
        client.startComponent(TEST_COMP_NAME, TEST_COMP_VERSION,
                TEST_COMP_ALIAS, null, null, false);

        var descriptor = client.stopComponent(TEST_COMP_ALIAS);
        assertThat(descriptor.getAlias()).isEqualTo(TEST_COMP_ALIAS);
        assertThat(descriptor.isActivated()).isFalse();
    }

    @Test
    public void stopComponent_nonExistentId_serverExceptionThrown() {
        assertThatThrownBy(() -> client.stopComponent(Integer.MAX_VALUE))
                .isInstanceOf(ServerException.class);
    }

    @Test
    public void stopComponent_nonExistentAlias_serverExceptionThrown() {
        assertThatThrownBy(() -> client.stopComponent("ghost-alias"))
                .isInstanceOf(ServerException.class);
    }

    // -------------------------------------------------------------------------
    // getAddedComponents / getResolvedComponents /
    // getDeployedComponents / getActivatedComponents
    // -------------------------------------------------------------------------

    @Test
    public void getAddedComponents_afterAdd_containsComponent() throws Exception {
        client.addComponent(TEST_COMPONENT_XML);

        var added = client.getAddedComponents();
        assertThat(added).anyMatch(c -> c.getName().equals(TEST_COMP_NAME)
                && c.getVersion().equals(TEST_COMP_VERSION));
    }

    @Test
    public void getAddedComponents_afterRemove_doesNotContainComponent() throws Exception {
        client.addComponent(TEST_COMPONENT_XML);
        client.removeComponent(TEST_COMP_NAME, TEST_COMP_VERSION);

        var added = client.getAddedComponents();
        assertThat(added).noneMatch(c -> c.getName().equals(TEST_COMP_NAME));
    }

    @Test
    public void getResolvedComponents_afterResolve_containsComponent() throws Exception {
        client.addComponent(TEST_COMPONENT_XML);
        client.resolveComponent(TEST_COMP_NAME, TEST_COMP_VERSION);

        var resolved = client.getResolvedComponents();
        assertThat(resolved).anyMatch(c -> c.getName().equals(TEST_COMP_NAME));
    }

    @Test
    public void getResolvedComponents_afterUnresolve_doesNotContainComponent() throws Exception {
        client.addComponent(TEST_COMPONENT_XML);
        client.resolveComponent(TEST_COMP_NAME, TEST_COMP_VERSION);
        client.unresolveComponent(TEST_COMP_NAME, TEST_COMP_VERSION);

        var resolved = client.getResolvedComponents();
        assertThat(resolved).noneMatch(c -> c.getName().equals(TEST_COMP_NAME));
    }

    @Test
    public void getDeployedComponents_afterDeploy_containsComponent() throws Exception {
        client.addComponent(TEST_COMPONENT_XML);
        client.resolveComponent(TEST_COMP_NAME, TEST_COMP_VERSION);
        var deployed = client.deployComponent(TEST_COMP_NAME, TEST_COMP_VERSION,
                null, null, null, false);

        var deployedList = client.getDeployedComponents();
        assertThat(deployedList).anyMatch(d -> d.getId().equals(deployed.getId()));
    }

    @Test
    public void getDeployedComponents_afterUndeploy_doesNotContainComponent() throws Exception {
        client.addComponent(TEST_COMPONENT_XML);
        client.resolveComponent(TEST_COMP_NAME, TEST_COMP_VERSION);
        var deployed = client.deployComponent(TEST_COMP_NAME, TEST_COMP_VERSION,
                null, null, null, false);
        client.undeployComponent(deployed.getId());

        var deployedList = client.getDeployedComponents();
        assertThat(deployedList).noneMatch(d -> d.getId().equals(deployed.getId()));
    }

    @Test
    public void getActivatedComponents_afterActivate_containsComponent() throws Exception {
        client.addComponent(TEST_COMPONENT_XML);
        client.resolveComponent(TEST_COMP_NAME, TEST_COMP_VERSION);
        var deployed = client.deployComponent(TEST_COMP_NAME, TEST_COMP_VERSION,
                null, null, null, false);
        client.activateComponent(deployed.getId());

        var activated = client.getActivatedComponents();
        assertThat(activated).anyMatch(d -> d.getId().equals(deployed.getId()));
    }

    @Test
    public void getActivatedComponents_afterDeactivate_doesNotContainComponent() throws Exception {
        client.addComponent(TEST_COMPONENT_XML);
        client.resolveComponent(TEST_COMP_NAME, TEST_COMP_VERSION);
        var deployed = client.deployComponent(TEST_COMP_NAME, TEST_COMP_VERSION,
                null, null, null, false);
        client.activateComponent(deployed.getId());
        client.deactivateComponent(deployed.getId());

        var activated = client.getActivatedComponents();
        assertThat(activated).noneMatch(d -> d.getId().equals(deployed.getId()));
    }

    // -------------------------------------------------------------------------
    // getComponentsState / getLayersInfo
    // -------------------------------------------------------------------------
    @Test
    public void getComponentsState_twoComponentsStarted_correctComponentsState() throws Exception {
        var componentsState = client.getComponentsState();
        assertThat(componentsState.getId()).isEqualTo(4);
        assertThat(componentsState.getDeployedCount()).isEqualTo(2);
        assertThat(componentsState.getActivatedCount()).isEqualTo(2);
        client.deactivateComponent(1); // repo
        componentsState = client.getComponentsState();
        assertThat(componentsState.getId()).isEqualTo(5);
        assertThat(componentsState.getDeployedCount()).isEqualTo(2);
        assertThat(componentsState.getActivatedCount()).isEqualTo(1);
    }

    @Test
    public void getLayersInfo_twoComponentsStarted_correctLayersInfo() throws Exception {
        var layersInfo = client.getLayersInfo();
        layersInfo.resolveReferences();
        assertThat(layersInfo.getLayersById()).hasSize(3);
        assertThat(layersInfo.getLayersById().get(0).getName()).isEqualTo(framework.getFullName());
    }

    // -------------------------------------------------------------------------
    // Lifecycle scenario tests
    // -------------------------------------------------------------------------

    @Test
    public void fullLifecycle_addToActivate_allStatesSucceed() throws Exception {
        // add → resolve → deploy → activate
        var addedConfig = client.addComponent(TEST_COMPONENT_XML);
        assertThat(addedConfig.getName()).isEqualTo(TEST_COMP_NAME);

        var resolvedConfig = client.resolveComponent(TEST_COMP_NAME, TEST_COMP_VERSION);
        assertThat(resolvedConfig.getName()).isEqualTo(TEST_COMP_NAME);

        var deployedDescriptor = client.deployComponent(TEST_COMP_NAME, TEST_COMP_VERSION,
                TEST_COMP_ALIAS, null, null, false);
        assertThat(deployedDescriptor.isActivated()).isFalse();

        var activatedDescriptor = client.activateComponent(deployedDescriptor.getId());
        assertThat(activatedDescriptor.isActivated()).isTrue();
    }

    @Test
    public void fullLifecycle_activateToRemove_allStatesSucceed() throws Exception {
        // Bring to activated state first
        client.addComponent(TEST_COMPONENT_XML);
        client.resolveComponent(TEST_COMP_NAME, TEST_COMP_VERSION);
        var deployed = client.deployComponent(TEST_COMP_NAME, TEST_COMP_VERSION,
                TEST_COMP_ALIAS, null, null, false);
        client.activateComponent(deployed.getId());

        // deactivate → undeploy → unresolve → remove
        var deactivated = client.deactivateComponent(deployed.getId());
        assertThat(deactivated.isActivated()).isFalse();

        client.undeployComponent(deployed.getId());
        client.unresolveComponent(TEST_COMP_NAME, TEST_COMP_VERSION);

        var removedConfig = client.removeComponent(TEST_COMP_NAME, TEST_COMP_VERSION);
        assertThat(removedConfig.getName()).isEqualTo(TEST_COMP_NAME);
    }

    @Test
    public void combinedLifecycle_installAndStart_componentIsActivated() throws Exception {
        // install = add + resolve
        client.installComponent(TEST_COMPONENT_XML);

        // start = deploy + activate
        var descriptor = client.startComponent(TEST_COMP_NAME, TEST_COMP_VERSION,
                TEST_COMP_ALIAS, null, null, false);
        assertThat(descriptor.isActivated()).isTrue();
        assertThat(descriptor.getAlias()).isEqualTo(TEST_COMP_ALIAS);
    }

    @Test
    public void combinedLifecycle_stopAndUninstall_componentIsRemoved() throws Exception {
        client.installComponent(TEST_COMPONENT_XML);
        var started = client.startComponent(TEST_COMP_NAME, TEST_COMP_VERSION,
                TEST_COMP_ALIAS, null, null, false);

        // stop = deactivate + undeploy
        var stopped = client.stopComponent(started.getId());
        assertThat(stopped.isActivated()).isFalse();

        // uninstall = unresolve + remove
        var config = client.uninstallComponent(TEST_COMP_NAME, TEST_COMP_VERSION);
        assertThat(config.getName()).isEqualTo(TEST_COMP_NAME);

        // Verify gone from all lists
        assertThat(client.getAddedComponents()).noneMatch(c -> c.getName().equals(TEST_COMP_NAME));
    }

    @Test
    public void deployComponent_withParentClassLoader_flagIsReflectedInDescriptor() throws Exception {
        client.addComponent(TEST_COMPONENT_XML);
        client.resolveComponent(TEST_COMP_NAME, TEST_COMP_VERSION);

        var descriptor = client.deployComponent(TEST_COMP_NAME, TEST_COMP_VERSION,
                null, null, null, true);
        assertThat(descriptor.isParentClassLoaderUsed()).isTrue();
    }
}

