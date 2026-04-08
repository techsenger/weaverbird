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

package com.techsenger.alpha.gui.settings;

import com.techsenger.tabshell.core.settings.SettingsCallback;
import com.techsenger.tabshell.core.settings.SettingsSubscription;
import com.techsenger.tabshell.core.settings.SubscriptionUtils;
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

    public Color getLayerColor() {
        return layerColor.get();
    }

    public void setLayerColor(Color layerColor) {
        this.layerColor.set(layerColor);
    }

    public SettingsSubscription onLayerColorChanged(SettingsCallback<Color> callback) {
        return SubscriptionUtils.onChanged(layerColor, callback);
    }

    public Color getModuleColor() {
        return moduleColor.get();
    }

    public void setModuleColor(Color moduleColor) {
        this.moduleColor.set(moduleColor);
    }

    public SettingsSubscription onModuleColorChanged(SettingsCallback<Color> callback) {
        return SubscriptionUtils.onChanged(moduleColor, callback);
    }

    public LineType getLineType() {
        return lineType.get();
    }

    public void setLineType(LineType lineType) {
        this.lineType.set(lineType);
    }

    public SettingsSubscription onLineTypeChanged(SettingsCallback<LineType> callback) {
        return SubscriptionUtils.onChanged(lineType, callback);
    }

    public LayoutEngine getLayoutEngine() {
        return layoutEngine.get();
    }

    public void setLayoutEngine(LayoutEngine layoutEngine) {
        this.layoutEngine.set(layoutEngine);
    }

    public SettingsSubscription onLayoutEngineChanged(SettingsCallback<LayoutEngine> callback) {
        return SubscriptionUtils.onChanged(layoutEngine, callback);
    }

    public int getLimitSize() {
        return limitSize.get();
    }

    public void setLimitSize(int limitSize) {
        this.limitSize.set(limitSize);
    }

    public SettingsSubscription onLimitSizeChanged(SettingsCallback<Integer> callback) {
        return SubscriptionUtils.onChanged(limitSize, callback);
    }

}
