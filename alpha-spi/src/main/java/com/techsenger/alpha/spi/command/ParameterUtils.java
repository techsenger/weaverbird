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

package com.techsenger.alpha.spi.command;

/**
 *
 * @author Pavel Castornii
 */
public final class ParameterUtils {

    /**
     * If we have spaces in a string and pass a parameter with quotes, for example, -r "C://do it now/a" then
     * jcommander will make the following string: "\"C://do it now/a\"". To removes double quotes we use this method.
     * For details @see <a href="https://github.com/cbeust/jcommander/issues/458">this issue</a>
     *
     * <p>If parameter is quoted, then parameter will be unquoted. Otherwise parameter will be return as it was.
     * To test this function use message:print -m.
     *
     * @param in
     * @return
     */
    public static String unquote(String in) {
        if (in != null && (in.startsWith("\"") && in.endsWith("\""))) {
            in = in.substring(1, in.length() - 1);
        }
        return in;
    }

    private ParameterUtils() {
        //empty
    }
}
