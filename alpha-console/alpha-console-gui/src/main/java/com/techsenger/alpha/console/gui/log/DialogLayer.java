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

package com.techsenger.alpha.console.gui.log;

import com.techsenger.alpha.api.Constants;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Pavel Castornii
 */
class DialogLayer extends AbstractDialogElement {

    public static DialogLayer from(ModuleLayer layer, String layerName, Set<Module> selectedModules) {
        var dialogModules = new ArrayList<DialogModule>();
        var dialogLayer = new DialogLayer(layerName, dialogModules);
        for (var m : layer.modules()) {
            var version = "";
            if (m.getDescriptor().version().isPresent()) {
                version = m.getDescriptor().version().get().toString();
            }
            var dialogModule = new DialogModule(m.getName() + Constants.NAME_VERSION_SEPARATOR + version, m);
            if (selectedModules != null && selectedModules.contains(m)) {
                dialogModule.selectedProperty().set(true);
                dialogLayer.selectedProperty().set(true);
            }
            dialogModules.add(dialogModule);
        }
        return dialogLayer;
    }

    private final List<DialogModule> modules;

    DialogLayer(String name, List<DialogModule> modules) {
        super(name);
        this.modules = modules;
    }

    public List<DialogModule> getModules() {
        return modules;
    }


}
