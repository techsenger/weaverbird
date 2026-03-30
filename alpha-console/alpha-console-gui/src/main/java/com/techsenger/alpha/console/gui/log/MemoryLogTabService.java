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

package com.techsenger.alpha.console.gui.log;

import com.techsenger.alpha.api.Framework;
import java.util.ArrayList;
import java.util.List;
import javafx.concurrent.Task;
import org.apache.logging.log4j.core.LogEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
class MemoryLogTabService extends AbstractLogTabService<LogEvent> {

    private static final Logger logger = LoggerFactory.getLogger(MemoryLogTabService.class);

    MemoryLogTabService() {

    }

    @Override
    protected Task<List<LogEvent>> createTask() {
        return new Task() {

            @Override
            protected Void call() throws Exception {
                this.updateTitle("Memory Log Reader");
                var waitingMessage = "Waiting for new log events";
                this.updateMessage(waitingMessage);
                var inputEvents = Framework.getLogManager().getMemoryLog().getEvents();
                List<LogEvent> currentEvents = new ArrayList<>();
                try {
                    while (true) {
                        if (isCancelled()) {
                            return null;
                        }
                        if (inputEvents.isEmpty()) {
                            //wait until there is more of the file for us to read
                            this.updateMessage(waitingMessage);
                            Thread.sleep(1000);
                            continue;
                        }
                        //we need the second loop to process all existing events
                        int count = 0;
                        if (!inputEvents.isEmpty()) {
                            this.updateMessage("Processing log events");
                        }
                        while (!inputEvents.isEmpty()) {
                            LogEvent event = inputEvents.poll();
                            currentEvents.add(event);
                            count++;
                            if (count >= BATCH_SIZE) {
                                count = 0;
                                this.updateValue(currentEvents);
                                currentEvents = new ArrayList<>();
                            }
                        }
                        //rest events that are outside batch_size
                        if (!currentEvents.isEmpty()) {
                            count = 0;
                            this.updateValue(currentEvents);
                            currentEvents = new ArrayList<>();
                        }
                        //to allow user read information
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException ex) {
                    logger.debug("Sleeping thread was interrupted", ex);
                } catch (Exception ex) {
                    logger.error("Error reading memory log", ex);
                }
                return null;
            }
        };
    }
}
