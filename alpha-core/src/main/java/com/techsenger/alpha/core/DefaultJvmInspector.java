/*
 * Copyright 2018-2025 Pavel Castornii.
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

package com.techsenger.alpha.core;

import com.techsenger.alpha.api.Framework;
import com.techsenger.alpha.api.JvmInspector;
import com.techsenger.alpha.api.component.Component;
import com.techsenger.alpha.api.component.ComponentDescriptor;
import com.techsenger.alpha.api.model.ComponentLayerModel;
import com.techsenger.alpha.api.model.ComponentModuleModel;
import com.techsenger.alpha.api.model.DefaultLayersInfo;
import com.techsenger.alpha.api.model.DefaultModulesInfo;
import com.techsenger.alpha.api.model.LayerReferenceInfo;
import com.techsenger.alpha.api.model.LayersInfo;
import com.techsenger.alpha.api.model.ModuleDirectiveReference;
import com.techsenger.alpha.api.model.ModulesInfo;
import com.techsenger.alpha.api.model.ReadsReference;
import com.techsenger.alpha.api.module.DirectiveType;
import com.techsenger.toolkit.core.jpms.ModuleUtils;
import com.techsenger.toolkit.core.model.ClassLoaderModel;
import com.techsenger.toolkit.core.model.ConfigurationModel;
import com.techsenger.toolkit.core.model.ModuleDescriptorModel;
import com.techsenger.toolkit.core.model.ModuleModel;
import com.techsenger.toolkit.core.model.ModuleReferenceModel;
import com.techsenger.toolkit.core.model.ResolvedModuleModel;
import com.techsenger.toolkit.core.model.ThreadInfoModel;
import com.techsenger.toolkit.core.model.ThreadModel;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.lang.module.Configuration;
import java.lang.module.ModuleReference;
import java.lang.module.ResolvedModule;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 *
 * @author Pavel Castornii
 */
public class DefaultJvmInspector implements JvmInspector {

    @Override
    public List<ThreadModel> getThreads() {
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        List<ThreadModel> result = new ArrayList<>();
        for (Thread thread:threadSet) {
            result.add(ThreadModel.from(thread));
        }
        return result;
    }

    @Override
    public List<ThreadInfoModel> getThreadInfos() {
        final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        final ThreadInfo[] threadInfos = threadMXBean.getThreadInfo(threadMXBean.getAllThreadIds(), 100);
        List<ThreadInfoModel> result = new ArrayList<>();
        for (ThreadInfo info:threadInfos) {
            result.add(ThreadInfoModel.from(info));
        }
        return result;
    }

    @Override
    public ModulesInfo getModulesInfo() {
        Map<String, ModuleLayer> layersByName = new LinkedHashMap<>();
        layersByName.put(ComponentLayerModel.resolveName(Framework.getLayerFullName(), 0),
                Framework.class.getModule().getLayer());
        var result = new DefaultModulesInfo();
        synchronized (Framework.getComponentManager()) {
            result.setComponentsState(Framework.getComponentManager().getComponentsState());
            List<ComponentDescriptor> descriptors =
                new ArrayList<>(Framework.getComponentManager().getDescriptors());
            //sort them by id
            Collections.sort(descriptors, new Comparator<ComponentDescriptor>() {
                @Override
                public int compare(ComponentDescriptor d1, ComponentDescriptor d2) {
                    return d1.getId() - d2.getId();
                }
            });
            for (ComponentDescriptor descriptor: descriptors) {
                Component component = Framework.getComponentManager().getComponent(descriptor.getId());
                var config = descriptor.getConfig();
                layersByName.put(ComponentLayerModel.resolveName(config.getFullName(), descriptor.getId()),
                        component.getLayer());
            }
        }

        Map<String, List<ModuleModel>> map = new LinkedHashMap<>();
        result.setModulesByLayerName(map);
        for (Map.Entry<String, ModuleLayer> entry : layersByName.entrySet()) {
            List<ModuleModel> modules = new ArrayList<>();
            entry.getValue()
                    .modules()
                    .stream()
                    .forEach(m -> {
                        var module = new ModuleModel();
                        module.setName(m.getName());
                        var desc = ModuleDescriptorModel.from(m.getDescriptor());
                        module.setDescriptor(desc);
                        if (m.getClassLoader() != null) {
                            var cl = new ClassLoaderModel();
                            cl.setName(m.getClassLoader().getName());
                            cl.setToString(m.getClassLoader().toString());
                            if (m.getClassLoader().getParent() != null) {
                                var parentCl = new ClassLoaderModel();
                                cl.setParent(parentCl);
                                parentCl.setName(m.getClassLoader().getParent().getName());
                                parentCl.setToString(m.getClassLoader().getParent().toString());
                            }
                            module.setClassLoader(cl);
                        }
                        modules.add(module);
                    });
            map.put(entry.getKey(), modules);
        }
        return result;
    }

    @Override
    public LayersInfo getLayersInfo() {
        Map<Configuration, Integer> idsByLayerConfig = new HashMap<>();
        idsByLayerConfig.put(Framework.class.getModule().getLayer().configuration(), 0);
        var result = new DefaultLayersInfo();
        Collection<Component> components = null;
        synchronized (Framework.getComponentManager()) {
            Framework.getComponentManager().getComponents().forEach(c -> {
                idsByLayerConfig.put(c.getLayer().configuration(), c.getDescriptor().getId());
            });
            result.setComponentsState(Framework.getComponentManager().getComponentsState());
            components = Framework.getComponentManager().getComponents();
        }

        var map = new HashMap<Integer, ComponentLayerModel>();
        result.setLayersById(map);
        //boot layer or non boot
        var bootLayer = createLayer(Framework.getLayerFullName(), 0, Framework.class.getModule().getLayer(),
                idsByLayerConfig, null);
        map.put(bootLayer.getId(), bootLayer);
        components.forEach(c -> {
            var d = c.getDescriptor();
            var layer = createLayer(d.getConfig().getFullName(), d.getId(), c.getLayer(),
                    idsByLayerConfig, c);
            map.put(layer.getId(), layer);
        });
        return result;
    }

    private ComponentLayerModel createLayer(String name, int id, ModuleLayer layer,
            Map<Configuration, Integer> idsByLayerConfig, Component component) {
        var referenceInfo = new LayerReferenceInfo();
        var model = new ComponentLayerModel(referenceInfo);
        model.setId(id);
        Map<String, Set<ReadsReference>> readsByModuleName = new HashMap<>();
        model.setConfiguration(createConfiguration(id, layer.configuration(), readsByModuleName,
                idsByLayerConfig));
        var modulesByName = layer.modules().stream().map(m -> createModule(m, id))
                .collect(Collectors.toMap(m -> m.getName(), m -> m));
        model.setModulesByName(modulesByName);
        if (!readsByModuleName.isEmpty()) {
            referenceInfo.setReadsByModuleName(readsByModuleName);
        }
        if (component != null) { //no boot layer
            //no parents, but parent ids
            var parentsIds = layer.parents().stream().map(p -> idsByLayerConfig.get(p.configuration()))
                    .collect(Collectors.toList());
            if (!parentsIds.isEmpty()) {
                referenceInfo.setParentIds(parentsIds);
            }
            //no ancestors, but ancestor ids
            Set<Integer> ancestorIds = new HashSet<>();
            component.getDescriptor().findAncestors().forEach(d -> ancestorIds.add(d.getId()));
            ancestorIds.add(0); //adding boot layer
            if (!ancestorIds.isEmpty()) {
                referenceInfo.setAncestorIds(ancestorIds);
            }
            //no descendants, but descendant ids
            var descendantIds = component.getDescriptor().findDescendants().stream().map(d -> d.getId())
                    .collect(Collectors.toSet());
            if (!descendantIds.isEmpty()) {
                referenceInfo.setDescendantIds(descendantIds);
            }
        } else { //boot layer
            Set<Integer> descendantIds = new HashSet<>(idsByLayerConfig.values());
            descendantIds.remove(0); //removing boot layer
            referenceInfo.setDescendantIds(descendantIds);
        }
        model.setName(ComponentLayerModel.resolveName(name, id));
        referenceInfo.setModuleDirectives(createModuleDirectives(component, idsByLayerConfig));
        return model;
    }

    private List<ModuleDirectiveReference> createModuleDirectives(Component component,
            Map<Configuration, Integer> idsByLayerConfig) {
        var result = new ArrayList<ModuleDirectiveReference>();
        if (component == null) {
            RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
            List<String> jvmArguments = runtimeMXBean.getInputArguments();
            final String exportsP = "--add-exports=";
            final String opensP = "--add-opens=";
            final String readsP = "--add-reads=";
            //ex: --add-opens=java.base/java.time=com.foo.Bar
            DirectiveType type = null;
            String params = null;
            for (var argument :jvmArguments) {
                type = null;
                if (argument.startsWith(exportsP)) {
                    params = argument.substring(exportsP.length());
                    type = DirectiveType.EXPORTS;
                } else if (argument.startsWith(opensP)) {
                    params = argument.substring(opensP.length());
                    type = DirectiveType.OPENS;
                } else if (argument.startsWith(readsP)) {
                    params = argument.substring(readsP.length());
                    type = DirectiveType.READS;
                }
                if (type != null) {
                    //since standard modules (java.*, jdk.*) are already well-defined and ready for use,
                    //the --add-* settings should be regarded as modifications related to user-defined modules
                    var equalIndex = params.indexOf("=");
                    String packageName = null;
                    String sourceName = null;
                    if (type != DirectiveType.READS) {
                        var moduleAndPackage = params.substring(0, equalIndex).split(Pattern.quote("/"));
                        sourceName = moduleAndPackage[0];
                        packageName = moduleAndPackage[1];
                    } else {
                        sourceName = params.substring(0, equalIndex);
                    }
                    boolean sourceIsStandard = ModuleUtils.isStandard(sourceName);
                    var targets = params.substring(equalIndex + 1).split(Pattern.quote(","));
                    for (var targetName : targets) {
                        var targetIsStandard = ModuleUtils.isStandard(targetName);
                        var resolvedType = type;
                        var resolvedSourceName = sourceName;
                        var resolvedTargetName = targetName;
                        if (sourceIsStandard && !targetIsStandard) {
                            resolvedType = type.getOpposite();
                            resolvedSourceName = targetName;
                            resolvedTargetName = sourceName;
                        }
                        var directive = new ModuleDirectiveReference();
                        directive.setPackageName(packageName);
                        directive.setSourceLayerId(0);
                        directive.setSourceModuleName(resolvedSourceName);
                        directive.setTargetLayerId(0);
                        directive.setTargetModuleName(resolvedTargetName);
                        directive.setType(resolvedType);
                        result.add(directive);
                    }
                }
            }
        } else {
            for (var d : component.getModuleDirectives()) {
                var directive = new ModuleDirectiveReference();
                directive.setPackageName(d.getPackage());
                directive.setType(d.getType());
                directive.setSourceLayerId(idsByLayerConfig.get(d.getSourceModule().getLayer().configuration()));
                directive.setSourceModuleName(d.getSourceModule().getName());
                directive.setTargetLayerId(idsByLayerConfig.get(d.getTargetModule().getLayer().configuration()));
                directive.setTargetModuleName(d.getTargetModule().getName());
                result.add(directive);
            }
        }
        if (!result.isEmpty()) {
            return result;
        } else {
            return  null;
        }
    }

    private ConfigurationModel createConfiguration(int id, Configuration config,
            Map<String, Set<ReadsReference>> readsByModuleName,
            Map<Configuration, Integer> componentIdsByLayerConfig) {
        var model = new ConfigurationModel();
        var modulesByName = config.modules().stream()
                .map(m -> createResolvedModule(m, readsByModuleName, componentIdsByLayerConfig))
                .collect(Collectors.toMap(m -> m.getName(), m -> m));
        model.setModulesByName(modulesByName);
        return model;
    }

    private ResolvedModuleModel createResolvedModule(ResolvedModule module,
            Map<String, Set<ReadsReference>> readsByModuleName,
            Map<Configuration, Integer> componentIdsByLayerConfig) {
        var model = new ResolvedModuleModel();
        //no configuration
        model.setName(module.name());
        //no reads, only reference map for layer
        Set<ReadsReference> reads = new HashSet<>();
        for (var readsModule : module.reads()) {
            var ref = new ReadsReference();
            var componentId = componentIdsByLayerConfig.get(readsModule.configuration());
            ref.setLayerId(componentId);
            ref.setModuleName(readsModule.name());
            reads.add(ref);
        }
        if (!reads.isEmpty()) {
            readsByModuleName.put(module.name(), reads);
        }
        model.setReference(createReference(module.reference()));
        return model;
    }

    private ModuleReferenceModel createReference(ModuleReference reference) {
        var model = new ModuleReferenceModel();
        model.setDescriptor(ModuleDescriptorModel.from(reference.descriptor()));
        if (reference.location().isPresent()) {
            model.setLocation(reference.location().get().toString());
        }
        return model;
    }

    private ModuleModel createModule(Module module, int layerId) {
        var model = new ComponentModuleModel();
        model.setId(ComponentLayerModel.resolveModuleId(layerId, module.getName()));
        //no descriptor
        //no layer
        model.setName(module.getName());
        model.setNamed(module.isNamed());
        model.setPackages(module.getPackages());
        //no annotation
        return model;
    }
}
