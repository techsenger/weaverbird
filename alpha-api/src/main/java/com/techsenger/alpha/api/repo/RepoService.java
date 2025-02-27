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

package com.techsenger.alpha.api.repo;

import com.techsenger.alpha.api.message.MessagePrinter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Pavel Castornii
 */
public interface RepoService {

    /**
     * Resolves artifact in local repo using remote repos.
     *
     * @param localRepo
     * @param artifact
     * @return true if was installed and false, if not.
     */
    boolean resolve(Path localRepo, Map<String, String> remoteReposByName, ModuleArtifact artifact,
            MessagePrinter printer);

    /**
     * Resolves artifacts in local repo using remote repos.
     *
     * @param localRepo
     * @param artifacts
     * @return true if was installed and false, if not.
     */
    boolean resolve(Path localRepo, Map<String, String> remoteReposByName, List<ModuleArtifact> artifacts,
            MessagePrinter printer);
    /**
     * Unresolves artifact in local repo.
     *
     * @param localRepo
     * @param artifact
     * @param printer
     * @return true if was uninstalled and false, if not.
     */
    boolean unresolve(Path localRepo, ModuleArtifact artifact, MessagePrinter printer);

    /**
     * Unresolves artifacts in local repo.
     *
     * @param localRepo
     * @param printer
     * @return true if was uninstalled and false, if not.
     */
    boolean unresolve(Path localRepo, List<ModuleArtifact> artifacts, MessagePrinter printer);

    /**
     * Scans repo and returns artifacts of all found war/jar files.
     *
     * @param localRepo
     * @return
     */
    List<ModuleArtifact> scanRepo(Path localRepo) throws IOException;
}
