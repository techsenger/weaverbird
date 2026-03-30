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

package com.techsenger.alpha.repo.core;

import com.techsenger.alpha.core.api.Framework;
import com.techsenger.alpha.core.api.message.MessagePrinter;
import com.techsenger.alpha.core.api.module.DefaultModuleArtifact;
import com.techsenger.alpha.core.api.module.ModuleArtifact;
import com.techsenger.alpha.core.api.module.ModuleType;
import com.techsenger.alpha.core.spi.repo.RepoService;
import com.techsenger.reposium.core.ArtifactDescriptor;
import com.techsenger.reposium.core.DefaultArtifactDescriptor;
import com.techsenger.reposium.core.MavenRepo;
import com.techsenger.toolkit.core.SingletonFactory;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
            MessagePrinter printer) {
        return this.resolve(remoteReposByName, List.of(artifact), printer);
    }

    @Override
    public boolean resolve(Map<String, String> remoteReposByName, List<ModuleArtifact> artifacts,
            MessagePrinter printer) {
        List<ArtifactDescriptor> descriptors = artifacts.stream()
                .map(d -> artifactToDescriptor(d))
                .collect(Collectors.toList());
        var checksumEnabled = framework.getSettings().isRepoChecksumEnabled();
        var localRepo = framework.getPathManager().getRepositoryDirectory();
        com.techsenger.reposium.core.MessagePrinter mp = null;
        if (printer != null) {
            mp = (message) -> printer.printlnMessage(message);
        }
        return mavenRepo.resolve(localRepo, remoteReposByName, checksumEnabled, descriptors, mp);
    }

    @Override
    public List<ModuleArtifact> scanRepo() throws IOException {
        var localRepo = framework.getPathManager().getRepositoryDirectory();
        return mavenRepo.scanRepo(localRepo)
                .stream()
                .map(a -> descriptorToArtifact(a))
                .collect(Collectors.toList());
    }

    @Override
    public boolean unresolve(ModuleArtifact artifact, MessagePrinter printer) {
        return this.unresolve(List.of(artifact), printer);
    }

    @Override
    public boolean unresolve(List<ModuleArtifact> artifacts, MessagePrinter printer) {
        var localRepo = framework.getPathManager().getRepositoryDirectory();
        List<ArtifactDescriptor> descriptors = artifacts.stream()
                .map(d -> artifactToDescriptor(d))
                .collect(Collectors.toList());
        com.techsenger.reposium.core.MessagePrinter mp = null;
        if (printer != null) {
            mp = (message) -> printer.printlnMessage(message);
        }
        return mavenRepo.unresolve(localRepo, descriptors, mp);
    }

    void setFramework(Framework framework) {
        this.framework = framework;
    }

    private ArtifactDescriptor artifactToDescriptor(ModuleArtifact artifactor) {
        return new DefaultArtifactDescriptor(
                        artifactor.getGroupId(),
                        artifactor.getArtifactId(),
                        artifactor.getVersion(),
                        artifactor.getClassifier(),
                        artifactor.getType().toString().toLowerCase());
    }

    private ModuleArtifact descriptorToArtifact(ArtifactDescriptor descriptor) {
        return new DefaultModuleArtifact(
                        descriptor.getGroupId(),
                        descriptor.getArtifactId(),
                        descriptor.getVersion(),
                        descriptor.getClassifier(),
                        ModuleType.valueOf(descriptor.getType().toUpperCase()));
    }
}
