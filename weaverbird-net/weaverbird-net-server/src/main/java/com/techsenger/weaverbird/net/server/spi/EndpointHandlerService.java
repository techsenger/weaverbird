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

package com.techsenger.weaverbird.net.server.spi;

import com.techsenger.toolkit.http.handler.EndpointHandler;
import java.util.List;

/**
 *
 * @author Pavel Castornii
 */
public interface EndpointHandlerService {

    /**
     * Provides handler classes to be registered in the {@link ServerService}.
     *
     * <p>The returned classes must implement {@link EndpointHandler} and have an accessible no-argument constructor.
     *
     * <p>Important: This service must be located in the same module layer as the {@link ServerService}.
     *
     * @return list of command classes to be registered
     */
    List<Class<? extends EndpointHandler<FrameworkRequestContext, ?>>> getHandlers();
}
