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

package com.techsenger.alpha.api.net.security;

import com.techsenger.alpha.api.command.CommandContext;
import com.techsenger.toolkit.core.function.Executable;

/**
 * When remote command is executed this context is used.
 *
 * This class must know nothing about RemoteCommandException.
 *
 * @author Pavel Castornii
 */
public interface CommandSecurityContext {

    CommandContext getCommandContext();

    void execute(Executable executable) throws AuthorizationException, Exception;

    void close() throws Exception;
}
