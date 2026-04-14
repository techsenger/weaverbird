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
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.regex.Pattern;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.interpolation.PropertiesBasedValueSource;
import org.codehaus.plexus.interpolation.RegexBasedInterpolator;
import org.eclipse.aether.artifact.Artifact;

/**
 *
 * @author Pavel Castornii
 */
@Mojo(name = Goals.ASSEMBLE_DISTRO)
public class AssembleDistroMojo extends AbstractAssembleMojo {

    @Parameter(required = true)
    private String mainClass;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            if (Files.exists(getPath())) {
                getLog().info(getPath() + " already exists. Skipping...");
                return;
            }
            createRepo(true);
            createData();
            createConfig();
            createTemp();
            addLoggerConfig();
            createBin();
        } catch (Exception ex) {
            getLog().error(ex);
        }
    }

    private void createBin() throws Exception {
        var binPath = getPath().resolve("bin");
        Files.createDirectories(binPath);
        var s = System.lineSeparator();
        var shModulePath = "MODULE_PATH=\"\"" + s;
        var batModulePath = "set \"MODULE_PATH=\"" + s;
        for (var artifact : getModulePathModules()) {
            shModulePath += "MODULE_PATH=\"$MODULE_PATH:$REPO_PATH" + resolvePath(artifact, false) + "\"" + s;
            batModulePath += "set \"MODULE_PATH=!MODULE_PATH!%REPO_PATH%" + resolvePath(artifact, true) +  ";\"" + s;
        }
        createOsScript(shModulePath, "framework.sh", binPath);
        createOsScript(batModulePath, "framework.bat", binPath);
    }

    private void addLoggerConfig() throws Exception {
        var config = getPath().resolve("config");
        var logConfigContent = String.join(System.lineSeparator(), readFile("log4j2.xml"));
        FileUtils.writeFile(config.resolve("log4j2.xml"), logConfigContent, StandardCharsets.UTF_8);
    }

    private void createOsScript(String modulePath, String fileName, Path binPath) throws Exception {
        var properties = new Properties();
        properties.put("modulepath", modulePath);
        properties.put("mainClass", mainClass);
        var shContent = String.join(System.lineSeparator(), readFile(fileName));
        shContent = interpolate(shContent, properties);
        var shPath = binPath.resolve(fileName);
        FileUtils.writeFile(shPath, shContent, StandardCharsets.UTF_8);
        shPath.toFile().setExecutable(true);
    }

    private String interpolate(String content, Properties properties) throws Exception {
        RegexBasedInterpolator interpolator = new RegexBasedInterpolator();
        interpolator.addValueSource(new PropertiesBasedValueSource(properties));
        interpolator.addValueSource(new PropertiesBasedValueSource(System.getProperties()));
        return interpolator.interpolate(content);
    }

    public String resolvePath(Artifact artifact, boolean windows) {
        String separator = null;
        String modulePath = null;
        if (windows) {
            separator = "\\";
            modulePath = separator + artifact.getGroupId().replaceAll(Pattern.quote("."), "\\\\");
        } else {
            separator = File.separator;
            modulePath = separator + artifact.getGroupId().replaceAll(Pattern.quote("."), File.separator);
        }
        modulePath = modulePath
                + separator
                + artifact.getArtifactId()
                + separator
                + artifact.getVersion()
                + separator
                + artifact.getArtifactId()
                + "-"
                + artifact.getVersion()
                + "."
                + artifact.getExtension();
        return modulePath;
    }
}
