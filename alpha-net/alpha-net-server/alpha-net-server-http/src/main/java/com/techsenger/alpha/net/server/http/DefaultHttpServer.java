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

package com.techsenger.alpha.net.server.http;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsServer;
import com.techsenger.alpha.api.net.security.AuthenticationException;
import com.techsenger.alpha.api.net.session.SessionInfo;
import com.techsenger.alpha.net.shared.http.Constants;
import com.techsenger.alpha.net.shared.http.UrlPaths;
import com.techsenger.toolkit.core.ssl.SslUtils;
import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.net.ssl.SSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
public class DefaultHttpServer {

    private static final Logger logger = LoggerFactory.getLogger(DefaultHttpServer.class);

    /**
     * ScavengerTimerTask.
     */
    private final class ScavengerTimerTask extends TimerTask {

        private static final long SESSION_TIMEOUT_SECONDS = 30 * 60;

        @Override
        public void run() {
            int collectedSessionCount = 0;
            var iterator = sessionsByUuid.entrySet().iterator();
            while (iterator.hasNext()) {
                var entry = iterator.next();
                HttpSession session = entry.getValue();
                long currentTime = Instant.now().getEpochSecond();
                // delete if value is up to date, otherwise leave for next round
                if (currentTime - session.getLastAccessedTime() >= SESSION_TIMEOUT_SECONDS) {
                    iterator.remove();
                    doStopSession(session);
                    closeSession(session);
                    collectedSessionCount++;
                }
            }
            logger.debug("SessionScavenger collected {} sessions; currently {} sessions", collectedSessionCount,
                    sessionsByUuid.size());
        }
    };

    /**
     * Http server. Note, there are two classes: HttpServer and HttpsServer
     */
    private HttpServer server;

    /**
     * Sessions.
     */
    private final Map<String, HttpSession> sessionsByUuid = new ConcurrentHashMap<>();

    /**
     * Scavenger timer.
     */
    private Timer scavengerTimer;

    /**
     * Scavenger timer task.
     */
    private TimerTask scavengerTimerTask;

    private boolean secure;

    public Collection<SessionInfo> getSessionInfos() {
        return Collections.unmodifiableCollection(sessionsByUuid.values());
    }

    public void start(String host, int port, boolean secure) {
        if (this.server != null) {
            throw new IllegalStateException("Server is already running");
        }
        try {
            var address = new InetSocketAddress(host, port);
            if (secure) {
                this.server = HttpsServer.create(address, 0);
                // Set up the HTTPS context and parameters
                SSLContext sslContext = SslUtils.buildContext(Constants.SSL_CERTIFICATE_ALIAS);
                var httpsConfigurator = new DefaultHttpConfigurator(sslContext);
                ((HttpsServer) this.server).setHttpsConfigurator(httpsConfigurator);
            } else {
                this.server = HttpServer.create(address, 0);
            }
            server.createContext(UrlPaths.LOGIN, new LoginHandler(this));
            server.createContext(UrlPaths.COMMAND_EXECUTE, new CommandExecuteHandler(this));
            server.createContext(UrlPaths.COMMAND_INFO, new CommandInfoHandler(this));
            server.createContext(UrlPaths.LAYER_INFO, new LayerInfoHandler(this));
            server.createContext(UrlPaths.COMPONENT_STATE, new ComponentStateHandler(this));
            server.createContext(UrlPaths.LOGOUT, new LogoutHandler(this));
            server.setExecutor(null); // creates a default executor
            server.start();
            this.startScavenger();
            this.secure = secure;
            logger.debug("HTTP server started");
        } catch (Exception ex) {
            logger.error("Error starting HTTP server", ex);
        }
    }

    public void stop() {
        if (this.server == null) {
            throw new IllegalStateException("Server is not running");
        } else {
            this.stopScavenger();
            this.server.stop(0);
            this.server = null;
            //now we need to destroy all active session.
            //creating new set from set view
            var uuidsSet = new HashSet<String>(this.sessionsByUuid.keySet());
            uuidsSet.forEach((uuid) -> {
                var session = this.stopSession(uuid);
                if (session != null) {
                    this.closeSession(session);
                }
            });
            logger.debug("HTTP server stopped");
        }
    }

    /**
     * Creates a session and returns it.
     *
     * @return
     */
    HttpSession startSession() {
        //we DO NOT encode in 64 base, because Shiro uses ordinary format.
        String uuid = UUID.randomUUID().toString();
        HttpSession session = new HttpSession(uuid);
        session.setLastAccessedTime(Instant.now().getEpochSecond());
        this.sessionsByUuid.put(uuid, session);
        logger.debug("Session with uuid {} was started; currently {} sessions", uuid, this.sessionsByUuid.size());
        return session;
    }

    /**
     * Attention! Scavenger doesn't use this method.
     *
     * @param uuid
     * @return
     */
    HttpSession stopSession(String uuid) {
        HttpSession session = this.sessionsByUuid.remove(uuid);
        doStopSession(session);
        return session;
    }

    /**
     * Returns the session.
     *
     * @param uuid
     */
    HttpSession getSession(String uuid) {
        HttpSession session = this.sessionsByUuid.get(uuid);
        return session;
    }

    boolean isSecure() {
        return secure;
    }

    /**
     * This method is used by scavenger.
     *
     * @param session
     */
    private void doStopSession(HttpSession session) {
        if (session != null) {
            logger.debug("Session with uuid {} was stopped; currently {} sessions", session.getUuid(),
                    this.sessionsByUuid.size());
        }
    }

    /**
     * Closes the session without exceptions.
     *
     * @param session
     */
    private void closeSession(HttpSession session) {
        try {
            session.close();
        } catch (AuthenticationException ex) {
            //do nothing
        } catch (Exception ex) {
            logger.error("Error closing session", ex);
        }
    }

    private void startScavenger() {
        scavengerTimerTask = this.new ScavengerTimerTask();
        scavengerTimer = new Timer(true);
        //we run scavenger every five minute.
        scavengerTimer.scheduleAtFixedRate(this.scavengerTimerTask, 0, 5 * 60 * 1000);
        logger.debug("Scavenger was created");
    }

    private void stopScavenger() {
        scavengerTimerTask.cancel();
        scavengerTimer.cancel();
        scavengerTimerTask = null;
        scavengerTimer = null;
        logger.debug("Scavenger was destroyed");
    }
}
