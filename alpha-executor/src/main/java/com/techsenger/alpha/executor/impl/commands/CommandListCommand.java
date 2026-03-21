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
import com.techsenger.toolkit.ascii.table.AsciiTableUtils;
import com.techsenger.toolkit.ascii.table.ColumnWidth;
import com.techsenger.toolkit.core.collection.ListUtils;
import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Pavel Castornii
 */
@LocalCommand
@RemoteCommand
@CommandMeta(name = "command:list", description = "Gives list of all existing commands.")
public class CommandListCommand extends AbstractCommand {

    private static final class InternalCommandInfo {

        private String name;

        private String module;

        private String description;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getModule() {
            return module;
        }

        public void setModule(String module) {
            this.module = module;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    @Parameter(names = {"-d", "--details"}, required = false,
            description = "adds details of commands (module, description etc)")
    private boolean detailsIncluded = false;

    @Override
    public String getTitle() {
        return "Listing existing commands";
    }

    @Override
    public void execute(final CommandContext context, MessagePrinter printer) throws Exception {
        List<InternalCommandInfo> commandInfos = context.getExecutor().getCommandsByName().values()
                .stream()
                .filter(c -> {
                    if (context.isExecutionLocal()) {
                        return c.isLocal();
                    } else {
                        return c.isRemote();
                    }
                })
                .map(c -> {
                    var info = new InternalCommandInfo();
                    info.setName(c.getName());
                    info.setDescription(c.getDescription());
                    info.setModule(c.getModuleName());
                    return info;
                })
                .collect(Collectors.toList());
        if (commandInfos.isEmpty()) {
            printer.printlnMessage("No commands found");
            return;
        }
        Collections.sort(commandInfos, Comparator.comparing(InternalCommandInfo::getName));
        AsciiTable table = null;
        if (this.detailsIncluded) {
            table = this.createTableWithDetails(commandInfos, printer.getWidth());
        } else {
            table = this.createTableWithoutDetails(commandInfos, printer.getWidth());
        }
        printer.printlnMessage("Supported commands:");
        printer.printlnMessage(table.render());

    }

    private AsciiTable createTableWithDetails(List<InternalCommandInfo> commandInfos, int width) {
        AsciiTable table = new AsciiTable();
        var cwc = AsciiTableUtils.createColumnWidthCalculator(width,
                ColumnWidth.chars(5),
                ColumnWidth.percent(15),
                ColumnWidth.percent(65),
                ColumnWidth.percent(20));
        table.getRenderer().setCWC(cwc);
        table.setTextAlignment(TextAlignment.LEFT);
        table.addRule();
        table.addRow("#", "Command", "Description", "Module");
        table.addRule();
        for (var i = 0; i < commandInfos.size(); i++) {
            var info = commandInfos.get(i);
            table.addRow(i + 1, info.getName(), info.getDescription(), info.getModule());
            table.addRule();
        }
        return table;
    }

    private AsciiTable createTableWithoutDetails(List<InternalCommandInfo> commandInfos, int width) {
        AsciiTable table = new AsciiTable();
        var cwc = AsciiTableUtils.createColumnWidthCalculator(width,
                ColumnWidth.percent(33),
                ColumnWidth.percent(33),
                ColumnWidth.percent(34));
        table.getRenderer().setCWC(cwc);
        table.setTextAlignment(TextAlignment.LEFT);
        table.addRule();
        int partitionSize = commandInfos.size() / 3;
        if ((commandInfos.size() % 3) != 0) {
            partitionSize++;
        }
        List<List<InternalCommandInfo>> sublists = ListUtils.partition(commandInfos, partitionSize);
        var list0 = sublists.get(0);
        List<InternalCommandInfo> list1 = null;
        List<InternalCommandInfo> list2 = null;
        if (sublists.size() > 1) {
            list1 = sublists.get(1);
        }
        if (sublists.size() > 2) {
            list2 = sublists.get(2);
        }
        for (var i = 0; i < list0.size(); i++) {
            String column0 = list0.get(i).getName();
            String column1 = "";
            String column2 = "";
            if (list1 != null && i < list1.size()) {
                column1 = list1.get(i).getName();
            }
            if (list2 != null && i < list2.size()) {
                column2 = list2.get(i).getName();
            }
            table.addRow(column0, column1, column2);
            table.addRule();
        }
        return table;
    }
}
