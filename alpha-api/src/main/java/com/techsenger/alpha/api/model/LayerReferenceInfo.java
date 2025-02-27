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

package com.techsenger.alpha.api.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class is used to resolved references when {@link ComponentLayerModel} user is ready to do it. This class
 * lets to hide all service data.
 *
 * @author Pavel Castornii
 */
public class LayerReferenceInfo implements Serializable {

    private List<Integer> parentIds;

    /**
     * Key is the name one of the resolved modules of this layer.
     */
    private Map<String, Set<ReadsReference>> readsByModuleName;

    private List<ModuleDirectiveReference> moduleDirectives;

    private Set<Integer> ancestorIds;

    private Set<Integer> descendantIds;

    public List<Integer> getParentIds() {
        return parentIds;
    }

    public void setParentIds(List<Integer> parentIds) {
        this.parentIds = parentIds;
    }

    public Map<String, Set<ReadsReference>> getReadsByModuleName() {
        return readsByModuleName;
    }

    public void setReadsByModuleName(Map<String, Set<ReadsReference>> readsByModuleName) {
        this.readsByModuleName = readsByModuleName;
    }

    public List<ModuleDirectiveReference> getModuleDirectives() {
        return moduleDirectives;
    }

    public void setModuleDirectives(List<ModuleDirectiveReference> moduleDirectives) {
        this.moduleDirectives = moduleDirectives;
    }

    public Set<Integer> getAncestorIds() {
        return ancestorIds;
    }

    public void setAncestorIds(Set<Integer> ancestorIds) {
        this.ancestorIds = ancestorIds;
    }

    public Set<Integer> getDescendantIds() {
        return descendantIds;
    }

    public void setDescendantIds(Set<Integer> descendantIds) {
        this.descendantIds = descendantIds;
    }
}
