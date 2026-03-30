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

package com.techsenger.alpha.core.api.model;

import com.techsenger.alpha.core.api.Constants;
import com.techsenger.toolkit.core.model.ModuleLayerModel;
import com.techsenger.toolkit.core.model.ResolvedModuleModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * When the instance of this class is built then not all references of its objects are resolved. So, it is necessary
 * to call {@link resolveReferences(Map)} method.
 *
 * @author Pavel Castornii
 */
public class ComponentLayerModel extends ModuleLayerModel {

    /**
     * Resolves references. For example, after getting layer models from server it is nessary to call this method.
     *
     * @param layersById
     */
    public static void resolveReferences(Map<Integer, ComponentLayerModel> layersById) {
        //building map
        var allResolvedModulesByReference = new HashMap<ReadsReference, ResolvedModuleModel>();
        for (var l : layersById.values()) {
            for (var m : l.getConfiguration().getModulesByName().values()) {
                var r = new ReadsReference();
                r.setLayerId(l.id);
                r.setModuleName(m.getName());
                allResolvedModulesByReference.put(r, m);
            }
        }

        //resolving referecens
        for (var l : layersById.values()) {
            //parents
            var parents = new ArrayList<ComponentLayerModel>();
            resolveLayers(layersById, l.referenceInfo.getParentIds(), parents);
            l.setParents((List) parents);
            //ancestors
            l.ancestors = new HashSet<ComponentLayerModel>();
            resolveLayers(layersById, l.referenceInfo.getAncestorIds(), l.ancestors);
            //descendants
            l.descendants = new HashSet<ComponentLayerModel>();
            resolveLayers(layersById, l.referenceInfo.getDescendantIds(), l.descendants);

            for (var m : l.getConfiguration().getModulesByName().values()) {
                var readsReferences = l.referenceInfo.getReadsByModuleName().get(m.getName());
                if (readsReferences != null) {
                    var readsModules = new HashSet<ResolvedModuleModel>();
                    //reads
                    m.setReads(readsModules);
                    for (var r : readsReferences) {
                        var readsModule = allResolvedModulesByReference.get(r);
                        readsModules.add(readsModule);
                    }
                }
                //configuration
                m.setConfiguration(l.getConfiguration());
            }
            for (var m: l.getModulesByName().values()) {
                var ref = new ReadsReference();
                ref.setLayerId(l.id);
                ref.setModuleName(m.getName());
                var resolvedModule = allResolvedModulesByReference.get(ref);
                //descriptor
                m.setDescriptor(resolvedModule.getReference().getDescriptor());
                //layer
                m.setLayer(l);
            }
            //module directives
            if (l.referenceInfo.getModuleDirectives() != null) {
                var moduleDirectives = new ArrayList<ModuleDirectiveModel>();
                l.moduleDirectives = moduleDirectives;
                for (var d : l.referenceInfo.getModuleDirectives()) {
                    var directive = new ModuleDirectiveModel();
                    directive.setPackageName(d.getPackageName());
                    directive.setType(d.getType());
                    directive.setSourceModule((ComponentModuleModel) layersById.get(d.getSourceLayerId())
                            .getModulesByName().get(d.getSourceModuleName()));
                    directive.setTargetModule((ComponentModuleModel) layersById.get(d.getTargetLayerId())
                            .getModulesByName().get(d.getTargetModuleName()));
                    moduleDirectives.add(directive);
                }
            }
            l.referenceInfo = null;
        }
    }

    /**
     * Resolves ModuleId = componentId + separator + moduleName.
     *
     * @param module
     * @return
     */
    public static String resolveModuleId(int layerId, String moduleName) {
        var moduleId = layerId + Constants.NAME_VERSION_SEPARATOR + moduleName;
        return moduleId;
    }

    /**
     * Returns the name of the component that consists of component full name and component id, or only of framework
     * full name if it is framework layer.
     *
     * @return
     */
    public static String resolveName(String componentFullName, int componentId) {
        if (componentId == 0) {
            return componentFullName;
        } else {
            return componentFullName + " (id: " + componentId + ")";
        }
    }

    private static void resolveLayers(Map<Integer, ComponentLayerModel> layersById, Collection<Integer> ids,
            Collection<ComponentLayerModel> layers) {
        if (ids != null) {
            for (var id : ids) {
                layers.add(layersById.get(id));
            }
        }
    }

    private int id = -1;

    private String name;

    private LayerReferenceInfo referenceInfo;

    /**
     * Directives from component configuration and from jvm input arguments for boot layer.
     */
    private List<ModuleDirectiveModel> moduleDirectives;

    private Set<ComponentLayerModel> ancestors;

    private Set<ComponentLayerModel> descendants;

    public ComponentLayerModel() {

    }

    public ComponentLayerModel(LayerReferenceInfo referenceInfo) {
        this.referenceInfo = referenceInfo;
    }

    /**
     * Is equal to component id, and 0 for framework layer.
     */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the name of the component that consists of component full name and component id, or only of framework
     * full name if it is framework layer.
     *
     * @return
     */
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ModuleDirectiveModel> getModuleDirectives() {
        return moduleDirectives;
    }

    public Set<ComponentLayerModel> getAncestors() {
        return ancestors;
    }

    public Set<ComponentLayerModel> getDescendants() {
        return descendants;
    }
}
