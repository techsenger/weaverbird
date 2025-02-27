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

package com.techsenger.alpha.repo.maven.resolver;

import com.techsenger.alpha.api.repo.ModuleArtifact;
import com.techsenger.alpha.api.repo.ModulePathResolver;
import com.techsenger.toolkit.core.os.OsUtils;
import java.io.File;
import java.nio.file.Path;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
public class ModulePathResolverProvider implements ModulePathResolver {

    private static final Logger logger = LoggerFactory.getLogger(ModulePathResolverProvider.class);

    @Override
    public Path resolveModulePath(Path localRepo, ModuleArtifact artifact) {
        String modulePath = null;
        if (!OsUtils.isWindows()) {
            modulePath = artifact.getGroupId().replaceAll(Pattern.quote("."), File.separator);
        } else {
            modulePath = artifact.getGroupId().replaceAll(Pattern.quote("."), "\\\\");
        }
        modulePath = modulePath
                + File.separator
                + artifact.getArtifactId()
                + File.separator
                + artifact.getVersion()
                + File.separator
                + artifact.getFileName();
        var resolvedPath = localRepo.resolve(modulePath);
        logger.trace("Module {} has path {}", artifact.getArtifactId(), resolvedPath);
        return resolvedPath;
    }

}
