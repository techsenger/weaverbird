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

module com.techsenger.weaverbird.gui {
    requires com.techsenger.toolkit.core;
    requires com.techsenger.toolkit.fx;
    requires org.slf4j;
    requires com.techsenger.weaverbird.core;
    requires com.techsenger.weaverbird.executor;
    requires com.techsenger.weaverbird.net.client;
    requires com.techsenger.patternfx.core;
    requires com.techsenger.patternfx.mvp;
    requires com.techsenger.shellfx.material;
    requires com.techsenger.shellfx.core;
    requires com.techsenger.shellfx.icons;
    requires com.techsenger.shellfx.shared;
    requires com.techsenger.shellfx.layout;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.swing;
    requires atlantafx.base;
    requires jfx.incubator.richtext;
    requires net.sourceforge.plantuml;

    exports com.techsenger.weaverbird.gui;
    exports com.techsenger.weaverbird.gui.console;
    exports com.techsenger.weaverbird.gui.diagram;
    exports com.techsenger.weaverbird.gui.settings;
    exports com.techsenger.weaverbird.gui.style;

    provides com.techsenger.weaverbird.core.spi.module.ModuleActivator
            with com.techsenger.weaverbird.gui.ModuleActivatorProvider;
}

