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

package com.techsenger.alpha.api.executor;

import com.techsenger.alpha.api.command.CommandDescriptor;
import java.util.Collection;

/**
 * This listener allows to get notifications when when layers are created/destroyed so, executor adds/removes command
 * factories. This listener can be useful in consoles, when console needs to know all currently supported commands.
  *
 * @author Pavel Castornii
 */
public interface CommandListener {

    void onAdded(Collection<CommandDescriptor> commands);

    void onRemoved(Collection<CommandDescriptor> commands);
}
