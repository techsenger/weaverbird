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

import com.techsenger.weaverbird.core.api.message.MessagePrinter;
import com.techsenger.weaverbird.executor.api.CommandContext;
import com.techsenger.weaverbird.executor.spi.AbstractCommand;
import com.techsenger.weaverbird.executor.spi.CommandMeta;
import com.techsenger.weaverbird.executor.spi.LocalCommand;
import com.techsenger.weaverbird.executor.spi.RemoteCommand;
import com.techsenger.weaverbird.net.client.api.DomainClient;
import com.techsenger.toolkit.ascii.table.AsciiTableUtils;
import com.techsenger.toolkit.ascii.table.ColumnWidth;
import com.techsenger.toolkit.core.model.ThreadModel;
import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.asciitable.CWC_FixedWidth;
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Pavel Castornii
 */
@LocalCommand
@RemoteCommand
@CommandMeta(name = "thread:list", description = "Gives list of all threads.")
public class ThreadListCommand extends AbstractCommand {

    @Override
    public String getTitle() {
        return "Listing threads";
    }

    @Override
    public void execute(final CommandContext context, MessagePrinter printer) throws Exception {
        printer.printlnMessage("Thread dump:");
        List<ThreadModel> threads;
        if (context.isExecutionLocal()) {
            threads = context.getFramework().getJvmInspector().getThreads();
        } else {
            var client = new DomainClient(context.getClient(), context.getSession());
            threads = client.getThreads();
        }
        //sort them by id
        Collections.sort(threads, new Comparator<ThreadModel>() {
            @Override
            public int compare(ThreadModel d1, ThreadModel d2) {
                return Long.compare(d1.getId(), d2.getId());
            }
        });
        AsciiTable table = new AsciiTable();
        CWC_FixedWidth cwc = AsciiTableUtils.createColumnWidthCalculator(printer.getWidth(),
                ColumnWidth.chars(5),
                ColumnWidth.percent(5),
                ColumnWidth.percent(15),
                ColumnWidth.percent(5),
                ColumnWidth.percent(5),
                ColumnWidth.percent(20),
                ColumnWidth.percent(25),
                ColumnWidth.percent(25));
        table.getRenderer().setCWC(cwc);
        table.setTextAlignment(TextAlignment.LEFT);
        table.addRule();
        table.addRow("#", "Id", "State", "Prt", "Daemon", "Group", "Name", "ContextCL");
        table.addRule();
        var threadCount = 0;
        for (ThreadModel thread:threads) {
            table.addRow(
                    ++threadCount,
                    Objects.toString(thread.getId(), ""),
                    Objects.toString(thread.getState(), ""),
                    Objects.toString(thread.getPriority(), ""),
                    Objects.toString(thread.isDaemon(), ""),
                    Objects.toString(thread.getGroup(), ""),
                    Objects.toString(thread.getName(), ""),
                    Objects.toString(thread.getContextClassLoader(), ""));
            table.addRule();
        }
        printer.printlnMessage(table.render());
    }
}
