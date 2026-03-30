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

package com.techsenger.alpha.core.api;

import com.techsenger.alpha.core.api.logging.MemoryLog;

/**
 *
 * @author Pavel Castornii
 */
public interface LogManager {

    /**
     * Initializes all log managers. This method is called by Framework at startup.
     */
    void initialize();

    /**
     * Returns memory log or null, if memory log module was not added to system.
     * @return
     */
    MemoryLog getMemoryLog();

    /**
     * Deinitializes all log managers. This method is called by Framework at shutdown.
     */
    void deinitialize();
}
