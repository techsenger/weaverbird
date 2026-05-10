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
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.interpolation.PropertiesBasedValueSource;
import org.codehaus.plexus.interpolation.RegexBasedInterpolator;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;

/**
 *
 * @author Pavel Castornii
 */
public abstract class AbstractAssembleMojo extends AbstractBaseMojo {

    private static final String REPO_COMPONENT = "weaverbird-repo";

    private static final String SERVER_COMPONENT = "weaverbird-server";

    private static final String CLI_COMPONENT = "weaverbird-cli";

    private static final String GUI_COMPONENT = "weaverbird-gui";

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

    @Parameter(required = true)
    private List<String> components;

    /**
     * Use only via {@link #getComponentSet()}.
     */
    private Set<String> componentSet;

    List<Artifact> getModulePathModules() {
        return modulePathModules;
    }

    void createData() throws Exception {
        var dataPath = getPath().resolve("data");
        Files.createDirectories(dataPath);
        var registry = readFileToStr("registry.xml");
        StringBuilder builder = new StringBuilder();
        for (var c : components) {
            addComponent(builder, c);
        }
        var addedComponents = builder.toString();
        builder.setLength(0);
        var resolvedComponents = "";
        if (getComponentSet().contains(REPO_COMPONENT)) {
            addComponent(builder, REPO_COMPONENT);
            resolvedComponents = builder.toString();
        }
        var properties = new Properties();
        properties.put("addedComponents", addedComponents);
        properties.put("resolvedComponents", resolvedComponents);
        registry = interpolate(registry, properties);
        FileUtils.writeFile(dataPath.resolve("weaverbird-registry.xml"), registry, StandardCharsets.UTF_8);
    }

    void createConfig() throws Exception {
        var configPath = getPath().resolve("config");
        if (getComponentSet().contains(REPO_COMPONENT)) {
            var repoDirPath = configPath.resolve(REPO_COMPONENT).resolve(project.getVersion());
            Files.createDirectories(repoDirPath);
            var repoConfig = readFileToStr("repo-config.xml");
            FileUtils.writeFile(repoDirPath.resolve("configuration.xml"), repoConfig, StandardCharsets.UTF_8);
        }

        if (getComponentSet().contains(SERVER_COMPONENT)) {
            var serverDirPath = configPath.resolve(SERVER_COMPONENT).resolve(project.getVersion());
            Files.createDirectories(serverDirPath);
            var serverConfig = readFileToStr("server-config.xml");
            FileUtils.writeFile(serverDirPath.resolve("configuration.xml"), serverConfig, StandardCharsets.UTF_8);
            var serverSettings = readFileToStr("server-settings.xml");
            FileUtils.writeFile(serverDirPath.resolve("settings.xml"), serverSettings, StandardCharsets.UTF_8);
        }

        if (getComponentSet().contains(CLI_COMPONENT)) {
            var cliDirPath = configPath.resolve(CLI_COMPONENT).resolve(project.getVersion());
            Files.createDirectories(cliDirPath);
            var cliConfig = readFileToStr("cli-config.xml");
            FileUtils.writeFile(cliDirPath.resolve("configuration.xml"), cliConfig, StandardCharsets.UTF_8);
        }

        if (getComponentSet().contains(GUI_COMPONENT)) {
            var guiDirPath = configPath.resolve(GUI_COMPONENT).resolve(project.getVersion());
            Files.createDirectories(guiDirPath);
            var guiConfig = readFileToStr("gui-config.xml");
            FileUtils.writeFile(guiDirPath.resolve("configuration.xml"), guiConfig, StandardCharsets.UTF_8);
        }
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

        if (getComponentSet().contains(REPO_COMPONENT)) {
            var repoComponentArtifacts = readArtifacts("repo-modules.txt");
            for (var a : repoComponentArtifacts) {
                resolveModule(a, session);
            }
        }
    }

    void createTemp() throws Exception {
        var tempPath = getPath().resolve("temp");
        Files.createDirectories(tempPath);
    }

    String interpolate(String content, Properties properties) throws Exception {
        RegexBasedInterpolator interpolator = new RegexBasedInterpolator();
        interpolator.addValueSource(new PropertiesBasedValueSource(properties));
        interpolator.addValueSource(new PropertiesBasedValueSource(System.getProperties()));
        return interpolator.interpolate(content);
    }

    private Set<String> getComponentSet() {
        if (this.componentSet == null) {
            this.componentSet = new HashSet<>(components);
        }
        return componentSet;
    }

    private void addComponent(StringBuilder builder, String name) {
        if (builder.length() > 0) {
            builder.append("\n");
        }
        builder.append("        <Component name=\"");
        builder.append(name);
        builder.append("\" version=\"");
        builder.append(project.getVersion());
        builder.append("\"/>");
    }
}
