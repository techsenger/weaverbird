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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Pavel Castornii
 */
public class InMemoryMessagePrinter extends AbstractMessagePrinter {

    private final List<DefaultMessage> messages = new ArrayList<>();

    @Override
    public synchronized void printlnMessage(String message) {
        var msg = new DefaultMessage();
        msg.setType(MessageType.OUTPUT);
        msg.setText(message);
        messages.add(msg);
    }

    @Override
    public synchronized void printlnError(String error) {
        var msg = new DefaultMessage();
        msg.setType(MessageType.ERROR);
        msg.setText(error);
        messages.add(msg);
    }

    public List<DefaultMessage> getMessages() {
        return messages;
    }
}
