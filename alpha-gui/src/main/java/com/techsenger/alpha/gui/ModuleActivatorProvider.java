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

package com.techsenger.alpha.gui;

import com.techsenger.alpha.core.api.Framework;
import com.techsenger.alpha.core.spi.module.ModuleActivator;
import com.techsenger.alpha.core.spi.module.ModuleContext;
import javafx.application.Application;
import javafx.application.Platform;

/**
 *
 * @author Pavel Castornii
 */
public class ModuleActivatorProvider implements ModuleActivator {

    private static Framework framework;

    static Framework getFramework() {
        return framework;
    }

    @Override
    public void activate(ModuleContext context) throws Exception {

        // JavaFX Application.launch() blocks the calling thread until the application exits.
        // To prevent blocking the activator, we start it in a separate not daemon thread.
        var thread = new Thread(() -> {
            Application.launch(AlphaApplication.class);
            context.getFramework().shutdown();
        });
        thread.start();
    }

    @Override
    public void deactivate(ModuleContext context) throws Exception {
        Platform.exit();
    }
}
