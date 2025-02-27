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

package com.techsenger.alpha.console.gui.settings;

import com.techsenger.tabshell.core.page.AbstractPageViewModel;
import com.techsenger.tabshell.core.page.PageKey;
import com.techsenger.tabshell.core.theme.TabShellTheme;
import com.techsenger.tabshell.kit.core.settings.Settings;
import java.util.EnumSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.text.Font;

/**
 *
 * @author Pavel Castornii
 */
class AppearancePageViewModel extends AbstractPageViewModel {

    private static final PageKey key = new PageKey("Appearance Page");

    private ObservableList<TabShellTheme> themes =
            FXCollections.observableList(EnumSet.allOf(TabShellTheme.class).stream()
                    .filter(t -> t.isSupported()).collect(Collectors.toList()));

    private ObjectProperty<TabShellTheme> theme = new SimpleObjectProperty<>();

    private ObservableList<String> fontFamilies =
            FXCollections.observableList(Font.getFamilies());

    private ObservableList<Integer> fontSizes =
            FXCollections.observableList(IntStream.range(8, 25).boxed().collect(Collectors.toList()));

    private StringProperty fontFamily = new SimpleStringProperty();

    private ObjectProperty<Integer> fontSize = new SimpleObjectProperty<>();

    private ObservableList<String> viewerFontFamilies =
            FXCollections.observableList(Font.getFamilies());

    private ObservableList<Integer> viewerFontSizes =
            FXCollections.observableList(IntStream.range(8, 25).boxed().collect(Collectors.toList()));

    private StringProperty viewerFontFamily = new SimpleStringProperty();

    private ObjectProperty<Integer> viewerFontSize = new SimpleObjectProperty<>();


//    private ObservableList<Integer> tabSizes = FXCollections.observableList(List.of(2, 4, 8));
//
//    private IntegerProperty tabSize = new  SimpleIntegerProperty();
//
//    private BooleanProperty useSpacesForTab = new  SimpleBooleanProperty();

    AppearancePageViewModel(Settings settings) {
//        this.tabSize.set();
//        this.useSpacesForTab.set((settings.getTextSettings().getUseSpacesForTab()));
        this.theme.set(settings.getAppearance().getTheme());
        this.fontFamily.set(settings.getAppearance().getRegularFont().getFamily());
        this.fontSize.set((int) Math.round(settings.getAppearance().getRegularFont().getSize()));
        this.viewerFontFamily.set(settings.getViewer().getFont().getFamily());
        this.viewerFontSize.set((int) Math.round(settings.getViewer().getFont().getSize()));

        setTitle("Appearance");
    }

    @Override
    public PageKey getKey() {
        return key;
    }

    protected ObservableList<TabShellTheme> getThemes() {
        return themes;
    }

    protected ObjectProperty<TabShellTheme> themeProperty() {
        return theme;
    }

    protected ObservableList<Integer> getFontSizes() {
        return fontSizes;
    }

    protected ObservableList<String> getFontFamilies() {
        return fontFamilies;
    }

    protected ObjectProperty<Integer> fontSizeProperty() {
        return fontSize;
    }

    protected StringProperty fontFamilyProperty() {
        return fontFamily;
    }

    protected ObservableList<String> getViewerFontFamilies() {
        return viewerFontFamilies;
    }

    protected ObservableList<Integer> getViewerFontSizes() {
        return viewerFontSizes;
    }

    protected StringProperty viewerFontFamilyProperty() {
        return viewerFontFamily;
    }

    protected ObjectProperty<Integer> viewerFontSizeProperty() {
        return viewerFontSize;
    }

//    public ObservableList<Integer> getTabSizes() {
//        return tabSizes;
//    }
//
//    public IntegerProperty tabSizeProperty() {
//        return tabSize;
//    }
//
//    public BooleanProperty useSpacesForTabProperty() {
//        return useSpacesForTab;
//    }
}
