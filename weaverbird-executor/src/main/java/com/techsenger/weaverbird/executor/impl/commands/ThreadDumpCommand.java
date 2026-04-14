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
import com.techsenger.toolkit.core.model.ThreadInfoModel;
import de.vandermeer.asciitable.AT_Row;
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
@CommandMeta(name = "thread:dump", description = "Gives dump of all threads.")
public class ThreadDumpCommand extends AbstractCommand {

    @Override
    public String getTitle() {
        return "Dumping threads";
    }

    @Override
    public void execute(final CommandContext context, MessagePrinter printer) throws Exception {
        printer.printlnMessage("Thread dump:");
        List<ThreadInfoModel> threadInfos;
        if (context.isExecutionLocal()) {
            threadInfos = context.getFramework().getJvmInspector().getThreadInfos();
        } else {
            var client = new DomainClient(context.getClient(), context.getSession());
            threadInfos = client.getThreadInfos();
        }
        //sort them by thread id
        Collections.sort(threadInfos, new Comparator<ThreadInfoModel>() {
            @Override
            public int compare(ThreadInfoModel d1, ThreadInfoModel d2) {
                return Long.compare(d1.getThreadId(), d2.getThreadId());
            }
        });
        AsciiTable table = new AsciiTable();
        CWC_FixedWidth cwc = AsciiTableUtils.createColumnWidthCalculator(printer.getWidth(),
                ColumnWidth.chars(5),
                ColumnWidth.percent(5),
                ColumnWidth.percent(25),
                ColumnWidth.percent(15),
                ColumnWidth.percent(25),
                ColumnWidth.percent(5),
                ColumnWidth.percent(25));
        table.getRenderer().setCWC(cwc);
        table.setTextAlignment(TextAlignment.LEFT);
        table.addRule();
        table.addRow("#", "ThreadId", "ThreadName", "ThreadState", "LockName", "LockOwnerId", "LockOwnerName");
        table.addRule();
        var threadCount = 0;
        StringBuilder builder = new StringBuilder();
        for (ThreadInfoModel info:threadInfos) {
            table.addRow(
                    ++threadCount,
                    Objects.toString(info.getThreadId(), ""),
                    Objects.toString(info.getThreadName(), ""),
                    Objects.toString(info.getThreadState(), ""),
                    Objects.toString(info.getLockName(), ""),
                    Objects.toString(info.getLockOwnerId(), ""),
                    Objects.toString(info.getLockOwnerName(), ""));
            table.addRule();
            final StackTraceElement[] stackTraceElements = info.getStackTraceElements();
            for (final StackTraceElement stackTraceElement : stackTraceElements) {
                builder.append("        at ");
                builder.append(stackTraceElement + "<br>");
            }
            AT_Row row = table.addRow(null, null, null, null, null, null, builder.toString());
            row.getCells().get(6).getContext().setTextAlignment(TextAlignment.LEFT);
            table.addRule();
            builder.setLength(0);
        }
        printer.printlnMessage(table.render());
    }
}
