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

package com.techsenger.alpha.gui.console;

import com.techsenger.alpha.gui.style.ConsoleIcons;
import com.techsenger.alpha.core.api.Framework;
import com.techsenger.alpha.core.api.message.DefaultMessage;
import com.techsenger.alpha.core.api.message.Message;
import com.techsenger.alpha.core.api.message.MessageType;
import com.techsenger.alpha.executor.api.CommandExecutor;
import com.techsenger.alpha.executor.api.CommandExecutorFactory;
import com.techsenger.alpha.net.client.api.ClientSession;
import com.techsenger.patternfx.mvp.Descriptor;
import com.techsenger.tabshell.core.CloseCheckResult;
import com.techsenger.tabshell.core.ClosePreparationResult;
import com.techsenger.tabshell.core.settings.SettingsSubscription;
import com.techsenger.tabshell.core.tab.AbstractTabPresenter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.techsenger.alpha.gui.AlphaComponents;

/**
 *
 * @author Pavel Castornii
 */
public class ConsoleTabPresenter<V extends ConsoleTabView, C extends ConsoleTabComposer>
        extends AbstractTabPresenter<V, C> implements PopupOwnerPort {

    private static final Logger logger = LoggerFactory.getLogger(ConsoleTabPresenter.class);

    private static final String PROMPT = "> ";

    private volatile String sessionPrompt;

    private boolean highlighterEnabled;

    /**
     * Command executor that will execute commands from this console.
     */
    private final CommandExecutor executor;

    private SettingsSubscription fontSubscription;

    public ConsoleTabPresenter(V view, Framework framework) {
        super(view);
        CommandExecutor ex = null;
        try {
            ex = CommandExecutorFactory.create(framework);
        } catch (Exception e) {
            logger.error("{} Error creating executor", getDescriptor().getLogPrefix(), e);
        }
        this.executor = ex;
    }

    @Override
    protected Descriptor createDescriptor() {
        return new Descriptor(AlphaComponents.CONSOLE_TAB);
    }

    @Override
    public CloseCheckResult isReadyToClose() {
        return CloseCheckResult.READY;
    }

    @Override
    public void prepareToClose(Consumer<ClosePreparationResult> resultCallback) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getSessionPrompt() {
        return sessionPrompt;
    }

    public boolean isHighlighterEnabled() {
        return highlighterEnabled;
    }

    @Override
    public void onAttributeSubmitted(AttributePopupType type, String attribute) {
        getView().apppendText(attribute + " ");
        getComposer().removePopup();
        getView().requestFocus();
    }

    @Override
    public void onPopupClose() {
        getComposer().removePopup();
        getView().requestFocus();
    }

    @Override
    protected void postInitialize() {
        super.postInitialize();
        setTitle("Console");
        setIcon(ConsoleIcons.CONSOLE);
        showPrompt();
        getView().requestFocus();
        var settings = getShell().getContext().getSettings().getAppearance();
        getView().setMonospaceFont(settings.getMonospaceFont());
        this.fontSubscription = settings.onMonospaceFontChanged((oldV, newV) -> getView().setMonospaceFont(newV));
    }

    @Override
    protected void preDeinitialize() {
        super.preDeinitialize();
        this.fontSubscription.unsubscribe();
    }

    protected void showPrompt() {
        getView().printPrompt(getCurrentPrompt());
    }

    protected void onAutocompleteRequest() {
        getComposer().addCommandPopup(executor.getCommandsByName());
    }

    protected void onCommandsSubmitted(List<String> paragraphs, int width) {
        setHighlighterEnabled(false);
//        this.commandScrollingIndex = -1;
        if (paragraphs.isEmpty()) {
            this.showPrompt();
            return;
        }
        var commands = String.join("\n", paragraphs);
        // removing prompt
        commands = commands.substring(getCurrentPrompt().length());
        // text area will add \n after enter, so, remove it
        // commands = commands.substring(0, commands.length() - "\n".length()).trim();
//        if (saveToHistory) {
//            lastCommands.add(commands);
//        }
        var service = new CommandExecuteService(this.executor, commands, width);
        service.stateProperty().addListener((ov, oldV, newV) -> {
            if (newV == Worker.State.SUCCEEDED || newV == Worker.State.FAILED || newV == Worker.State.CANCELLED) {
                if (newV == Worker.State.FAILED) {
                    Platform.runLater(() -> {
                        logger.error("Error executing commands from GUI console", service.getException());
                        var ex = service.getException();
                        List<Message> messages = new ArrayList<>();

                        if (ex != null) {
                            messages.add(new DefaultMessage(MessageType.ERROR, ex.getMessage()));
                            if (ex.getCause() != null) {
                                messages.add(new DefaultMessage(MessageType.ERROR, ex.getCause().getMessage()));
                            }
                        }
                        messages.add(new DefaultMessage(MessageType.OUTPUT,
                                "Enter \"command:list\" to get a list of all commands."));
                        messages.add(new DefaultMessage(MessageType.OUTPUT,
                                "Enter \"command -?\" to get help on a specific command."));
                        getView().printMessages(messages);
                        this.showPrompt();
                        this.setHighlighterEnabled(true);
                    });
                } else {
//                    if (Framework.getMode() == FrameworkMode.CLIENT) {
//                        var sesDescriptor = (SessionDescriptor) commandContext
//                                .getProperty(ContextParameter.SESSION_DESCRIPTOR);
//                        if (sesDescriptor != null) {
//                            this.sessionPrompt = sesDescriptor.getLoginName() + "@" + sesDescriptor.getHost();
//                            updateRemoteCommandInfos(service.getValue());
//                            this.commandInfosManager.setSessionDescriptor(sesDescriptor);
//                            syncSessionBarAndContext(sesDescriptor);
//                        } else {
//                            this.sessionPrompt = null;
//                            this.commandInfosManager.setSessionDescriptor(null);
//                            syncSessionBarAndContext(null);
//                        }
//                    }
                    Platform.runLater(() -> {
                        service.getValue().forEach(r -> {
//                            if (r.getCommandName().equals(Commands.LOG_PRINT)) {
//                                this.logMessages.next(r.getMessages());
//                            } else {
                                getView().printMessages(r.getMessages());
//                            }
                        });
                        this.showPrompt();
                        this.setHighlighterEnabled(true);
                    });
                }
            }
        });
        service.start();
    }


    protected void setSessionPrompt(String sessionPrompt) {
        this.sessionPrompt = sessionPrompt;
    }

    protected void setHighlighterEnabled(boolean highlighterEnabled) {
        this.highlighterEnabled = highlighterEnabled;
        getView().setHighlighterEnabled(highlighterEnabled);
    }

    private String buildSessionPrompt(ClientSession session) {
        var sb = new StringBuilder();
        sb.append(session.getLoginName());
        sb.append("@");
        sb.append(session.getHost());
        sb.append(PROMPT);
        return sb.toString();
    }

    private String getCurrentPrompt() {
        if (this.sessionPrompt != null) {
            return this.sessionPrompt;
        } else {
            return PROMPT;
        }
    }



}
