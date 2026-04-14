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
//import com.beust.jcommander.Parameter;
//import com.techsenger.weaverbird.api.Framework;
//import com.techsenger.weaverbird.api.command.CommandContext;
//import com.techsenger.weaverbird.api.executor.CommandSkippedException;
//import com.techsenger.weaverbird.api.message.MessagePrinter;
//import com.techsenger.weaverbird.api.net.ServerService;
//import com.techsenger.weaverbird.api.net.session.Protocol;
//import com.techsenger.weaverbird.spi.command.AbstractCommand;
//import com.techsenger.weaverbird.spi.command.CommandMeta;
//import com.techsenger.weaverbird.spi.command.LocalCommand;
//import com.techsenger.weaverbird.spi.command.ProtocolConverter;
//import com.techsenger.weaverbird.spi.command.RemoteCommand;
//
///**
// *
// * @author Pavel Castornii
// */
//@LocalCommand
//@RemoteCommand
//@CommandMeta(name = "server:stop", description = "This command is used to stop Weaverbird server.")
//public class ServerStopCommand extends AbstractCommand {
//
//    @Parameter(names = {"-r", "--protocol"}, required = false, converter = ProtocolConverter.class,
//            description = "sets the protocol(HTTP/RMI), default is HTTP")
//    private Protocol protocol = Protocol.HTTP;
//
//    @Override
//    public String getTitle() {
//        return "Stopping " + this.protocol + " server";
//    }
//
//    @Override
//    public void execute(CommandContext context, MessagePrinter printer) throws Exception {
//        ServerService server = Framework.getServiceManager().getServer(protocol);
//        if (server == null) {
//            new Exception(this.protocol + " server not found");
//        } else {
//            if (server.isRunning()) {
//                server.stop();
//                printer.printlnMessage(this.protocol + " server stopped");
//            } else {
//                new CommandSkippedException(this.protocol + " server is not running");
//            }
//        }
//    }
//
//}
