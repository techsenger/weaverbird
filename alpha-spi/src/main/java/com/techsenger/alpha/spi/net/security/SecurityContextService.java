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

/**
 * This class must know nothing about RemoteCommandException.
 *
 * @author Pavel Castornii
 */
public interface SecurityContextService {

    /**
     * Returns factory.
     *
     * @param shouldCreate false: if factory exists it will return it, if not, it will return null; true: if factory
     * exists it will return it, if not, it will create a new factory and return it.
     * @return
     */
    SecurityContextFactory getFactory(boolean shouldCreate) throws Exception;
}


