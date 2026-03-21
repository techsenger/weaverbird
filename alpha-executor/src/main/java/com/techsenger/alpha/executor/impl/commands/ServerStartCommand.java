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
//import com.techsenger.alpha.api.net.session.Protocol;
//import com.techsenger.alpha.spi.command.AbstractCommand;
//import com.techsenger.alpha.spi.command.CommandMeta;
//import com.techsenger.alpha.spi.command.LocalCommand;
//import com.techsenger.alpha.spi.command.ProtocolConverter;
//import com.techsenger.alpha.spi.command.RemoteCommand;
//import com.techsenger.toolkit.core.StringUtils;
//
///**
// *
// * @author Pavel Castornii
// */
//@LocalCommand
//@RemoteCommand
//@CommandMeta(name = "server:start", description = "This command is used to start Alpha server.")
//public class ServerStartCommand extends AbstractCommand {
//
//    @Parameter(names = {"-h", "--host"}, required = true, description = "sets the host of the server")
//    private String host;
//
//    @Parameter(names = {"-p", "--port"}, required = true, description = "sets the port of the server")
//    private Integer port;
//
//    @Parameter(names = {"-r", "--protocol"}, required = false, converter = ProtocolConverter.class,
//            description = "sets the protocol(HTTP/RMI), default is HTTP")
//    private Protocol protocol = Protocol.HTTP;
//
//    @Parameter(names = {"-s", "--secure"}, required = false,
//            description = "flag indicating if connections are secure; default is false")
//    private boolean secure = false;
//
//    @Override
//    public String getTitle() {
//        return StringUtils.format("Starting {} server on {}:{}", this.protocol, this.host, this.port);
//    }
//
//    @Override
//    public void execute(CommandContext context, MessagePrinter printer) throws Exception {
//        var server = Framework.getServiceManager().getServer(protocol);
//        if (server != null) {
//            if (server.isRunning()) {
//                throw new CommandSkippedException(this.protocol + " server is already running");
//            } else {
//                server.start(host, port, secure);
//                printer.printlnMessage(this.protocol + " server started on "
//                        + this.host + ":" + this.port + ", with secure=" + this.secure);
//            }
//        } else {
//            throw new Exception(this.protocol + " server not found");
//        }
//    }
//}
