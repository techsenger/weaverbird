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

package com.techsenger.alpha.core.impl.logging;

import com.techsenger.alpha.core.api.logging.MemoryLog;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
public class DefaultMemoryLog implements MemoryLog {

    private static final Logger logger = LoggerFactory.getLogger(DefaultMemoryLog.class);

    /**
     * LinkedBlockingQueue is slower.
     */
    private final Queue<LogEvent> events = new ConcurrentLinkedQueue<>();

    public DefaultMemoryLog() {

    }

    @Override
    public Queue<LogEvent> getEvents() {
        return events;
    }

    /**
     * We take log4j2 configuration that is based on xml file and add new appender to it.
     * We can't do it via xml until this issue is resolved: https://issues.apache.org/jira/browse/LOG4J2-3451
     */
    @Override
    public void open() {
        var appender = new QueueAppender("Queue", null, events);
        appender.start();
        final LoggerContext context = (LoggerContext) LogManager.getContext(false);
        final Configuration config = context.getConfiguration();
        config.addAppender(appender);
        LoggerConfig rootLoggerConfig = config.getRootLogger();
        rootLoggerConfig.addAppender(appender, rootLoggerConfig.getLevel(), rootLoggerConfig.getFilter());
        context.updateLoggers();
        logger.info("Memory log was opened");
    }

    @Override
    public void close() {
        logger.info("Memory log was closed");
    }
}
