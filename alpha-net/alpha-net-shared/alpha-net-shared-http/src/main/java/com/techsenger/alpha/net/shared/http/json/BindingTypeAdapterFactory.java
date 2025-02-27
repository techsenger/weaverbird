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
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

/**
 * This adapter factory allows to parse interfaces by providing type of the implementation.
 * @author Pavel Castornii
 */
public class BindingTypeAdapterFactory<T1, T2> implements TypeAdapterFactory {

    private final TypeToken<T1> superTypeToken;

    private final TypeToken<T2> subTypeToken;

    public BindingTypeAdapterFactory(final TypeToken<T1> superTypeToken, final TypeToken<T2> subTypeToken) {
        this.superTypeToken = superTypeToken;
        this.subTypeToken = subTypeToken;
    }

    @Override
    public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> typeToken) {
        if (!typeToken.equals(superTypeToken)) {
            return null;
        }
        @SuppressWarnings("unchecked")
        final TypeAdapter<T> typeAdapter = (TypeAdapter<T>) gson.getDelegateAdapter(this, subTypeToken);
        return typeAdapter;
    }
}
