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

module com.techsenger.alpha.console.gui {
    requires com.techsenger.toolkit.core;
    requires com.techsenger.toolkit.fx;
    requires org.slf4j;
    requires org.apache.logging.log4j.core;
    requires org.apache.logging.log4j;
    requires com.techsenger.alpha.api;
    requires com.techsenger.alpha.spi;
    requires com.techsenger.mvvm4fx.core;
    requires com.techsenger.tabshell.material;
    requires com.techsenger.tabshell.core;
    requires com.techsenger.tabshell.kit.material;
    requires com.techsenger.tabshell.kit.core;
    requires com.techsenger.tabshell.kit.text;
    requires com.techsenger.tabshell.kit.dialog;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.swing;
    requires org.controlsfx.controls;
    requires atlantafx.base;
    requires org.fxmisc.richtext;
    requires org.fxmisc.flowless;
    requires org.fxmisc.undo;
    requires wellbehavedfx;
    requires reactfx;
    requires com.techsenger.ansi4j.core.api;
    requires net.sourceforge.plantuml;
    requires jakarta.xml.bind;

    exports com.techsenger.alpha.console.gui.diagram;
    exports com.techsenger.alpha.console.gui.keys;
    exports com.techsenger.alpha.console.gui.log;
    exports com.techsenger.alpha.console.gui.shell;
    exports com.techsenger.alpha.console.gui.style;
    exports com.techsenger.alpha.console.gui.utils;

    opens com.techsenger.alpha.console.gui.diagram to javafx.base;
    opens com.techsenger.alpha.console.gui.settings to jakarta.xml.bind;
    opens com.techsenger.alpha.console.gui.style;

    provides com.techsenger.alpha.spi.console.ConsoleService
            with com.techsenger.alpha.console.gui.ConsoleProvider;
    provides com.techsenger.alpha.spi.module.ModuleActivator
            with com.techsenger.alpha.console.gui.ModuleActivatorProvider;
}

