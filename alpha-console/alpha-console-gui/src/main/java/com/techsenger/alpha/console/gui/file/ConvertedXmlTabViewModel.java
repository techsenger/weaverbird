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

package com.techsenger.alpha.console.gui.file;

import com.techsenger.alpha.console.gui.keys.ConsoleComponentKeys;
import com.techsenger.tabshell.core.tab.AbstractTabViewModel;
import com.techsenger.tabshell.core.tab.TabKey;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Pavel Castornii
 */
public class ConvertedXmlTabViewModel extends AbstractTabViewModel {

    private StringProperty xmlCode = new SimpleStringProperty();

    public ConvertedXmlTabViewModel(String xmlCode) {
        super();
        this.xmlCode.set(xmlCode);
    }

    @Override
    public TabKey getKey() {
        return ConsoleComponentKeys.CONVERTED_XML_TAB;
    }

    public ReadOnlyStringProperty xmlCodeProperty() {
        return xmlCode;
    }
}
