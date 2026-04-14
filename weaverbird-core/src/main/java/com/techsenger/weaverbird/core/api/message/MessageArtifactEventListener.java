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

package com.techsenger.weaverbird.core.api.message;

import com.techsenger.weaverbird.core.api.module.ArtifactEventListener;
import com.techsenger.weaverbird.core.api.module.ModuleArtifact;

/**
 *
 * @author Pavel Castornii
 */
public class MessageArtifactEventListener implements ArtifactEventListener {

    private final MessagePrinter printer;

    private final boolean resolving;

    public MessageArtifactEventListener(MessagePrinter printer, boolean resolving) {
        this.printer = printer;
        this.resolving = resolving;
    }

    @Override
    public void onStarted(ModuleArtifact artifact) {
        if (this.resolving) {
            printer.printlnMessage("Resolving: " + artifact);
        } else {
            printer.printlnMessage("Unresolving: " + artifact);
        }
    }

    @Override
    public void onFinished(ModuleArtifact artifact) {
        if (this.resolving) {
            printer.printlnMessage("Resolved: " + artifact);
        } else {
            printer.printlnMessage("Unresolved: " + artifact);
        }
    }

}
