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

package com.techsenger.alpha.api;

/**
 * This enumeration describes Alpha framework mode. It is not about the type of the main application(s).
 *
 * @author Pavel Castornii
 */
public enum FrameworkMode {

    /**
     * No Alpha client will be connected to this instance of framework.
     */
    STANDALONE,

    /**
     * Another Alpha framework will connect to this instance of framework.
     */
    SERVER,

    /**
     * This framework will connect to another instance of Alpha framework.
     */
    CLIENT
}
