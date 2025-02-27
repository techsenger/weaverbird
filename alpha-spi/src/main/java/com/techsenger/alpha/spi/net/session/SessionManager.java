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

package com.techsenger.alpha.spi.net.session;

import com.techsenger.alpha.api.net.session.SessionInfo;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
public class SessionManager<T extends Session> {

    private static final Logger logger = LoggerFactory.getLogger(SessionManager.class);

    private final Map<String, T> sessionsByName = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, ReentrantLock> locksByName = new ConcurrentHashMap<>();

    public SessionManager() {

    }

    public Collection<SessionInfo> getSessionInfos() {
        return sessionsByName.values().stream().collect(Collectors.toList());
    }

    public void addSession(T session) {
        sessionsByName.put(session.getName(), session);
        locksByName.put(session.getName(), new ReentrantLock());
    }

    public T acquireSession(String name, boolean shouldWait) throws TimeoutException {
        var lock = this.locksByName.get(name);
        if (lock == null) {
            return null;
        }
        T session = null;
        if (lock.tryLock()) {
            session = this.sessionsByName.get(name); //session can be closed
        } else {
            if (shouldWait) {
                try {
                    if (lock.tryLock(10, TimeUnit.SECONDS)) {
                        session = this.sessionsByName.get(name); //session can be closed
                    } else {
                        throw new TimeoutException();
                    }
                } catch (InterruptedException ex) {
                    logger.debug("Error trying to lock", ex);
                }
            }
        }
        return session;
    }

    public void releaseSession(T session) {
        var lock = locksByName.get(session.getName());
        lock.unlock();
    }

    public void removeSession(T session) {
        var c = sessionsByName.remove(session.getName());
        var lock = locksByName.remove(session.getName());
        if (lock != null && lock.isLocked()) {
             lock.unlock();
         }
    }
}
