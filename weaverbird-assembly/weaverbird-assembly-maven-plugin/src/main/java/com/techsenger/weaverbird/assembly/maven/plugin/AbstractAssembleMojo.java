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

package com.techsenger.weaverbird.assembly.maven.plugin;

import com.techsenger.toolkit.core.file.FileUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;

/**
 *
 * @author Pavel Castornii
 */
public abstract class AbstractAssembleMojo extends AbstractBaseMojo {

    static List<String> readFile(String fileName) throws IOException {
        try (InputStream is = AbstractAssembleMojo.class.getResourceAsStream(fileName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.toList());
        }
    }

    static String readFileToStr(String fileName) throws IOException {
        return String.join(System.lineSeparator(), readFile(fileName));
    }

    static List<Artifact> readArtifacts(String fileName) throws IOException {
        var lines = readFile(fileName);
        var artifacts = new ArrayList<Artifact>();
        for (var line : lines) {
            if (!line.isBlank()) {
                Artifact artifact = null;
                var splits = line.split(Pattern.quote(":"));
                if (splits.length == 3) {
                    artifact = new DefaultArtifact(splits[0], splits[1], "jar", splits[2]);
                    artifacts.add(artifact);
                } else {
                    throw new UnsupportedOperationException();
                }
            }
        }
        return artifacts;
    }

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    /**
     * Contains artifacts that are required the start the framework.
     */
    private final List<Artifact> modulePathModules = new ArrayList<>();

    List<Artifact> getModulePathModules() {
        return modulePathModules;
    }

    void createData() throws Exception {
        var dataPath = getPath().resolve("data");
        Files.createDirectories(dataPath);
        var registry = readFileToStr("registry.xml");
        FileUtils.writeFile(dataPath.resolve("weaverbird-registry.xml"), registry, StandardCharsets.UTF_8);
    }

    void createConfig() throws Exception {
        var configPath = getPath().resolve("config");

        var repoDirPath = configPath.resolve("weaverbird-repo").resolve(project.getVersion());
        Files.createDirectories(repoDirPath);
        var repoConfig = readFileToStr("repo-config.xml");
        FileUtils.writeFile(repoDirPath.resolve("configuration.xml"), repoConfig, StandardCharsets.UTF_8);

        var serverDirPath = configPath.resolve("weaverbird-server").resolve(project.getVersion());
        Files.createDirectories(serverDirPath);
        var serverConfig = readFileToStr("server-config.xml");
        FileUtils.writeFile(serverDirPath.resolve("configuration.xml"), serverConfig, StandardCharsets.UTF_8);
        var serverSettings = readFileToStr("server-settings.xml");
        FileUtils.writeFile(serverDirPath.resolve("settings.xml"), serverSettings, StandardCharsets.UTF_8);

        var cliDirPath = configPath.resolve("weaverbird-cli").resolve(project.getVersion());
        Files.createDirectories(cliDirPath);
        var cliConfig = readFileToStr("cli-config.xml");
        FileUtils.writeFile(cliDirPath.resolve("configuration.xml"), cliConfig, StandardCharsets.UTF_8);

        var guiDirPath = configPath.resolve("weaverbird-gui").resolve(project.getVersion());
        Files.createDirectories(guiDirPath);
        var guiConfig = readFileToStr("gui-config.xml");
        FileUtils.writeFile(guiDirPath.resolve("configuration.xml"), guiConfig, StandardCharsets.UTF_8);
    }

    void createRepo(boolean modulePathSupported) throws Exception {
        var repoPath = getPath().resolve("repo");
        Files.createDirectories(repoPath);

        var session = createTargetSession(repoPath);
        var frameworkArtifacts = readArtifacts("framework-modules.txt");
        for (var a : frameworkArtifacts) {
            resolveModule(a, session);
            this.modulePathModules.add(a);
        }

        if (modulePathSupported) {
            resolveProvidedModules(session, modulePathModules);
        } else {
            resolveProvidedModules(session, null);
        }

        var repoComponentArtifacts = readArtifacts("repo-modules.txt");
        for (var a : repoComponentArtifacts) {
            resolveModule(a, session);
        }
    }

    void createTemp() throws Exception {
        var tempPath = getPath().resolve("temp");
        Files.createDirectories(tempPath);
    }
}
