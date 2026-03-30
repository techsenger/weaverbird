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

package com.techsenger.alpha.net.server.impl;

import com.techsenger.alpha.core.spi.module.ModuleActivator;
import com.techsenger.alpha.core.spi.module.ModuleContext;

/**
 *
 * @author Pavel Castornii
 */
public class ModuleActivatorProvider implements ModuleActivator {

    private HttpServer server;

    @Override
    public void activate(ModuleContext context) throws Exception {
        var pathResolver = context.getFramework().getPathManager().getPathResolver();
        var settingsPath = pathResolver.resolveSettingsFile(context.getComponent().getLayer());
        var settings = new SettingsXmlReader().read(settingsPath);
        this.server = new HttpServer(context.getFramework());
        this.server.start(settings.getHost(), settings.getPort());
    }

    @Override
    public void deactivate(ModuleContext context) throws Exception {
        this.server.stop();
    }

}
