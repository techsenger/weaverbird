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

package com.techsenger.alpha.spi.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a command is local and is executed within the same alpha framework instance where the command
 * text originates (e.g., from a user or a script). Local commands can be executed in all three platform modes:
 * standalone, client, and server. However, a local command is executed only if it is invoked locally, i.e., not
 * from a client over network.
 *
 * <p>Important: When a class is processed, only the annotation present on that class is considered. Annotations
 * declared on parent classes are completely ignored.</p>
 *
 * @author Pavel Castornii
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LocalCommand {

}
