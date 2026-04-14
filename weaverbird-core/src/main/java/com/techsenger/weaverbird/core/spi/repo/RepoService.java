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

package com.techsenger.weaverbird.core.spi.repo;

import com.techsenger.weaverbird.core.api.module.ArtifactEventListener;
import com.techsenger.weaverbird.core.api.module.ModuleArtifact;
import java.io.IOException;
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
     * @param remoteReposByName
     * @param artifact
     * @param listener
     * @return true if was installed and false, if not.
     */
    boolean resolve(Map<String, String> remoteReposByName, ModuleArtifact artifact, ArtifactEventListener listener);

    /**
     * Resolves artifacts in local repo using remote repos.
     *
     * @param remoteReposByName
     * @param artifacts
     * @param listener
     * @return true if was installed and false, if not.
     */
    boolean resolve(Map<String, String> remoteReposByName, List<ModuleArtifact> artifacts,
            ArtifactEventListener listener);

    /**
     * Unresolves/deletes artifact in local repo.
     *
     * @param artifact
     * @param listener
     * @return true if was deleted and false, if not.
     */
    boolean unresolve(ModuleArtifact artifact, ArtifactEventListener listener);

    /**
     * Unresolves/deletes artifacts in local repo.
     *
     * @param artifacts
     * @param listener
     * @return true if was deleted and false, if not.
     */
    boolean unresolve(List<ModuleArtifact> artifacts, ArtifactEventListener listener);

    /**
     * Scans repo and returns artifacts of all found war/jar files.
     *
     * @return
     */
    List<ModuleArtifact> scanRepo() throws IOException;
}
