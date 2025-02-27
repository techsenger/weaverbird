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

package com.techsenger.alpha.demo.net.auth;

import com.techsenger.alpha.api.Framework;
import com.techsenger.alpha.api.command.CommandContext;
import com.techsenger.alpha.api.net.security.AuthenticationException;
import com.techsenger.alpha.api.net.security.AuthorizationException;
import com.techsenger.alpha.api.net.security.CommandSecurityContext;
import com.techsenger.alpha.api.net.security.VersionMismatchException;
import com.techsenger.alpha.spi.net.security.SecurityContextFactory;
import com.techsenger.alpha.spi.net.security.SecurityContextService;
import com.techsenger.toolkit.core.function.Executable;
import com.techsenger.toolkit.core.version.Version;

/**
 *
 * @author Pavel Castornii
 */
public class SecurityContextProvider implements SecurityContextService {

    @Override
    public SecurityContextFactory getFactory(boolean shouldCreate) throws Exception {
        return new SecurityContextFactory() {
            @Override
            public CommandSecurityContext create(String loginName, String loginPassword, Version version) throws
                    AuthenticationException, AuthorizationException, VersionMismatchException, Exception {
                if (loginName.equals("admin") && loginName.equals("admin")) {
                    return new CommandSecurityContext() {

                        private CommandContext commandContext =
                                Framework.getServiceManager().getCommandExecutor().createContext(null);

                        @Override
                        public void execute(Executable executable) throws AuthorizationException, Exception {
                            executable.execute();
                        }

                        @Override
                        public void close() throws Exception {

                        }

                        @Override
                        public CommandContext getCommandContext() {
                            return this.commandContext;
                        }
                    };
                } else {
                    throw new AuthenticationException();
                }
            }

            @Override
            public void close() throws Exception {

            }
        };

    }

}
