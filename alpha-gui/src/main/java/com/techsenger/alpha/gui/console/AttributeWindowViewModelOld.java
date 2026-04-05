package com.techsenger.alpha.gui.console;

///*
// * Copyright 2018-2026 Pavel Castornii.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.techsenger.alpha.console.gui.shell;
//
//import com.techsenger.alpha.console.gui.keys.ConsoleComponentKeys;
//import com.techsenger.alpha.console.gui.window.AbstractWindowViewModel;
//import com.techsenger.tabshell.core.pane.PaneKey;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//import javafx.beans.property.IntegerProperty;
//import javafx.beans.property.ObjectProperty;
//import javafx.beans.property.SimpleIntegerProperty;
//import javafx.beans.property.SimpleObjectProperty;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//
///**
// * Attribute is either command or parameter.
// *
// * @author Pavel Castornii
// */
//class AttributeWindowViewModelOld extends AbstractWindowViewModel {
//
//    /**
//     * The command a user selected in a window.
//     */
//    private String selectedAttribute;
//
//    /**
//     * The index of the selected attribute.
//     */
//    private IntegerProperty selectedAttributeIndex = new SimpleIntegerProperty(-1);
//
//    /**
//     * The command/piece of command the user entered.
//     */
//    private String enteredAttribute;
//
//    /**
//     * All existing commands.
//     */
//    private final Map<String, List<StyledText>> descriptionsByAttribute;
//
//    /**
//     * Current list is the filtered list in current lisview.
//     */
//    private final ObjectProperty<ObservableList<String>> currentAttributes = new SimpleObjectProperty<>();
//
//    /**
//     * Descriptions for current attributes.
//     */
//    private List<List<StyledText>> currentDescriptions;
//
//    /**
//     * For example for parameter prefix is "* ".
//     */
//    private int attributePrefixLength = 0;
//
//    private final AttributeWindowType type;
//
//    AttributeWindowViewModelOld(Map<String, List<StyledText>> descriptionsByAttribute, AttributeWindowType type) {
//        super();
//        this.descriptionsByAttribute = descriptionsByAttribute;
//        this.type = type;
//    }
//
//    @Override
//    public PaneKey getKey() {
//        return ConsoleComponentKeys.SHELL_WINDOW;
//    }
//
//    public AttributeWindowType getType() {
//        return type;
//    }
//
//    public ObjectProperty<ObservableList<String>> currentAttributesProperty() {
//        return currentAttributes;
//    }
//
//    public String getSelectedAttribute() {
//        return selectedAttribute;
//    }
//
//    public IntegerProperty selectedAttributeIndexProperty() {
//        return selectedAttributeIndex;
//    }
//
//    public String getEnteredAttribute() {
//        return enteredAttribute;
//    }
//
//    public List<List<StyledText>> getCurrentDescriptions() {
//        return currentDescriptions;
//    }
//
//    public void selectUp() {
//        if (this.currentAttributes.get() == null) {
//            return;
//        }
//        var index = selectedAttributeIndex.get();
//        if (index <= 0) {
//            return;
//        }
//        this.select(index - 1);
//    }
//
//    public void selectDown() {
//        if (this.currentAttributes.get() == null) {
//            return;
//        }
//        var index = selectedAttributeIndex.get();
//        if (index < 0 || index >= this.currentAttributes.get().size()) {
//            return;
//        }
//        this.select(index + 1);
//    }
//
//    public void updateData(String enteredAttribute) {
//        this.enteredAttribute = enteredAttribute;
//        currentAttributes.set(null);
//        currentAttributes.set(FXCollections.observableArrayList(new ArrayList<>()));
//        currentDescriptions = new ArrayList<>();
//        List<Map.Entry<String, List<StyledText>>> entries = new ArrayList<>(descriptionsByAttribute.entrySet());
//        if (enteredAttribute != null) {
//            entries = descriptionsByAttribute.entrySet()
//                .stream()
//                .filter(e -> e.getKey().substring(attributePrefixLength).startsWith(enteredAttribute))
//                .collect(Collectors.toList());
//        }
//        entries.forEach(e -> {
//            currentAttributes.get().add(e.getKey());
//            currentDescriptions.add(e.getValue());
//        });
//        this.select(0);
//    }
//
//    public int getAttributePrefixLength() {
//        return attributePrefixLength;
//    }
//
//    public void setAttributePrefixLength(int attributePrefixLength) {
//        this.attributePrefixLength = attributePrefixLength;
//    }
//
//    protected void doOnAttributeChanged(String attribute, String attributeString) {
//        selectedAttribute = attribute;
//        if (selectedAttribute != null) {
//            selectedAttribute = attributeString.substring(attributePrefixLength);
//        }
//    }
//
//    private void select(int i) {
//        if (this.currentAttributes != null && this.currentAttributes.get().size() > i) {
//            selectedAttributeIndex.set(i);
//        }
//    }
//}
