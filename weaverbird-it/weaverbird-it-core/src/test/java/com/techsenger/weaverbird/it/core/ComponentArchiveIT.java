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

package com.techsenger.weaverbird.it.core;

import com.techsenger.toolkit.core.version.Version;
import com.techsenger.weaverbird.core.api.ComponentManager;
import com.techsenger.weaverbird.core.api.Framework;
import com.techsenger.weaverbird.core.api.FrameworkFactory;
import com.techsenger.weaverbird.core.api.FrameworkSettings;
import com.techsenger.weaverbird.core.api.PathResolver;
import com.techsenger.weaverbird.core.api.component.ComponentConfig;
import com.techsenger.weaverbird.core.api.component.ComponentException;
import com.techsenger.weaverbird.it.shared.TestUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests building and adding of component archives, whose structure mirrors the framework structure.
 *
 * @author Pavel Castornii
 */
public class ComponentArchiveIT {

    private static final String NAME = "archive-test";

    private static final String VERSION = "1.0.0";

    private static final String CONFIG_ENTRY = "config/" + NAME + "/" + VERSION + "/configuration.xml";

    private static final String DATA_ENTRY = "data/" + NAME + "/" + VERSION + "/files/data.txt";

    private static final String LEGAL_ENTRY = "legal/" + NAME + "/" + VERSION + "/LICENSE.txt";

    private static String createXmlConfig(String name, String version) {
        return """
                <?xml version="1.0" encoding="UTF-8" ?>
                <Configuration name="%s" version="%s" type="test">
                    <Repositories>
                        <Repository name="central" url="https://repo1.maven.org/maven2/"/>
                    </Repositories>
                    <Modules>
                        <Module groupId="com.google.code.gson" artifactId="gson" version="2.12.1"/>
                    </Modules>
                </Configuration>
                """.formatted(name, version);
    }

    private Framework framework;

    private ComponentManager componentManager;

    private PathResolver pathResolver;

    @TempDir
    private Path tempFwPath;

    @TempDir
    private Path tempArchivePath;

    @BeforeEach
    public void startFramework() throws Exception {
        var frameworkPath = Paths.get(System.getProperty("basedir"), "target", "framework");
        TestUtils.copyDirectory(frameworkPath, tempFwPath);
        var settings = FrameworkSettings.builder()
                .repoChecksumEnabled(false)
                .application(app -> app
                    .name("core-test")
                    .version(Version.of("1.0.0")))
                .build();
        framework = FrameworkFactory.create(settings, tempFwPath);
        componentManager = framework.getComponentManager();
        pathResolver = framework.getPathManager().getPathResolver();
    }

    @AfterEach
    public void stopFramework() throws Exception {
        framework.shutdown();
    }

    @Test
    public void buildComponent_componentWithDataAndLegal_archiveMirrorsFrameworkStructure() throws Exception {
        var config = addComponentWithFiles();
        var archivePath = componentManager.buildComponent(config, tempArchivePath, "zip");
        try (var zipFile = new ZipFile(archivePath.toFile())) {
            var names = zipFile.stream().map(ZipEntry::getName).toList();
            assertThat(names).containsExactlyInAnyOrder(CONFIG_ENTRY, DATA_ENTRY, LEGAL_ENTRY);
        }
    }

    @Test
    public void addComponent_builtArchive_filesRestoredToFrameworkDirectories() throws Exception {
        var config = addComponentWithFiles();
        var archivePath = componentManager.buildComponent(config, tempArchivePath, "zip");
        componentManager.removeComponent(config);
        assertThat(pathResolver.resolveConfigDirectory(config)).doesNotExist();
        assertThat(pathResolver.resolveDataDirectory(config)).doesNotExist();
        assertThat(pathResolver.resolveLegalDirectory(config)).doesNotExist();

        var addedConfig = componentManager.addComponent(archivePath);

        assertThat(addedConfig.getName()).isEqualTo(NAME);
        assertThat(addedConfig.getVersion().getFull()).isEqualTo(VERSION);
        assertThat(framework.getRegistry().isComponentAdded(NAME, addedConfig.getVersion())).isTrue();
        assertThat(pathResolver.resolveConfigFile(addedConfig)).exists();
        assertThat(pathResolver.resolveDataDirectory(addedConfig).resolve("files/data.txt"))
                .hasContent("data content");
        assertThat(pathResolver.resolveLegalDirectory(addedConfig).resolve("LICENSE.txt"))
                .hasContent("license content");
    }

    @Test
    public void addComponent_archiveWithConfigInConfigRoot_exceptionThrown() throws Exception {
        var archivePath = createArchive(Map.of("config/configuration.xml", createXmlConfig(NAME, VERSION)));
        assertThatThrownBy(() -> componentManager.addComponent(archivePath))
                .isInstanceOf(ComponentException.class)
                .hasMessageContaining("config not found");
    }

    @Test
    public void addComponent_archiveWithTwoConfigs_exceptionThrown() throws Exception {
        var entries = new LinkedHashMap<String, String>();
        entries.put(CONFIG_ENTRY, createXmlConfig(NAME, VERSION));
        entries.put("config/other/2.0.0/configuration.xml", createXmlConfig("other", "2.0.0"));
        var archivePath = createArchive(entries);
        assertThatThrownBy(() -> componentManager.addComponent(archivePath))
                .isInstanceOf(ComponentException.class)
                .hasMessageContaining("more than one config");
    }

    @Test
    public void addComponent_configNotMatchingItsPath_exceptionThrownAndNothingWritten() throws Exception {
        var archivePath = createArchive(Map.of(CONFIG_ENTRY, createXmlConfig("another-name", VERSION)));
        assertThatThrownBy(() -> componentManager.addComponent(archivePath))
                .isInstanceOf(ComponentException.class)
                .hasMessageContaining("doesn't match");
        assertThat(framework.getPathManager().getConfigDirectory().resolve("another-name")).doesNotExist();
        assertThat(framework.getPathManager().getConfigDirectory().resolve(NAME)).doesNotExist();
    }

    @Test
    public void addComponent_fileOfAnotherComponent_exceptionThrownAndNothingWritten() throws Exception {
        var entries = new LinkedHashMap<String, String>();
        entries.put(CONFIG_ENTRY, createXmlConfig(NAME, VERSION));
        entries.put(DATA_ENTRY, "data content");
        entries.put("data/other/1.0.0/data.txt", "foreign content");
        var archivePath = createArchive(entries);
        assertThatThrownBy(() -> componentManager.addComponent(archivePath))
                .isInstanceOf(ComponentException.class)
                .hasMessageContaining("Illegal file");
        assertThat(framework.getPathManager().getConfigDirectory().resolve(NAME)).doesNotExist();
        assertThat(framework.getPathManager().getDataDirectory().resolve(NAME)).doesNotExist();
        assertThat(framework.getPathManager().getDataDirectory().resolve("other")).doesNotExist();
        assertThat(framework.getRegistry().getAddedComponents().stream()
                .anyMatch(e -> NAME.equals(e.getName()))).isFalse();
    }

    @Test
    public void addComponent_entryOutsideComponentDirectories_exceptionThrownAndNothingWritten() throws Exception {
        var entries = new LinkedHashMap<String, String>();
        entries.put(CONFIG_ENTRY, createXmlConfig(NAME, VERSION));
        entries.put("bin/evil.sh", "evil");
        var archivePath = createArchive(entries);
        assertThatThrownBy(() -> componentManager.addComponent(archivePath))
                .isInstanceOf(ComponentException.class)
                .hasMessageContaining("Illegal file");
        assertThat(framework.getPathManager().getBinDirectory().resolve("evil.sh")).doesNotExist();
        assertThat(framework.getPathManager().getConfigDirectory().resolve(NAME)).doesNotExist();
    }

    @Test
    public void addComponent_entryEscapingComponentDirectory_exceptionThrownAndNothingWritten() throws Exception {
        var entries = new LinkedHashMap<String, String>();
        entries.put(CONFIG_ENTRY, createXmlConfig(NAME, VERSION));
        entries.put("data/" + NAME + "/" + VERSION + "/../../../escaped.txt", "escaped");
        var archivePath = createArchive(entries);
        assertThatThrownBy(() -> componentManager.addComponent(archivePath))
                .isInstanceOf(ComponentException.class)
                .hasMessageContaining("Illegal file");
        assertThat(framework.getPathManager().getDataDirectory().resolve("escaped.txt")).doesNotExist();
        assertThat(framework.getPathManager().getConfigDirectory().resolve(NAME)).doesNotExist();
    }

    @Test
    public void addComponent_alreadyAddedComponent_exceptionThrown() throws Exception {
        var config = addComponentWithFiles();
        var archivePath = componentManager.buildComponent(config, tempArchivePath, "zip");
        assertThatThrownBy(() -> componentManager.addComponent(archivePath))
                .isInstanceOf(ComponentException.class)
                .hasMessageContaining("already added");
    }

    /**
     * Adds the component by xml and puts data and legal files to its framework directories.
     */
    private ComponentConfig addComponentWithFiles() throws Exception {
        var config = componentManager.addComponent(createXmlConfig(NAME, VERSION));
        writeFile(pathResolver.resolveDataDirectory(config).resolve("files/data.txt"), "data content");
        writeFile(pathResolver.resolveLegalDirectory(config).resolve("LICENSE.txt"), "license content");
        return config;
    }

    private Path createArchive(Map<String, String> contentsByEntry) throws IOException {
        var archivePath = tempArchivePath.resolve("component.zip");
        try (var zos = new ZipOutputStream(Files.newOutputStream(archivePath))) {
            for (var entry : contentsByEntry.entrySet()) {
                zos.putNextEntry(new ZipEntry(entry.getKey()));
                zos.write(entry.getValue().getBytes(StandardCharsets.UTF_8));
                zos.closeEntry();
            }
        }
        return archivePath;
    }

    private void writeFile(Path path, String content) throws IOException {
        Files.createDirectories(path.getParent());
        Files.writeString(path, content, StandardCharsets.UTF_8);
    }
}
