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

import jfx.incubator.scene.control.richtext.model.StyleAttributeMap;

/**
 *
 * @author Pavel Castornii
 */
public class StyledText {

    private final String text;

    private final StyleAttributeMap style;

    public StyledText(StyleAttributeMap style, String text) {
        this.style = style;
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public StyleAttributeMap getStyle() {
        return style;
    }
}
