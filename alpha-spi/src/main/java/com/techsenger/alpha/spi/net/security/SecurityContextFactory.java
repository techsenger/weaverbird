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

package com.techsenger.alpha.spi.net.security;

import com.techsenger.alpha.api.net.security.AuthenticationException;
import com.techsenger.alpha.api.net.security.AuthorizationException;
import com.techsenger.alpha.api.net.security.CommandSecurityContext;
import com.techsenger.alpha.api.net.security.VersionMismatchException;
import com.techsenger.toolkit.core.version.Version;

/**
 * Factory can be created much later than SecurityContextService. For example, when
 * alpha starts we don't have DI context. That's why we have service -> factory -> context
 *
 * <p>This class must know nothing about RemoteCommandException.
 *
 * @author Pavel Castornii
 */
public interface SecurityContextFactory {

    /**
     * Creates context if subject exists with such login name/password pair otherwise throws exceptions.
     *
     * @return
     */
    CommandSecurityContext create(String loginName, String loginPassword, Version version)
            throws AuthenticationException, AuthorizationException, VersionMismatchException, Exception;


    void close() throws Exception;
}
