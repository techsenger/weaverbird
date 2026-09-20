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

package com.techsenger.weaverbird.gui.session;

import com.techsenger.patternfx.mvvm.ChildComposer;
import com.techsenger.shellfx.core.area.AbstractAreaViewModel;
import com.techsenger.toolkit.fx.value.ObservableSource;
import com.techsenger.toolkit.fx.value.SimpleObservableSource;
import com.techsenger.weaverbird.net.client.api.ClientService;
import com.techsenger.weaverbird.net.client.api.ClientSession;
import java.util.Comparator;
import java.util.List;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Base ViewModel for toolbars that let the user pick the active {@code ClientSession} out of the sessions
 * currently known to a {@code ClientService}, and refresh that list on demand.
 *
 * @param <C> the composer type
 * @author Pavel Castornii
 */
public abstract class AbstractSessionToolBarViewModel<C extends ChildComposer> extends AbstractAreaViewModel<C> {

    private final ObservableList<ClientSession> modifiableSessions = FXCollections.observableArrayList();

    private final ObservableList<ClientSession> sessions =
            FXCollections.unmodifiableObservableList(modifiableSessions);

    private final ReadOnlyObjectWrapper<ClientSession> session = new ReadOnlyObjectWrapper<>();

    private final ObservableSource<ClientSession> sessionSource = new SimpleObservableSource<>();

    private final ClientService client;

    private SessionToolBarParams params;

    public AbstractSessionToolBarViewModel(SessionToolBarParams params) {
        super(params);
        this.client = params.getClient();
        this.params = params;
        this.session.addListener((ov, oldV, newV) -> onSessionChanged(newV));
    }

    /**
     * Returns an unmodifiable list of sessions.
     */
    public ObservableList<ClientSession> getSessions() {
        return sessions;
    }

    public ClientSession getSession() {
        return session.get();
    }

    public ReadOnlyObjectProperty<ClientSession> sessionProperty() {
        return session.getReadOnlyProperty();
    }

    /**
     * Reloads the session list from the {@code ClientService} and re-applies the currently selected session.
     */
    public void refresh() {
        setSession(getSession());
    }

    @Override
    protected void postInitialize() {
        super.postInitialize();
        setSession(params.getSession());
        this.params = null;
    }

    /**
     * Called whenever the selected session actually changes, whether from a user pick in the combo box or from
     * a programmatic {@link #setSession(ClientSession)}. No-op by default; overridden by subclasses that need
     * to react.
     *
     * @param session the newly selected session
     */
    protected void onSessionChanged(ClientSession session) {
        // no-op, hook for subclasses
    }

    /**
     * Reloads the session list and requests the given session be selected in the combo box.
     *
     * @param session the session to select
     */
    protected void setSession(ClientSession session) {
        var clientSessions = fetchClientSessions();
        if (clientSessions != null) {
            modifiableSessions.setAll(clientSessions);
        }
        sessionSource.next(session);
    }

    ReadOnlyObjectWrapper<ClientSession> sessionWrapper() {
        return session;
    }

    ObservableSource<ClientSession> sessionSource() {
        return sessionSource;
    }

    private List<ClientSession> fetchClientSessions() {
        if (client == null) {
            return null;
        }
        return client.getSessionsByName().values().stream()
                .sorted(Comparator.comparing(ClientSession::getName)).toList();
    }
}
