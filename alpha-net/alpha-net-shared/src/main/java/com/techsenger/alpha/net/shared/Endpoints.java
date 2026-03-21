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

package com.techsenger.alpha.net.shared;

/**
 *
 * @author Pavel Castornii
 */
public final class Endpoints {

    public static final String LOGIN = "/api/authentication/login";

    public static final String LOGOUT = "/api/authentication/logout";

    public static final String ENDPOINT_LIST = "/api/endpoint/list";

    public static final String LAYER_INFO = "/api/layer/info";

    public static final String COMPONENT_DESCRIPTOR = "/api/component/descriptor";

    public static final String COMPONENT_ADD = "/api/component/add";

    public static final String COMPONENT_REMOVE = "/api/component/remove";

    public static final String COMPONENT_RESOLVE = "/api/component/resolve";

    public static final String COMPONENT_UNRESOLVE = "/api/component/unresolve";

    public static final String COMPONENT_DEPLOY = "/api/component/deploy";

    public static final String COMPONENT_UNDEPLOY = "/api/component/undeploy";

    public static final String COMPONENT_ACTIVATE = "/api/component/activate";

    public static final String COMPONENT_DEACTIVATE = "/api/component/deactivate";

    public static final String COMPONENT_INSTALL = "/api/component/install";

    public static final String COMPONENT_UNINSTALL = "/api/component/uninstall";

    public static final String COMPONENT_START = "/api/component/start";

    public static final String COMPONENT_STOP = "/api/component/stop";

    public static final String COMPONENT_LIST = "/api/component/list";

    public static final String COMPONENT_STATE = "/api/component/state";

    public static final String MODULE_RESOLVE = "/api/module/resolve";

    public static final String MODULE_UNRESOLVE = "/api/module/unresolve";

    public static final String MODULE_LIST = "/api/module/list";

    public static final String SESSION_LIST = "/api/session/list";

    public static final String THREAD_LIST = "/api/thread/list";

    public static final String THREAD_INFO_LIST = "/api/thread/info/list";

    private Endpoints() {
        // empty
    }
}
