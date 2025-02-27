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

import com.techsenger.alpha.api.message.AbstractMessagePrinter;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

/**
 *
 * @author Pavel Castornii
 */
public class SystemMessagePrinter extends AbstractMessagePrinter {

    private final AttributedStyle errorStyle = new AttributedStyle().foreground(204, 0, 0);

    @Override
    public void printlnMessage(final String message) {
        System.out.println(message);
    }

    @Override
    public void printlnError(final String error) {
        var asb = new AttributedStringBuilder();
        asb.style(errorStyle);
        asb.append(error);
        System.err.println(asb.toAnsi());
    }
}
