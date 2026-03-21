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

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

/**
 *
 * @author Pavel Castornii
 */
@Plugin(name = "QueueAppender", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE)
public class QueueAppender extends AbstractAppender {

    private final Queue<LogEvent> events;

    /**
     * This constructor is used by our clients.
     * @param name
     * @param filter
     * @param events
     */
    protected QueueAppender(String name, Filter filter, Queue<LogEvent> events) {
        super(name, filter, null);
        this.events = events;
    }

    /**
     * This constructor is used by log4j.
     * @param name
     * @param filter
     */
    protected QueueAppender(String name, Filter filter) {
        super(name, filter, null);
        this.events = new ConcurrentLinkedQueue<>();
    }

    @PluginFactory
    public static QueueAppender createAppender(@PluginAttribute("name") String name,
            @PluginElement("Filter") Filter filter) {
        return new QueueAppender(name, filter);
    }

    @Override
    public void append(LogEvent event) {
        events.add(event.toImmutable());
    }
}
