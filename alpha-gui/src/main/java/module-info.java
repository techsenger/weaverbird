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

module com.techsenger.alpha.gui {
    requires com.techsenger.toolkit.core;
    requires com.techsenger.toolkit.fx;
    requires org.slf4j;
    requires com.techsenger.alpha.core;
    requires com.techsenger.alpha.executor;
    requires com.techsenger.alpha.net.client;
    requires com.techsenger.patternfx.core;
    requires com.techsenger.patternfx.mvp;
    requires com.techsenger.tabshell.material;
    requires com.techsenger.tabshell.core;
    requires com.techsenger.tabshell.icons;
    requires com.techsenger.tabshell.shared;
    requires com.techsenger.tabshell.layout;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.swing;
    requires atlantafx.base;
    requires jfx.incubator.richtext;
//    requires org.fxmisc.richtext;
//    requires org.fxmisc.flowless;
//    requires org.fxmisc.undo;
//    requires wellbehavedfx;
//    requires reactfx;
    requires net.sourceforge.plantuml;

    exports com.techsenger.alpha.gui;
//    exports com.techsenger.alpha.console.gui.log;
//    exports com.techsenger.alpha.console.gui.shell;
//    exports com.techsenger.alpha.console.gui.style;
//    exports com.techsenger.alpha.console.gui.utils;

//    opens com.techsenger.alpha.console.gui.diagram to javafx.base;
//    opens com.techsenger.alpha.console.gui.settings to jakarta.xml.bind;
//    opens com.techsenger.alpha.console.gui.style;

//    provides com.techsenger.alpha.spi.console.ConsoleService
//            with com.techsenger.alpha.console.gui.ConsoleProvider;
    provides com.techsenger.alpha.core.spi.module.ModuleActivator
            with com.techsenger.alpha.gui.ModuleActivatorProvider;
}

