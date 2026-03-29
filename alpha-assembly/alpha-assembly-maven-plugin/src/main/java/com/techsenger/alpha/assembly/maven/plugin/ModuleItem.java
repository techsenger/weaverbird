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

package com.techsenger.alpha.assembly.maven.plugin;

/**
 *
 * @author Pavel Castornii
 */
public class ModuleItem {

    private String groupId;

    private String artifactId;

    private String version;

    private String classifier;

    private String type = "jar";

    private Boolean onModulePath;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getClassifier() {
        return classifier;
    }

    public void setClassifier(String classifier) {
        this.classifier = classifier;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getOnModulePath() {
        return onModulePath;
    }

    public void setOnModulePath(Boolean onModulePath) {
        this.onModulePath = onModulePath;
    }

    @Override
    public String toString() {
        return "ModuleItem{" + "groupId=" + groupId + ", artifactId=" + artifactId + ", version=" + version
                + ", classifier=" + classifier + ", type=" + type + ", onModulePath=" + onModulePath + '}';
    }
}
