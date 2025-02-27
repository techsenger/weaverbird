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

module com.techsenger.alpha.api {
    requires com.techsenger.toolkit.core;
    requires org.slf4j;
    requires org.apache.logging.log4j.core;

    exports com.techsenger.alpha.api;
    exports com.techsenger.alpha.api.command;
    exports com.techsenger.alpha.api.component;
    exports com.techsenger.alpha.api.executor;
    exports com.techsenger.alpha.api.logging;
    exports com.techsenger.alpha.api.message;
    exports com.techsenger.alpha.api.model;
    exports com.techsenger.alpha.api.module;
    exports com.techsenger.alpha.api.net;
    exports com.techsenger.alpha.api.net.security;
    exports com.techsenger.alpha.api.net.session;
    exports com.techsenger.alpha.api.registry;
    exports com.techsenger.alpha.api.repo;
    exports com.techsenger.alpha.api.spi to com.techsenger.alpha.core;
    exports com.techsenger.alpha.api.state;

    opens com.techsenger.alpha.api.command;
    opens com.techsenger.alpha.api.executor;
    opens com.techsenger.alpha.api.message;
    opens com.techsenger.alpha.api.model;
    opens com.techsenger.alpha.api.state;

    uses com.techsenger.alpha.api.spi.FrameworkService;
}


