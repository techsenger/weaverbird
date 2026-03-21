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

package com.techsenger.alpha.console.gui.session;

import com.techsenger.alpha.api.Framework;
import com.techsenger.alpha.api.net.session.SessionDescriptor;
import com.techsenger.alpha.console.gui.keys.ConsoleComponentKeys;
import com.techsenger.tabshell.core.node.AbstractNodeViewModel;
import com.techsenger.tabshell.core.node.NodeKey;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Pavel Castornii
 */
public class SessionsToolBarViewModel extends AbstractNodeViewModel {

    private final ObservableList<SessionDescriptor> sessions = FXCollections.observableArrayList();

    /**
     * This sessions is updated only if there is no current session in the sessions after refresh/.
     */
    private final ObjectProperty<SessionDescriptor> session = new SimpleObjectProperty<>();

    private final ObjectProperty<SessionDescriptor> internalSession = new SimpleObjectProperty<>();

    private boolean refreshing = false;

    public SessionsToolBarViewModel() {
        refreshSessions();
        internalSession.addListener((ov, oldV, newV) -> {
            if (!refreshing) {
                session.set(newV);
            }
        });
        session.addListener((ov, oldV, newV) -> internalSession.set(newV));
    }

    @Override
    public NodeKey getKey() {
        return ConsoleComponentKeys.SESSIONS_TOOL_BAR;
    }

    public ObservableList<SessionDescriptor> getSessions() {
        return sessions;
    }

    public ObjectProperty<SessionDescriptor> sessionProperty() {
        return session;
    }

    public boolean select(SessionDescriptor session) {
        for (var s : sessions) {
            if (s.equals(session)) {
                internalSession.set(s);
                return true;
            }
        }
        internalSession.set(null);
        return false;
    }

    ObjectProperty<SessionDescriptor> internalSessionProperty() {
        return internalSession;
    }

    void refreshSessions() {
        this.refreshing = true;
        sessions.clear();
        var list = Framework.getServiceManager().getClientSessions().stream().map(s -> new SessionDescriptor(s))
                .collect(Collectors.toCollection(ArrayList::new));
        list.sort(Comparator.comparing(SessionDescriptor::getName));
        sessions.addAll(list);
        if (session.get() != null) {
            if (!select(session.get())) {
                session.set(null);
            }
        }
        this.refreshing = false;
    }

    void detachSession() {
        session.set(null);
    }
}
