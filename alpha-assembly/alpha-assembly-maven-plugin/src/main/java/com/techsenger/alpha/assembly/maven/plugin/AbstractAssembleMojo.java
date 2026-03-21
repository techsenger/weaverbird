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

package com.techsenger.alpha.assembly.maven.plugin;

import com.techsenger.toolkit.core.file.FileUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.installation.InstallRequest;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResult;

/**
 *
 * @author Pavel Castornii
 */
public abstract class AbstractAssembleMojo extends AbstractMojo {

    static List<String> readFile(String fileName) throws IOException {
        try (InputStream is = AssembleRuntimeMojo.class.getResourceAsStream(fileName);
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

    @Parameter(required = true)
    private Path path;

    @Component
    private RepositorySystem repoSystem;

    @Parameter(defaultValue = "${repositorySystemSession}", readonly = true)
    private RepositorySystemSession session;

    @Parameter(defaultValue = "${project.remoteProjectRepositories}", readonly = true)
    private List<RemoteRepository> remoteRepositories;

    /**
     * Contains artifacts that are required the start the framework.
     */
    private final List<Artifact> bootLayerArtifacts = new ArrayList<>();

    Path getPath() {
        return path;
    }

    List<Artifact> getBootLayerArtifacts() {
        return bootLayerArtifacts;
    }

    void createData() throws Exception {
        var dataPath = path.resolve("data");
        Files.createDirectories(dataPath);
        var registry = readFileToStr("registry.xml");
        FileUtils.writeFile(dataPath.resolve("alpha-registry.xml"), registry, StandardCharsets.UTF_8);
    }

    void createConfig() throws Exception {
        var configPath = path.resolve("config");

        var repoDirPath = configPath.resolve("alpha-repo").resolve(project.getVersion());
        Files.createDirectories(repoDirPath);
        var repoConfig = readFileToStr("repo-config.xml");
        FileUtils.writeFile(repoDirPath.resolve("configuration.xml"), repoConfig, StandardCharsets.UTF_8);

        var serverDirPath = configPath.resolve("alpha-server").resolve(project.getVersion());
        Files.createDirectories(serverDirPath);
        var serverConfig = readFileToStr("server-config.xml");
        FileUtils.writeFile(serverDirPath.resolve("configuration.xml"), serverConfig, StandardCharsets.UTF_8);
        var serverSettings = readFileToStr("server-settings.xml");
        FileUtils.writeFile(serverDirPath.resolve("settings.xml"), serverSettings, StandardCharsets.UTF_8);

        var cliDirPath = configPath.resolve("alpha-console-cli").resolve(project.getVersion());
        Files.createDirectories(cliDirPath);
        var cliConfig = readFileToStr("cli-config.xml");
        FileUtils.writeFile(cliDirPath.resolve("configuration.xml"), cliConfig, StandardCharsets.UTF_8);
    }

    void createRepo(List<ArtifactItem> extraArtifacts) throws Exception {
        var repoPath = path.resolve("repo");
        Files.createDirectories(repoPath);
        var targetSession = new DefaultRepositorySystemSession(session);
        targetSession.setLocalRepositoryManager(
            repoSystem.newLocalRepositoryManager(
                targetSession, new LocalRepository(repoPath.toFile())
            )
        );

        var frameworkArtifacts = readArtifacts("framework-artifacts.txt");
        for (var a : frameworkArtifacts) {
            resolveAndInstall(a, targetSession);
            this.bootLayerArtifacts.add(a);
        }
        if (extraArtifacts != null) {
            for (var item : extraArtifacts) {
                Artifact artifact = new DefaultArtifact(
                        item.getGroupId(),
                        item.getArtifactId(),
                        item.getClassifier(),
                        item.getType(),
                        item.getVersion());
                this.bootLayerArtifacts.add(artifact);
                resolveAndInstall(artifact, targetSession);
            }
        }

        var repoComponentArtifacts = readArtifacts("repo-artifacts.txt");
        for (var a : repoComponentArtifacts) {
            resolveAndInstall(a, targetSession);
        }
    }

    void createTemp() throws Exception {
        var tempPath = path.resolve("temp");
        Files.createDirectories(tempPath);
    }

    void resolveAndInstall(Artifact artifact, DefaultRepositorySystemSession targetSession)
            throws MojoExecutionException {
        try {
            ArtifactRequest request = new ArtifactRequest(artifact, remoteRepositories, null);
            ArtifactResult result = repoSystem.resolveArtifact(session, request);

            InstallRequest install = new InstallRequest();
            install.addArtifact(result.getArtifact());
            repoSystem.install(targetSession, install);
            getLog().info("Installed: " + artifact);
        } catch (Exception e) {
            throw new MojoExecutionException("Failed: " + artifact, e);
        }
    }

}
