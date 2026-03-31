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

package com.techsenger.alpha.console.gui;

import com.techsenger.alpha.executor.api.command.CommandInfo;
import com.techsenger.patternfx.mvp.Descriptor;
import com.techsenger.tabshell.core.CloseCheckResult;
import com.techsenger.tabshell.core.ClosePreparationResult;
import com.techsenger.tabshell.core.popup.AbstractPopupPresenter;
import com.techsenger.tabshell.core.popup.PopupComposer;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 *
 * @author Pavel Castornii
 */
public class AttributePopupPresenter<V extends AttributePopupView, C extends PopupComposer>
        extends AbstractPopupPresenter<V, C> {

    private final Map<String, CommandInfo> commandsByName;

    private final AttributePopupType type;

    private final PopupOwnerPort owner;

    private List<String> items;

    private String selectedItem;

    public AttributePopupPresenter(V view, Map<String, CommandInfo> commandsByName, AttributePopupType type,
            PopupOwnerPort owner) {
        super(view, false);
        this.commandsByName = commandsByName;
        this.type = type;
        this.owner = owner;
    }

    @Override
    protected Descriptor createDescriptor() {
        return new Descriptor(ConsoleComponents.SHELL_POPUP);
    }

    @Override
    public CloseCheckResult isReadyToClose() {
        return CloseCheckResult.READY;
    }

    @Override
    public void prepareToClose(Consumer<ClosePreparationResult> resultCallback) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public AttributePopupType getType() {
        return type;
    }

    @Override
    protected void postInitialize() {
        super.postInitialize();
        if (this.type == AttributePopupType.COMMAND) {
            setItems(this.commandsByName.keySet().stream().sorted().toList());
        }
    }

    protected void onItemSelected(String item) {
        this.selectedItem = item;
        if (this.type == AttributePopupType.COMMAND) {
            var command = this.commandsByName.get(selectedItem);
            getView().setInfo(command.getDescription(), command.getModuleName());
        }
    }

    protected void onItemSubmitted(String item) {
        this.owner.onAttributeSubmitted(type, item);
    }

    protected void onClose() {
        this.owner.onPopupClose();
    }

    protected void setItems(List<String> items) {
        this.items = items;
        getView().setItems(items);
    }
}
