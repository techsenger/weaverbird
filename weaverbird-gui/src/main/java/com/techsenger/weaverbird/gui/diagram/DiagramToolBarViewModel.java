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

package com.techsenger.weaverbird.gui.diagram;

import com.techsenger.patternfx.mvvm.ChildComposer;
import com.techsenger.toolkit.fx.value.ObservableSource;
import com.techsenger.toolkit.fx.value.SimpleObservableSource;
import com.techsenger.weaverbird.gui.session.AbstractSessionToolBarViewModel;
import com.techsenger.weaverbird.net.client.api.ClientSession;
import java.util.List;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @param <C> the composer type
 * @author Pavel Castornii
 */
public class DiagramToolBarViewModel<C extends ChildComposer> extends AbstractSessionToolBarViewModel<C> {

    private static final ObservableList<String> ZOOM_LEVELS = FXCollections.unmodifiableObservableList(
            FXCollections.observableArrayList(List.of(
                    "33%", "50%", "67%", "80%", "90%",
                    "100%", "110%", "120%", "133%", "150%",
                    "170%", "200%", "300%", "400%", "500%")));

    private final ReadOnlyStringWrapper zoomLevel = new ReadOnlyStringWrapper();

    private final ObservableSource<String> selectZoomLevelSource = new SimpleObservableSource<>();

    private final DiagramToolBarAwarePort toolBarAware;

    public DiagramToolBarViewModel(DiagramToolBarParams params) {
        super(params);
        this.toolBarAware = params.getToolBarAware();
        zoomLevel.addListener((ov, oldV, newV) -> onZoomLevelChanged(newV));
    }

    public ObservableList<String> getZoomLevels() {
        return ZOOM_LEVELS;
    }

    public String getZoomLevel() {
        return zoomLevel.get();
    }

    public ReadOnlyStringProperty zoomLevelProperty() {
        return zoomLevel.getReadOnlyProperty();
    }

    @Override
    protected void postInitialize() {
        super.postInitialize();
        selectZoomLevelSource.next("100%");
    }

    protected void onLayerDiagram() {
        toolBarAware.onLayerDiagram();
    }

    @Override
    protected void onSessionChanged(ClientSession session) {
        super.onSessionChanged(session);
        toolBarAware.onSessionChanged(session);
    }

    protected void onZoomOut() {
        var index = ZOOM_LEVELS.indexOf(getZoomLevel());
        index--;
        if (index >= 0) {
            selectZoomLevelSource.next(ZOOM_LEVELS.get(index));
        }
    }

    protected void onZoomIn() {
        var index = ZOOM_LEVELS.indexOf(getZoomLevel());
        index++;
        if (index < ZOOM_LEVELS.size()) {
            selectZoomLevelSource.next(ZOOM_LEVELS.get(index));
        }
    }

    private void onZoomLevelChanged(String level) {
        if (level == null) {
            return;
        }
        var l = level.substring(0, level.length() - 1);
        toolBarAware.onZoomLevelChanged(Integer.valueOf(l));
    }

    ReadOnlyStringWrapper zoomLevelWrapper() {
        return zoomLevel;
    }

    ObservableSource<String> selectZoomLevelSource() {
        return selectZoomLevelSource;
    }
}
