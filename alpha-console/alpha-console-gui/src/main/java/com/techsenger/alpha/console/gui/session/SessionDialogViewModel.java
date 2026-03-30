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

package com.techsenger.alpha.console.gui.session;

import com.techsenger.alpha.api.Framework;
import com.techsenger.alpha.api.net.session.ClientSession;
import com.techsenger.alpha.console.gui.keys.ConsoleComponentKeys;
import com.techsenger.alpha.console.gui.settings.ConsoleSettings;
import com.techsenger.alpha.console.gui.style.ConsoleIcons;
import com.techsenger.mvvm4fx.core.HistoryPolicy;
import com.techsenger.tabshell.core.dialog.DialogKey;
import com.techsenger.tabshell.core.dialog.DialogScope;
import com.techsenger.tabshell.core.history.HistoryManager;
import com.techsenger.tabshell.kit.dialog.AbstractSimpleDialogViewModel;
import com.techsenger.tabshell.kit.dialog.alert.AlertDialogType;
import com.techsenger.tabshell.kit.dialog.alert.AlertDialogViewModel;
import com.techsenger.tabshell.material.icon.FontIcon;
import com.techsenger.toolkit.fx.value.ObservableSource;
import com.techsenger.toolkit.fx.value.SimpleObservableSource;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Pavel Castornii
 */
public class SessionDialogViewModel extends AbstractSimpleDialogViewModel {

    private final ObservableList<ClientSession> sessions = FXCollections.observableArrayList();

    private final ObjectProperty<ClientSession> session = new SimpleObjectProperty<>();

    private final ObservableSource<Boolean> sortRequired = new SimpleObservableSource<>();

    private final ConsoleSettings settings;

    public SessionDialogViewModel(ConsoleSettings settings, HistoryManager historyManager) {
        super(DialogScope.SHELL, true);
        this.settings = settings;
        setTitle("Sessions");
        setIcon(new FontIcon(ConsoleIcons.NETWORK));
        setHistoryPolicy(HistoryPolicy.APPEARANCE);
        setHistoryProvider(() -> historyManager.getHistory(SessionDialogHistory.class, SessionDialogHistory::new));
        setMinWidth(600);
        setMinHeight(300);
        refreshSessions();
    }

    @Override
    public DialogKey getKey() {
        return ConsoleComponentKeys.SESSIONS_DIALOG;
    }

    @Override
    public SessionDialogHelper getComponentHelper() {
        return (SessionDialogHelper) super.getComponentHelper();
    }

    ObservableList<ClientSession> getSessions() {
        return sessions;
    }

    ObjectProperty<ClientSession> sessionProperty() {
        return session;
    }

    ObservableSource<Boolean> getSortRequired() {
        return sortRequired;
    }

    void createNewSession() {
        var sessionDialog = new NewSessionDialogViewModel(this.settings);
        sessionDialog.okActionProperty().set(() -> {
            if (sessionDialog.validate()) {
                sessionDialog.close();
                try {
                    var con = sessionDialog.connectionProperty().get();
                    var name = sessionDialog.nameProperty().get();
                    var client = Framework.getServiceManager().getClient(con.getProtocol());
                    client.openSession(name, con.getHost(), con.getPort(), con.isSecure(), con.getLoginName(),
                            con.getLoginPassword());
                    refreshSessions();
                } catch (Exception ex) {
                    var alertDialog = new AlertDialogViewModel(DialogScope.SHELL, AlertDialogType.ERROR,
                            "Error creating a new session - " + ex.getMessage());
                    getComponentHelper().openAlertDialog(alertDialog);
                }
            }
        });
        getComponentHelper().openNewSessionDialog(sessionDialog);
    }

    void closeSession() {
        var ses = session.get();
        if (ses != null) {
            try {
                Framework.getServiceManager().getClient(ses.getProtocol()).closeSession(ses.getName());
                refreshSessions();
            } catch (Exception ex) {
                var alertDialog = new AlertDialogViewModel(DialogScope.SHELL, AlertDialogType.ERROR,
                            "Error closing the session - " + ex.getMessage());
                    getComponentHelper().openAlertDialog(alertDialog);
            }
        }
    }

    void refreshSessions() {
        sessions.clear();
        sessions.addAll(Framework.getServiceManager().getClientSessions());
        sortRequired.next(true);
    }
}
