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

package com.techsenger.weaverbird.gui.console;

import com.techsenger.shellfx.core.UiExecutor;
import com.techsenger.shellfx.core.close.CloseCheckResult;
import com.techsenger.shellfx.core.close.ClosePreparationResult;
import com.techsenger.shellfx.core.settings.SettingsSubscription;
import com.techsenger.shellfx.core.tab.AbstractHostTabViewModel;
import com.techsenger.toolkit.fx.value.ObservableSource;
import com.techsenger.toolkit.fx.value.SimpleObservableSource;
import com.techsenger.weaverbird.core.api.Constants;
import com.techsenger.weaverbird.core.api.message.DefaultMessage;
import com.techsenger.weaverbird.core.api.message.Message;
import com.techsenger.weaverbird.core.api.message.MessageType;
import com.techsenger.weaverbird.executor.api.CommandExecutor;
import com.techsenger.weaverbird.executor.api.CommandExecutorFactory;
import com.techsenger.weaverbird.executor.api.CommandSyntax;
import com.techsenger.weaverbird.executor.api.command.Commands;
import com.techsenger.weaverbird.gui.style.WeaverbirdIcons;
import com.techsenger.weaverbird.net.client.api.ClientService;
import com.techsenger.weaverbird.net.client.api.ClientSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.text.Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @param <C> the composer type
 * @author Pavel Castornii
 */
public class ConsoleTabViewModel<C extends ConsoleTabComposer> extends AbstractHostTabViewModel<C>
        implements CompletionPopupAwarePort, ConsoleToolBarAwarePort {

    /**
     * Input without prompt. Contains the information about the current token (next to caret).
     *
     * @param text the text without prompt
     * @param caretOffset the caret offset inside input text.
     */
    private record PromptlessInput(String text, int caretOffset, int elementIndex,
        int elementLength, boolean elementFirst) { };

    private static final Logger logger = LoggerFactory.getLogger(ConsoleTabViewModel.class);

    private static final String PROMPT = "> ";

    /**
     * Returns the start index and length of the word near the caret position.
     * The caret can be inside the word or directly before/after it without a space.
     *
     * @param text   the text to search in
     * @param offset the caret position
     * @return int[]{start, length} or null if no word is found near the caret
     */
    public static int[] getTokenRange(String text, int offset) {
        if (text == null || text.isEmpty() || offset < 0 || offset > text.length()) {
            return null;
        }
        // Check character at caret position, then fallback to the left
        int pos = offset;
        if (pos == text.length() || !isTokenChar(text.charAt(pos))) {
            pos = pos - 1;
        }
        // No word character found near caret
        if (pos < 0 || !isTokenChar(text.charAt(pos))) {
            return null;
        }
        // Walk left to find the start of the word
        while (pos > 0 && isTokenChar(text.charAt(pos - 1))) {
            pos--;
        }
        int start = pos;
        // Walk right to find the end of the word
        while (pos < text.length() && isTokenChar(text.charAt(pos))) {
            pos++;
        }
        int length = pos - start;
        return new int[]{start, length};
    }

    private static boolean isTokenChar(char ch) {
        return Character.isLetterOrDigit(ch)
                || ch == '-'
                || String.valueOf(ch).equals(Constants.NAME_VERSION_SEPARATOR)
                || String.valueOf(ch).equals(CommandSyntax.LOCAL_COMMAND);
    }

    private final ReadOnlyObjectWrapper<Font> monospaceFont = new ReadOnlyObjectWrapper<>();

    private final ObservableSource<String> printPromptSource = new SimpleObservableSource<>();

    private final ObservableSource<String> updatePromptSource = new SimpleObservableSource<>();

    private final ObservableSource<List<Message>> printMessagesSource = new SimpleObservableSource<>();

    private final ObservableSource<Set<String>> highlightCommandsSource = new SimpleObservableSource<>();

    private final ObservableSource<String> replaceInputSource = new SimpleObservableSource<>();

    private final ObservableSource<Void> beepSource = new SimpleObservableSource<>();

    private final ObservableSource<Void> clearSource = new SimpleObservableSource<>();

    private final ObservableSource<Void> copySource = new SimpleObservableSource<>();

    private final ObservableSource<Void> pasteSource = new SimpleObservableSource<>();

    private final ClientService client;

    private final ClientSession initialSession;

    /**
     * Command executor that will execute commands from this console.
     */
    private final CommandExecutor executor;

    private volatile String sessionPrompt;

    private SettingsSubscription fontSubscription;

    /**
     * If null then the caret is outside of the editable region.
     */
    private Integer caretOffset;

    private PromptlessInput input;

    /**
     * The history of the commands.
     */
    private final List<String> lastCommands = new ArrayList<>();

    /**
     * Keeps the index of the last history scrolling command. It is reset after every command.
     */
    private volatile int commandIndex = -1;

    public ConsoleTabViewModel(ConsoleTabParams params) {
        super(params);
        this.client = params.getClient();
        this.initialSession = params.getSession();
        CommandExecutor ex = null;
        try {
            ex = CommandExecutorFactory.create(params.getFramework(), params.getClient());
        } catch (Exception e) {
            logger.error("{} Error creating executor", getDescriptor().getLogPrefix(), e);
        }
        this.executor = ex;
        selectedProperty().addListener((ov, oldV, newV) -> {
            if (newV) {
                requestFocus();
            }
        });
    }

    @Override
    public CloseCheckResult isReadyToClose() {
        return CloseCheckResult.READY;
    }

    @Override
    public void prepareToClose(Consumer<ClosePreparationResult> resultCallback) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Font getMonospaceFont() {
        return monospaceFont.get();
    }

    public ReadOnlyObjectProperty<Font> monospaceFontProperty() {
        return monospaceFont.getReadOnlyProperty();
    }

    @Override
    public void onElementSubmitted(CompletionType type, String text) {
        addElement(type, text);
    }

    @Override
    public void onPopupClose() {
        getComposer().closePopup();
        requestFocus();
    }

    @Override
    public void onClear() {
        this.lastCommands.clear();
        this.commandIndex = -1;
        clearSource.next(null);
        printPromptSource.next(getPrompt());
        requestFocus();
    }

    @Override
    public void onCopy() {
        copySource.next(null);
    }

    @Override
    public void onPaste() {
        pasteSource.next(null);
    }

    @Override
    public void onSessionChanged(ClientSession session) {
        var cmdContext = this.executor.getCommandContext();
        if (cmdContext.getSession() != session) {
            cmdContext.setSession(session);
            updatePrompt();
            updatePromptSource.next(getPrompt());
            requestFocus();
        }
    }

    @Override
    protected void postInitialize() {
        super.postInitialize();
        setTitle("Console");
        setIcon(WeaverbirdIcons.CONSOLE);
        updatePrompt();
        showPrompt();
        requestFocus();
        var settings = getShellContext().getSettings().getAppearance();
        setMonospaceFont(settings.getMonospaceFont());
        this.fontSubscription = settings.onMonospaceFontChanged((oldV, newV) -> setMonospaceFont(newV));
        highlightCommandsSource.next(executor.getCommandsByName().keySet());
    }

    @Override
    protected void preDeinitialize() {
        super.preDeinitialize();
        this.fontSubscription.unsubscribe();
    }

    protected void showPrompt() {
        UiExecutor.execute(() -> printPromptSource.next(getPrompt()));
    }

    protected void onElementSubmitted() {
        var composer = getComposer();
        var popup = composer.getPopupPort();
        addElement(popup.getType(), popup.getItemText());
        composer.closePopup();
        requestFocus();
    }

    protected void onAutocomplete(String paragraph) {
        this.input = createInput(paragraph);
        String elementToken = null;
        int offset = this.input.caretOffset() + getPrompt().length();
        if (this.input.elementIndex() >= 0) {
            elementToken = input.text().substring(input.elementIndex(), input.elementIndex() + input.elementLength());
            offset = this.input.elementIndex() + getPrompt().length();
        }

        var processingCommand = false;
        if ((input.text().substring(0, input.caretOffset()).isBlank() || input.elementFirst())) {
            processingCommand = true;
        }

        if (processingCommand) {
            var commands = executor.getCommandsByName().values();
            var sessionExists = executor.getCommandContext().getSession() != null;
            var params = new CompletionPopupParams(commands, sessionExists, null, elementToken, this);
            getComposer().openCommandPopup(params, offset);
        } else {
            var splits = this.input.text().trim().split(Pattern.quote(" "));
            var cmd = splits[0].trim();
            if (cmd.startsWith(CommandSyntax.LOCAL_COMMAND) && cmd.length() > 1) {
                cmd = cmd.substring(1);
            }
            var command = executor.getCommandsByName().get(cmd);
            if (command != null) {
                var params = new CompletionPopupParams(null, false, command.getParameters(), elementToken, this);
                getComposer().openParameterPopup(params, offset);
            }
        }
    }

    protected void onCaretChanged(Integer caretOffset) {
        this.caretOffset = caretOffset;
    }

    protected void onCopyAvailable(boolean value) {
        getComposer().getToolBarPort().onCopyAvailable(value);
    }

    protected void onCommandsSubmitted(String paragraph, int width) {
        var input = paragraph.substring(getPrompt().length()); // removing prompt
        if (input.isEmpty()) {
            this.showPrompt();
            return;
        }
        Thread.startVirtualThread(() -> {
            try {
                lastCommands.add(input); // even if the command can fail
                commandIndex = -1;
                var oldSession = executor.getCommandContext().getSession();
                var results = executor.executeCommands(input, null, null, width);
                results.forEach(r -> {
                    if (!r.getCommandName().equals(Commands.LOG_PRINT)) {
                        UiExecutor.execute(() -> printMessagesSource.next(r.getMessages()));
                    }
                });
                var newSession = executor.getCommandContext().getSession();
                if (!Objects.equals(oldSession, newSession)) {
                    UiExecutor.execute(() -> getComposer().getToolBarPort().updateSession(newSession));
                }
                updatePrompt();
                this.showPrompt();
            } catch (Exception ex) {
                logger.error("{} Error executing commands from GUI console", getDescriptor().getLogPrefix(), ex);
                List<Message> messages = new ArrayList<>();
                messages.add(new DefaultMessage(MessageType.ERROR, ex.getMessage()));
                if (ex.getCause() != null) {
                    messages.add(new DefaultMessage(MessageType.ERROR, ex.getCause().getMessage()));
                }
                messages.add(new DefaultMessage(MessageType.OUTPUT,
                        "Enter \"command:list\" to get a list of all commands."));
                messages.add(new DefaultMessage(MessageType.OUTPUT,
                        "Enter \"command -?\" to get help on a specific command."));
                UiExecutor.execute(() -> printMessagesSource.next(messages));
                this.showPrompt();
            }
        });
    }

    protected void onTextInput(String paragraph) {
        var popup = getComposer().getPopupPort();
        if (popup == null) {
            return;
        }
        var input = createInput(paragraph);
        if (popup.getType() == CompletionType.COMMAND) {
            if (input.elementIndex() >= 0) {
                var command = input.text().substring(input.elementIndex(),
                        input.elementIndex() + input.elementLength());
                popup.updateItems(command);
            }
        }
    }

    protected void onMoveUp() {
        var popup = getComposer().getPopupPort();
        if (popup == null) {
            scrollHistoryUp();
        } else {
            popup.moveUp();
        }
    }

    protected void onMoveDown() {
        var popup = getComposer().getPopupPort();
        if (popup == null) {
            scrollHistoryDown();
        } else {
            popup.moveDown();
        }
    }

    private void setMonospaceFont(Font font) {
        if (Objects.equals(getMonospaceFont(), font)) {
            return;
        }
        this.monospaceFont.set(font);
    }

    private void setSessionPrompt(String sessionPrompt) {
        this.sessionPrompt = sessionPrompt;
    }

    private String buildSessionPrompt(ClientSession session) {
        var sb = new StringBuilder();
        sb.append(session.getLoginName());
        sb.append("@");
        sb.append(session.getHost());
        sb.append(PROMPT);
        return sb.toString();
    }

    private void scrollHistoryUp() {
        int index = 0;
        if (this.commandIndex == -1) {
            index = this.lastCommands.size() - 1;
        } else {
            index = this.commandIndex - 1;
        }
        if (index >= 0) {
            this.commandIndex = index;
            replaceInputSource.next(lastCommands.get(index));
        } else {
            beepSource.next(null);
        }
    }

    private void scrollHistoryDown() {
        int index = 0;
        if (this.commandIndex == -1) {
            index = -1;
        } else {
            index = this.commandIndex + 1;
        }
        if (index >= 0 && index < this.lastCommands.size()) {
            this.commandIndex = index;
            replaceInputSource.next(lastCommands.get(index));
        } else if (index == this.lastCommands.size()) {
            this.commandIndex = -1;
            replaceInputSource.next("");
        } else {
            beepSource.next(null);
        }
    }

    private void addElement(CompletionType type, String element) {
        String oldInput = null;
        if (this.input.elementIndex() >= 0) {
            oldInput = this.input.text().substring(0, this.input.elementIndex());
        } else {
            oldInput = this.input.text().substring(0, this.input.caretOffset());
        }
        var newInput = oldInput + (element == null ? "" : element + " ");
        replaceInputSource.next(newInput);
        getComposer().closePopup();
        requestFocus();
        this.input = null;
    }

    private PromptlessInput createInput(String paragraph) {
        var input = paragraph.substring(getPrompt().length()); // removing prompt
        var inputOffset = caretOffset - getPrompt().length();
        int elemIndex = -1;
        int elemLength = -1;
        boolean elemFirst = false;
        var wordRange = getTokenRange(input, inputOffset);
        if (wordRange != null) {
            elemIndex = wordRange[0];
            elemLength = wordRange[1];
            elemFirst = input.substring(0, elemIndex).isBlank();
        }
        return new PromptlessInput(input, inputOffset, elemIndex, elemLength, elemFirst);
    }

    private void updatePrompt() {
        var session = executor.getCommandContext().getSession();
        if (session != null) {
            setSessionPrompt(buildSessionPrompt(session));
        } else {
            setSessionPrompt(null);
        }
    }

    private String getPrompt() {
        if (this.sessionPrompt != null) {
            return this.sessionPrompt;
        } else {
            return PROMPT;
        }
    }

    /**
     * Returns the client this console was opened with, read by {@link ConsoleTabComposer#compose()} when
     * creating the toolbar. Direct invocation by user code outside the composer is undefined behavior.
     */
    ClientService getClient() {
        return client;
    }

    /**
     * Returns the session this console was opened with, read by {@link ConsoleTabComposer#compose()} when
     * creating the toolbar. Direct invocation by user code outside the composer is undefined behavior.
     */
    ClientSession getInitialSession() {
        return initialSession;
    }

    ObservableSource<String> printPromptSource() {
        return printPromptSource;
    }

    ObservableSource<String> updatePromptSource() {
        return updatePromptSource;
    }

    ObservableSource<List<Message>> printMessagesSource() {
        return printMessagesSource;
    }

    ObservableSource<Set<String>> highlightCommandsSource() {
        return highlightCommandsSource;
    }

    ObservableSource<String> replaceInputSource() {
        return replaceInputSource;
    }

    ObservableSource<Void> beepSource() {
        return beepSource;
    }

    ObservableSource<Void> clearSource() {
        return clearSource;
    }

    ObservableSource<Void> copySource() {
        return copySource;
    }

    ObservableSource<Void> pasteSource() {
        return pasteSource;
    }
}
