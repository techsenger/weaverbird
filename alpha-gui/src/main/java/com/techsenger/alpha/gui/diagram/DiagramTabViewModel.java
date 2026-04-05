package com.techsenger.alpha.gui.diagram;

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
//package com.techsenger.alpha.console.gui.diagram;
//
//import com.techsenger.alpha.api.Framework;
//import com.techsenger.alpha.api.FrameworkMode;
//import com.techsenger.alpha.api.model.LayersInfo;
//import com.techsenger.alpha.console.gui.file.SupportedFileType;
//import com.techsenger.alpha.console.gui.keys.ConsoleComponentKeys;
//import com.techsenger.alpha.console.gui.session.SessionsToolBarViewModel;
//import com.techsenger.alpha.console.gui.settings.ConsoleSettings;
//import com.techsenger.alpha.console.gui.style.ConsoleIcons;
//import com.techsenger.mvvm4fx.core.HistoryPolicy;
//import com.techsenger.tabshell.core.TabShellViewModel;
//import com.techsenger.tabshell.core.menu.SimpleMenuItemHelper;
//import com.techsenger.tabshell.core.tab.ShellTabKey;
//import com.techsenger.tabshell.kit.core.file.FileInfo;
//import com.techsenger.tabshell.kit.core.file.FileTabViewModel;
//import com.techsenger.tabshell.kit.core.file.FileTaskProvider;
//import com.techsenger.tabshell.kit.core.menu.FileMenuKeys;
//import com.techsenger.tabshell.kit.core.workertab.AbstractWorkerTabViewModel;
//import com.techsenger.tabshell.material.icon.FontIcon;
//import java.io.IOException;
//import java.io.PipedInputStream;
//import java.io.PipedOutputStream;
//import java.util.ArrayList;
//import java.util.List;
//import javafx.beans.property.BooleanProperty;
//import javafx.beans.property.DoubleProperty;
//import javafx.beans.property.IntegerProperty;
//import javafx.beans.property.ObjectProperty;
//import javafx.beans.property.SimpleBooleanProperty;
//import javafx.beans.property.SimpleDoubleProperty;
//import javafx.beans.property.SimpleIntegerProperty;
//import javafx.beans.property.SimpleObjectProperty;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.scene.image.Image;
//import javafx.stage.FileChooser;
//import net.sourceforge.plantuml.FileFormat;
//import net.sourceforge.plantuml.FileFormatOption;
//import net.sourceforge.plantuml.SourceStringReader;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
///**
// *
// * @author Pavel Castornii
// */
//public class DiagramTabViewModel extends AbstractWorkerTabViewModel implements FileTabViewModel<Image> {
//
//    private static final Logger logger = LoggerFactory.getLogger(DiagramTabViewModel.class);
//
//    private final ObjectProperty<Image> diagram = new SimpleObjectProperty<>();
//
//    private final FileInfo fileInfo = new FileInfo(null, null, null, null);
//
//    private final BooleanProperty contentModified = new SimpleBooleanProperty(false);
//
//    private final LocalImageTaskProvider fileTaskProvider = new LocalImageTaskProvider();
//
//    private final SessionsToolBarViewModel sessions;
//
//    private List<LayerConfig> previousLayerConfigs;
//
//    private LayersInfo previousLayersInfo;
//
//    private final ObservableList<String> zoomLevels = FXCollections.observableArrayList(List.of(
//            "33%", "50%", "67%", "80%", "90%", "100%", "110%", "120%", "133%", "150%", "170%", "200%", "300%",
//            "400%", "500%"
//    ));
//
//    private final ObjectProperty<String> zoomLevel = new SimpleObjectProperty<>();
//
//    private final IntegerProperty zoomLevelIndex = new SimpleIntegerProperty();
//
//    /**
//     * If null then local framework layers are used.
//     */
//    private String previousSessionName;
//
//    private final DoubleProperty fitWidth = new SimpleDoubleProperty();
//
//    private final DoubleProperty fitHeight = new SimpleDoubleProperty();
//
//    public DiagramTabViewModel(TabShellViewModel tabShell) {
//        super(tabShell);
//        setTitle("Diagrams");
//        setIcon(new FontIcon(ConsoleIcons.DIAGRAMS));
//        setHistoryPolicy(HistoryPolicy.ALL);
//        setHistoryProvider(() -> getTabShell().getHistoryManager()
//                .getHistory(DiagramTabHistory.class, DiagramTabHistory::new));
//        this.contentModified.addListener((ov, oldValue, newValue) -> {
//            this.titleProperty().set(this.resolveTabTitle(this.titleProperty().get()));
//        });
//        if (Framework.getMode() == FrameworkMode.CLIENT) {
//            sessions = new SessionsToolBarViewModel();
//        } else {
//            sessions = null;
//        }
//        zoomLevel.addListener((ov, oldV, newV) -> {
//            if (newV != null) {
//                setFitSizes(newV);
//            }
//        });
//        addMenuItemHelpers(
//            new SimpleMenuItemHelper(FileMenuKeys.SAVE_AS) {
//                @Override
//                public Boolean getItemValid() {
//                    return diagram.get() != null;
//                }
//            },
//            new SimpleMenuItemHelper(FileMenuKeys.SAVE) {
//                @Override
//                public Boolean getItemValid() {
//                    return fileInfo.getPath() != null;
//                }
//            }
//        );
//    }
//
//    @Override
//    public ShellTabKey getKey() {
//        return ConsoleComponentKeys.DIAGRAM_TAB;
//    }
//
//    @Override
//    public boolean isContentModified() {
//        return this.contentModified.get();
//    }
//
//    @Override
//    public List<FileChooser.ExtensionFilter> getExtensionFilters() {
//        return List.of(SupportedFileType.PNG.getExtensionFilter());
//    }
//
//    @Override
//    public String resolveDefaultExtension(FileChooser.ExtensionFilter filter) {
//        return SupportedFileType.PNG.getExtension();
//    }
//
//    @Override
//    public String getDefaultExtension() {
//        return SupportedFileType.PNG.getExtension();
//    }
//
//    @Override
//    public FileInfo getFileInfo() {
//        return this.fileInfo;
//    }
//
//    @Override
//    public FileTaskProvider getFileTaskProvider() {
//        return this.fileTaskProvider;
//    }
//
//    @Override
//    public Image getContent() {
//        return this.diagram.get();
//    }
//
//    @Override
//    public void setContentModified(boolean modified) {
//        this.contentModified.set(modified);
//    }
//
//    @Override
//    public void setContent(Image content) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    @Override
//    public DiagramTabHelper<?> getComponentHelper() {
//        return (DiagramTabHelper) super.getComponentHelper();
//    }
//
//    protected void openLayerDialog() {
//        try {
//            LayersInfo layersInfo = null;
//            //remote
//            if (this.sessions != null) {
//                var session = this.sessions.sessionProperty().get();
//                if (session != null) {
//                    //same session
//                    if (this.previousSessionName != null && this.previousSessionName.equals(session.getName())) {
//                        //checking state id - it is very fast
//                        var currentState = Framework.getServiceManager().getClient(session.getProtocol())
//                                .getComponentsState(session.getName());
//                        if (currentState.getId() == this.previousLayersInfo.getComponentsState().getId()) {
//                            layersInfo = this.previousLayersInfo;
//                        } else {
//                            layersInfo = Framework.getServiceManager().getClient(session.getProtocol())
//                                .getLayersInfo(session.getName());
//                            layersInfo.resolveReferences();
//                        }
//                    } else {
//                        this.previousSessionName = session.getName();
//                        layersInfo = Framework.getServiceManager().getClient(session.getProtocol())
//                                .getLayersInfo(session.getName());
//                        layersInfo.resolveReferences();
//                        this.previousLayerConfigs = null;
//                    }
//                }
//            }
//            //local
//            if (layersInfo == null) {
//                if (this.previousSessionName != null) {
//                    this.previousLayerConfigs = null;
//                }
//                //trying to use the previous one
//                if (this.previousSessionName == null && this.previousLayersInfo != null
//                        && this.previousLayersInfo.getComponentsState().getId()
//                        == Framework.getComponentManager().getComponentsState().getId()) {
//                    layersInfo = this.previousLayersInfo;
//                } else {
//                    layersInfo = Framework.getJvmInspector().getLayersInfo();
//                    layersInfo.resolveReferences();
//                }
//                this.previousSessionName = null;
//            }
//            this.previousLayersInfo = layersInfo;
//
//            var layersByComponentId = layersInfo.getLayersById();
//            var viewModel = new LayerDialogViewModel(new ArrayList<>(layersByComponentId.values()),
//                    this.previousLayerConfigs, getTabShell().getHistoryManager());
//            viewModel.okActionProperty().set(() -> {
//                try {
//                    this.previousLayerConfigs = viewModel.getLayerConfigs();
//                    var code = viewModel.generateUmlCode((ConsoleSettings) getTabShell().getSettings());
//                    var newDiagram = createDiagram(code);
//                    if (newDiagram != null) {
//                        diagram.set(newDiagram);
//                        setFitSizes(zoomLevel.get());
//                        this.contentModified.set(true);
//                    }
//                    viewModel.close();
//                } catch (Exception ex) {
//                    logger.error("Error creating diagram", ex);
//                }
//            });
//            getComponentHelper().openLayerDialog(viewModel);
//        } catch (Exception ex) {
//            logger.error("Error getting layers info from server", ex);
//        }
//    }
//
//    protected ObjectProperty<Image> diagramProperty() {
//        return diagram;
//    }
//
//    SessionsToolBarViewModel getSessions() {
//        return sessions;
//    }
//
//    ObservableList<String> getZoomLevels() {
//        return zoomLevels;
//    }
//
//    ObjectProperty<String> zoomLevelProperty() {
//        return zoomLevel;
//    }
//
//    IntegerProperty zoomLevelIndexProperty() {
//        return zoomLevelIndex;
//    }
//
//    DoubleProperty fitWidthProperty() {
//        return fitWidth;
//    }
//
//    DoubleProperty fitHeightProperty() {
//        return fitHeight;
//    }
//
//    void zoomIn() {
//        if (this.diagram.get() == null) {
//            return;
//        }
//        var nextIndex = this.zoomLevelIndex.get() + 1;
//        if (nextIndex < this.zoomLevels.size()) {
//            var level = this.zoomLevels.get(nextIndex);
//            this.zoomLevel.set(level);
//        }
//    }
//
//    void zoomOut() {
//        if (this.diagram.get() == null) {
//            return;
//        }
//        var previousIndex = this.zoomLevelIndex.get() - 1;
//        if (previousIndex >= 0) {
//            var level = this.zoomLevels.get(previousIndex);
//            this.zoomLevel.set(level);
//        }
//    }
//
//    private void setFitSizes(final String zoomLevel) {
//        if (this.diagram.get() == null) {
//            return;
//        }
//        var level = zoomLevel.substring(0, zoomLevel.length() - 1);
//        double iLevel = Integer.valueOf(level);
//        double k = iLevel / 100;
//        this.fitWidth.set(this.diagram.get().getWidth() * k);
//        this.fitHeight.set(this.diagram.get().getHeight() * k);
//    }
//
//    private Image createDiagram(String code)
//            throws InterruptedException, IOException {
//        if (System.getProperty("PLANTUML_LIMIT_SIZE") == null) {
//            var limitSize = ((ConsoleSettings) getTabShell().getSettings()).getDiagram().getLimitSize();
//            System.setProperty("PLANTUML_LIMIT_SIZE", String.valueOf(limitSize)); //by default image width limit is 4096
//        }
//        SourceStringReader reader = new SourceStringReader(code);
//        PipedInputStream in = new PipedInputStream();
//        PipedOutputStream out = new PipedOutputStream(in);
//        //we need a second thread that will write to stream while we will consume it in current thread
//        var thread = new Thread(() -> {
//            try {
//                //desc gives entities count, for example: (144 entities)
//                String desc = reader.outputImage(out, new FileFormatOption(FileFormat.PNG)).getDescription();
//            } catch (Exception ex) {
//                logger.error("Error writing diagram to stream");
//            }
//        });
//        thread.start();
//        var diagram = new Image(in);
//        thread.join();
//        out.close();
//        in.close();
//        return diagram;
//    }
//}
