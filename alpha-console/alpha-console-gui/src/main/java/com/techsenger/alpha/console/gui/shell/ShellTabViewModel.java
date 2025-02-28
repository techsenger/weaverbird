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

package com.techsenger.alpha.console.gui.shell;

import com.techsenger.alpha.api.Framework;
import com.techsenger.alpha.api.FrameworkMode;
import com.techsenger.alpha.api.command.CommandContext;
import com.techsenger.alpha.api.command.CommandInfo;
import com.techsenger.alpha.api.command.CommandResult;
import com.techsenger.alpha.api.command.Commands;
import com.techsenger.alpha.api.executor.CommandExecutor;
import com.techsenger.alpha.api.executor.CommandInfos;
import com.techsenger.alpha.api.executor.CommandSpecialSymbols;
import com.techsenger.alpha.api.message.DefaultMessage;
import com.techsenger.alpha.api.message.Message;
import com.techsenger.alpha.api.message.MessageType;
import com.techsenger.alpha.api.net.session.SessionDescriptor;
import com.techsenger.alpha.console.gui.file.SupportedFileType;
import com.techsenger.alpha.console.gui.keys.ConsoleComponentKeys;
import com.techsenger.alpha.console.gui.session.SessionsToolBarViewModel;
import com.techsenger.alpha.console.gui.style.ConsoleIcons;
import com.techsenger.alpha.console.gui.textstyle.StyledText;
import com.techsenger.alpha.spi.command.ContextParameter;
import com.techsenger.alpha.spi.console.ConsoleService;
import com.techsenger.alpha.spi.console.DefaultCommandInfosManager;
import com.techsenger.mvvm4fx.core.HistoryPolicy;
import com.techsenger.tabshell.core.TabShellViewModel;
import com.techsenger.tabshell.core.menu.SimpleMenuItemHelper;
import com.techsenger.tabshell.core.tab.ShellTabKey;
import com.techsenger.tabshell.kit.core.file.FileInfo;
import com.techsenger.tabshell.kit.core.file.LocalTextFileTaskProvider;
import com.techsenger.tabshell.kit.text.menu.EditMenuKeys;
import com.techsenger.tabshell.kit.text.viewer.AbstractViewerTabViewModel;
import com.techsenger.tabshell.material.icon.FontIcon;
import com.techsenger.toolkit.fx.value.ObservableSource;
import com.techsenger.toolkit.fx.value.SimpleObservableSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Worker;
import javafx.scene.input.Clipboard;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
public class ShellTabViewModel extends AbstractViewerTabViewModel {

    /**
     * Logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(ShellTabViewModel.class);

    /**
     * Command executor that will execute commands from this console.
     */
    private final CommandExecutor executor;

    /**
     * The history of the commands.
     */
    private final List<String> lastCommands = new CopyOnWriteArrayList<>();

    /**
     * Prompt texts that must be printed by view.
     */
    private final ObservableSource<List<StyledText>> promptTexts = new SimpleObservableSource<>();

    private final ObservableSource<List<Message>> messages = new SimpleObservableSource<>();

    private final ObservableSource<List<Message>> logMessages = new SimpleObservableSource<>();

    /**
     * Other texts. We separate prompt from other because they are printed differently.
     */
    private final ObservableSource<List<StyledText>> texts = new SimpleObservableSource<>();

    private AttributeWindowViewModel attributeWindow;

    private final Clipboard clipboard = Clipboard.getSystemClipboard();

    private final CommandContext commandContext;

    private final DefaultCommandInfosManager commandInfosManager =
            new DefaultCommandInfosManager(Framework.getServiceManager().getCommandExecutor());

    /**
     * Text listener that is attached when the modal window is on.
     */
    private ChangeListener<String> textListener;

    private final SessionsToolBarViewModel sessions;

    private final IntegerProperty outputWidth = new SimpleIntegerProperty();

    /**
     * The position in text area text before which all text is not modifiable and blocked.
     */
    private volatile int promptPosition;

    private volatile String sessionPrompt;

    private volatile boolean sessionListenerEnabled = true;

    /**
     * Keeps the index of the last histry scrolling command. It is reset after every command.
     */
    private volatile int commandScrollingIndex = -1;

    /**
     * Highlighter enabled.
     */
    private boolean highlighterEnabled = true;

    /**
     * Constructor.
     */
    public ShellTabViewModel(TabShellViewModel tabShell) {
        super(tabShell, new FileInfo(null, null, null, null), new LocalTextFileTaskProvider());
        this.setTitle("Shell");
        this.setIcon(new FontIcon(ConsoleIcons.SHELL));
        setHistoryPolicy(HistoryPolicy.APPEARANCE);
        setHistoryProvider(() -> tabShell.getHistoryManager().getHistory(ShellTabHistory.class, ShellTabHistory::new));
        this.contentModifiedProperty().addListener((ov, oldValue, newValue) -> {
            if (this.getFileInfo().getPath() == null) {
                return;
            }
            this.titleProperty().set(this.resolveTabTitle(this.titleProperty().get()));
        });
        //here we need class loader of the parent (alpha-control) layer
        this.executor = Framework.getServiceManager().getCommandExecutor();
        this.commandContext = Framework.getServiceManager().getCommandExecutor().createContext(null);
        if (Framework.getMode() == FrameworkMode.CLIENT) {
            sessions = new SessionsToolBarViewModel();
            sessions.sessionProperty().addListener((ov, oldV, newV) -> {
                if (this.sessionListenerEnabled) {
                    this.texts.next(List.of(new StyledText("base", "\n")));
                    if (newV == null) {
                        executeCommand(CommandSpecialSymbols.LOCAL_COMMAND + Commands.SESSION_DETACH
                                + "\n", false);
                    } else {
                        executeCommand(CommandSpecialSymbols.LOCAL_COMMAND + Commands.SESSION_ATTACH + " "
                                + newV.getName() + "\n", false);
                    }
                }
            });
        } else {
            sessions = null;
        }
        addMenuItemHelpers(
            new SimpleMenuItemHelper(EditMenuKeys.CUT) {
                @Override
                public Boolean getItemValid() {
                    return isCutItemValid();
                }

            },
            new SimpleMenuItemHelper(EditMenuKeys.PASTE) {
                @Override
                public Boolean getItemValid() {
                    return isPasteItemValid();
                }
            }
        );
    }

    @Override
    public ShellTabKey getKey() {
        return ConsoleComponentKeys.SHELL_TAB;
    }

    @Override
    public List<FileChooser.ExtensionFilter> getExtensionFilters() {
        return List.of(SupportedFileType.TEXT.getExtensionFilter());
    }

    @Override
    public String resolveDefaultExtension(FileChooser.ExtensionFilter filter) {
        return SupportedFileType.TEXT.getExtension();
    }

    @Override
    public String getDefaultExtension() {
        return SupportedFileType.TEXT.getExtension();
    }

    @Override
    public ShellTabHelper<?> getComponentHelper() {
        return (ShellTabHelper) super.getComponentHelper();
    }

    protected int getPromptPosition() {
        return promptPosition;
    }

    protected void setPromptPosition(int promptPosition) {
        this.promptPosition = promptPosition;
    }

    protected boolean isHighlighterEnabled() {
        return highlighterEnabled;
    }

    protected void setHighlighterEnabled(boolean highlighterEnabled) {
        this.highlighterEnabled = highlighterEnabled;
    }

    protected ObservableSource<List<StyledText>> getTexts() {
        return texts;
    }

    protected ObservableSource<List<StyledText>> getPromptTexts() {
        return promptTexts;
    }

    protected int getCommandScrollingIndex() {
        return commandScrollingIndex;
    }

    protected void setCommandScrollingIndex(int commandScrollingIndex) {
        this.commandScrollingIndex = commandScrollingIndex;
    }

    protected List<String> getLastCommands() {
        return lastCommands;
    }

    protected void showPrompt() {
        var newTexts = new ArrayList<StyledText>();
        if (this.sessionPrompt != null) {
            newTexts.add(new StyledText("base", sessionPrompt));
            newTexts.add(new StyledText("prompt", ConsoleService.PROMPT));
        } else {
            newTexts.add(new StyledText("prompt", ConsoleService.PROMPT));
        }
        this.promptTexts.next(newTexts);
    }

    protected void executeCommand(String commands, boolean saveToHistory) {
        this.setHighlighterEnabled(false);
        this.commandScrollingIndex = -1;
        if (commands == null) {
            this.showPrompt();
            return;
        }
        //text area will add \n after enter, so, remove it
        commands = commands.substring(0, commands.length() - "\n".length()).trim();
        if (saveToHistory) {
            lastCommands.add(commands);
        }
        var service = new CommandExecuteService(this.executor, this.commandContext, commands, outputWidth.get());
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
                        this.messages.next(messages);
                        this.showPrompt();
                        this.setHighlighterEnabled(true);
                    });
                } else {
                    if (Framework.getMode() == FrameworkMode.CLIENT) {
                        var sesDescriptor = (SessionDescriptor) commandContext
                                .getProperty(ContextParameter.SESSION_DESCRIPTOR);
                        if (sesDescriptor != null) {
                            this.sessionPrompt = sesDescriptor.getLoginName() + "@" + sesDescriptor.getHost();
                            updateRemoteCommandInfos(service.getValue());
                            this.commandInfosManager.setSessionDescriptor(sesDescriptor);
                            syncSessionBarAndContext(sesDescriptor);
                        } else {
                            this.sessionPrompt = null;
                            this.commandInfosManager.setSessionDescriptor(null);
                            syncSessionBarAndContext(null);
                        }
                    }
                    Platform.runLater(() -> {
                        service.getValue().forEach(r -> {
                            if (r.getCommandName().equals(Commands.LOG_PRINT)) {
                                this.logMessages.next(r.getMessages());
                            } else {
                                this.messages.next(r.getMessages());
                            }
                        });
                        this.showPrompt();
                        this.setHighlighterEnabled(true);
                    });
                }
            }
        });
        submitWorker(service);
    }

    protected void doOnListViewSelected() {
        this.appendAttributeFromWindow();
    }

    protected int scrollHistoryUp() {
        int index = 0;
        if (this.commandScrollingIndex == -1) {
            index = this.lastCommands.size() - 1;
        } else {
            index = this.commandScrollingIndex - 1;
        }
        return index;
    }

    protected int scrollHistoryDown() {
        int index = 0;
        if (this.commandScrollingIndex == -1) {
            index = -1;
        } else {
            index = this.commandScrollingIndex + 1;
        }
        return index;
    }

    protected boolean isCutItemValid() {
        var selection = selectionProperty().get();
        if (selection != null && selection.getStart() >= promptPosition && selection.getEnd()
                >= promptPosition && selection.getStart() != selection.getEnd()) {
            return true;
        }
        return false;
    }

    protected boolean isPasteItemValid() {
        if (caretPositionProperty().get() >= promptPosition && clipboard.getString() != null) {
            return true;
        }
        return false;
    }

    SessionsToolBarViewModel getSessions() {
        return sessions;
    }

    ObservableSource<List<Message>> getMessages() {
        return messages;
    }

    ObservableSource<List<Message>> getLogMessages() {
        return logMessages;
    }

    IntegerProperty outputWidthProperty() {
        return outputWidth;
    }

    void executeCommands() {
        var commands = this.getUnsubmittedCommands(textProperty().get());
        executeCommand(commands, true);
    }

    int getPromptLength() {
        if (this.sessionPrompt == null) {
            return 2;
        } else {
            return this.sessionPrompt.length() + 2;
        }
    }

    void openAttributeWindow() {
        var text = textProperty().get();
        var commands = getUnsubmittedCommands(text);
        if (commands != null) {
            if (!commands.contains(" ")) {
                this.openCommandWindow();
                commands = removeCommandPrefix(commands);
                this.attributeWindow.updateData(commands);
            } else {
                var splits = commands.split(Pattern.quote(" "));
                this.openParameterWindow(splits[0]);
                //if parameters were fount for such command
                if (this.attributeWindow != null) {
                    String enteredParameter = getLastParameter(text);
                    this.attributeWindow.updateData(enteredParameter);
                }
            }
        } else {
            this.openCommandWindow();
            this.attributeWindow.updateData(null);
        }
    }

    void closeAttributeWindow() {
        if (this.attributeWindow == null) {
            return;
        }
        getComponentHelper().closeAttributeWindow();
        textProperty().removeListener(textListener);
        this.attributeWindow = null;
    }

    void setAttributeWindow(AttributeWindowViewModel window) {
        this.attributeWindow = window;
    }

    public DefaultCommandInfosManager getCommandInfosManager() {
        return commandInfosManager;
    }

    private void syncSessionBarAndContext(SessionDescriptor sessionDescriptor) {
        if (sessionDescriptor != null) {
            var displayedSes = this.sessions.sessionProperty().get();
            this.sessionListenerEnabled = false;
            if (displayedSes == null) {
                this.sessions.select(sessionDescriptor);
            } else {
                if (!displayedSes.equals(sessionDescriptor)) {
                    this.sessions.select(sessionDescriptor);
                }
            }
            this.sessionListenerEnabled = true;
        } else {
            this.sessionListenerEnabled = false;
            this.sessions.sessionProperty().set(null);
            this.sessionListenerEnabled = true;
        }
    }

    private String getLastParameter(String text) {
        text = text.substring(promptPosition);
        if (text.endsWith(" ")) {
            return null;
        }
        var splits = text.split(Pattern.quote(" "));
        var lastParam = splits[splits.length - 1];
        if (lastParam.startsWith("-")) {
            return lastParam;
        } else {
            return null;
        }
    }

    private String getUnsubmittedCommands(String text) {
        var tempCommands = text.substring(promptPosition);
        if (tempCommands.trim().isEmpty()) {
            return null;
        }
        return tempCommands;
    }

    private void appendAttributeFromWindow() {
        this.attributeWindow.getSelectedAttribute();
        var diff = 0;
        var enteredAttribute = this.attributeWindow.getEnteredAttribute();
        if (enteredAttribute != null) {
            diff = enteredAttribute.length();
        }
        var selectedAttribute = this.attributeWindow.getSelectedAttribute().substring(diff);
        //and only now we can append text and empty string at the end.
        var texts = new ArrayList<StyledText>();
        texts.add(new StyledText(null, selectedAttribute + " "));
        this.texts.next(texts);
    }

    private void openCommandWindow() {
        if (this.attributeWindow != null) {
            return;
        }
        var eol = "\n" + "\n";
        var commandInfos = provideCommandInfos(null);
        Map<String, List<StyledText>> descriptionsByCommands = commandInfos.getItems()
                .stream()
                .collect(Collectors.toMap(
                        i -> i.getName(),
                        i ->  {
                            var descriptions = new ArrayList<StyledText>();
                            descriptions.add(new StyledText("marked-text", "Description: "));
                            descriptions.add(new StyledText("simple-text",
                                    i.getDescription() + eol));
                            descriptions.add(new StyledText("marked-text", "Module: "));
                            descriptions.add(new StyledText("simple-text",
                                    i.getModuleName()  + eol));
                            return descriptions;
                        }));
        descriptionsByCommands = new TreeMap<>(descriptionsByCommands);
        this.attributeWindow = new AttributeWindowViewModel(descriptionsByCommands, AttributeWindowType.COMMAND);
        this.textListener = (ob, oldValue, newValue) -> {
            var commands = getUnsubmittedCommands(newValue);
            commands = removeCommandPrefix(commands);
            this.attributeWindow.updateData(commands);
        };
        textProperty().addListener(textListener);
        this.getComponentHelper().openAttributeWindow(attributeWindow);
    }

    private String removeCommandPrefix(String commands) {
        if (commands != null && commands.startsWith(CommandSpecialSymbols.LOCAL_COMMAND)) {
            commands = commands.substring(1);
            if (commands.length() == 0) {
                commands = null;
            }
        }
        return commands;
    }

    private void openParameterWindow(String command) {
        if (attributeWindow != null) {
            return;
        }
        var commandInfos = provideCommandInfos(command);
        if (command.startsWith(CommandSpecialSymbols.LOCAL_COMMAND)) {
            command = command.substring(1);
        }
        CommandInfo info = null;
        for (var i : commandInfos.getItems()) {
            if (i.getName().equals(command)) {
                info = i;
                break;
            }
        }
        if (info == null || info.getParameters() == null) {
            return;
        }
        var eol = "\n" + "\n";
        String requiredPrefix = "* ";
        String nonRequiredPrefix = "  "; //vertical alignment of parameters is required
        //we need to put required parameters before non required
        Map<String, List<StyledText>> descriptionsByParameter = new TreeMap<>((s1, s2) -> {
            if (s1.startsWith(nonRequiredPrefix) && s2.startsWith(requiredPrefix)) {
                return s1.compareTo(s2) * -1;
            } else {
                return s1.compareTo(s2);
            }
        });
        info.getParameters().forEach(d -> {
            //main parameters that don't have 'names' and that are marked with '*' are ignored
            if (!d.isMain()) {
                List<StyledText> descriptions = new ArrayList<>();
                descriptions.add(new StyledText("marked-text", "Description: "));
                descriptions.add(new StyledText("simple-text", d.getDescription() + eol));
                descriptions.add(new StyledText("marked-text", "Required: "));
                descriptions.add(new StyledText("simple-text", d.isRequired() + eol));
                String prefix;
                if (d.isRequired()) {
                    prefix = requiredPrefix;
                } else {
                    prefix = nonRequiredPrefix;
                }
                //if two parameters are possible we use only word parameter
                if (d.getShortName() != null && d.getLongName() == null) {
                    if (d.getLongName() != null) {
                        descriptions.add(new StyledText("marked-text", "Alias: "));
                        descriptions.add(new StyledText("simple-text", d.getLongName()));
                    }
                    descriptionsByParameter.put(prefix + d.getShortName(), descriptions);
                }
                if (d.getLongName() != null) {
                    if (d.getShortName() != null) {
                        descriptions.add(new StyledText("marked-text", "Alias: "));
                        descriptions.add(new StyledText("simple-text", d.getShortName()));
                    }
                    descriptionsByParameter.put(prefix + d.getLongName(), descriptions);
                }
            }
        });
        this.attributeWindow = new AttributeWindowViewModel(descriptionsByParameter, AttributeWindowType.PARAMETER);
        this.attributeWindow.setAttributePrefixLength(requiredPrefix.length());
        this.textListener = (ob, oldValue, newValue) -> {
            var lastParam = getLastParameter(newValue);
            this.attributeWindow.updateData(lastParam);
        };
        textProperty().addListener(textListener);
        getComponentHelper().openAttributeWindow(this.attributeWindow);
    }

    private CommandInfos provideCommandInfos(String unsubmittedCommands) {
        if (this.commandInfosManager.getSessionDescriptor() != null) {
            if (unsubmittedCommands == null) {
                unsubmittedCommands = getUnsubmittedCommands(textProperty().get());
            }
            if (unsubmittedCommands != null && unsubmittedCommands.startsWith(CommandSpecialSymbols.LOCAL_COMMAND)) {
                return this.commandInfosManager.getLocalInfos();
            } else {
                return this.commandInfosManager.getRemoteInfos();
            }
        } else {
            return Framework.getServiceManager().getCommandExecutor().getCommandInfos();
        }
    }

    /**
     * The main idea is simple. It is difficult to find out what command with what sessions a user has done. So, we do
     * this way - if the user has executed only one command and this command is remote, then it is obvious that he
     * uses the current session. Otherwise in any case command infos are updated.
     * @param results
     */
    private void updateRemoteCommandInfos(List<CommandResult> results) {
        boolean shouldUpdate = false;
        if (this.commandInfosManager.getRemoteInfos() != null) {
            if (results.size() == 1) {
                var result = results.get(0);
                if (result.isExecutedRemotely() && result.getComponentsStateInfo().getFinalState().getId()
                        != this.commandInfosManager.getRemoteInfos().getComponentsState().getId()) {
                    shouldUpdate = true;
                }
            } else {
                shouldUpdate = true;
            }
        } else {
            shouldUpdate = true;
        }
        if (shouldUpdate) {
            var sessionDescriptor = (SessionDescriptor)
                    this.commandContext.getProperty(ContextParameter.SESSION_DESCRIPTOR);
            var service = new CommandListService(sessionDescriptor);
            service.stateProperty().addListener((ov, oldV, newV) -> {
                if (newV == Worker.State.SUCCEEDED || newV == Worker.State.FAILED || newV == Worker.State.CANCELLED) {
                    this.commandInfosManager.setRemoteInfos(null);
                    if (newV == Worker.State.SUCCEEDED) {
                        this.commandInfosManager.setRemoteInfos(service.getValue());
                    }
                }
            });
            submitWorker(service);
        }
    }
}
