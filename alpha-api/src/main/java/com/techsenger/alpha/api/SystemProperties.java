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
 *
 * @author Pavel Castornii
 */
public final class SystemProperties {

    /**
     * The mode of the framework instance.
     */
    public static final String MODE = "com.techsenger.alpha.core.mode";

    /**
     * The component with alpha-executor that will be used for executing boot script.
     */
    public static final String EXECUTOR = "com.techsenger.alpha.core.executor";

    /**
     * The alias of control component.
     */
    public static final String EXECUTOR_ALIAS = "com.techsenger.alpha.core.executor.alias";

    /**
     * Script that will be executed when on framework launch.
     */
    public static final String SCRIPT = "com.techsenger.alpha.core.script";

    /**
     * Root path property name.
     */
    public static final String ROOT_PATH = "com.techsenger.alpha.core.root.path";

    /**
     * True if memory log is used and false otherwise. Default = false.
     */
    public static final String LOG_MEMORY = "com.techsenger.alpha.core.log.memory";

    /**
     * The path of the log file.
     */
    public static final String LOG_PATH = "com.techsenger.alpha.core.log.path";

    private SystemProperties() {
        //empty
    }
}
