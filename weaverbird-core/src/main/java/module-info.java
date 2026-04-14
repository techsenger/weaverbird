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

module com.techsenger.weaverbird.core {
    requires com.techsenger.toolkit.core;
    requires java.management;
    requires org.slf4j;
    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;
    requires java.xml;
    requires jakarta.el;
    requires org.fusesource.jansi;

    exports com.techsenger.weaverbird.core.api;
    exports com.techsenger.weaverbird.core.api.component;
    exports com.techsenger.weaverbird.core.api.logging;
    exports com.techsenger.weaverbird.core.api.message;
    exports com.techsenger.weaverbird.core.api.model;
    exports com.techsenger.weaverbird.core.api.module;
    exports com.techsenger.weaverbird.core.api.registry;
    exports com.techsenger.weaverbird.core.api.state;
    exports com.techsenger.weaverbird.core.spi.module;
    exports com.techsenger.weaverbird.core.spi.repo;

    opens com.techsenger.weaverbird.core.api.message;
    opens com.techsenger.weaverbird.core.api.model;
    opens com.techsenger.weaverbird.core.api.module;
    opens com.techsenger.weaverbird.core.api.state;
    opens com.techsenger.weaverbird.core.impl.project to com.techsenger.toolkit.core;
}

