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

package com.techsenger.alpha.api.registry;

import java.util.List;

/**
 * Registry keeps base data of alpha framework.
 *
 * @author Pavel Castornii
 */
public interface Registry {

    /**
     * Returns script result or null if script hasn't been executed.
     *
     * @return
     */
    InstallResultEntry getInstallResult();

    /**
     * Returns added components or empty collection.
     *
     * @return
     */
    List<ComponentEntry> getAddedComponents();

    /**
     * Returns resolved components or empty collection.
     *
     * @return
     */
    List<ComponentEntry> getResolvedComponents();
}
