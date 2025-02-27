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

import com.techsenger.alpha.spi.console.ConsoleService;
import com.techsenger.toolkit.core.SingletonFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
public final class ConsoleProvider implements ConsoleService {

    /**
     * SPI service factory.
     */
    private static final SingletonFactory<ConsoleService> singletonFactory =
            new SingletonFactory<>(() -> new ConsoleProvider());

    /**
     * SPI service provider.
     *
     * @return
     */
    public static ConsoleService provider() {
        return singletonFactory.singleton();
    }

    private static final Logger logger = LoggerFactory.getLogger(ConsoleProvider.class);

    private volatile Console console;

    @Override
    public synchronized void open() throws Exception {
        if (isOpen()) {
            if (this.console.isClosing()) {
                logger.debug("Waiting until previous console is closing");
                this.console.getLoopThread().join();
            } else {
                throw new IllegalStateException("Console is already open");
            }
        }
        this.console = new Console();
        this.console.open();
    }

    /**
     * The most important thing about close method is that it can be closed from
     * one of the threads - console loop thread or another thread.
     *
     * So, we have two variants of closing - via flag and via interrupt.
     */
    @Override
    public synchronized void close() {
        if (!isOpen()) {
            throw new IllegalStateException("Console is not open");
        }
        if (this.console.isClosing()) {
            throw new IllegalStateException("Console is already being closed");
        }
        if (Thread.currentThread() == this.console.getLoopThread()) {
            this.console.setClosing(true);
        } else {
            this.console.getLoopThread().interrupt();
            this.console.close();
        }
        this.console = null;
    }

    boolean isOpen() {
        return this.console != null;
    }
}
