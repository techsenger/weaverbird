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

package com.techsenger.alpha.assembly.security;

import com.techsenger.toolkit.http.security.SecurityContext;
import com.techsenger.toolkit.http.security.SecurityContextFactory;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
public class SecurityContextFactoryStub implements SecurityContextFactory {

    private static final Logger logger = LoggerFactory.getLogger(SecurityContextFactoryStub.class);

    private static final String DEFAULT_LOGIN_NAME = "admin";

    private static final String DEFAULT_LOGIN_PASSWORD = "admin";

    public SecurityContextFactoryStub() {
        logger.warn("!!! SecurityContextFactory stub in use! DO NOT USE IN PRODUCTION! "
                + "Default credentials ({}:{}) are active. "
                + "Replace this stub with a proper security configuration before deployment.",
                DEFAULT_LOGIN_NAME, DEFAULT_LOGIN_PASSWORD);
    }

    @Override
    public Optional<SecurityContext> create(String loginName, String loginPassword) throws Exception {
        if (Objects.equals(loginName, DEFAULT_LOGIN_NAME)
                && Objects.equals(loginPassword, DEFAULT_LOGIN_PASSWORD)) {
            var context = new SecurityContext() {
                @Override
                public String getLoginName() {
                    return DEFAULT_LOGIN_NAME;
                }

                @Override
                public boolean isAuthorized(String endpoint) throws Exception {
                    return true;
                }

                @Override
                public void close() throws Exception {
                    logger.debug("Default security context closed");
                }
            };
            return Optional.of(context);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void close() throws Exception {
        logger.debug("Default security context factory losed");
    }


}
