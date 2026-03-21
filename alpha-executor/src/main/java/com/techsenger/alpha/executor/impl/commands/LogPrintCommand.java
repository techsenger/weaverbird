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
//package com.techsenger.alpha.action.commands;
//
//import com.beust.jcommander.Parameter;
//import com.techsenger.alpha.api.Framework;
//import com.techsenger.alpha.api.command.CommandContext;
//import com.techsenger.alpha.api.command.Commands;
//import com.techsenger.alpha.api.message.MessagePrinter;
//import com.techsenger.alpha.spi.command.AbstractCommand;
//import com.techsenger.alpha.spi.command.CommandMeta;
//import com.techsenger.alpha.spi.command.LocalCommand;
//import com.techsenger.alpha.spi.command.RemoteCommand;
//import com.techsenger.toolkit.core.StringUtils;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//import java.util.List;
//
///**
// *
// * @author Pavel Castornii
// */
//@LocalCommand
//@RemoteCommand
//@CommandMeta(name = Commands.LOG_PRINT, description = "Prints the lines of the log.")
//public class LogPrintCommand extends AbstractCommand {
//
//    enum Position {
//        HEAD, TAIL
//    }
//
//    @Parameter(names = {"-p", "--position"}, required = false,
//            description = "sets the start position (head/tail); default is tail")
//    private Position position = Position.TAIL;
//
//    @Parameter(names = {"-o", "--offset"}, required = false, description = "sets the offset in lines; default is 0")
//    private int offset = 0;
//
//    @Parameter(names = {"-l", "--limit"}, required = false, description = "sets the limit of lines; default is 20")
//    private int limit = 20;
//
//    @Override
//    public String getTitle() {
//        return StringUtils.format("Printing {} log events from {} and limit {}", this.limit, this.position,
//                this.offset);
//    }
//
//    @Override
//    public void execute(CommandContext context, MessagePrinter printer) throws Exception {
//        var path = Framework.getPathManager().getLogFilePath();
//        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
//        int startPos = -1;
//        if (position == Position.HEAD) {
//            if (offset < lines.size()) {
//                startPos = offset;
//            }
//        } else {
//            startPos = Math.max(0, lines.size() - this.limit - this.offset);
//        }
//        if (startPos >= 0) {
//            int index = startPos;
//            int counter = this.limit;
//            while (index < lines.size() && counter > 0) {
//                printer.printlnMessage(lines.get(index));
//                index++;
//                counter--;
//            }
//        }
//    }
//}
