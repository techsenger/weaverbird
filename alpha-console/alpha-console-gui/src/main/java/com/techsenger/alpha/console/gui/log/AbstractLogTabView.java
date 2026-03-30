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

package com.techsenger.alpha.console.gui.log;

import com.techsenger.alpha.console.gui.style.ConsoleIcons;
import com.techsenger.alpha.console.gui.utils.LogPrinterView;
import com.techsenger.alpha.console.gui.utils.LogStyleUtils;
import com.techsenger.mvvm4fx.core.ComponentHelper;
import com.techsenger.tabshell.core.TabShellView;
import com.techsenger.tabshell.kit.core.style.CoreIcons;
import com.techsenger.tabshell.kit.core.style.StyleClasses;
import com.techsenger.tabshell.kit.material.textarea.ExtendedTextArea;
import com.techsenger.tabshell.kit.material.textarea.TextAreaStyle;
import com.techsenger.tabshell.kit.text.viewer.AbstractViewerTabView;
import com.techsenger.tabshell.material.icon.FontIconView;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import org.apache.logging.log4j.Level;
import org.fxmisc.richtext.LineNumberFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
abstract class  AbstractLogTabView<T extends AbstractLogTabViewModel> extends AbstractViewerTabView<T>
        implements LogPrinterView {

    private static final Logger logger = LoggerFactory.getLogger(AbstractLogTabView.class);

    private final Button clearButton = new Button(null, new FontIconView(CoreIcons.CLEAR));

    private final ToggleButton levelFilterButton = new ToggleButton("Level", new FontIconView(ConsoleIcons.FILTER));

    private final ToggleButton fatalButton = new ToggleButton("Fatal");

    private final ToggleButton errorButton = new ToggleButton("Error");

    private final ToggleButton warnButton = new ToggleButton("Warn");

    private final ToggleButton infoButton = new ToggleButton("Info");

    private final ToggleButton debugButton = new ToggleButton("Debug");

    private final ToggleButton traceButton = new ToggleButton("Trace");

    /**
     * Sometimes there are Configuration and other levels.
     */
    private final ToggleButton otherButton = new ToggleButton("Other");

    private final Button logEventButton = new Button(null, new FontIconView(ConsoleIcons.ADD));

    private final Map<String, Collection<TextAreaStyle>> styleCollectionsByClassName = new HashMap<>();

    AbstractLogTabView(TabShellView<?> tabShell, T viewModel, ExtendedTextArea textArea) {
        super(tabShell, viewModel, textArea);
    }

    @Override
    protected void build(T viewModel) {
        super.build(viewModel);
        this.getToolBar().getItems().addAll(clearButton, new Separator(Orientation.VERTICAL), getCopyButton(),
                getFindButton(), new Separator(Orientation.VERTICAL), getWrapTextButton(), logEventButton,
                new Separator(Orientation.VERTICAL), levelFilterButton, fatalButton, errorButton, warnButton,
                infoButton, debugButton, traceButton, otherButton);
        clearButton.setTooltip(new Tooltip("Clear"));
        clearButton.getStyleClass().add(StyleClasses.ICONED_BUTTON);
        this.levelFilterButton.setTooltip(new Tooltip("Filter by Level"));
        this.fatalButton.setTooltip(new Tooltip("Fatal Level"));
        this.errorButton.setTooltip(new Tooltip("Error Level"));
        this.warnButton.setTooltip(new Tooltip("Warn Level"));
        this.infoButton.setTooltip(new Tooltip("Info Level"));
        this.debugButton.setTooltip(new Tooltip("Debug Level"));
        this.traceButton.setTooltip(new Tooltip("Trace Level"));
        this.otherButton.setTooltip(new Tooltip("Other Level"));
        this.logEventButton.setTooltip(new Tooltip("Add Log Event"));
        this.logEventButton.getStyleClass().add(StyleClasses.ICONED_BUTTON);

        var textArea = this.getTextArea();
        textArea.setEditable(false);
        textArea.setParagraphGraphicFactory(LineNumberFactory.get(textArea));
        //in richtextfx padding via css doesn't work, so, we do this way
        textArea.setPadding(new Insets(0, 0, 0, 0));
        textArea.getStyleClass().add("log-text-area");
        getTopPane().getChildren().addAll(getToolBar(), this.getTextScrollPane());
        getTextAreaMenu().getItems().addAll(getCopyItem());

        viewModel.getCssClassesByLevel().values().stream().forEach((v) -> this.styleCollectionsByClassName
                .put(v, Collections.singleton(new TextAreaStyle(v))));
        var css = AbstractLogTabView.class.getResource("log.css").toExternalForm();
        this.getTopPane().getStylesheets().add(css);
        css = LogStyleUtils.class.getResource("log-levels.css").toExternalForm();
        this.getTopPane().getStylesheets().add(css);

        //by default all buttons are selected
        this.initializaLevelDescriptorsAndButton(viewModel.getDescriptorByLevel(Level.FATAL), getFatalButton());
        this.initializaLevelDescriptorsAndButton(viewModel.getDescriptorByLevel(Level.ERROR), getErrorButton());
        this.initializaLevelDescriptorsAndButton(viewModel.getDescriptorByLevel(Level.WARN), getWarnButton());
        this.initializaLevelDescriptorsAndButton(viewModel.getDescriptorByLevel(Level.INFO), getInfoButton());
        this.initializaLevelDescriptorsAndButton(viewModel.getDescriptorByLevel(Level.DEBUG), getDebugButton());
        this.initializaLevelDescriptorsAndButton(viewModel.getDescriptorByLevel(Level.TRACE), getTraceButton());
        this.initializaLevelDescriptorsAndButton(viewModel.getDescriptorByLevel(null), getOtherButton());
    }

    @Override
    protected void addHandlers(T viewModel) {
        super.addHandlers(viewModel);
        logEventButton.setOnAction((e) -> viewModel.openLogEventDialog());
    }

    @Override
    protected void addListeners(T viewModel) {
        super.addListeners(viewModel);
        viewModel.getTexts().addListener((texts) -> this.printLogEvents(texts, getTextArea(), getTextScrollPane(),
                styleCollectionsByClassName));
    }

    @Override
    protected void bind(T viewModel) {
        super.bind(viewModel);
        getLevelFilterButton().selectedProperty().bindBidirectional(viewModel.levelFilterButtonSelectedProperty());
    }

    @Override
    protected ComponentHelper<?> createComponentHelper() {
        return new LogTabHelper(this);
    }

    protected Button getClearButton() {
        return clearButton;
    }

    protected ToggleButton getLevelFilterButton() {
        return levelFilterButton;
    }

    protected ToggleButton getFatalButton() {
        return fatalButton;
    }

    protected ToggleButton getErrorButton() {
        return errorButton;
    }

    protected ToggleButton getWarnButton() {
        return warnButton;
    }

    protected ToggleButton getInfoButton() {
        return infoButton;
    }

    protected ToggleButton getDebugButton() {
        return debugButton;
    }

    protected ToggleButton getTraceButton() {
        return traceButton;
    }

    protected ToggleButton getOtherButton() {
        return otherButton;
    }

    protected Button getLogEventButton() {
        return logEventButton;
    }

    private void initializaLevelDescriptorsAndButton(AbstractLogTabViewModel.LevelDescriptor levelDescriptor,
            ToggleButton button) {
        button.setSelected(true);
        levelDescriptor.setButtonBaseText(button.getText());
        levelDescriptor.buttonTextProperty().bindBidirectional(button.textProperty());
        button.selectedProperty().bindBidirectional(levelDescriptor.buttonSelectedProperty());
        button.disableProperty().bindBidirectional(levelDescriptor.buttonDisableProperty());
    }

}
