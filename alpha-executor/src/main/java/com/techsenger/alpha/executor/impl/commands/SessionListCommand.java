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

import com.techsenger.alpha.core.api.message.MessagePrinter;
import com.techsenger.alpha.executor.api.CommandContext;
import com.techsenger.alpha.executor.spi.AbstractCommand;
import com.techsenger.alpha.executor.spi.CommandMeta;
import com.techsenger.alpha.executor.spi.LocalCommand;
import com.techsenger.alpha.executor.spi.RemoteCommand;
import com.techsenger.alpha.net.client.api.DomainClient;
import com.techsenger.toolkit.ascii.table.AsciiTableUtils;
import com.techsenger.toolkit.ascii.table.ColumnWidth;
import de.vandermeer.asciitable.AsciiTable;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 *
 * @author Pavel Castornii
 */
@LocalCommand
@RemoteCommand
@CommandMeta(name = "session:list", description = "Lists all existing sessions.")
public class SessionListCommand extends AbstractCommand {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final AsciiTable table = new AsciiTable();

    @Override
    public String getTitle() {
        return "Listing all existing sessions";
    }

    @Override
    public void execute(CommandContext context, MessagePrinter printer) throws Exception {
        if (context.isExecutionLocal()) {
            listClientSessions(context, printer);
        } else {
            listServerSessions(context, printer);
        }
    }

    private void listClientSessions(CommandContext context, MessagePrinter printer) {
        var sessions = context.getClient().getSessionsByName().values();
        if (sessions.isEmpty()) {
            printer.printlnMessage("Currently no open sessions");
            return;
        }
        table.addRule();
        table.addRow("#", "Name", "Login Name", "Opened At", "Host", "Port", "Status");
        table.addRule();

        var cwc = AsciiTableUtils.createColumnWidthCalculator(printer.getWidth(),
                    ColumnWidth.chars(5),
                    ColumnWidth.percent(34),
                    ColumnWidth.percent(33),
                    ColumnWidth.percent(33),
                    ColumnWidth.chars(30),
                    ColumnWidth.chars(7),
                    ColumnWidth.chars(7));
        int index = 0;
        var columns = new ArrayList<String>();
        for (var session : sessions) {
            columns.add(String.valueOf(++index));
            columns.add(session.getName());
            columns.add(session.getLoginName());
            columns.add(session.getOpenedAt().format(formatter));
            columns.add(session.getHost());
            columns.add(String.valueOf(session.getPort()));
            columns.add(getStatus(session.isClosed()));
            table.addRow(columns);
            columns.clear();
            table.addRule();
        }

        table.getRenderer().setCWC(cwc);
        printer.printlnMessage(table.render());
    }

    private void listServerSessions(CommandContext context, MessagePrinter printer) throws Exception {
        var client = new DomainClient(context.getClient(), context.getSession());
        var sessions = client.getSessions();
        if (sessions.isEmpty()) {
            printer.printlnMessage("Currently no open sessions");
            return;
        }
        table.addRule();
        table.addRow("#", "Login Name", "Opened At", "Host", "Port", "Status");
        table.addRule();

        var cwc = AsciiTableUtils.createColumnWidthCalculator(printer.getWidth(),
                    ColumnWidth.chars(5),
                    ColumnWidth.percent(50),
                    ColumnWidth.percent(50),
                    ColumnWidth.chars(30),
                    ColumnWidth.chars(7),
                    ColumnWidth.chars(7));

        int index = 0;
        var columns = new ArrayList<String>();
        for (var session : sessions) {
            columns.add(String.valueOf(++index));
            columns.add(session.getLoginName());
            columns.add(session.getOpenedAt().format(formatter));
            columns.add(session.getRemoteHost());
            columns.add(String.valueOf(session.getRemotePort()));
            columns.add(getStatus(session.isClosed()));
            table.addRow(columns);
            columns.clear();
            table.addRule();
        }

        table.getRenderer().setCWC(cwc);
        printer.printlnMessage(table.render());
    }

    private String getStatus(boolean closed) {
        return closed ? "CLOSED" : "OPEN";
    }

}
