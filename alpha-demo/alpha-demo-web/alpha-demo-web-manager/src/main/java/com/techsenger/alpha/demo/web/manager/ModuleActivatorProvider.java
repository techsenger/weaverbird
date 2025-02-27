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

package com.techsenger.alpha.demo.web.manager;

import com.techsenger.alpha.api.component.AbstractComponentObserver;
import com.techsenger.alpha.api.component.Component;
import com.techsenger.alpha.api.component.ComponentObserver;
import com.techsenger.alpha.spi.module.ModuleActivator;
import com.techsenger.alpha.spi.module.ModuleContext;
import com.techsenger.toolkit.core.SingletonFactory;

/**
 *
 * @author Pavel Castornii
 */
public class ModuleActivatorProvider implements ModuleActivator {

    private static final SingletonFactory<ModuleActivator> singletonFactory =
            new SingletonFactory<>(() -> new ModuleActivatorProvider());

    public static final ModuleActivator provider() {
        return singletonFactory.singleton();
    }

    private ServerManager serverManager = new JettyServerManager();

    private ComponentObserver observer = new AbstractComponentObserver() {

        /**
         * When a component started we deploy web application.
         */
        @Override
        public void onActivated(Component component) {
            super.onActivated(component);
            if (component.getDescriptor().getConfig().containsWarModules()) {
                serverManager.deployWebComponent(component);
            }
        }

        /**
         * Before stopping a component we must undeploy web application.
         */
        @Override
        public void onDeactivating(Component component) {
            super.onDeactivating(component);
            if (component.getDescriptor().getConfig().containsWarModules()) {
                serverManager.undeployWebComponent(component);
            }
        }
    };

    @Override
    public void activate(ModuleContext context) throws Exception {
        serverManager.startServer();
        context.getComponent().addObserver(observer);
    }

    @Override
    public void deactivate(ModuleContext context) throws Exception {
        context.getComponent().removeObserver(observer);
        serverManager.stopServer();
    }
}
