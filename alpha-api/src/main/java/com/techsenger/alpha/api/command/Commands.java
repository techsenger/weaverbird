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

package com.techsenger.alpha.api.command;

/**
 * Commands that classes in different modules are aware of.
 *
 * @author Pavel Castornii
 */
public final class Commands {

    /**
     * Script execute command name.
     */
    public static final String SCRIPT_EXECUTE = "script:execute";

    public static final String LOG_PRINT = "log:print";

    public static final String SESSION_ATTACH = "session:attach";

    public static final String SESSION_DETACH = "session:detach";

    private Commands() {
        //empty
    }
}
