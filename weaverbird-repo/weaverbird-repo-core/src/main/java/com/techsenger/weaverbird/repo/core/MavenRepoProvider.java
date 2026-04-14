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

package com.techsenger.weaverbird.repo.core;

import com.techsenger.reposium.core.MavenRepo;
import com.techsenger.toolkit.core.SingletonFactory;
import com.techsenger.toolkit.core.version.Version;
import com.techsenger.weaverbird.core.api.Framework;
import com.techsenger.weaverbird.core.api.module.ArtifactEventListener;
import com.techsenger.weaverbird.core.api.module.DefaultModuleArtifact;
import com.techsenger.weaverbird.core.api.module.ModuleArtifact;
import com.techsenger.weaverbird.core.api.module.ModuleType;
import com.techsenger.weaverbird.core.spi.repo.RepoService;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;

/**
 *
 * @author Pavel Castornii
 */
public class MavenRepoProvider implements RepoService {

    private static final SingletonFactory<RepoService> singletonFactory =
            new SingletonFactory<>(() -> new MavenRepoProvider());

    public static final RepoService provider() {
        return singletonFactory.singleton();
    }

    private final MavenRepo mavenRepo = new MavenRepo();

    private Framework framework;

    public MavenRepoProvider() {

    }

    @Override
    public boolean resolve(Map<String, String> remoteReposByName, ModuleArtifact artifact,
            ArtifactEventListener listener) {
        return this.resolve(remoteReposByName, List.of(artifact), listener);
    }

    @Override
    public boolean resolve(Map<String, String> remoteReposByName, List<ModuleArtifact> artifacts,
            ArtifactEventListener listener) {
        List<Artifact> rArtifacts = artifacts.stream()
                .map(d -> moduleToArtifact(d))
                .collect(Collectors.toList());
        var checksumEnabled = framework.getSettings().isRepoChecksumEnabled();
        var localRepo = framework.getPathManager().getRepositoryDirectory();
        return mavenRepo.resolve(localRepo, remoteReposByName, checksumEnabled, rArtifacts, createListener(listener));
    }

    @Override
    public List<ModuleArtifact> scanRepo() throws IOException {
        var localRepo = framework.getPathManager().getRepositoryDirectory();
        return mavenRepo.scanRepo(localRepo)
                .stream()
                .map(a -> artifactToModule(a))
                .collect(Collectors.toList());
    }

    @Override
    public boolean unresolve(ModuleArtifact artifact, ArtifactEventListener listener) {
        return this.unresolve(List.of(artifact), listener);
    }

    @Override
    public boolean unresolve(List<ModuleArtifact> artifacts, ArtifactEventListener listener) {
        var localRepo = framework.getPathManager().getRepositoryDirectory();
        List<Artifact> rArtifacts = artifacts.stream()
                .map(d -> moduleToArtifact(d))
                .collect(Collectors.toList());
        return mavenRepo.unresolve(localRepo, rArtifacts, createListener(listener));
    }

    void setFramework(Framework framework) {
        this.framework = framework;
    }

    private Artifact moduleToArtifact(ModuleArtifact artifact) {
        return new DefaultArtifact(
                artifact.getGroupId(),
                artifact.getArtifactId(),
                artifact.getClassifier(),
                artifact.getType().toString().toLowerCase(),
                artifact.getVersion().toString());
    }

    private ModuleArtifact artifactToModule(Artifact artifact) {
        return new DefaultModuleArtifact(
                artifact.getGroupId(),
                artifact.getArtifactId(),
                Version.of(artifact.getVersion()),
                artifact.getClassifier(),
                ModuleType.valueOf(artifact.getExtension().toUpperCase()));
    }

    private com.techsenger.reposium.core.ArtifactEventListener createListener(ArtifactEventListener l) {
        if (l == null) {
            return null;
        } else {
            return new com.techsenger.reposium.core.ArtifactEventListener() {
                @Override
                public void onStarted(Artifact artifact) {
                    l.onStarted(artifactToModule(artifact));
                }

                @Override
                public void onFinished(Artifact artifact) {
                    l.onFinished(artifactToModule(artifact));
                }
            };
        }
    }
}
