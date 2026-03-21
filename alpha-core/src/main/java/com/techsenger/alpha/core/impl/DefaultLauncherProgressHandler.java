package com.techsenger.alpha.core.impl;

///*
// * Copyright 2018-2025 Pavel Castornii.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.techsenger.alpha.core.launcher;
//
//import com.techsenger.alpha.api.Framework;
//import com.techsenger.alpha.api.command.Commands;
//import com.techsenger.alpha.api.executor.CommandExecutionDetails;
//import com.techsenger.alpha.spi.launcher.LauncherProgressHandler;
//import static org.fusesource.jansi.Ansi.Color.BLUE;
//import static org.fusesource.jansi.Ansi.Color.GREEN;
//import static org.fusesource.jansi.Ansi.Color.RED;
//import static org.fusesource.jansi.Ansi.ansi;
//
///**
// *
// * @author Pavel Castornii
// */
//class DefaultLauncherProgressHandler implements LauncherProgressHandler {
//
//    private static final int LINE_WIDTH = 100;
//
//    private int titleLength;
//
//    DefaultLauncherProgressHandler() {
//
//    }
//
//    @Override
//    public void initialize() {
//        printBanner();
//    }
//
//    @Override
//    public void beforeExecution(String scriptName, int level, int levelTotal, CommandExecutionDetails details) {
//        if (level == 0 && details.getIndex() == 0) {
//            System.out.println("Executing the " + scriptName + " script:" + System.lineSeparator());
//        }
//        var title = details.getTitle();
//        title = this.addTabs(title, level);
//        this.titleLength = title.length();
//        System.out.print(title);
//        if (details.getName().equals(Commands.SCRIPT_EXECUTE)) {
//            //terminating line
//            System.out.println(":");
//        }
//    }
//
//    @Override
//    public void afterExecution(String scriptName, int level, int levelTotal, CommandExecutionDetails details) {
//        if (details.getName().equals(Commands.SCRIPT_EXECUTE)) {
//            //terminating line
//            System.out.print(this.addTabs("Executed script", level));
//        }
//        int percents = (int) ((((double) details.getIndex() + 1) / levelTotal) * 100);
//        var result = "";
//        switch (details.getResult().getStatus()) {
//            case SUCCESS:
//                result = ansi().fg(GREEN).a("SUCCESS").reset().toString();
//            break;
//            case SKIPPED:
//                result = ansi().fg(BLUE).a("SKIPPED").reset().toString();
//            break;
//            case FAILURE:
//                result = ansi().fg(RED).a("FAILURE").reset().toString();
//            break;
//            default:
//                throw new AssertionError("Unknown type of command result");
//        }
//        var dots = ".".repeat(LINE_WIDTH - this.titleLength - 15); //15 = 7(result) + 2([]) + 2([]) +4(%)
//            System.out.println(String.format("%s[%s][%3d%%]", dots, result, percents));
//        if (level == 0 && levelTotal - 1 == details.getIndex()) {
//            System.out.println(System.lineSeparator() + "The " + scriptName + " script executed successfully.");
//        }
//    }
//
//    @Override
//    public void onError(String scriptName, int level, int levelTotal, CommandExecutionDetails info) {
//        System.out.println(System.lineSeparator() + "The execution of the " + scriptName + " script failed.");
//    }
//
//    @Override
//    public void deinitialize() {
//
//    }
//
//    private String addTabs(String string, int commandLevel) {
//        var tabs = "";
//        for (var i = 0; i < commandLevel; i++) {
//            tabs += "    ";
//        }
//        return tabs + string;
//    }
//
//    /**
//     * Prints framework banner at start up.
//     */
//    private void printBanner() {
//        System.out.println("");
//        System.out.println("-".repeat(100));
//        var name = "Techsenger Alpha";
//        var version = "v. " +  Framework.getVersion();
//        var spacer = " ".repeat(100 - name.length() - version.length());
//        System.out.println(name + spacer + version);
//        System.out.println("-".repeat(100));
//        System.out.println("");
//    }
//}
