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

package com.techsenger.alpha.api.executor;

/**
 * When command can't execute because some conditions are not met command can throw this exception. This exception
 * shows that there is no exception in command execution but command is not going to be executed.
 *
 * @author Pavel Castornii
 */
public class CommandSkippedException extends Exception {

    public CommandSkippedException() {
    }

    public CommandSkippedException(String message) {
        super(message);
    }

    public CommandSkippedException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommandSkippedException(Throwable cause) {
        super(cause);
    }

    public CommandSkippedException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
