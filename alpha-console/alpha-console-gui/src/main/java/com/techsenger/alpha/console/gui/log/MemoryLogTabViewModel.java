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

package com.techsenger.alpha.console.gui.log;

import com.techsenger.alpha.console.gui.keys.ConsoleComponentKeys;
import com.techsenger.alpha.console.gui.style.ConsoleIcons;
import com.techsenger.alpha.console.gui.textstyle.StyledText;
import com.techsenger.mvvm4fx.core.HistoryPolicy;
import com.techsenger.tabshell.core.TabShellViewModel;
import com.techsenger.tabshell.core.tab.ShellTabKey;
import com.techsenger.tabshell.material.icon.FontIcon;
import com.techsenger.toolkit.fx.value.ObservableSource;
import com.techsenger.toolkit.fx.value.SimpleObservableSource;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.apache.logging.log4j.core.LogEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
public class MemoryLogTabViewModel extends AbstractLogTabViewModel {

    /**
     * Logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(MemoryLogTabView.class);

    /**
     * String builder.
     */
    private final StringBuilder builder = new StringBuilder();

    /**
     * Date format.
     */
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    /**
     * List holds all events. It is modified only from JavaFX thread. When certain levels are
     * selected we iterate this list and select only necessary events. So, we can't have here List of StyledText.
     */
    private final List<LogEvent> logEvents = new ArrayList<>();

    /**
     * We need to clear text area in exact point that can not be achieved by listeners on level button enabled property.
     */
    private final ObservableSource<Boolean> textAreaClear = new SimpleObservableSource<>();

    private boolean firstLogEvent = true;

    private Set<Module> filterModules;

    private final BooleanProperty moduleFilterButtonSelected = new SimpleBooleanProperty(false);

    /**
     * Constructor.
     */
    public MemoryLogTabViewModel(TabShellViewModel tabShell) {
        super(tabShell);
        setIcon(new FontIcon(ConsoleIcons.MEMORY_LOG));
        setTitle("Memory Log");
        setHistoryPolicy(HistoryPolicy.APPEARANCE);
        setHistoryProvider(() -> tabShell.getHistoryManager()
                .getHistory(MemoryLogTabHistory.class, MemoryLogTabHistory::new));
        contentModifiedProperty().addListener((ov, oldValue, newValue) -> {
            if (this.getFileInfo().getPath() == null) {
                return;
            }
            this.titleProperty().set(this.resolveTabTitle(this.titleProperty().get()));
        });
    }

    @Override
    public ShellTabKey getKey() {
        return ConsoleComponentKeys.MEMORY_LOG_TAB;
    }

    public void startService() {
        var service = new MemoryLogTabService();
        service.valueProperty().addListener((ov, oldV, newV) -> {
            if (newV != null) {
                this.processLogEvents(newV);
            }
        });
        this.submitWorker(service);
    }

    public ObservableSource<Boolean> getTextAreaClear() {
        return textAreaClear;
    }

    @Override
    public MemoryLogTabHelper getComponentHelper() {
        return (MemoryLogTabHelper) super.getComponentHelper();
    }

    @Override
    protected void refreshEvents() {
        this.textAreaClear.next(true);
        this.firstLogEvent = true;
        List<StyledText> currentTexts = new ArrayList<>();
        for (var logEvent : this.logEvents) {
            var levelFilterPassed = false;
            var moduleFilterPassed = false;
            if (levelFilterButtonSelectedProperty().get()) {
                var descriptor = this.resolveDescriptor(logEvent.getLevel());
                if (descriptor.buttonSelectedProperty().get()) {
                    levelFilterPassed = true;
                }
            } else {
                levelFilterPassed = true;
            }
            if (levelFilterPassed) {
                this.logEventToText(logEvent, currentTexts);
            }
        }
        if (!currentTexts.isEmpty()) {
            getTexts().next(currentTexts);
        }
    }

    protected void clearEvents() {
        if (this.getFind() != null) {
            this.getFind().resetMatches();
        }
        this.logEvents.clear();
        this.firstLogEvent = true;
        getDescriptorsByLevel().values().forEach((d) -> d.resetCount());
        this.textAreaClear.next(true);
    }

    protected BooleanProperty moduleFilterButtonSelectedProperty() {
        return moduleFilterButtonSelected;
    }

    private void processLogEvents(List<LogEvent> events) {
        List<StyledText> currentTexts = new ArrayList<>();
        for (var event: events) {
            this.logEvents.add(event);
            var descriptor = this.resolveDescriptor(event.getLevel());
            descriptor.increaseCount();
            if (descriptor.buttonSelectedProperty().get()) {
                this.logEventToText(event, currentTexts);
            }
        }
        if (!currentTexts.isEmpty()) {
            getTexts().next(currentTexts);
        }
    }

    private void logEventToText(LogEvent logEvent, List<StyledText> texts) {
        var level = logEvent.getLevel();
        StyledText text = null;
        if (!firstLogEvent) {
            builder.append("\n");
        }

        builder.append(dateFormat.format(new Date(logEvent.getInstant().getEpochMillisecond())));
        builder.append(" ");
        text = new StyledText("base", builder.toString());
        texts.add(text);
        builder.setLength(0);

        builder.append("[");
        builder.append(logEvent.getLevel().toString());
        builder.append("]");

        String styleClass = null;
        //FATAL=red bright blink, ERROR=red bright, WARN=yellow bright, INFO=magenta bright, DEBUG=green, TRACE=blue
        styleClass = this.getCssClassesByLevel().get(level);
        if (styleClass == null) {
            styleClass = "base";
        }
        text = new StyledText(styleClass, builder.toString());
        texts.add(text);
        builder.setLength(0);

        builder.append(" [");
        builder.append(logEvent.getThreadName());
        builder.append("] ");
        builder.append(logEvent.getLoggerName());
        builder.append(" - ");
        var message = logEvent.getMessage().getFormattedMessage();
        if (message != null) {
            //as richtextfx doesn't support \r\n it is necessary to replace it, to get correct text length
            message = message.replace("\r\n", "\n");
        }
        builder.append(message);

        var thrown = logEvent.getThrown();
        if (thrown != null) {
            StringWriter writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            thrown.printStackTrace(printWriter);
            printWriter.flush();
            var stackTrace = writer.toString();
            //removing lineSeparator in the end
            stackTrace = stackTrace.substring(0, stackTrace.length() - System.lineSeparator().length());
            stackTrace = stackTrace.replace("\r\n", "\n");
            builder.append("\n" + stackTrace);
        }
        text = new StyledText("base", builder.toString());
        texts.add(text);
        builder.setLength(0);
        this.firstLogEvent = false;
    }

    protected void updateModuleFilterSettings() {
        var viewModel = new ModuleFilterDialogViewModel(this.filterModules);
        getComponentHelper().openModuleFilterDialog(viewModel);
        viewModel.okActionProperty().set(() -> {
            this.filterModules = viewModel.getSelectedModules();
            viewModel.close();
        });
    }


}
