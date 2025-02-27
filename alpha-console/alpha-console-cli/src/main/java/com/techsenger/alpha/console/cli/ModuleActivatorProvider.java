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

package com.techsenger.alpha.console.cli;

import com.techsenger.alpha.spi.module.ModuleActivator;
import com.techsenger.alpha.spi.module.ModuleContext;

/**
 *
 * @author Pavel Castornii
 */
public class ModuleActivatorProvider implements ModuleActivator {

    @Override
    public void activate(ModuleContext context) throws Exception {

    }

    @Override
    public void deactivate(ModuleContext context) throws Exception {
        //even is console hasn't been created it is very easy to create it as almost all fields initialized in open()
        var console = (ConsoleProvider) ConsoleProvider.provider();
        if (console.isOpen()) {
            console.close();
        }
    }
}
