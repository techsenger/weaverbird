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

import com.techsenger.toolkit.core.version.Version;

/**
 *
 * @author Pavel Castornii
 */
public class LoginRequest extends AbstractRequest {

    /**
     * The login name of the client.
     */
    private final String loginName;

    /**
     * The loginPassword of the client.
     */
    private final String loginPassword;

    /**
     * Client version.
     */
    private final Version version;

    public LoginRequest(String loginName, String loginPassword, Version version) {
        super(null);
        this.loginName = loginName;
        this.loginPassword = loginPassword;
        this.version = version;
    }

    public String getLoginName() {
        return loginName;
    }

    public String getLoginPassword() {
        return loginPassword;
    }

    public Version getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "LoginRequest{" + "loginName=" + loginName + ", loginPassword=" + loginPassword + ", version="
                + version.getFull() + '}' + "->" + super.toString();
    }


}
