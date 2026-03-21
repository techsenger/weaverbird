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

package com.techsenger.alpha.core.api;

import com.techsenger.alpha.core.api.model.LayersInfo;
import com.techsenger.alpha.core.api.model.ModulesInfo;
import com.techsenger.toolkit.core.model.ThreadInfoModel;
import com.techsenger.toolkit.core.model.ThreadModel;
import java.util.List;

/**
 *
 * @author Pavel Castornii
 */
public interface JvmInspector {

    /**
     * Returns threads.
     *
     * @return threads.
     */
    List<ThreadModel> getThreads();

    /**
     * Returns the thread info.
     *
     * @return the thread info.
     */
    List<ThreadInfoModel> getThreadInfos();

    /**
     * Returns the information about all modules.
     *
     * @return
     */
    ModulesInfo getModulesInfo();

    /**
     * Returns the information about all existing layers.
     *
     * @return
     */
    LayersInfo getLayersInfo();
}
