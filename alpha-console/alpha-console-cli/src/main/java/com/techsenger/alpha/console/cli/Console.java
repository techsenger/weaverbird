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

package com.techsenger.alpha.console.cli;

import com.techsenger.alpha.core.api.Framework;
import com.techsenger.alpha.executor.api.CommandExecutor;
import com.techsenger.alpha.executor.api.CommandExecutorFactory;
import com.techsenger.alpha.executor.api.ParameterProvider;
import com.techsenger.alpha.net.client.api.ClientSession;
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

    private static final Logger logger = LoggerFactory.getLogger(Console.class);

    private static final String PROMPT = "> ";

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

    /**
     * Indicates if console is stopping.
     */
    private volatile boolean closing = false;

    /**
     * Constructor.
     */
    public Console(Framework framework) throws Exception {
        //here we need class loader of the parent (alpha-control) layer
        this.commandExecutor = CommandExecutorFactory.create(framework);
        var commandContext = this.commandExecutor.getCommandContext();
        this.printer = new SystemMessagePrinter();
        this.terminal = TerminalBuilder.builder()
                // .system(true)
                .ffm(true) // enable foreign function and memory api
                .build();
        this.lineReader = LineReaderBuilder.builder()
                .terminal(terminal)
                .completer(new ConsoleCompleter(commandExecutor.getCommandsByName(), commandContext))
                .highlighter(new ConsoleHighlighter(commandExecutor.getCommandsByName()))
                // disable removing ! prefix. see line 3027 in org/jline/reader/impl/LineReaderImpl.java
                .option(LineReader.Option.DISABLE_EVENT_EXPANSION, true)
                // .option(LineReader.Option.AUTO_FRESH_LINE, true)
                .option(LineReader.Option.HISTORY_BEEP, false)
                .option(LineReader.Option.AUTO_LIST, true) // Automatically list options
                .option(LineReader.Option.LIST_PACKED, true) // Display completions in a compact form
                .option(LineReader.Option.AUTO_MENU, true) // Show menu automatically
                .option(LineReader.Option.MENU_COMPLETE, true)
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
                while (true) {
                    var session = commandContext.getSession();
                    if (session != null) {
                        this.sessionPrompt = buildSessionPrompt(session);
                    } else {
                        this.sessionPrompt = null;
                    }
                    if (sessionPrompt == null) {
                        commandLine = lineReader.readLine(PROMPT);
                    } else {
                        commandLine = lineReader.readLine(sessionPrompt);
                    }
                    printer.setWidth(this.terminal.getWidth());
                    if (commandLine != null) {
                        commandLine = commandLine.trim();
                        try {
                            var results = this.commandExecutor.executeCommands(commandLine, null,
                                      null, printer.getWidth());
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
                // this exception is thrown when this thread is interrupted by another thread
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

    private String buildSessionPrompt(ClientSession session) {
        var sb = new StringBuilder();
        sb.append(session.getLoginName());
        sb.append("@");
        sb.append(session.getHost());
        sb.append(PROMPT);
        return sb.toString();
    }
}
