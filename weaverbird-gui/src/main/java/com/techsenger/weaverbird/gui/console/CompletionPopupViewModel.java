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

import com.techsenger.shellfx.core.CloseCheckResult;
import com.techsenger.shellfx.core.ClosePreparationResult;
import com.techsenger.shellfx.core.popup.AbstractPopupViewModel;
import com.techsenger.shellfx.core.popup.PopupComposer;
import com.techsenger.toolkit.core.Pair;
import com.techsenger.toolkit.fx.value.ObservableSource;
import com.techsenger.toolkit.fx.value.SimpleObservableSource;
import com.techsenger.weaverbird.executor.api.CommandSyntax;
import com.techsenger.weaverbird.executor.api.command.CommandInfo;
import com.techsenger.weaverbird.executor.api.command.ParameterDescriptor;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @param <C> the composer type
 * @author Pavel Castornii
 */
public class CompletionPopupViewModel<C extends PopupComposer> extends AbstractPopupViewModel<C>
        implements CompletionPopupPort {

    private final ObservableList<CompletionItem<?>> modifiableItems = FXCollections.observableArrayList();

    private final ObservableList<CompletionItem<?>> items =
            FXCollections.unmodifiableObservableList(modifiableItems);

    private final ReadOnlyObjectWrapper<CompletionItem<?>> item = new ReadOnlyObjectWrapper<>();

    private final ObservableSource<Pair<CompletionItem<?>, Direction>> itemSource = new SimpleObservableSource<>();

    private final Collection<CommandInfo> commands;

    private final List<ParameterDescriptor> parameters;

    private final CompletionType type;

    private final CompletionPopupAwarePort popupAware;

    private final boolean sessionExists;

    private final String token;

    public CompletionPopupViewModel(CompletionPopupParams params) {
        super(params);
        this.commands = params.getCommands();
        this.parameters = params.getParameterDescriptors();
        this.type = this.commands != null ? CompletionType.COMMAND : CompletionType.PARAMETER;
        this.popupAware = params.getPopupAware();
        this.token = params.getToken();
        this.sessionExists = params.isSessionExists();
    }

    @Override
    public CloseCheckResult isReadyToClose() {
        return CloseCheckResult.READY;
    }

    @Override
    public void prepareToClose(Consumer<ClosePreparationResult> resultCallback) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public CompletionType getType() {
        return type;
    }

    /**
     * Returns an unmodifiable list of items.
     */
    public ObservableList<CompletionItem<?>> getItems() {
        return items;
    }

    public CompletionItem<?> getItem() {
        return item.get();
    }

    public ReadOnlyObjectProperty<CompletionItem<?>> itemProperty() {
        return item.getReadOnlyProperty();
    }

    @Override
    public String getItemText() {
        return getItemText(getItem());
    }

    @Override
    public void updateItems(String text) {
        if (this.type == CompletionType.COMMAND) {
            setItems(createCommandItems(text));
        } else {
            setItems(createParameterItems(text));
        }
    }

    @Override
    public void moveUp() {
        var index = items.indexOf(getItem());
        if (index > 0) {
            itemSource.next(new Pair<>(items.get(index - 1), Direction.UP));
        }
    }

    @Override
    public void moveDown() {
        var index = items.indexOf(getItem());
        if (index + 1 < items.size()) {
            itemSource.next(new Pair<>(items.get(index + 1), Direction.DOWN));
        }
    }

    @Override
    protected void postInitialize() {
        super.postInitialize();
        updateItems(token);
    }

    protected void onItemSubmitted(CompletionItem<?> item) {
        popupAware.onElementSubmitted(type, getItemText(item));
    }

    protected void onClose() {
        popupAware.onPopupClose();
    }

    private void setItems(List<CompletionItem<?>> items) {
        modifiableItems.setAll(items);
        itemSource.next(new Pair<>(items.isEmpty() ? null : items.getFirst(), Direction.NONE));
    }

    private List<CompletionItem<?>> createCommandItems(String token) {
        var local = !sessionExists;
        var prefix = "";
        if (token != null && token.trim().startsWith(CommandSyntax.LOCAL_COMMAND)) {
            local = true;
            prefix = CommandSyntax.LOCAL_COMMAND;
        }
        var finalLocal = local;
        var finalPrefix = prefix;
        return this.commands.stream()
                .filter(c -> finalLocal ? c.isLocal() : c.isRemote())
                .<CompletionItem<?>>map(c -> new CompletionItem<>(finalPrefix + c.getName(), c))
                .filter(c -> token == null ? true : c.getText().startsWith(token))
                .sorted(Comparator.comparing(CompletionItem::getText))
                .toList();
    }

    private List<CompletionItem<?>> createParameterItems(String token) {
        return this.parameters.stream()
                .filter(c -> token == null ? true : c.getLongName().startsWith(token))
                .<CompletionItem<?>>map(c ->
                        new CompletionItem<>(c.isRequired() ? c.getLongName() + "*" : c.getLongName(), c))
                .sorted(Comparator
                        .<CompletionItem<?>, Boolean>comparing(i -> !i.getText().endsWith("*"))
                        .thenComparing(i -> i.getText()))
                .toList();
    }

    private String getItemText(CompletionItem<?> item) {
        if (this.type == CompletionType.COMMAND) {
            return item.getText();
        } else {
            ParameterDescriptor param = (ParameterDescriptor) item.getElement();
            return param.isMain() ? null : param.getLongName();
        }
    }

    ReadOnlyObjectWrapper<CompletionItem<?>> itemWrapper() {
        return item;
    }

    ObservableSource<Pair<CompletionItem<?>, Direction>> itemSource() {
        return itemSource;
    }
}
