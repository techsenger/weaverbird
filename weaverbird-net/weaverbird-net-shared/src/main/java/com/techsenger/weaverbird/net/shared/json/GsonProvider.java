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

package com.techsenger.weaverbird.net.shared.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import com.techsenger.toolkit.core.model.ModuleModel;
import com.techsenger.weaverbird.core.api.model.ComponentModuleModel;
import com.techsenger.weaverbird.core.api.model.DefaultLayersInfo;
import com.techsenger.weaverbird.core.api.model.LayersInfo;
import com.techsenger.weaverbird.core.api.state.ComponentsState;
import com.techsenger.weaverbird.core.api.state.DefaultComponentsState;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
public final class GsonProvider {

    private static final Logger logger = LoggerFactory.getLogger(GsonProvider.class);

    private static volatile Gson gson;

    public static Gson gson() {
        if (gson == null) {
            create();
        }
        return gson;
    }

    private static synchronized void create() {
        if (gson == null) {
            gson = new GsonBuilder()
                    .serializeNulls()
                    .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>)
                        (src, type, ctx) -> new JsonPrimitive(src.toString()))
                    .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>)
                        (json, type, ctx) -> LocalDateTime.parse(json.getAsString()))
                    .registerTypeAdapterFactory(new BindingTypeAdapterFactory(
                        new TypeToken<LayersInfo>() { },
                        new TypeToken<DefaultLayersInfo>() { }))
                    .registerTypeAdapterFactory(new BindingTypeAdapterFactory(
                        new TypeToken<ModuleModel>() { },
                        new TypeToken<ComponentModuleModel>() { }))
                    .registerTypeAdapterFactory(new BindingTypeAdapterFactory(
                        new TypeToken<ComponentsState>() { },
                        new TypeToken<DefaultComponentsState>() { }))
                    .create();
        }
    }

    private GsonProvider() {
        //empty
    }
}
