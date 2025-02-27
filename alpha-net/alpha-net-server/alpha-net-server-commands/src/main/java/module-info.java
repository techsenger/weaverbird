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

module com.techsenger.alpha.net.server.commands {
    requires jcommander;
    requires com.techsenger.toolkit.core;
    requires com.techsenger.alpha.api;
    requires com.techsenger.alpha.spi;
    requires java.xml;
    requires org.slf4j;

    uses com.techsenger.alpha.api.executor.CommandExecutor;

    opens com.techsenger.alpha.net.server.commands to jcommander, com.techsenger.alpha.executor;

    provides com.techsenger.alpha.spi.command.CommandFactory
            with com.techsenger.alpha.net.server.commands.CommandFactoryProvider;

}
