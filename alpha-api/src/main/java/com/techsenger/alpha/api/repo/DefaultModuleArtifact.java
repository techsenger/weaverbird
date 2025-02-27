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

import com.techsenger.alpha.api.module.ModuleType;
import java.util.Objects;

/**
 *
 * @author Pavel Castornii
 */
public class DefaultModuleArtifact implements ModuleArtifact {

    private final String groupId;

    private final String artifactId;

    private final String version;

    private final String classifier;

    private final ModuleType type;

    public DefaultModuleArtifact(String groupId, String artifactId, String version, String classifier,
            ModuleType type) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.classifier = classifier;
        this.type = type;
    }

    @Override
    public String getGroupId() {
        return groupId;
    }

    @Override
    public String getArtifactId() {
        return artifactId;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getClassifier() {
        return classifier;
    }

    @Override
    public ModuleType getType() {
        return type;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.groupId);
        hash = 47 * hash + Objects.hashCode(this.artifactId);
        hash = 47 * hash + Objects.hashCode(this.version);
        hash = 47 * hash + Objects.hashCode(this.classifier);
        hash = 47 * hash + Objects.hashCode(this.type);
        //activity is not considered!
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DefaultModuleArtifact other = (DefaultModuleArtifact) obj;
        if (!Objects.equals(this.groupId, other.groupId)) {
            return false;
        }
        if (!Objects.equals(this.artifactId, other.artifactId)) {
            return false;
        }
        if (!Objects.equals(this.version, other.version)) {
            return false;
        }
        if (!Objects.equals(this.classifier, other.classifier)) {
            return false;
        }
        if (this.type != other.type) {
            return false;
        }
        //activity is not considered!
        return true;
    }

    @Override
    public String getFileName() {
        String stringType = "jar";
        if (this.type != null) {
            stringType = this.type.toString().toLowerCase();
        }
        String clsfr = "";
        if (this.classifier != null) {
            clsfr = "-" + this.classifier;
        }
        var fileName = this.artifactId
                + "-"
                + version
                + clsfr
                + "."
                + stringType;
        return fileName;
    }

    @Override
    public String toString() {
        return "DefaultModuleArtifact{" + "groupId=" + groupId + ", artifactId=" + artifactId
                + ", version=" + version + ", classifier=" + classifier + ", type=" + type + '}';
    }
}
