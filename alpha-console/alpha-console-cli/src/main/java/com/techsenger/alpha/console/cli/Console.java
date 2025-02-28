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

package com.techsenger.alpha.console.cli;

import com.techsenger.alpha.api.Framework;
import com.techsenger.alpha.api.executor.CommandExecutor;
import com.techsenger.alpha.api.executor.ParameterProvider;
import com.techsenger.alpha.api.net.session.SessionDescriptor;
import com.techsenger.alpha.spi.command.ContextParameter;
import com.techsenger.alpha.spi.console.ConsoleService;
import com.techsenger.alpha.spi.console.DefaultCommandInfosManager;
import java.io.IOException;
import java.util.Objects;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
public class Console {

    private static final Logger logger = LoggerFactory.getLogger(ConsoleProvider.class);

    private final String prompt;

    private volatile String sessionPrompt;

    /**
     * Command executor that will execute commands from this console.
     */
    private final CommandExecutor commandExecutor;

    /**
     * Message printer.
     */
    private final SystemMessagePrinter printer;

    /**
     * Terminal.
     */
    private final Terminal terminal;

    /**
     * Console reader.
     */
    private final LineReader lineReader;

    /**
     * Loop thread that reads commands from console and sends.
     */
    private final Thread loopThread;

    private final ConsoleHighlighter highlighter;

    /**
     * Indicates if console is stopping.
     */
    private volatile boolean closing = false;

    private final DefaultCommandInfosManager commandInfosManager;

    /**
     * Constructor.
     */
    public Console() throws IOException {
        this.prompt = buildPrompt();
        //here we need class loader of the parent (alpha-control) layer
        this.commandExecutor = Framework.getServiceManager().getCommandExecutor();
        this.commandInfosManager = new DefaultCommandInfosManager(commandExecutor);
        this.printer = new SystemMessagePrinter();
        this.terminal = TerminalBuilder.builder()
                //.ffm(false) //disable foreign function and memory api, jline 3.28
                .build();
        this.highlighter = new ConsoleHighlighter(this.commandInfosManager);
        this.lineReader = LineReaderBuilder.builder()
                .terminal(terminal)
                .completer(new ConsoleCompleter(commandInfosManager))
                .highlighter(highlighter)
                //disable removing ! prefix. see line 3027 in org/jline/reader/impl/LineReaderImpl.java
                .option(LineReader.Option.DISABLE_EVENT_EXPANSION, true)
                .build();

        this.loopThread = new Thread(() -> {
            try {
                String commandLine = null;
                SystemMessagePrinter printer = new SystemMessagePrinter();
                ParameterProvider provider = (String description, boolean secured) -> {
                    if (secured) {
                        return this.lineReader.readLine(description, ' ');
                    } else {
                        return this.lineReader.readLine(description);
                    }
                };
                var commandContext = this.commandExecutor.createContext(provider);
                while (true) {
                    var session = (SessionDescriptor) commandContext
                            .getProperty(ContextParameter.SESSION_DESCRIPTOR);
                    if (session != null) {
                        if (!Objects.equals(this.commandInfosManager.getSessionDescriptor(), session)) {
                            this.sessionPrompt = buildSessionPrompt(session);
                            this.commandInfosManager.setRemoteInfos(Framework.getServiceManager()
                                    .getClient(session.getProtocol()).getCommandInfos(session.getName()));
                            this.commandInfosManager.setSessionDescriptor(session);
                        }
                    } else {
                        this.sessionPrompt = null;
                        this.commandInfosManager.setSessionDescriptor(null);
                        this.commandInfosManager.setRemoteInfos(null);
                    }
                    if (sessionPrompt == null) {
                        commandLine = lineReader.readLine(prompt);
                    } else {
                        commandLine = lineReader.readLine(sessionPrompt);
                    }
                    printer.setWidth(this.terminal.getWidth());
                    if (commandLine != null) {
                        commandLine = commandLine.trim();
                        try {
                            var results = this.commandExecutor.executeCommands(commandLine, commandContext, null, null,
                                    printer.getWidth());
                            results.forEach(r -> printer.print(r.getMessages()));
                        } catch (Exception ex) {
                            logger.error("Error executing commands from CLI console", ex);
                            printer.printlnError(ex.getMessage());
                            if (ex.getCause() != null) {
                                printer.printlnError(ex.getCause().getMessage());
                            }
                            printer.printlnMessage("Enter \"command:list\" to get a list of all commands.");
                            printer.printlnMessage("Enter \"command -?\" to get help on a specific command.");
                        }
                        if (this.closing) {
                            break;
                        }
                    }
                }
                close();
            } catch (UserInterruptException ex) {
                //this exception is thrown when this thread is interrupted by another thread
            } catch (Exception ex) {
                logger.error("There was an error ", ex);
            }
        });
    }

    void open() throws Exception {
        this.loopThread.start();
    }

    /**
     * We need extra method, because {@code this.run -> executeCommands -> command.execute -> this.close()}.
     */
    void close() {
        try {
            this.terminal.close();
            logger.debug("Terminal was closed");
        } catch (Exception ex) {
            logger.error("Error closing terminal", ex);
        }
        this.commandInfosManager.deinitialize();
        logger.debug("Console was closed");
    }

    Thread getLoopThread() {
        return loopThread;
    }

    boolean isClosing() {
        return closing;
    }

    void setClosing(boolean closing) {
        this.closing = closing;
    }

    private String buildPrompt() {
        return ConsoleService.PROMPT;
    }

    private String buildSessionPrompt(SessionDescriptor sessionDescriptor) {
        var sb = new StringBuilder();
        sb.append(sessionDescriptor.getLoginName());
        sb.append("@");
        sb.append(sessionDescriptor.getHost());
        sb.append(ConsoleService.PROMPT);
        return sb.toString();
    }
}
