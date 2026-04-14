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

package com.techsenger.weaverbird.executor.impl.commands;

import com.beust.jcommander.Parameter;
import com.techsenger.weaverbird.core.api.message.MessagePrinter;
import com.techsenger.weaverbird.executor.api.CommandContext;
import com.techsenger.weaverbird.executor.spi.CommandMeta;
import com.techsenger.weaverbird.executor.spi.LocalCommand;
import com.techsenger.toolkit.core.StringUtils;
import com.techsenger.toolkit.http.exceptions.AuthenticationException;
import com.techsenger.toolkit.http.exceptions.AuthorizationException;
import com.techsenger.toolkit.http.exceptions.VersionMismatchException;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
@LocalCommand
@CommandMeta(name = "session:open",
        description = "Opens a new session with the specified name and attaches to it.")
public class SessionOpenCommand extends AbstractSessionNameCommand {

    private static final Logger logger = LoggerFactory.getLogger(SessionOpenCommand.class);

    @Parameter(names = {"-a", "--address"}, required = true, description = "sets the address of the server host:port")
    private String address;

    @Parameter(names = {"-n", "--login-name"}, required = false, description = "sets the login name")
    private String loginName;

    @Parameter(names = {"-p", "--login-password"}, required = false, description = "sets the login password")
    private String loginPassword;

    @Override
    public String getTitle() {
        return StringUtils.format("Opening the session '{}' to {}", getName(), this.address);
    }

    @Override
    public void execute(CommandContext context, MessagePrinter printer) throws Exception {
        if (this.loginName == null) {
            this.loginName = context.getParameterProvider().provide("Enter login name: ", true);
        }
        if (this.loginPassword == null) {
            this.loginPassword = context.getParameterProvider().provide("Enter login password: ", true);
        }
        String[] addresses = this.address.split(Pattern.quote(":"));
        try {
            var session = context.getClient().openSession(getName(), addresses[0], Integer.valueOf(addresses[1]),
                    loginName, loginPassword, context.getFramework().getVersion());
            printer.printlnMessage("Opened the session '" + getName() + "'");
            attachTo(session, context, printer);
        } catch (VersionMismatchException ex) {
            printer.printlnError("Couldn't open a new session to " + this.address
                    + ". Client version isn't compatible with server version");
        } catch (AuthenticationException ex) {
            printer.printlnError("Couldn't open a new session to " + this.address
                    + ". Login name/password pair is incorrect");
        } catch (AuthorizationException ex) {
            printer.printlnError("Couldn't open a new session to " + this.address
                    + ". You don't have rights to do it");
        } catch (Exception ex) {
            logger.error("Couldn't open a new session", ex);
        }
    }
}
