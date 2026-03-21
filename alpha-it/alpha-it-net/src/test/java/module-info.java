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

open module com.techsenger.alpha.it.net {
    requires com.techsenger.toolkit.http;
    requires com.techsenger.toolkit.core;
    requires com.techsenger.alpha.core;
    requires com.techsenger.alpha.net.shared;
    requires com.techsenger.alpha.net.client;
    requires com.techsenger.alpha.net.server;
    requires com.techsenger.alpha.assembly.security;
    requires com.techsenger.alpha.it.shared;
    requires jdk.httpserver;
    requires commons.logging;
    requires org.slf4j;
    requires java.xml;
    requires com.google.gson;
    requires jakarta.el;
    requires org.glassfish.expressly;

    requires org.junit.jupiter.api;
    requires org.assertj.core;
    requires org.apache.logging.log4j.slf4j2.impl;
}
