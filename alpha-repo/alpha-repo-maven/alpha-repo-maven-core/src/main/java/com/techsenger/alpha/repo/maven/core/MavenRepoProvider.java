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

package com.techsenger.alpha.repo.maven.core;

import com.techsenger.alpha.api.message.MessagePrinter;
import com.techsenger.alpha.api.module.ModuleType;
import com.techsenger.alpha.api.repo.DefaultModuleArtifact;
import com.techsenger.alpha.api.repo.ModuleArtifact;
import com.techsenger.alpha.api.repo.RepoService;
import com.techsenger.reposium.core.ArtifactDescriptor;
import com.techsenger.reposium.core.DefaultArtifactDescriptor;
import com.techsenger.reposium.core.MavenRepo;
import com.techsenger.toolkit.core.SingletonFactory;
import java.io.IOException;
import java.nio.file.Path;
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

    public MavenRepoProvider() {

    }

    @Override
    public boolean resolve(Path localRepo, Map<String, String> remoteReposByName, ModuleArtifact artifact,
            MessagePrinter printer) {
        return this.resolve(localRepo, remoteReposByName, List.of(artifact), printer);
    }

    @Override
    public boolean resolve(Path localRepo, Map<String, String> remoteReposByName, List<ModuleArtifact> artifacts,
            MessagePrinter printer) {
        List<ArtifactDescriptor> descriptors = artifacts.stream()
                .map(d -> artifactToDescriptor(d))
                .collect(Collectors.toList());
        return mavenRepo
                .resolve(localRepo, remoteReposByName, descriptors, (message) -> printer.printlnMessage(message));
    }

    @Override
    public List<ModuleArtifact> scanRepo(Path localRepo) throws IOException {
        return mavenRepo.scanRepo(localRepo)
                .stream()
                .map(a -> descriptorToArtifact(a))
                .collect(Collectors.toList());
    }

    @Override
    public boolean unresolve(Path localRepo, ModuleArtifact artifact, MessagePrinter printer) {
        return this.unresolve(localRepo, List.of(artifact), printer);
    }

    @Override
    public boolean unresolve(Path localRepo, List<ModuleArtifact> artifacts, MessagePrinter printer) {
        List<ArtifactDescriptor> descriptors = artifacts.stream()
                .map(d -> artifactToDescriptor(d))
                .collect(Collectors.toList());
        return mavenRepo.unresolve(localRepo, descriptors, (message) -> printer.printlnMessage(message));
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
