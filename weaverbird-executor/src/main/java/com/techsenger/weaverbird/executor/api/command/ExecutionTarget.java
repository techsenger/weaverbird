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

import com.techsenger.weaverbird.executor.api.CommandSyntax;

/**
 *
 * @author Pavel Castornii
 */
public enum ExecutionTarget {

    /**
     * When there is no an active session or {@link CommandSyntax#LOCAL_COMMAND} prefix is used.
     */
    LOCAL,

    /**
     * When there is an active session and {@link CommandSyntax#LOCAL_COMMAND} prefix is not used.
     */
    REMOTE
}
