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

/**
 *
 * @author Pavel Castornii
 */
public final class UrlPaths {

    public static final String LOGIN = "/rest/authentication/login";

    public static final String LOGOUT = "/rest/authentication/logout";

    public static final String COMMAND_EXECUTE = "/rest/command/execute";

    public static final String COMMAND_INFO = "/rest/command/info";

    public static final String LAYER_INFO = "/rest/layer/info";

    public static final String COMPONENT_STATE = "/rest/component/state";

    private UrlPaths() {
        //empty
    }
}
