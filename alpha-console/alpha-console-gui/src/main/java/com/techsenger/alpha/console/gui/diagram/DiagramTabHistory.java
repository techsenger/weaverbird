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
//import com.techsenger.tabshell.kit.core.workertab.AbstractWorkerTabHistory;
//
///**
// *
// * @author Pavel Castornii
// */
//public class DiagramTabHistory extends AbstractWorkerTabHistory<DiagramTabViewModel> {
//
//    private String zoomLevel;
//
//    public String getZoomLevel() {
//        return zoomLevel;
//    }
//
//    public void setZoomLevel(String zoomLevel) {
//        this.zoomLevel = zoomLevel;
//    }
//
//    @Override
//    public void setDefaultValues() {
//        super.setDefaultValues();
//        this.zoomLevel = "100%";
//    }
//
//    @Override
//    public void restoreData(DiagramTabViewModel viewModel) {
//        super.restoreData(viewModel);
//        viewModel.zoomLevelProperty().set(this.zoomLevel);
//    }
//
//    @Override
//    public void saveData(DiagramTabViewModel viewModel) {
//        super.saveData(viewModel);
//        this.zoomLevel = viewModel.zoomLevelProperty().get();
//    }
//}
