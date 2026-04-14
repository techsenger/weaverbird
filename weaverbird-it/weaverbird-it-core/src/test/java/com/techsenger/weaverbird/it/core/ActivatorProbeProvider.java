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

package com.techsenger.weaverbird.it.core;

import com.techsenger.weaverbird.it.shared.ActivatorProbe;
import com.techsenger.toolkit.core.SingletonFactory;

/**
 *
 * @author Pavel Castornii
 */
public class ActivatorProbeProvider implements ActivatorProbe {

    private static final SingletonFactory<ActivatorProbeProvider> singletonFactory =
            new SingletonFactory<>(() -> new ActivatorProbeProvider());

    public static ActivatorProbeProvider provider() {
        return singletonFactory.singleton();
    }

    private int activatedCount;

    private int deactivatedCount;

    @Override
    public void notifyActivated() {
        this.activatedCount++;
    }

    @Override
    public void notifyDeactivated() {
        this.deactivatedCount++;
    }

    int getActivatedCount() {
        return activatedCount;
    }

    int getDeactivatedCount() {
        return deactivatedCount;
    }

    void reset() {
        this.activatedCount = 0;
        this.deactivatedCount = 0;
    }
}
