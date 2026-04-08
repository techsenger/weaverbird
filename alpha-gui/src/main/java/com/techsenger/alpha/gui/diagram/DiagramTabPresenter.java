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

import com.techsenger.alpha.core.api.Framework;
import com.techsenger.alpha.core.api.model.LayersInfo;
import com.techsenger.alpha.gui.AlphaComponents;
import com.techsenger.alpha.gui.settings.ConsoleSettings;
import com.techsenger.alpha.gui.style.ConsoleIcons;
import com.techsenger.alpha.net.client.api.ClientService;
import com.techsenger.alpha.net.client.api.ClientSession;
import com.techsenger.alpha.net.client.api.DomainClient;
import com.techsenger.patternfx.mvp.Descriptor;
import com.techsenger.tabshell.core.CloseCheckResult;
import com.techsenger.tabshell.core.ClosePreparationResult;
import com.techsenger.tabshell.core.tab.AbstractTabPresenter;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javafx.scene.image.Image;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
public class DiagramTabPresenter<V extends DiagramTabView, C extends DiagramTabComposer>
        extends AbstractTabPresenter<V, C> implements DiagramToolBarAwarePort {

    private static final Logger logger = LoggerFactory.getLogger(DiagramTabPresenter.class);

    private final Framework framework;

    private final ClientService client;

    private ClientSession session;

    private String previousSessionUuid;

    private List<LayerConfig> previousLayerConfigs;

    private LayersInfo previousLayersInfo;

    private int zoomLevel;

    private Image diagram;

    public DiagramTabPresenter(V view, Framework framework, ClientService client, ClientSession session) {
        super(view);
        this.framework = framework;
        this.client = client;
        this.session = session;
        getComposer().setClient(client);
        getComposer().setSession(session);
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
            var dialog = getComposer().openLayerDialog(new ArrayList<>(layersByComponentId.values()),
                    previousLayerConfigs);
            dialog.setResultAction((b) -> {
                if (b == LayerDialogButtons.OK) {
                    try {
                        this.previousLayerConfigs = dialog.getLayerConfigs();
                        ConsoleSettings settings = (ConsoleSettings) getShell().getContext().getSettings();
                        var generator = new LayerDiagramGenerator(previousLayerConfigs, settings);
                        var code = generator.generate();
                        this.diagram = createDiagram(code, settings);
                        if (this.diagram != null) {
                            getView().setDiagram(this.diagram);
                            getView().setDiagramSize(calculateDiagramWidth(), calculateDiagramHeight());
                            // getView().setDiagramSize();
                            // setFitSizes(zoomLevel.get());
                            // this.contentModified.set(true);
                        }
                    } catch (Exception ex) {
                        logger.error("Error creating diagram", ex);
                    }
                }
                dialog.requestClose();
            });
        } catch (Exception ex) {
            logger.error("{} Error getting layers info from server", getDescriptor().getLogPrefix(), ex);
        }
    }

    @Override
    public void onZoomLevelChanged(int level) {
        this.zoomLevel = level;
        if (this.diagram != null) {
            getView().setDiagramSize(calculateDiagramWidth(), calculateDiagramHeight());
        }
    }

    @Override
    protected Descriptor createDescriptor() {
        return new Descriptor(AlphaComponents.DIAGRAM_TAB);
    }

    @Override
    protected void postInitialize() {
        super.postInitialize();
        setTitle("Diagrams");
        setIcon(ConsoleIcons.DIAGRAMS);
    }

    private Image createDiagram(String code, ConsoleSettings settings) throws InterruptedException, IOException {
        if (System.getProperty("PLANTUML_LIMIT_SIZE") == null) {
            var limitSize = settings.getDiagram().getLimitSize();
            System.setProperty("PLANTUML_LIMIT_SIZE", String.valueOf(limitSize)); //by default image width limit is 4096
        }
        SourceStringReader reader = new SourceStringReader(code);
        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out = new PipedOutputStream(in);
        //we need a second thread that will write to stream while we will consume it in current thread
        var thread = new Thread(() -> {
            try {
                //desc gives entities count, for example: (144 entities)
                String desc = reader.outputImage(out, new FileFormatOption(FileFormat.PNG)).getDescription();
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
        if (this.diagram == null) {
            return 0;
        }
        double k = this.zoomLevel / 100.0;
        var width = this.diagram.getWidth() * k;
        return width;
    }

    private double calculateDiagramHeight() {
        if (this.diagram == null) {
            return 0;
        }
        double k = this.zoomLevel / 100.0;
        var height = this.diagram.getHeight() * k;
        return height;
    }

}
