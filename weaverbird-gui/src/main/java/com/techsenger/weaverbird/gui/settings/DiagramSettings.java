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

package com.techsenger.weaverbird.gui.settings;

import javafx.scene.paint.Color;

/**
 *
 * @author Pavel Castornii
 */
public class DiagramSettings {

    private Color layerColor;

    private Color moduleColor;

    private LineType lineType;

    private LayoutEngine layoutEngine;

    private int limitSize;

    public Color getLayerColor() {
        return layerColor;
    }

    public void setLayerColor(Color layerColor) {
        this.layerColor = layerColor;
    }

    public Color getModuleColor() {
        return moduleColor;
    }

    public void setModuleColor(Color moduleColor) {
        this.moduleColor = moduleColor;
    }

    public LineType getLineType() {
        return lineType;
    }

    public void setLineType(LineType lineType) {
        this.lineType = lineType;
    }

    public LayoutEngine getLayoutEngine() {
        return layoutEngine;
    }

    public void setLayoutEngine(LayoutEngine layoutEngine) {
        this.layoutEngine = layoutEngine;
    }

    public int getLimitSize() {
        return limitSize;
    }

    public void setLimitSize(int limitSize) {
        this.limitSize = limitSize;
    }

}
