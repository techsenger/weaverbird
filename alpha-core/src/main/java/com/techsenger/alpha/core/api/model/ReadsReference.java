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

package com.techsenger.alpha.core.api.model;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author Pavel Castornii
 */
public class ReadsReference implements Serializable {

    private int layerId;

    private String moduleName;

    public ReadsReference() {

    }

    public int getLayerId() {
        return layerId;
    }

    public void setLayerId(int componentId) {
        this.layerId = componentId;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + this.layerId;
        hash = 37 * hash + Objects.hashCode(this.moduleName);
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
        final ReadsReference other = (ReadsReference) obj;
        if (this.layerId != other.layerId) {
            return false;
        }
        return Objects.equals(this.moduleName, other.moduleName);
    }
}
