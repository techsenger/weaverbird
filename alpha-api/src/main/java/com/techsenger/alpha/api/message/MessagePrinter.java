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

package com.techsenger.alpha.api.message;

import static com.techsenger.alpha.api.message.MessageType.ERROR;
import static com.techsenger.alpha.api.message.MessageType.OUTPUT;
import java.util.List;

/**
 *
 * @author Pavel Castornii
 */
public interface MessagePrinter {

    int DEFAULT_WIDTH = 120;

    /**
     * Prints message instances to this printer.
     *
     * @param messages
     */
    default void print(List<Message> messages) {
        if (messages != null) {
            for (var m : messages) {
                switch (m.getType()) {
                    case OUTPUT:
                        printlnMessage(m.getText());
                        break;
                    case ERROR:
                        printlnError(m.getText());
                        break;
                    default:
                        throw new AssertionError();
                }
            }
        }
    }

    /**
     * Prints message and moves the cursor to a new line.
     * @param message to be printed.
     */
    void printlnMessage(String message);

    /**
     * Prints error and moves the cursor to a new line.
     * @param error to be printed.
     */
    void printlnError(String error);

    /**
     * Returns the width of this printer in terms of the number of characters.
     *
     * @return
     */
    int getWidth();
}
