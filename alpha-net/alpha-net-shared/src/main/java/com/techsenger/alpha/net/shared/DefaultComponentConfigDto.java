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

import com.techsenger.alpha.core.api.component.ComponentConfig;
import com.techsenger.alpha.core.api.component.ComponentConfigDto;
import com.techsenger.toolkit.core.version.Version;

/**
 *
 * @author Pavel Castornii
 */
public class DefaultComponentConfigDto implements ComponentConfigDto {

    public static DefaultComponentConfigDto of(ComponentConfig config) {
        if (config == null) {
            return null;
        }
        var dto = new DefaultComponentConfigDto();
        dto.name = config.getName();
        dto.fullName = config.getFullName();
        dto.version = config.getVersion();
        dto.title = config.getTitle();
        dto.type = config.getType();
        return dto;
    }

    private String name;

    private String fullName;

    private Version version;

    private String title;

    private String type;

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }



}
