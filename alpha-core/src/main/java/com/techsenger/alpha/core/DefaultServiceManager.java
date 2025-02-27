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

package com.techsenger.alpha.core;

import com.techsenger.alpha.api.Framework;
import com.techsenger.alpha.api.FrameworkMode;
import com.techsenger.alpha.api.ServiceManager;
import com.techsenger.alpha.api.ServiceTracker;
import com.techsenger.alpha.api.component.Component;
import com.techsenger.alpha.api.executor.CommandExecutor;
import com.techsenger.alpha.api.net.ClientService;
import com.techsenger.alpha.api.net.ServerService;
import com.techsenger.alpha.api.net.session.Protocol;
import com.techsenger.alpha.api.net.session.SessionInfo;
import com.techsenger.alpha.api.net.session.SessionService;
import com.techsenger.alpha.api.repo.RepoService;
import com.techsenger.toolkit.core.jpms.ServiceUtils;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
public class DefaultServiceManager implements ServiceManager {

    private static final Logger logger = LoggerFactory.getLogger(DefaultServiceManager.class);

    private final ServiceTracker serviceTracker = new DefaultServiceTracker();

    private final Map<Protocol, SessionService> servicesByProtocol = new ConcurrentHashMap();

    public DefaultServiceManager() {
        this.serviceTracker.registerTracking(CommandExecutor.class);
        this.serviceTracker.registerTracking(RepoService.class);
    }

    @Override
    public CommandExecutor getCommandExecutor() {
        var trackingResult = this.serviceTracker.getTrackingResult(CommandExecutor.class);
        if (trackingResult.isEmpty()) {
            throw new IllegalStateException("Command executor not found");
        }
        var layers = trackingResult.get();
        if (layers.size() != 1) {
            throw new IllegalStateException("Invalid number of layer - " + layers.size());
        }
        var optional = ServiceUtils.loadProvider(layers.iterator().next(), false, CommandExecutor.class);
        return optional.get();
    }

    @Override
    public RepoService getRepoService() {
        var trackingResult = this.serviceTracker.getTrackingResult(RepoService.class);
        if (trackingResult.isEmpty()) {
            throw new IllegalStateException("Repo service not found");
        }
        var layers = trackingResult.get();
        if (layers.size() != 1) {
            throw new IllegalStateException("Invalid number of layer - " + layers.size());
        }
        var optional = ServiceUtils.loadProvider(layers.iterator().next(), false, RepoService.class);
        return optional.get();
    }

    @Override
    public ServiceTracker getServiceTracker() {
        return this.serviceTracker;
    }

    @Override
    public Collection<SessionInfo> getSessionInfos() {
        return servicesByProtocol.values().stream()
                .flatMap(s -> s.getSessionInfos().stream()).collect(Collectors.toList());
    }

    @Override
    public Map<String, SessionInfo> getSessionInfosByName() {
        return servicesByProtocol.values().stream()
                .flatMap(s -> s.getSessionInfos().stream())
                .collect(Collectors.toMap(c -> c.getName(), c -> c));
    }

    @Override
    public ClientService getClient(Protocol protocol) {
        if (Framework.getMode() != FrameworkMode.CLIENT) {
            return null;
        } else {
            return (ClientService) this.servicesByProtocol.get(protocol);
        }
    }

    @Override
    public ServerService getServer(Protocol protocol) {
        if (Framework.getMode() != FrameworkMode.SERVER) {
            return null;
        } else {
            return (ServerService) this.servicesByProtocol.get(protocol);
        }
    }

    ComponentListener createComponentListener() {
        return new ComponentListener() {
            @Override
            public void onComponentDeployed(Component component) {
                if (Framework.getMode() == FrameworkMode.CLIENT) {
                    var service = ServiceUtils.loadProvider(component.getLayer(), false, ClientService.class);
                    if (service.isPresent()) {
                        servicesByProtocol.put(service.get().getProtocol(), service.get());
                        logger.debug("Added {} client from {}", service.get().getProtocol(),
                                component.getDescriptor().getConfig().getFullName());
                    }
                } else {
                    var service = ServiceUtils.loadProvider(component.getLayer(), false, ServerService.class);
                    if (service.isPresent()) {
                        servicesByProtocol.put(service.get().getProtocol(), service.get());
                        logger.debug("Added {} server from {}", service.get().getProtocol(),
                                component.getDescriptor().getConfig().getFullName());
                    }
                }
            }

            @Override
            public void onComponentUndeployed(Component component) {
                servicesByProtocol.entrySet().removeIf(entry -> {
                    if (entry.getValue().getClass().getModule().getLayer() == component.getLayer()) {
                        if (Framework.getMode() == FrameworkMode.CLIENT) {
                            logger.debug("Removed {} client from {}", entry.getKey(),
                                component.getDescriptor().getConfig().getFullName());
                        } else {
                            logger.debug("Removed {} server from {}", entry.getKey(),
                                component.getDescriptor().getConfig().getFullName());
                        }
                        return true;
                    } else {
                        return false;
                    }
                });
            }
        };
    }
}
