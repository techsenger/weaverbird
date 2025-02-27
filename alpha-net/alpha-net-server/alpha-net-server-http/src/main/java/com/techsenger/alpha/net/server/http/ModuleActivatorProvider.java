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

    private final ComponentObserver observer = new ComponentObserverImpl();

    @Override
    public void activate(ModuleContext context) throws Exception {
        context.getComponent().addObserver(observer);
    }

    @Override
    public void deactivate(ModuleContext context) throws Exception {
        context.getComponent().removeObserver(observer);
    }

}
