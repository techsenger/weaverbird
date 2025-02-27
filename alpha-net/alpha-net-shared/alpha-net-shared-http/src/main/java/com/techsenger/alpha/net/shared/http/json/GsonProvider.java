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

package com.techsenger.alpha.net.shared.http.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.techsenger.alpha.api.command.CommandInfo;
import com.techsenger.alpha.api.command.ComponentsStateInfo;
import com.techsenger.alpha.api.command.DefaultCommandInfo;
import com.techsenger.alpha.api.command.DefaultComponentsStateInfo;
import com.techsenger.alpha.api.command.DefaultParameterDescriptor;
import com.techsenger.alpha.api.command.ParameterDescriptor;
import com.techsenger.alpha.api.executor.CommandInfos;
import com.techsenger.alpha.api.executor.CommandLine;
import com.techsenger.alpha.api.executor.DefaultCommandInfos;
import com.techsenger.alpha.api.executor.DefaultCommandLine;
import com.techsenger.alpha.api.model.ComponentModuleModel;
import com.techsenger.alpha.api.model.DefaultLayersInfo;
import com.techsenger.alpha.api.model.LayersInfo;
import com.techsenger.alpha.api.state.ComponentsState;
import com.techsenger.alpha.api.state.DefaultComponentsState;
import com.techsenger.toolkit.core.model.ModuleModel;
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
                    .registerTypeAdapterFactory(
                            new BindingTypeAdapterFactory(
                                    new TypeToken<CommandLine>() { },
                                    new TypeToken<DefaultCommandLine>() { }))
                    .registerTypeAdapterFactory(new BindingTypeAdapterFactory(
                                    new TypeToken<ParameterDescriptor>() { },
                                    new TypeToken<DefaultParameterDescriptor>() { }))
                    .registerTypeAdapterFactory(new BindingTypeAdapterFactory(
                                    new TypeToken<CommandInfo>() { },
                                    new TypeToken<DefaultCommandInfo>() { }))
                    .registerTypeAdapterFactory(new BindingTypeAdapterFactory(
                                    new TypeToken<CommandInfos>() { },
                                    new TypeToken<DefaultCommandInfos>() { }))
                    .registerTypeAdapterFactory(new BindingTypeAdapterFactory(
                                    new TypeToken<ComponentsStateInfo>() { },
                                    new TypeToken<DefaultComponentsStateInfo>() { }))
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
