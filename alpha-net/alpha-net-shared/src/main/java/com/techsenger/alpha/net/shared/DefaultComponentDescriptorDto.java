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

package com.techsenger.alpha.net.shared;

import com.techsenger.alpha.core.api.component.ComponentDescriptor;
import com.techsenger.alpha.core.api.component.ComponentDescriptorDto;
import java.util.List;

/**
 *
 * @author Pavel Castornii
 */
public class DefaultComponentDescriptorDto implements ComponentDescriptorDto {

    public static DefaultComponentDescriptorDto of(ComponentDescriptor descriptor) {
        if (descriptor == null) {
            return null;
        }
        var dto = new DefaultComponentDescriptorDto();
        dto.id = descriptor.getId();
        dto.alias = descriptor.getAlias();
        dto.activated = descriptor.isActivated();
        dto.config = DefaultComponentConfigDto.of(descriptor.getConfig());
        dto.parents = descriptor.getParents().stream().map(d -> of(d)).toList();
        dto.parentClassLoaderUsed = descriptor.isParentClassLoaderUsed();
        return dto;
    }

    private Integer id;

    private String alias;

    private boolean activated;

    private DefaultComponentConfigDto config;

    private List<? extends ComponentDescriptorDto> parents;

    private boolean parentClassLoaderUsed;

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Override
    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    @Override
    public DefaultComponentConfigDto getConfig() {
        return config;
    }

    public void setConfig(DefaultComponentConfigDto config) {
        this.config = config;
    }

    @Override
    public List<? extends ComponentDescriptorDto> getParents() {
        return parents;
    }

    public void setParents(List<? extends ComponentDescriptorDto> parents) {
        this.parents = parents;
    }

    public boolean isParentClassLoaderUsed() {
        return parentClassLoaderUsed;
    }

    public void setParentClassLoaderUsed(boolean parentClassLoaderUsed) {
        this.parentClassLoaderUsed = parentClassLoaderUsed;
    }
}
