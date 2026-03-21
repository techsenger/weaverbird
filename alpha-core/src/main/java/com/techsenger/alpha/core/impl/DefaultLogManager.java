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

package com.techsenger.alpha.core.impl;

import com.techsenger.alpha.core.api.LogManager;
import com.techsenger.alpha.core.api.logging.MemoryLog;
import org.slf4j.Logger;

/**
 *
 * @author Pavel Castornii
 */
public class DefaultLogManager implements LogManager {

    /**
     * Logger. Can be used only after initialization.
     */
    private static Logger logger;

    /**
     * Memory log.
     */
    private MemoryLog memoryLog;

    /**
     * Constructor.
     */
    public DefaultLogManager() {

    }

    @Override
    public void initialize() {
//        logger = LoggerFactory.getLogger(DefaultLogManager.class);
//        var sysProperty = System.getProperty(SystemProperties.LOG_MEMORY);
//        if (sysProperty != null) {
//            var enabled = Boolean.valueOf(sysProperty);
//            if (enabled) {
//                memoryLog = new DefaultMemoryLog();
//            }
//        }
//        if (this.memoryLog != null) {
//            this.memoryLog.open();
//        } else {
//            logger.debug("Memory log is not used");
//        }
    }

    @Override
    public MemoryLog getMemoryLog() {
        return this.memoryLog;
    }

    @Override
    public void deinitialize() {
//        logger.debug("Closing log resources");
//        if (this.memoryLog != null) {
//            this.memoryLog.close();
//        }
//        //we disable shutdownHook in xml configuration we need to shutdown log4j manually.
//        //we disable shutdownHook for log4j as we use this hook.
//        org.apache.logging.log4j.LogManager.shutdown();
    }

}
