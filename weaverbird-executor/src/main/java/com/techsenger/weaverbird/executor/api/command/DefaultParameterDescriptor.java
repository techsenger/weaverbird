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

package com.techsenger.weaverbird.executor.api.command;

/**
 *
 * @author Pavel Castornii
 */
public class DefaultParameterDescriptor implements ParameterDescriptor {

    private String shortName;

    private String longName;

    private boolean required;

    private String description;

    private boolean main;

    @Override
    public String getShortName() {
        return this.shortName;
    }

    @Override
    public String getLongName() {
        return this.longName;
    }

    @Override
    public boolean isRequired() {
        return this.required;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public boolean isMain() {
        return main;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMain(boolean main) {
        this.main = main;
    }
}
