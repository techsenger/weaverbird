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

import com.techsenger.alpha.console.gui.file.SupportedFileType;
import com.techsenger.alpha.console.gui.textstyle.StyledText;
import com.techsenger.alpha.console.gui.utils.LogStyleUtils;
import com.techsenger.tabshell.core.TabShellViewModel;
import com.techsenger.tabshell.core.menu.SimpleMenuItemHelper;
import com.techsenger.tabshell.kit.core.file.FileInfo;
import com.techsenger.tabshell.kit.core.file.LocalTextFileTaskProvider;
import com.techsenger.tabshell.kit.text.menu.EditMenuKeys;
import com.techsenger.tabshell.kit.text.viewer.AbstractViewerTabViewModel;
import com.techsenger.tabshell.kit.text.viewer.FindMatchesResetPolicy;
import com.techsenger.toolkit.fx.value.ObservableSource;
import com.techsenger.toolkit.fx.value.SimpleObservableSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author Pavel Castornii
 */
abstract class AbstractLogTabViewModel extends AbstractViewerTabViewModel {

    protected static class LevelDescriptor {

        private String buttonBaseText;

        private final StringProperty buttonText = new SimpleStringProperty();

        private final BooleanProperty buttonSelected = new SimpleBooleanProperty(true);

        private final BooleanProperty buttonDisable = new SimpleBooleanProperty(false);

        private int eventCount = 0;

        public LevelDescriptor() {
            this.updateButtonText();
        }

        public void increaseCount() {
            this.eventCount++;
            this.updateButtonText();
        }

        public void resetCount() {
            this.eventCount = 0;
            this.updateButtonText();
        }

        protected void setButtonBaseText(String buttonBaseText) {
            if (this.buttonBaseText != null) {
                throw new IllegalStateException("Button base text can be set only once");
            }
            this.buttonBaseText = buttonBaseText;
        }

        protected StringProperty buttonTextProperty() {
            return buttonText;
        }

        protected BooleanProperty buttonSelectedProperty() {
            return buttonSelected;
        }

        protected BooleanProperty buttonDisableProperty() {
            return buttonDisable;
        }

        private void updateButtonText() {
            this.buttonText.setValue(buttonBaseText + " (" + this.eventCount + ")");
        }
    }

    private class ButtonSelectedListener<T> implements ChangeListener<T> {

        private final Level level;

        ButtonSelectedListener(Level level) {
            this.level = level;
        }

        @Override
        public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {
            if (AbstractLogTabViewModel.this.descriptorsByLevel.get(this.level).eventCount == 0) {
                return;
            } else {
                AbstractLogTabViewModel.this.refreshEvents();
            }
        }
    }

    /**
     * Other descriptor is in this map by null key.
     */
    private final Map<Level, LevelDescriptor> descriptorsByLevel = new HashMap<>();

    /**
     * CSS classes by log level.
     */
    private final Map<Level, String> cssClassesByLevel = LogStyleUtils.createStyleClassesByLevel();

    private final ObservableSource<List<StyledText>> texts = new SimpleObservableSource<>();

    private final BooleanProperty levelFilterButtonSelected = new SimpleBooleanProperty(true);

    AbstractLogTabViewModel(TabShellViewModel tabShell) {
        super(tabShell, new FileInfo(null, null, null, null), new LocalTextFileTaskProvider());

        LevelDescriptor descriptor = new LevelDescriptor();
        this.descriptorsByLevel.put(Level.FATAL, descriptor);
        descriptor.buttonSelected.addListener(new ButtonSelectedListener<>(Level.FATAL));

        descriptor = new LevelDescriptor();
        this.descriptorsByLevel.put(Level.ERROR, descriptor);
        descriptor.buttonSelected.addListener(new ButtonSelectedListener<>(Level.ERROR));

        descriptor = new LevelDescriptor();
        this.descriptorsByLevel.put(Level.WARN, descriptor);
        descriptor.buttonSelected.addListener(new ButtonSelectedListener<>(Level.WARN));

        descriptor = new LevelDescriptor();
        this.descriptorsByLevel.put(Level.INFO, descriptor);
        descriptor.buttonSelected.addListener(new ButtonSelectedListener<>(Level.INFO));

        descriptor = new LevelDescriptor();
        this.descriptorsByLevel.put(Level.DEBUG, descriptor);
        descriptor.buttonSelected.addListener(new ButtonSelectedListener<>(Level.DEBUG));

        descriptor = new LevelDescriptor();
        this.descriptorsByLevel.put(Level.TRACE, descriptor);
        descriptor.buttonSelected.addListener(new ButtonSelectedListener<>(Level.TRACE));

        descriptor = new LevelDescriptor();
        //we use null key as there is no other level.
        this.descriptorsByLevel.put(null, descriptor);
        descriptor.buttonSelected.addListener(new ButtonSelectedListener<>(null));

        addMenuItemHelpers(
            new SimpleMenuItemHelper(EditMenuKeys.GO_TO_LINE, null, true)
        );

        this.setFindMatchesResetPolicy(FindMatchesResetPolicy.MANUAL);

        levelFilterButtonSelected.addListener((ov, oldV, newV) -> {
            this.descriptorsByLevel.values().forEach(d -> d.buttonDisableProperty().set(!newV));
            refreshEvents();
        });
    }

    @Override
    public List<FileChooser.ExtensionFilter> getExtensionFilters() {
        return List.of(SupportedFileType.LOG.getExtensionFilter());
    }

    @Override
    public String resolveDefaultExtension(FileChooser.ExtensionFilter filter) {
        return SupportedFileType.LOG.getExtension();
    }

    @Override
    public String getDefaultExtension() {
        return SupportedFileType.LOG.getExtension();
    }

    @Override
    public LogTabHelper<?> getComponentHelper() {
        return (LogTabHelper) super.getComponentHelper();
    }

    protected Map<Level, String> getCssClassesByLevel() {
        return cssClassesByLevel;
    }

    protected Map<Level, LevelDescriptor> getDescriptorsByLevel() {
        return descriptorsByLevel;
    }

    protected LevelDescriptor getDescriptorByLevel(Level level) {
        return this.descriptorsByLevel.get(level);
    }

    protected ObservableSource<List<StyledText>> getTexts() {
        return this.texts;
    }

    protected abstract void refreshEvents();

    protected void openLogEventDialog() {
        var dialog = new LogEventDialogViewModel();
        dialog.okActionProperty().set(() -> {
            var level =  dialog.levelProperty().get();
            var message = dialog.textProperty().get();
            LogManager.getLogger("Console").log(Level.valueOf(level), message);
            dialog.close();
        });
        getComponentHelper().openLogEventDialog(dialog);
    }

    protected LevelDescriptor resolveDescriptor(Level level) {
        var descriptor = this.descriptorsByLevel.get(level);
        if (descriptor == null) {
            return this.descriptorsByLevel.get(null);
        }
        return descriptor;
    }

    protected BooleanProperty levelFilterButtonSelectedProperty() {
        return levelFilterButtonSelected;
    }
}
