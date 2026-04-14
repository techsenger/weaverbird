package com.techsenger.weaverbird.executor.impl.commands;

///*
// * Copyright 2018-2026 Pavel Castornii.
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
//package com.techsenger.weaverbird.action.commands;
//
//import com.beust.jcommander.IStringConverter;
//import com.beust.jcommander.Parameter;
//import com.techsenger.weaverbird.api.command.CommandContext;
//import com.techsenger.weaverbird.api.message.MessagePrinter;
//import com.techsenger.weaverbird.spi.command.AbstractCommand;
//import com.techsenger.weaverbird.spi.command.CommandMeta;
//import com.techsenger.weaverbird.spi.command.LocalCommand;
//import com.techsenger.weaverbird.spi.command.RemoteCommand;
//import com.techsenger.toolkit.core.StringUtils;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.core.LoggerContext;
//import org.apache.logging.log4j.core.config.Configuration;
//import org.apache.logging.log4j.core.config.LoggerConfig;
//
///**
// *
// * @author Pavel Castornii
// */
//@LocalCommand
//@RemoteCommand
//@CommandMeta(name = "logger:configure", description = "Configures named or root logger (log4j2)")
//public class LoggerConfigureCommand extends AbstractCommand {
//
//    private static final class LevelConverter implements IStringConverter<org.apache.logging.log4j.Level> {
//
//        @Override
//        public org.apache.logging.log4j.Level convert(String string) {
//            return org.apache.logging.log4j.Level.valueOf(string);
//        }
//
//    }
//
//    @Parameter(names = {"-n", "--name"}, required = true,
//            description = "sets the name of the logger, for root logger use 'root'")
//    private String name;
//
//    @Parameter(names = {"-l", "--level"}, required = true, description = "sets logger level",
//            converter = LoggerConfigureCommand.LevelConverter.class)
//    private org.apache.logging.log4j.Level level;
//
//    @Override
//    public String getTitle() {
//        return StringUtils.format("Configuring logger {} to use level {}", this.name, this.level);
//    }
//
//    @Override
//    public void execute(CommandContext context, MessagePrinter printer) throws Exception {
//        LoggerContext loggerContext = (LoggerContext) LogManager.getContext(false);
//        Configuration config = loggerContext.getConfiguration();
//        LoggerConfig loggerConfig;
//        if (name.toLowerCase().equals("root")) {
//            loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
//        } else {
//            loggerConfig = config.getLoggerConfig(name);
//        }
//        loggerConfig.setLevel(level);
//        // This causes all Loggers to refetch information from their LoggerConfig
//        loggerContext.updateLoggers();
//        printer.printlnMessage("Logger was configured");
//    }
//
//}
