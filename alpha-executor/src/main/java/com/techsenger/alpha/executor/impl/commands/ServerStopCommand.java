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
//import com.techsenger.alpha.api.executor.CommandSkippedException;
//import com.techsenger.alpha.api.message.MessagePrinter;
//import com.techsenger.alpha.api.net.ServerService;
//import com.techsenger.alpha.api.net.session.Protocol;
//import com.techsenger.alpha.spi.command.AbstractCommand;
//import com.techsenger.alpha.spi.command.CommandMeta;
//import com.techsenger.alpha.spi.command.LocalCommand;
//import com.techsenger.alpha.spi.command.ProtocolConverter;
//import com.techsenger.alpha.spi.command.RemoteCommand;
//
///**
// *
// * @author Pavel Castornii
// */
//@LocalCommand
//@RemoteCommand
//@CommandMeta(name = "server:stop", description = "This command is used to stop Alpha server.")
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
