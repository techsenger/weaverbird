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

import com.techsenger.shellfx.core.close.CloseCheckResult;
import com.techsenger.shellfx.core.close.ClosePreparationResult;
import com.techsenger.shellfx.core.tab.AbstractHostTabViewModel;
import com.techsenger.weaverbird.core.api.Framework;
import com.techsenger.weaverbird.core.api.model.LayersInfo;
import com.techsenger.weaverbird.gui.settings.DiagramSettings;
import com.techsenger.weaverbird.gui.style.WeaverbirdIcons;
import com.techsenger.weaverbird.net.client.api.ClientService;
import com.techsenger.weaverbird.net.client.api.ClientSession;
import com.techsenger.weaverbird.net.client.api.DomainClient;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.image.Image;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @param <C> the composer type
 * @author Pavel Castornii
 */
public class DiagramTabViewModel<C extends DiagramTabComposer> extends AbstractHostTabViewModel<C>
        implements DiagramToolBarAwarePort {

    private static final Logger logger = LoggerFactory.getLogger(DiagramTabViewModel.class);

    private final ReadOnlyObjectWrapper<Image> diagram = new ReadOnlyObjectWrapper<>();

    private final ReadOnlyDoubleWrapper diagramWidth = new ReadOnlyDoubleWrapper();

    private final ReadOnlyDoubleWrapper diagramHeight = new ReadOnlyDoubleWrapper();

    private final Framework framework;

    private final ClientService client;

    private final DiagramSettings settings;

    private ClientSession session;

    private String previousSessionUuid;

    private List<LayerConfig> previousLayerConfigs;

    private LayersInfo previousLayersInfo;

    private int zoomLevel;

    public DiagramTabViewModel(DiagramTabParams params) {
        super(params);
        this.framework = params.getFramework();
        this.client = params.getClient();
        this.session = params.getSession();
        this.settings = params.getSettings();
        selectedProperty().addListener((ov, oldV, newV) -> {
            if (newV) {
                requestFocus();
            }
        });
    }

    @Override
    public CloseCheckResult isReadyToClose() {
        return CloseCheckResult.READY;
    }

    @Override
    public void prepareToClose(Consumer<ClosePreparationResult> resultCallback) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void onSessionChanged(ClientSession session) {
        this.session = session;
    }

    public Image getDiagram() {
        return diagram.get();
    }

    public ReadOnlyObjectProperty<Image> diagramProperty() {
        return diagram.getReadOnlyProperty();
    }

    public double getDiagramWidth() {
        return diagramWidth.get();
    }

    public ReadOnlyDoubleProperty diagramWidthProperty() {
        return diagramWidth.getReadOnlyProperty();
    }

    public double getDiagramHeight() {
        return diagramHeight.get();
    }

    public ReadOnlyDoubleProperty diagramHeightProperty() {
        return diagramHeight.getReadOnlyProperty();
    }

    @Override
    public void onLayerDiagram() {
        try {
            LayersInfo layersInfo = null;
            // remote
            if (this.session != null) {
                // same session
                if (this.previousSessionUuid != null && this.previousSessionUuid.equals(session.getUuid())) {
                    // checking state id - it is very fast
                    var domainClient = new DomainClient(client, session);
                    var currentState = domainClient.getComponentsState();
                    if (currentState.getId() == this.previousLayersInfo.getComponentsState().getId()) {
                        layersInfo = this.previousLayersInfo;
                    } else {
                        layersInfo = domainClient.getLayersInfo();
                        layersInfo.resolveReferences();
                    }
                } else {
                    this.previousSessionUuid = session.getUuid();
                    layersInfo = new DomainClient(client, session).getLayersInfo();
                    layersInfo.resolveReferences();
                    this.previousLayerConfigs = null;
                }
            }
            // local
            if (layersInfo == null) {
                if (this.previousSessionUuid != null) {
                    this.previousLayerConfigs = null;
                }
                //trying to use the previous one
                if (this.previousSessionUuid == null && this.previousLayersInfo != null
                        && this.previousLayersInfo.getComponentsState().getId()
                        == framework.getComponentManager().getComponentsState().getId()) {
                    layersInfo = this.previousLayersInfo;
                } else {
                    layersInfo = framework.getJvmInspector().getLayersInfo();
                    layersInfo.resolveReferences();
                }
                this.previousSessionUuid = null;
            }
            this.previousLayersInfo = layersInfo;

            var layersByComponentId = layersInfo.getLayersById();
            var appearance = getShellContext().getSettings().getAppearance();
            var params = new LayerDialogParams(appearance, new ArrayList<>(layersByComponentId.values()),
                    previousLayerConfigs);
            var dialog = getComposer().openLayerDialog(params);
            dialog.setOnResult((b) -> {
                if (b == LayerDialogButtons.OK) {
                    try {
                        this.previousLayerConfigs = dialog.getLayerConfigs();
                        var shellSettings = getShellContext().getSettings();
                        var generator = new LayerDiagramGenerator(previousLayerConfigs, shellSettings, settings);
                        var code = generator.generate();
                        setDiagram(createDiagram(code));
                        if (getDiagram() != null) {
                            setDiagramSize(calculateDiagramWidth(), calculateDiagramHeight());
                        }
                    } catch (Exception ex) {
                        logger.error("Error creating diagram", ex);
                    }
                }
                dialog.closeSafely();
            });
        } catch (Exception ex) {
            logger.error("{} Error getting layers info from server", getDescriptor().getLogPrefix(), ex);
        }
    }

    @Override
    public void onZoomLevelChanged(int level) {
        this.zoomLevel = level;
        if (getDiagram() != null) {
            setDiagramSize(calculateDiagramWidth(), calculateDiagramHeight());
        }
    }

    @Override
    protected void postInitialize() {
        super.postInitialize();
        setTitle("Diagrams");
        setIcon(WeaverbirdIcons.DIAGRAMS);
    }

    private void setDiagram(Image diagram) {
        if (Objects.equals(getDiagram(), diagram)) {
            return;
        }
        this.diagram.set(diagram);
    }

    private void setDiagramSize(double diagramWidth, double diagramHeight) {
        if (getDiagramWidth() == diagramWidth && getDiagramHeight() == diagramHeight) {
            return;
        }
        this.diagramWidth.set(diagramWidth);
        this.diagramHeight.set(diagramHeight);
    }

    private Image createDiagram(String code) throws InterruptedException, IOException {
        if (System.getProperty("PLANTUML_LIMIT_SIZE") == null) {
            var limitSize = settings.getLimitSize();
            //by default image width limit is 4096
            System.setProperty("PLANTUML_LIMIT_SIZE", String.valueOf(limitSize));
        }
        SourceStringReader reader = new SourceStringReader(code);
        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out = new PipedOutputStream(in);
        //we need a second thread that will write to stream while we will consume it in current thread
        var thread = new Thread(() -> {
            try {
                //desc gives entities count, for example: (144 entities)
                reader.outputImage(out, new FileFormatOption(FileFormat.PNG)).getDescription();
            } catch (Exception ex) {
                logger.error("Error writing diagram to stream");
            }
        });
        thread.start();
        var diagram = new Image(in);
        thread.join();
        out.close();
        in.close();
        return diagram;
    }

    private double calculateDiagramWidth() {
        if (getDiagram() == null) {
            return 0;
        }
        double k = this.zoomLevel / 100.0;
        return getDiagram().getWidth() * k;
    }

    private double calculateDiagramHeight() {
        if (getDiagram() == null) {
            return 0;
        }
        double k = this.zoomLevel / 100.0;
        return getDiagram().getHeight() * k;
    }

    /**
     * Returns the client this diagram tab was opened with, read by {@link DiagramTabComposer#compose()} when
     * creating the toolbar. Direct invocation by user code outside the composer is undefined behavior.
     */
    ClientService getClient() {
        return client;
    }

    /**
     * Returns the session this diagram tab was opened with, read by {@link DiagramTabComposer#compose()} when
     * creating the toolbar. Direct invocation by user code outside the composer is undefined behavior.
     */
    ClientSession getInitialSession() {
        return session;
    }
}
