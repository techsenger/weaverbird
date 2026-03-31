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

package com.techsenger.alpha.console.gui;

import com.techsenger.alpha.core.api.message.AbstractMessagePrinter;
import jfx.incubator.scene.control.richtext.RichTextArea;

/**
 *
 * @author Pavel Castornii
 */
class TextAreaMessagePrinter extends AbstractMessagePrinter {

    private final RichTextArea textArea;

    TextAreaMessagePrinter(RichTextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void printlnMessage(String message) {
        textArea.appendText(message + "\n");
    }

    @Override
    public void printlnError(String error) {
        textArea.appendText(error + "\n", TextAreaCssStyles.ERROR);
    }
}
