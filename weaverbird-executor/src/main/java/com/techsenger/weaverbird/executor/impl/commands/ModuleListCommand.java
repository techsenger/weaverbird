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

package com.techsenger.weaverbird.executor.impl.commands;

import com.beust.jcommander.Parameter;
import com.techsenger.toolkit.ascii.table.AsciiTableUtils;
import com.techsenger.toolkit.ascii.table.ColumnWidth;
import com.techsenger.toolkit.core.collection.ListUtils;
import com.techsenger.toolkit.core.model.ModuleModel;
import com.techsenger.weaverbird.core.api.Constants;
import com.techsenger.weaverbird.core.api.message.MessagePrinter;
import com.techsenger.weaverbird.executor.api.CommandContext;
import com.techsenger.weaverbird.executor.spi.AbstractCommand;
import com.techsenger.weaverbird.executor.spi.CommandMeta;
import com.techsenger.weaverbird.executor.spi.LocalCommand;
import com.techsenger.weaverbird.executor.spi.RemoteCommand;
import com.techsenger.weaverbird.net.client.api.DomainClient;
import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author Pavel Castornii
 */
@LocalCommand
@RemoteCommand
@CommandMeta(name = "module:list", description = "Gives list of all modules.")
public class ModuleListCommand extends AbstractCommand {

    @Parameter(names = {"-s", "--standard"}, required = false,
            description = "specifies whether to show standard modules of Java SE and Java JDK")
    private boolean standardRequired = false;

    @Parameter(names = {"-v", "--version"}, required = false,
            description = "specifies whether to show module versions")
    private boolean versionIncluded = false;

    @Override
    public String getTitle() {
        return "Listing modules";
    }

    @Override
    public void execute(final CommandContext context, MessagePrinter printer) throws Exception {
        Map<String, List<ModuleModel>> modulesByLayer = null;
        if (context.isExecutionLocal()) {
            modulesByLayer = context.getFramework().getJvmInspector().getModulesInfo().getModulesByLayerName();
        } else {
            var client = new DomainClient(context.getClient(), context.getSession());
            modulesByLayer = client.getModulesByLayerName();
        }

        if (!this.standardRequired) {
            modulesByLayer = modulesByLayer.entrySet()
                    .stream()
                    .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().stream()
                            .filter(m -> !m.getName().startsWith("java.") && !m.getName().startsWith("jdk.")
                                    && !m.getName().startsWith("oracle.") && !m.getName().startsWith("javafx."))
                            .collect(Collectors.toList()),
                        (a, b) -> a,
                        LinkedHashMap::new
                    ));
        }

        int totalModules = modulesByLayer.values().stream().mapToInt(l -> l.size()).sum();
        if (totalModules == 0) {
            return;
        }

        var charCount = Math.max(3, String.valueOf(totalModules).length()); // less 3 - bug in asciitable


        var moduleCounter = 0;
        for (Map.Entry<String, List<ModuleModel>> entry : modulesByLayer.entrySet()) {
            // Build class loader info string from the first module of the filtered modules
            String classLoaderInfo = "";
            String parentClassLoaderInfo = "";
            var modules = entry.getValue();
            if (!modules.isEmpty()) {
                var firstCl = modules.get(0).getClassLoader();
                if (firstCl != null) {
                    classLoaderInfo = firstCl.getToString();
                    if (firstCl.getParent() != null) {
                        parentClassLoaderInfo = firstCl.getParent().getToString();
                    }
                }
            }

            printer.printlnMessage("Layer: " + entry.getKey());
            printer.printlnMessage("ClassLoader: " + classLoaderInfo + " (parent: "
                    + parentClassLoaderInfo + ")");

            AsciiTable table = new AsciiTable();
            var cwc = AsciiTableUtils.createColumnWidthCalculator(printer.getWidth(),
                    ColumnWidth.chars(charCount),
                    ColumnWidth.percent(33),
                    ColumnWidth.chars(charCount),
                    ColumnWidth.percent(33),
                    ColumnWidth.chars(charCount),
                    ColumnWidth.percent(34));
            table.getRenderer().setCWC(cwc);
            table.setTextAlignment(TextAlignment.LEFT);
            table.addRule();
            table.addRow("#", "Module Name", "#", "Module Name", "#", "Module Name");
            table.addRule();

            var moduleNames = modules.stream()
                    .sorted((m1, m2) -> m1.getName().compareTo(m2.getName()))
                    .map(m -> {
                        var name = m.getName() != null ? m.getName() : "";
                        if (versionIncluded && m.getDescriptor().getVersion() != null) {
                            name += Constants.NAME_VERSION_SEPARATOR + m.getDescriptor().getVersion();
                        }
                        return name;
                    })
                    .collect(Collectors.toList());

            // Split module names into three columns
            int partitionSize = moduleNames.size() / 3;
            if ((moduleNames.size() % 3) != 0) {
                partitionSize++;
            }
            List<List<String>> sublists = ListUtils.partition(moduleNames, partitionSize);
            var list0 = sublists.get(0);
            List<String> list1 = sublists.size() > 1 ? sublists.get(1) : null;
            List<String> list2 = sublists.size() > 2 ? sublists.get(2) : null;
            for (var i = 0; i < list0.size(); i++) {
                String col0 = list0.get(i);
                String col0Count = String.valueOf(++moduleCounter);
                String col1 = "";
                String col1Count = "";
                if (list1 != null && i < list1.size()) {
                    col1Count = String.valueOf(++moduleCounter);
                    col1 = list1.get(i);
                }
                String col2 = "";
                String col2Count = "";
                if (list2 != null && i < list2.size()) {
                    col2Count = String.valueOf(++moduleCounter);
                    col2 = list2.get(i);
                }
                table.addRow(col0Count, col0, col1Count, col1, col2Count, col2);
                table.addRule();
            }
            printer.printlnMessage(table.render());
        }
    }
}



