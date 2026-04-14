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

package com.techsenger.weaverbird.gui.console;

import jfx.incubator.scene.control.richtext.model.StyleAttribute;
import jfx.incubator.scene.control.richtext.model.StyleAttributeMap;

/**
 *
 * @author Pavel Castornii
 */
public final class TextAreaCssStyles {

    protected static final StyleAttribute<String> CSS_ATTRIBUTE
            = new StyleAttribute("CSS_ATTRIBUTE", String.class, false);

    protected static final StyleAttributeMap ERROR = StyleAttributeMap
            .of(CSS_ATTRIBUTE, "-fx-fill: -color-danger-5;");

    protected static final StyleAttributeMap COMMAND = StyleAttributeMap
            .of(CSS_ATTRIBUTE, "-fx-font-weight: bold");

    protected static final StyleAttributeMap DEFAULT = StyleAttributeMap
            .of(CSS_ATTRIBUTE, "-fx-fill: -color-fg-default; -fx-font-weight: normal;");

    private TextAreaCssStyles() {
        // empty
    }
}
