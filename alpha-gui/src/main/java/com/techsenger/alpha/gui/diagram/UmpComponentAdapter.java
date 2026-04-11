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

package com.techsenger.alpha.gui.diagram;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;

/**
 *
 * @author Pavel Castornii
 */
abstract class UmlComponentAdapter {

    private final StringProperty name = new SimpleStringProperty();

    private final BooleanProperty included = new SimpleBooleanProperty();

    private final BooleanProperty colored = new SimpleBooleanProperty();

    private final ChangeListener<Boolean> autoIncludeListener = (obs, oldV, newV) -> {
        if (newV) {
            includedProperty().set(true);
        }
    };

    UmlComponentAdapter(AbstractUmlComponent component) {
        name.set(component.getName());
        included.set(component.isIncluded());
        colored.set(component.isColored());

        colored.addListener(autoIncludeListener);

        included.addListener((obs, o, v) -> component.setIncluded(v));
        colored.addListener((obs, o, v) -> component.setColored(v));
    }

    public ChangeListener<Boolean> getAutoIncludeListener() {
        return autoIncludeListener;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String v) {
        name.set(v);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public boolean isIncluded() {
        return included.get();
    }

    public void setIncluded(boolean v) {
        included.set(v);
    }

    public BooleanProperty includedProperty() {
        return included;
    }

    public boolean isColored() {
        return colored.get();
    }

    public void setColored(boolean v) {
        colored.set(v);
    }

    public BooleanProperty coloredProperty() {
        return colored;
    }

    public void reset() {
        included.set(false);
        colored.set(false);
    }
}
