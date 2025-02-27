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

module com.techsenger.alpha.core {
    requires com.techsenger.alpha.api;
    requires com.techsenger.alpha.spi;
    requires com.techsenger.toolkit.core;
    requires java.management;
    requires org.slf4j;
    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;
    requires java.xml;
    requires jakarta.el;
    requires org.fusesource.jansi;

    opens com.techsenger.alpha.core.project to com.techsenger.toolkit.core;

    uses com.techsenger.alpha.spi.launcher.LauncherProgressHandler;

    provides com.techsenger.alpha.api.spi.FrameworkService with com.techsenger.alpha.core.FrameworkProvider;
}

