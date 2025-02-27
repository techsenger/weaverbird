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

package com.techsenger.alpha.net.shared.http;

import java.io.Serializable;

/**
 *
 * @author Pavel Castornii
 */
public abstract class AbstractResponse implements Serializable {

    private final String exceptionClass;

    private final String exceptionMessage;

    public AbstractResponse(Exception ex) {
        if (ex != null) {
            this.exceptionClass = ex.getClass().getName();
            this.exceptionMessage = ex.getMessage();
        } else {
            this.exceptionClass = null;
            this.exceptionMessage = null;
        }
    }

    public String getExceptionClass() {
        return exceptionClass;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    @Override
    public String toString() {
        return "AbstractResponse{" + "exceptionClass=" + exceptionClass + '}';
    }
}
