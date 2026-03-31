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

package com.techsenger.alpha.assembly.maven.plugin;

import java.nio.file.Path;
import java.util.List;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
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
public abstract class AbstractBaseMojo extends AbstractMojo {

    @Parameter(defaultValue = "${mojoExecution}", readonly = true)
    private MojoExecution mojoExecution;

    @Parameter(required = true)
    private Path path;

    @Parameter(required = false)
    private List<ModuleItem> modules;

    @Component
    private RepositorySystem repoSystem;

    @Parameter(defaultValue = "${repositorySystemSession}", readonly = true)
    private RepositorySystemSession session;

    @Parameter(defaultValue = "${project.remoteProjectRepositories}", readonly = true)
    private List<RemoteRepository> remoteRepositories;

    Path getPath() {
        return path;
    }

    RepositorySystemSession createTargetSession(Path repoPath) {
        var targetSession = new DefaultRepositorySystemSession(session);
        targetSession.setLocalRepositoryManager(
            repoSystem.newLocalRepositoryManager(
                targetSession, new LocalRepository(repoPath.toFile())
            )
        );
        return targetSession;
    }

    void resolveProvidedModules(RepositorySystemSession session, List<Artifact> modulePathModules) throws Exception {
        if (modules != null) {
            for (var module : modules) {
                Artifact artifact = new DefaultArtifact(
                        module.getGroupId(),
                        module.getArtifactId(),
                        module.getClassifier(),
                        module.getType(),
                        module.getVersion());
                if (modulePathModules == null) {
                    if (module.getOnModulePath() != null) {
                        throw new MojoExecutionException("Parameter 'onModulePath' is not supported for goal '"
                                + this.mojoExecution.getGoal() + "'");
                    }
                } else {
                    if (Boolean.TRUE.equals(module.getOnModulePath())) {
                        modulePathModules.add(artifact);
                    }
                }
                resolveModule(artifact, session);
            }
        }
    }

    void resolveModule(Artifact artifact, final RepositorySystemSession targetSession) throws MojoExecutionException {
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
