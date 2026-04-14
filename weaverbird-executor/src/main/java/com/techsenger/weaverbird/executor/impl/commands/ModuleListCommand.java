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
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
        printer.printlnMessage("Modules by layer:");
        AsciiTable table = new AsciiTable();
        var cwc = AsciiTableUtils.createColumnWidthCalculator(printer.getWidth(),
                ColumnWidth.chars(5),
                ColumnWidth.percent(33),
                ColumnWidth.percent(33),
                ColumnWidth.percent(34));
        table.getRenderer().setCWC(cwc);
        table.setTextAlignment(TextAlignment.LEFT);
        table.addRule();
        Map<String, List<ModuleModel>> modulesByLayer = null;
        if (context.isExecutionLocal()) {
            modulesByLayer = context.getFramework().getJvmInspector().getModulesInfo().getModulesByLayerName();
        } else {
            var client = new DomainClient(context.getClient(), context.getSession());
            modulesByLayer = client.getModulesByLayerName();
        }
        int moduleCount = 0;
        for (Map.Entry<String,  List<ModuleModel>> entry : modulesByLayer.entrySet()) {
            table.addRow(null, null, null, "Layer: " + entry.getKey());
            table.addRule();
            table.addRow("#", "Module", "ClassLoader", "ClassLoaderParent");
            table.addRule();
            //sorting by name
            var sortedList = entry.getValue().stream()
                    .sorted((object1, object2) -> object1.getName().compareTo(object2.getName()))
                    .collect(Collectors.toList());
            for (ModuleModel module : sortedList) {
                if (!this.standardRequired) {
                    if (module.getName().startsWith("java.") || module.getName().startsWith("jdk.")
                        || module.getName().startsWith("oracle.") || module.getName().startsWith("javafx.")) {
                        continue;
                    }
                }
                String classLoader = null;
                String classLoaderParent = null;
                if (module.getClassLoader() != null) {
                    classLoader = module.getClassLoader().getToString();
                    if (module.getClassLoader().getParent() != null) {
                        classLoaderParent = module.getClassLoader().getParent().getToString();
                    }
                }
                var moduleName = "";
                if (module.getName() != null) {
                    moduleName = module.getName();
                }
                if (versionIncluded && module.getDescriptor().getVersion() != null) {
                    moduleName += Constants.NAME_VERSION_SEPARATOR + module.getDescriptor().getVersion();
                }
                table.addRow(
                        ++moduleCount,
                        moduleName,
                        Objects.toString(classLoader, ""),
                        Objects.toString(classLoaderParent, "")
                );
                table.addRule();
            }
        }
        printer.printlnMessage(table.render());
    }
}
