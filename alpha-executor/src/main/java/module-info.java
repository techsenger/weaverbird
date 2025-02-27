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

module com.techsenger.alpha.executor {
    requires com.techsenger.toolkit.core;
    requires org.slf4j;
    requires com.techsenger.alpha.api;
    requires com.techsenger.alpha.spi;
    requires jcommander;

    uses com.techsenger.alpha.spi.command.CommandFactory;
    uses com.techsenger.alpha.spi.net.security.SecurityContextService;

    provides com.techsenger.alpha.api.executor.CommandExecutor
            with com.techsenger.alpha.executor.CommandExecutorProvider;

    provides com.techsenger.alpha.spi.module.ModuleActivator
            with com.techsenger.alpha.executor.ModuleActivatorProvider;
}

