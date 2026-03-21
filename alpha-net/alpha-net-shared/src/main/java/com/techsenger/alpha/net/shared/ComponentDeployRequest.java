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

package com.techsenger.alpha.net.shared;

import com.techsenger.toolkit.core.version.Version;
import com.techsenger.toolkit.http.request.Request;
import java.util.List;

/**
 *
 * @author Pavel Castornii
 */
public class ComponentDeployRequest implements Request {

    private String name;

    private Version version;

    private String alias;

    private List<Integer> parentIds;

    private List<String> parentAliases;

    private boolean useParentClassLoader;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public List<Integer> getParentIds() {
        return parentIds;
    }

    public void setParentIds(List<Integer> parentIds) {
        this.parentIds = parentIds;
    }

    public List<String> getParentAliases() {
        return parentAliases;
    }

    public void setParentAliases(List<String> parentAliases) {
        this.parentAliases = parentAliases;
    }

    public boolean isUseParentClassLoader() {
        return useParentClassLoader;
    }

    public void setUseParentClassLoader(boolean useParentClassLoader) {
        this.useParentClassLoader = useParentClassLoader;
    }


}
