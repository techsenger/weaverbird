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

package com.techsenger.alpha.console.gui.shell;

import com.techsenger.alpha.api.message.AbstractMessagePrinter;
import com.techsenger.tabshell.kit.material.textarea.ExtendedTextArea;
import com.techsenger.tabshell.kit.material.textarea.TextAreaStyle;

/**
 *
 * @author Pavel Castornii
 */
class TextAreaMessagePrinter extends AbstractMessagePrinter {

    private final ExtendedTextArea textArea;

    TextAreaMessagePrinter(ExtendedTextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void printlnMessage(String message) {
        textArea.append(message + "\n", new TextAreaStyle("message"));
    }

    @Override
    public void printlnError(String error) {
        textArea.append(error + "\n", new TextAreaStyle("error"));
    }
}
