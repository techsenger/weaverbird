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

package com.techsenger.alpha.console.gui.settings;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;

/**
 *
 * @author Pavel Castornii
 */
public class DiagramSettings {

    private ObjectProperty<Color> layerColor = new SimpleObjectProperty<>();

    private ObjectProperty<Color> moduleColor = new SimpleObjectProperty<>();

    private ObjectProperty<LineType> lineType = new SimpleObjectProperty<>();

    private ObjectProperty<LayoutEngine> layoutEngine = new SimpleObjectProperty<>();

    private IntegerProperty limitSize = new SimpleIntegerProperty();

    @XmlJavaTypeAdapter(ColorAdapter.class)
    @XmlAttribute(name = "layerColor")
    public Color getLayerColor() {
        return layerColor.get();
    }

    public void setLayerColor(Color layerColor) {
        this.layerColor.set(layerColor);
    }

    public ObjectProperty<Color> layerColorProperty() {
        return this.layerColor;
    }

    @XmlJavaTypeAdapter(ColorAdapter.class)
    @XmlAttribute(name = "moduleColor")
    public Color getModuleColor() {
        return moduleColor.get();
    }

    public void setModuleColor(Color moduleColor) {
        this.moduleColor.set(moduleColor);
    }

    public ObjectProperty<Color> moduleColorProperty() {
        return this.moduleColor;
    }

    @XmlAttribute(name = "lineType")
    public LineType getLineType() {
        return lineType.get();
    }

    public void setLineType(LineType lineType) {
        this.lineType.set(lineType);
    }

    public ObjectProperty<LineType> lineTypeProperty() {
        return lineType;
    }

    @XmlAttribute(name = "layoutEngine")
    public LayoutEngine getLayoutEngine() {
        return layoutEngine.get();
    }

    public void setLayoutEngine(LayoutEngine layoutEngine) {
        this.layoutEngine.set(layoutEngine);
    }

    public ObjectProperty<LayoutEngine> layoutEngineProperty() {
        return layoutEngine;
    }

    @XmlAttribute(name = "limitSize")
    public int getLimitSize() {
        return limitSize.get();
    }

    public void setLimitSize(int limitSize) {
        this.limitSize.set(limitSize);
    }

    public IntegerProperty limitSizeProperty() {
        return this.limitSize;
    }

}
