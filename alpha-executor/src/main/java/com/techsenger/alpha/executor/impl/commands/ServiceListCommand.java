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

package com.techsenger.alpha.executor.impl.commands;

import com.beust.jcommander.Parameter;
import com.techsenger.alpha.core.api.message.MessagePrinter;
import com.techsenger.alpha.executor.api.CommandContext;
import com.techsenger.alpha.executor.spi.AbstractCommand;
import com.techsenger.alpha.executor.spi.CommandMeta;
import com.techsenger.alpha.executor.spi.LocalCommand;
import com.techsenger.alpha.executor.spi.RemoteCommand;
import com.techsenger.alpha.net.client.api.DomainClient;
import com.techsenger.toolkit.ascii.table.AsciiTableUtils;
import com.techsenger.toolkit.ascii.table.ColumnWidth;
import com.techsenger.toolkit.core.model.ModuleDescriptorModel;
import com.techsenger.toolkit.core.model.ModuleModel;
import de.vandermeer.asciitable.AT_Row;
import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.asciitable.CWC_FixedWidth;
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
@CommandMeta(name = "service:list", description = "Gives list all all available JPMS services.")
public class ServiceListCommand extends AbstractCommand {

    /**
     * Show properties. Just add -s to command!
     */
    @Parameter(names = {"-s", "--standard"}, required = false,
            description = "sets if it is necessary to show standard services of Java SE and Java JDK")
    private boolean standardRequired = false;

    @Override
    public String getTitle() {
        return "Listing services";
    }

    @Override
    public void execute(final CommandContext context, MessagePrinter printer) throws Exception {
        printer.printlnMessage("Services by layer:");
        AsciiTable table = new AsciiTable();
        CWC_FixedWidth cwc = AsciiTableUtils.createColumnWidthCalculator(printer.getWidth(),
                ColumnWidth.chars(5),
                ColumnWidth.percent(30),
                ColumnWidth.percent(35),
                ColumnWidth.percent(35));
        table.getRenderer().setCWC(cwc);
        table.setTextAlignment(TextAlignment.LEFT);
        table.addRule();
        Map<String, List<ModuleModel>> modulesByLayer = null;
        if (context.isExecutionLocal()) {
            modulesByLayer = context.getFramework().getJvmInspector().getModulesInfo()
                .getModulesByLayerName();
        } else {
            var client = new DomainClient(context.getClient(), context.getSession());
            modulesByLayer = client.getModulesByLayerName();
        }

        int serviceCount = 0;
        for (Map.Entry<String,  List<ModuleModel>> entry : modulesByLayer.entrySet()) {
            //sorting by name
            var sortedList = entry.getValue().stream()
                    .sorted((object1, object2) -> object1.getName().compareTo(object2.getName()))
                    .collect(Collectors.toList());
            table.addRow(null, null, null, "Layer: " + entry.getKey());
            table.addRule();
            table.addRow("#", "Module", "Uses", "Provides");
            table.addRule();
            for (ModuleModel module : sortedList) {
                ModuleDescriptorModel descriptor = module.getDescriptor();
                if (descriptor == null) {
                    continue;
                }
                if (!this.standardRequired) {
                    if (module.getName().startsWith("java.") || module.getName().startsWith("jdk.")
                        || module.getName().startsWith("oracle.") || module.getName().startsWith("javafx.")) {
                        continue;
                    }
                }
                String uses = null;
                String provides = null;
                if (descriptor.getProvides() != null && !descriptor.getProvides().isEmpty()) {
                    var sb = new StringBuilder();
                    var sep = "";
                    for (var p : descriptor.getProvides()) {
                        sb.append(sep);
                        sb.append(p.getService());
                        sb.append(" with ");
                        sb.append(p.getProviders().toString());
                        sep = ", ";
                    }
                    provides = sb.toString();
                }
                if (descriptor.getUses() != null && !descriptor.getUses().isEmpty()) {
                    uses = String.join(", ", descriptor.getUses());
                }
                if (uses != null || provides != null) {
                    AT_Row row = table.addRow(
                            ++serviceCount,
                            Objects.toString(module.getName(), ""),
                            Objects.toString(uses, ""),
                            Objects.toString(provides, "")
                    );
                    row.getCells().get(1).getContext().setTextAlignment(TextAlignment.LEFT);
                    row.getCells().get(2).getContext().setTextAlignment(TextAlignment.LEFT);
                    row.getCells().get(3).getContext().setTextAlignment(TextAlignment.LEFT);
                    table.addRule();
                }
            }
        }
        printer.printlnMessage(table.render());
    }
}
