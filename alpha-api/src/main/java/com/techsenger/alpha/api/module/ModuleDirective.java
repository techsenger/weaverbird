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

package com.techsenger.alpha.api.module;

/**
 *
 * @author Pavel Castornii
 */
public interface ModuleDirective {

    DirectiveType getType();

    String getPackage();

    /**
     * Returns target module.
     * @return
     */
    String getModule();

    /**
     * The name of one of the parent layers. If null, then the layer of the current component.
     * Name of component layer = "name:version" or "name".  Name of the boot layer =
     * {@link com.techsenger.alpha.api.Framework#getLayerName()}
     *
     * @return
     */
    String getLayer();
}
