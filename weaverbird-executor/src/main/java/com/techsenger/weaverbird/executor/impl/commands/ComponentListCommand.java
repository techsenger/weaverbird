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
import com.techsenger.weaverbird.core.api.component.ComponentConfigDto;
import com.techsenger.weaverbird.core.api.component.ComponentDescriptorDto;
import com.techsenger.weaverbird.core.api.message.MessagePrinter;
import com.techsenger.weaverbird.executor.api.CommandContext;
import com.techsenger.weaverbird.executor.spi.AbstractCommand;
import com.techsenger.weaverbird.executor.spi.CommandMeta;
import com.techsenger.weaverbird.executor.spi.LocalCommand;
import com.techsenger.weaverbird.executor.spi.RemoteCommand;
import com.techsenger.weaverbird.net.client.api.DomainClient;
import de.vandermeer.asciitable.AsciiTable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 *
 * @author Pavel Castornii
 */
@LocalCommand
@RemoteCommand
@CommandMeta(name = "component:list", description = "Lists the components with different states.")
public class ComponentListCommand extends AbstractCommand {

    /**
     * Name.
     */
    @Parameter(names = {"-s", "--state"}, required = false, description = "sets the state of the components "
            + "(added / resolved / deployed / activated), default is deployed")
    private String state = "deployed";

    @Override
    public String getTitle() {
        return "Listing components";
    }

    @Override
    public void execute(final CommandContext context, MessagePrinter printer) throws Exception {
        AsciiTable table;
        var framework = context.getFramework();
        if (this.state.equalsIgnoreCase("added")) {
            printer.printlnMessage("Added components:");
            List<? extends ComponentConfigDto> configs = new ArrayList<>();
            if (context.isExecutionLocal()) {
                List<ComponentConfigDto> modConfigs = new ArrayList<>();
                for (var entry : framework.getRegistry().getAddedComponents()) {
                    var config = framework.getComponentManager().readConfig(entry.getName(), entry.getVersion());
                    modConfigs.add(config);
                }
                configs = modConfigs;
            } else {
                var client = new DomainClient(context.getClient(), context.getSession());
                configs = client.getAddedComponents();
            }
            table = createConfigTable(configs, printer.getWidth());
        } else if (this.state.equalsIgnoreCase("resolved")) {
            printer.printlnMessage("Resolved components:");
            List<? extends ComponentConfigDto> configs;
            if (context.isExecutionLocal()) {
                List<ComponentConfigDto> modConfigs = new ArrayList<>();
                for (var entry : framework.getRegistry().getResolvedComponents()) {
                    var config = framework.getComponentManager().readConfig(entry.getName(), entry.getVersion());
                    modConfigs.add(config);
                }
                configs = modConfigs;
            } else {
                var client = new DomainClient(context.getClient(), context.getSession());
                configs = client.getResolvedComponents();
            }
            table = createConfigTable(configs, printer.getWidth());
        } else if (this.state.equalsIgnoreCase("deployed")) {
            printer.printlnMessage("Deployed components:");
            List<? extends ComponentDescriptorDto> descriptors;
            if (context.isExecutionLocal()) {
                descriptors = new ArrayList<>(context.getFramework().getComponentManager().getDescriptors());
            } else {
                var client = new DomainClient(context.getClient(), context.getSession());
                descriptors = client.getDeployedComponents();
            }
            table = createDescriptorTable(descriptors, printer.getWidth());
        } else if (this.state.equalsIgnoreCase("activated")) {
            printer.printlnMessage("Activated components:");
            List<? extends ComponentDescriptorDto> descriptors;
            if (context.isExecutionLocal()) {
                descriptors = new ArrayList<>(context.getFramework().getComponentManager().getDescriptors());
            } else {
                var client = new DomainClient(context.getClient(), context.getSession());
                descriptors = client.getActivatedComponents();
            }
            descriptors = descriptors.stream().filter((d) -> d.isActivated())
                    .collect(Collectors.toCollection(ArrayList::new));
            table = createDescriptorTable(descriptors, printer.getWidth());
        } else {
            throw new IllegalArgumentException("Type " + this.state + " is unknown");
        }
        printer.printlnMessage(table.render());
    }

    private AsciiTable createConfigTable(List<? extends ComponentConfigDto> configs, int width) {
        AsciiTable table = new AsciiTable();
        table.addRule();
        table.addRow("#", "Name", "Version");
        table.addRule();
        var cwc = AsciiTableUtils.createColumnWidthCalculator(width,
                ColumnWidth.percent(5),
                ColumnWidth.percent(75),
                ColumnWidth.percent(20));
        int index = 0;
        for (var config : configs) {
            table.addRow(String.valueOf(++index), config.getName(), config.getVersion());
            table.addRule();
        }
        table.getRenderer().setCWC(cwc);
        return table;
    }

    private AsciiTable createDescriptorTable(List<? extends ComponentDescriptorDto> descriptors, int width) {
        AsciiTable table = new AsciiTable();
        table.addRule();
        table.addRow("#", "Name", "Version", "Type", "Id", "Alias", "PId", "PAlias");
        table.addRule();
        var cwc = AsciiTableUtils.createColumnWidthCalculator(width,
                ColumnWidth.chars(5),
                ColumnWidth.percent(40),
                ColumnWidth.chars(19), //0.0.1-snapshot
                ColumnWidth.chars(14),
                ColumnWidth.chars(5),
                ColumnWidth.percent(30),
                ColumnWidth.chars(5),
                ColumnWidth.percent(30));
        int index = 0;
        //sort them by id
        Collections.sort(descriptors, new Comparator<ComponentDescriptorDto>() {
            @Override
            public int compare(ComponentDescriptorDto d1, ComponentDescriptorDto d2) {
                return d1.getId() - d2.getId();
            }
        });
        for (var descriptor : descriptors) {
            List<String> columns = new ArrayList<>();
            columns.add(String.valueOf(++index));
            columns.add(descriptor.getConfig().getName());
            columns.add(descriptor.getConfig().getVersion().getFull());
            var type = descriptor.getConfig().getType();
            type = type == null ? "" : type;
            columns.add(type);
            columns.add(String.valueOf(descriptor.getId()));
            if (descriptor.getAlias() != null) {
                columns.add(descriptor.getAlias());
            } else {
                columns.add("");
            }
            if (!descriptor.getParents().isEmpty()) {
                StringJoiner joiner1 = new StringJoiner(",");
                descriptor.getParents().stream().forEach(d -> joiner1.add(String.valueOf(d.getId())));
                columns.add(joiner1.toString());
                StringJoiner joiner2 = new StringJoiner(",");
                descriptor.getParents().stream().forEach(d -> joiner2.add(String.valueOf(d.getAlias())));
                columns.add(joiner2.toString());
            } else {
                columns.add("");
                columns.add("");
            }
            table.addRow(columns);
            table.addRule();
        }
        table.getRenderer().setCWC(cwc);
        return table;
    }

}
