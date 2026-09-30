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

package com.techsenger.weaverbird.gui.diagram;

import com.techsenger.shellfx.core.dialog.DialogConfig;
import com.techsenger.shellfx.layout.pagehost.BasePageHostConfig;
import java.io.Serial;

/**
 *
 * @author Pavel Castornii
 */
public class LayerDialogConfig extends DialogConfig {

    @Serial
    private static final long serialVersionUID = 1L;

    private BasePageHostConfig pageHost = new BasePageHostConfig();

    public LayerDialogConfig() {
        setWidth(1000);
        setHeight(600);
        pageHost.setDividerPosition(0.275);
    }

    public BasePageHostConfig getPageHost() {
        return pageHost;
    }

    public void setPageHost(BasePageHostConfig pageHost) {
        this.pageHost = pageHost;
    }
}
