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

module com.techsenger.alpha.demo.web.app {
    requires org.slf4j;
    requires jakarta.servlet;
    requires spring.core;
    requires spring.jcl;
    requires spring.beans;
    requires spring.context;
    requires spring.aop;
    requires spring.expression;
    requires micrometer.observation;
    requires micrometer.commons;
    requires spring.webmvc;
    requires spring.web;
    requires thymeleaf;
    requires thymeleaf.spring6;

    opens com.techsenger.alpha.demo.web.app;
}
