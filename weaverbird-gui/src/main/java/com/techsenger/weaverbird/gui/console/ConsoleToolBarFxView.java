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

import atlantafx.base.theme.Styles;
import com.techsenger.weaverbird.gui.session.AbstractSessionToolBarFxView;
import com.techsenger.weaverbird.gui.style.ConsoleIcons;
import com.techsenger.tabshell.material.icon.FontIconView;
import com.techsenger.tabshell.material.style.StyleClasses;
import com.techsenger.toolkit.fx.Spacer;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.Tooltip;

/**
 *
 * @author Pavel Castornii
 */
public class ConsoleToolBarFxView<P extends ConsoleToolBarPresenter<?, ?>> extends AbstractSessionToolBarFxView<P>
        implements ConsoleToolBarView {

    private final Button clearButton = new Button(null, new FontIconView(ConsoleIcons.CLEAR));

    private final Button copyButton = new Button(null, new FontIconView(ConsoleIcons.COPY));

    private final Button pasteButton = new Button(null, new FontIconView(ConsoleIcons.PASTE));

    @Override
    public void requestFocus() {

    }

    @Override
    public void setCopyDisabled(boolean disabled) {
        this.copyButton.setDisable(disabled);
    }

    @Override
    public void setPasteDisabled(boolean disabled) {
        this.pasteButton.setDisable(disabled);
    }

    @Override
    protected void build() {
        super.build();
        clearButton.getStyleClass().addAll(StyleClasses.ICON_BUTTON, Styles.FLAT);
        clearButton.setTooltip(new Tooltip("Clear"));
        copyButton.getStyleClass().addAll(StyleClasses.ICON_BUTTON, Styles.FLAT);
        copyButton.setTooltip(new Tooltip("Copy"));
        pasteButton.getStyleClass().addAll(StyleClasses.ICON_BUTTON, Styles.FLAT);
        pasteButton.setTooltip(new Tooltip("Paste"));

        getNode().getItems().addAll(clearButton, new Separator(Orientation.VERTICAL), copyButton, pasteButton,
                new Spacer(Orientation.HORIZONTAL), getSessionLabel(), getSessionComboBox(), getRefreshButton());
    }

    @Override
    protected void addHandlers() {
        super.addHandlers();
        clearButton.setOnAction(e -> getPresenter().onClear());
        copyButton.setOnAction(e -> getPresenter().onCopy());
        pasteButton.setOnAction(e -> getPresenter().onPaste());
    }
}
