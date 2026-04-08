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

import com.techsenger.alpha.core.api.Constants;
import com.techsenger.alpha.core.api.model.ComponentLayerModel;
import com.techsenger.alpha.core.api.model.ComponentModuleModel;
import com.techsenger.alpha.core.api.model.ModuleDirectiveModel;
import static com.techsenger.alpha.core.api.module.DirectiveType.EXPORTS;
import static com.techsenger.alpha.core.api.module.DirectiveType.OPENS;
import static com.techsenger.alpha.core.api.module.DirectiveType.READS;
import com.techsenger.alpha.gui.settings.ConsoleSettings;
import com.techsenger.alpha.gui.settings.LayoutEngine;
import com.techsenger.toolkit.core.model.ConfigurationModel;
import com.techsenger.toolkit.core.model.ModuleModel;
import com.techsenger.toolkit.core.model.ResolvedModuleModel;
import com.techsenger.toolkit.fx.color.ColorUtils;
import java.lang.module.ModuleDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
class LayerDiagramGenerator {

    private static final String EOL = System.lineSeparator();

    private static final String ABSENT_MODULE_PREFIX = "x" + Constants.NAME_VERSION_SEPARATOR;

    private static final Logger logger = LoggerFactory.getLogger(LayerDiagramGenerator.class);

    private static class PackageInfo {

        private final String name;

        private boolean showOpens;

        private boolean showExports;

        PackageInfo(String name) {
            this.name = name;
        }
    }

    /**
     * A layer is included if its includedProperty is true or it is present in this set.
     */
    private final Set<Integer> includedLayerIds = new HashSet<>();

    /**
     * A module is included if its includedProperty is true or it is present in this set.
     */
    private final Set<String> includedModuleIds = new HashSet<>();

    /**
     * Sometimes modules are not present, for example, those that are required statically. Layer id for these modules
     * is x, for example x:moduleName
     */
    private final Set<String> includedAbsentModuleNames = new HashSet<>();

    private final Map<ModuleModel, Map<String, PackageInfo>> includedPackagesByModule = new HashMap<>();

    private final List<LayerConfig> layerConfigs;

    private final ConsoleSettings settings;

    LayerDiagramGenerator(List<LayerConfig> layerConfigs, ConsoleSettings settings) {
        this.layerConfigs = layerConfigs;
        this.settings = settings;
    }

    String generate() {
        int borderColor;
        var palette = settings.getAppearance().getTheme().getPalette();
        if (settings.getAppearance().getTheme().isDark()) {
            borderColor = palette.getBase4Color();
        } else {
            borderColor = palette.getBase5Color();
        }
        StringBuilder code = new StringBuilder();
        var diagramSettings = settings.getDiagram();
        code.append("@startuml");
        code.append(EOL);
        if (diagramSettings.getLayoutEngine() == LayoutEngine.SMETANA) {
            code.append("!pragma layout smetana");
        }
        code.append(EOL);
        code.append("skinparam linetype " + diagramSettings.getLineType().toString().toLowerCase());
        code.append(EOL);
        code.append("skinparam defaultFontColor " + ColorUtils.toHex(palette.getDefaultFgColor()));
        code.append(EOL);
        code.append("skinparam defaultFontSize "
                + Math.round(this.settings.getAppearance().getRegularFont().getSize()));
        code.append(EOL);
        code.append("skinparam BackgroundColor #00000000"); //+ ColorUtils.toHex(palette.getDefaultBgColor()));
        code.append(EOL);
        code.append("skinparam ArrowColor " + ColorUtils.toHex(palette.getDefaultFgColor()));
        code.append(EOL);
//        code.append("skinparam ComponentFontColor " + ColorUtils.toHex(palette.getDefaultFgColor()));
//        code.append(eol);
        code.append("skinparam ComponentBackgroundColor #00000000"); //+ ColorUtils.toHex(palette.getOverlayBgColor()));
        code.append(EOL);
        code.append("skinparam ComponentBorderColor " + ColorUtils.toHex(borderColor));
        code.append(EOL);
        code.append("' Components:");
        var arrowsCode = new StringBuilder();
        generateModuleArrows(arrowsCode);
        generateComponents(code);
        generateAbsentModules(code);
        generateLayerArrows(arrowsCode);
        code.append(EOL);
        code.append("' Arrows:");
        code.append(arrowsCode.toString());
        code.append(EOL);
        code.append("@enduml");
        code.append(EOL);
        var umlCode = code.toString();
        logger.debug("UML code: {}{}", EOL, umlCode);
        return umlCode;
    }

    private void generateComponents(StringBuilder code) {
        for (var layerConfig : this.layerConfigs) {
            if (layerConfig.isIncluded() || includedLayerIds.contains(layerConfig.getLayer().getId())) {
                includedLayerIds.add(layerConfig.getLayer().getId()); //it is used in generating arrows between layers
                code.append(EOL);
                code.append("component \"");
                code.append(layerConfig.getName());
                code.append("\" <<layer>> ");
                code.append(" as [");
                code.append(layerConfig.getLayer().getId());
                code.append("] ");
                if (layerConfig.isColored()) {
                    code.append(ColorUtils.toHex(settings.getDiagram().getLayerColor()));
                }
                var moduleCode = new StringBuilder();
                for (var moduleConfig : layerConfig.getModules()) {
                    if (moduleConfig.isIncluded()
                            || includedModuleIds.contains(moduleConfig.getModule().getId())) {
                        if (moduleCode.length() == 0) {
                            moduleCode.append(" {");
                        }
                        moduleCode.append(EOL);
                        moduleCode.append("\tcomponent \"");
                        moduleCode.append(moduleConfig.getName());
                        var version = moduleConfig.getModule().getDescriptor().getVersion();
                        if (version != null && !version.isEmpty()) {
                            moduleCode.append(Constants.NAME_VERSION_SEPARATOR);
                            moduleCode.append(version);
                        }
                        moduleCode.append("\"");
                        if (moduleConfig.getResolvedModule().getReference().getDescriptor().isAutomatic()) {
                            moduleCode.append(" <<automatic>>");
                        }
                        if (!moduleConfig.getModule().isNamed()) {
                            moduleCode.append(" <<unnamed>>");
                        }
                        moduleCode.append(" <<module>>");
                        moduleCode.append(" as [");
                        var moduleId = moduleConfig.getModule().getId();
                        moduleCode.append(moduleId);
                        moduleCode.append("]");
                        if (moduleConfig.isColored()) {
                            moduleCode.append(" ");
                            moduleCode.append(ColorUtils.toHex(settings.getDiagram().getModuleColor()));
                        }
                        var packageInfoById = this.includedPackagesByModule.get(moduleConfig.getModule());
                        if (packageInfoById != null) {
                            moduleCode.append(" {");
                            for (var entry : packageInfoById.entrySet()) {
                                moduleCode.append(EOL);
                                moduleCode.append("\t\tcomponent \"");
                                moduleCode.append(entry.getValue().name);
                                moduleCode.append("\"");
                                if (entry.getValue().showExports) {
                                    moduleCode.append(" <<exported>>");
                                }
                                if (entry.getValue().showOpens) {
                                    moduleCode.append(" <<opened>>");
                                }
                                moduleCode.append("<<package>> as [");
                                moduleCode.append(entry.getKey());
                                moduleCode.append("]");
                            }
                            moduleCode.append(EOL);
                            moduleCode.append("\t}");
                        }
                    }
                }
                if (moduleCode.length() != 0) {
                    moduleCode.append(EOL);
                    moduleCode.append("}");
                    code.append(moduleCode.toString());
                }
            }
        }
    }

    private void generateAbsentModules(StringBuilder code) {
        if (!this.includedAbsentModuleNames.isEmpty()) {
            code.append(EOL);
            code.append("' Absent modules:");
        }
        for (var m : this.includedAbsentModuleNames) {
            code.append(EOL);
            code.append("component \"");
            code.append(m);
            code.append("\" <<absent>> ");
            code.append(" as [");
            code.append(ABSENT_MODULE_PREFIX + m);
            code.append("] ");
        }
    }

    private void generateModuleArrows(StringBuilder code) {
        var layersByConfig = this.layerConfigs.stream().map(c -> c.getLayer())
                .collect(Collectors.toMap(l -> l.getConfiguration(), l -> l));
        for (var layerConfig : this.layerConfigs) {
            //extra directives are directives from configurations and from jvm parameters, --add-*
            var extraDirectivesByModule = new HashMap<ComponentModuleModel, List<ModuleDirectiveModel>>();
            if (layerConfig.getLayer().getModuleDirectives() != null) {
                for (var d: layerConfig.getLayer().getModuleDirectives()) {
                    var directives = extraDirectivesByModule.get(d.getSourceModule());
                    if (directives == null) {
                        directives = new ArrayList<>();
                        extraDirectivesByModule.put(d.getSourceModule(), directives);
                    }
                    directives.add(d);
                }
            }

            if (layerConfig.isIncluded()) {
                for (var moduleConfig: layerConfig.getModules()) {
                    if (moduleConfig.isIncluded()) {
                        generateModuleBaseArrows(code, layerConfig, moduleConfig, layersByConfig);
                        generateModuleExtraDirectiveArrows(code, moduleConfig,
                                extraDirectivesByModule.get(moduleConfig.getModule()));
                    }
                }
            }
        }
    }

    private void generateModuleBaseArrows(StringBuilder code, LayerConfig layerConfig, ModuleConfig moduleConfig,
            Map<ConfigurationModel, ComponentLayerModel> layersByConfig) {
        //reads
        if (moduleConfig.isReads() && moduleConfig.getResolvedModule().getReads() != null) {
            for (var readsModule: moduleConfig.getResolvedModule().getReads()) {
                var readsLayer = layersByConfig.get(readsModule.getConfiguration());
                var readsModuleId = includeModule(readsModule, readsLayer);
                generateDependencyArrow(code, moduleConfig.getModule().getId(), readsModuleId, "reads");
            }
        }
        //exports
        var moduleDescriptor = moduleConfig.getModule().getDescriptor();
        if (moduleConfig.isExports()) {
            if (moduleConfig.getModule().isNamed()  && !moduleDescriptor.isAutomatic()) {
                if (moduleDescriptor.getExports() != null) {
                    for (var e : moduleDescriptor.getExports()) {
                        String packageId = includePackage(moduleConfig.getModule(), e.getSource(), 0);
                        for (var target: e.getTargets()) {
                            var targetModules = findAllModulesFromDownAndUp(layerConfig.getLayer(), target);
                            generatePackageArrows(code, packageId, targetModules, "exported to");
                        }
                    }
                }
            } else {
                //all packages
                for (var p : moduleConfig.getModule().getPackages()) {
                    String packageId = includePackage(moduleConfig.getModule(), p, 0);
                    var targetModules = getAllModulesFromAndUp(moduleConfig.getModule());
                    generatePackageArrows(code, packageId, targetModules, "exported to");
                }
            }
        }
        //opens
        if (moduleConfig.isOpens()) {
            if (moduleConfig.getModule().isNamed()  && !moduleDescriptor.isAutomatic()) {
                if (moduleDescriptor.getOpens() != null) {
                    for (var o : moduleDescriptor.getOpens()) {
                        String packageId = includePackage(moduleConfig.getModule(), o.getSource(), 1);
                        for (var target: o.getTargets()) {
                            var targetModules = findAllModulesFromDownAndUp(layerConfig.getLayer(), target);
                            generatePackageArrows(code, packageId, targetModules, "opened to");
                        }
                    }
                }
            } else {
                //all packages
                for (var p : moduleConfig.getModule().getPackages()) {
                    String packageId = includePackage(moduleConfig.getModule(), p, 1);
                    var targetModules = getAllModulesFromAndUp(moduleConfig.getModule());
                    generatePackageArrows(code, packageId, targetModules, "opened to");
                }
            }
        }
        //requires
        if (moduleConfig.isRequires() && moduleDescriptor.getRequires() != null) {
            var readsByModuleName = moduleConfig.getResolvedModule().getReads()
                    .stream().collect(Collectors.toMap(m -> m.getName(), m -> m));
            for (var r : moduleDescriptor.getRequires()) {
                var targetModule = readsByModuleName.get(r.getName());
                var text = "requires";
                if (r.getModifiers().contains(ModuleDescriptor.Requires.Modifier.STATIC)) {
                    text += " static";
                }
                if (r.getModifiers().contains(ModuleDescriptor.Requires.Modifier.TRANSITIVE)) {
                    text += " transitive";
                }
                if (targetModule != null) {
                    var targetLayer = layersByConfig.get(targetModule.getConfiguration());
                    var targetModuleId = includeModule(targetModule, targetLayer);
                    generateDependencyArrow(code, moduleConfig.getModule().getId(), targetModuleId, text);
                } else {
                    var targetModuleId = includeAbsentModule(r.getName());
                    generateDependencyArrow(code, moduleConfig.getModule().getId(), targetModuleId, text);
                }
            }
        }
    }

    private void generateModuleExtraDirectiveArrows(StringBuilder code, ModuleConfig moduleConfig,
            List<ModuleDirectiveModel> customDirectives) {
        if (customDirectives != null) {
            for (var d : customDirectives) {
                switch (d.getType()) {
                    case READS:
                        if (moduleConfig.isReads()) {
                            var targetModuleId = includeModule(d.getTargetModule());
                            generateDependencyArrow(code, moduleConfig.getModule().getId(),
                                    targetModuleId, "reads");
                        }
                        break;
                    case EXPORTS:
                        if (moduleConfig.isExports()) {
                            var targetModuleId = includeModule(d.getTargetModule());
                            var packageId = includePackage(d.getSourceModule(), d.getPackageName(), 0);
                            generateDependencyArrow(code, packageId, targetModuleId, "exported to");
                        }
                        break;
                    case OPENS:
                        if (moduleConfig.isOpens()) {
                            var targetModuleId = includeModule(d.getTargetModule());
                            var packageId = includePackage(d.getSourceModule(), d.getPackageName(), 1);
                            generateDependencyArrow(code, packageId, targetModuleId, "opened to");
                        }
                        break;
                    case REQUESTS_READ:
                        if (moduleConfig.isRequests()) {
                            var targetModuleId = includeModule(d.getTargetModule());
                            generateDependencyArrow(code, moduleConfig.getModule().getId(),
                                    targetModuleId, "requests read");
                        }
                        break;
                    case REQUESTS_EXPORT:
                        if (moduleConfig.isRequests()) {
                            var packageId = includePackage(d.getTargetModule(), d.getPackageName(), 0);
                            generateDependencyArrow(code, moduleConfig.getModule().getId(),
                                    packageId, "requests export");
                        }
                        break;
                    case REQUESTS_OPEN:
                        if (moduleConfig.isRequests()) {
                            var packageId = includePackage(d.getTargetModule(), d.getPackageName(), 1);
                            generateDependencyArrow(code, moduleConfig.getModule().getId(),
                                    packageId, "requests open");
                        }
                        break;
                    default:
                        throw new AssertionError();
                }
            }
        }
    }

    private void generateDependencyArrow(StringBuilder code, String fromId, String toId, String text) {
        code.append(EOL);
        code.append("[");
        code.append(fromId);
        code.append("]");
        code.append("..>");
        code.append("[");
        code.append(toId);
        code.append("]");
        if (text != null) {
            code.append(" : ");
            code.append(text);
        }
    }

    /**
     * Returns all modules except specified module from all layers up and down.
     *
     * @param module
     * @return
     */
    private List<ComponentModuleModel> getAllModulesFromAndUp(ComponentModuleModel module) {
        List<ModuleModel> result = new ArrayList<>();
        for (var m : module.getLayer().getModulesByName().values()) {
            if (m != module) {
                result.add(m);
            }
        }
        for (var d : module.getLayer().getDescendants()) {
            for (var m : d.getModulesByName().values()) {
                if (m != module) {
                    result.add(m);
                }
            }
        }
        return (List) result;
    }

    /**
     * Finds all modules in layer descendants and layer ancestors.
     * @param layer
     * @param moduleName
     * @return
     */
    private List<ComponentModuleModel> findAllModulesFromDownAndUp(ComponentLayerModel layer, String moduleName) {
        List<ModuleModel> result = new ArrayList<>();
        for (var a : layer.getAncestors()) {
            var module = a.getModulesByName().get(moduleName);
            if (module != null) {
                result.add(module);
            }
        }
        ModuleModel module = layer.getModulesByName().get(moduleName);
        if (module != null) {
            result.add(module);
        }
        for (var d : layer.getDescendants()) {
            module = d.getModulesByName().get(moduleName);
            if (module != null) {
                result.add(module);
            }
        }
        return (List) result;
    }

    private void generateLayerArrows(StringBuilder code) {
        for (var layerConfig : this.layerConfigs) {
            //arrows between layers
            if (this.includedLayerIds.contains(layerConfig.getLayer().getId())
                    && layerConfig.getLayer().getParents() != null) {
                if (layerConfig.getLayer().getParents() != null) {
                    for (var parent : layerConfig.getLayer().getParents()) {
                        var parentId = ((ComponentLayerModel) parent).getId();
                        if (includedLayerIds.contains(parentId)) {
                            generateDependencyArrow(code, String.valueOf(layerConfig.getLayer().getId()),
                                    String.valueOf(parentId), null);
                        }
                    }
                }
            }
        }
    }

    private void generatePackageArrows(StringBuilder code, String packageId, Collection<ComponentModuleModel>
            targetModules, String text) {
        for (var targetModule : targetModules) {
            var targetModuleId = includeModule(targetModule);
            generateDependencyArrow(code, packageId, targetModuleId, text);
        }
    }

    /**
     * Includes package.
     *
     * @param module
     * @param pkg
     * @param type 0 for exports, 1 for opens
     * @return package id.
     */
    private String includePackage(ComponentModuleModel module, String pkg, int type) {
        var infoMap = this.includedPackagesByModule.get(module);
        if (infoMap == null) {
            infoMap = new HashMap<>();
            this.includedPackagesByModule.put(module, infoMap);
        }
        var packageId = ((ComponentModuleModel) module).getId() + Constants.NAME_VERSION_SEPARATOR + pkg;
        var packageInfo = infoMap.get(packageId);
        if (packageInfo == null) {
            packageInfo = new PackageInfo(pkg);
            infoMap.put(packageId, packageInfo);
            includeModule(module);
        }
        if (type == 0) {
            packageInfo.showExports = true;
        } else {
            packageInfo.showOpens = true;
        }
        return packageId;
    }

    private String includeModule(ResolvedModuleModel module, ComponentLayerModel layer) {
        var moduleId = ComponentLayerModel.resolveModuleId(layer.getId(), module.getName());
        if (this.includedModuleIds.add(moduleId)) {
            includeLayer((ComponentLayerModel) layer);
        }
        return moduleId;
    }

    private String includeModule(ModuleModel module) {
        var componentModule = (ComponentModuleModel) module;
        var moduleId = componentModule.getId();
        if (this.includedModuleIds.add(moduleId)) {
            includeLayer(componentModule.getLayer());
        }
        return moduleId;
    }

    private String includeAbsentModule(String name) {
        includedAbsentModuleNames.add(name);
        var moduleId = ABSENT_MODULE_PREFIX + name;
        return moduleId;
    }

    private void includeLayer(ComponentLayerModel layer) {
        var layerId = layer.getId();
        this.includedLayerIds.add(layerId);
    }
}
