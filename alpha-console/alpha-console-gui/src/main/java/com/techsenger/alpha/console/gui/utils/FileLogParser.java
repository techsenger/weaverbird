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

package com.techsenger.alpha.console.gui.utils;

import com.techsenger.ansi4j.core.api.Environment;
import com.techsenger.ansi4j.core.api.Fragment;
import com.techsenger.ansi4j.core.api.FragmentType;
import com.techsenger.ansi4j.core.api.FunctionFragment;
import com.techsenger.ansi4j.core.api.ParserFactory;
import com.techsenger.ansi4j.core.api.iso6429.ControlFunctionType;
import com.techsenger.ansi4j.core.api.iso6429.ControlSequenceFunction;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.logging.log4j.Level;

/**
 *
 * @author Pavel Castornii
 */
public class FileLogParser {

    private final ParserFactory factory = new ParserFactory.Builder()
                    .functionTypes(ControlFunctionType.CONTROL_SEQUENCE)
                    .environment(Environment._7_BIT)
                    .build();

    private final Pattern letterPattern = Pattern.compile("[^a-zA-Z]+");

    public List<FileLogFragment> parse(String line) {
        var parser = factory.createParser(line);
        Fragment fragment = null;
        var levelText = false;
        var result = new ArrayList<FileLogFragment>();
        while ((fragment = parser.parse()) != null) {
            if (fragment.getType() == FragmentType.TEXT) {
                var text = fragment.getText();
                FileLogFragment fileLogFragment = null;
                if (levelText && !text.isEmpty()) {
                    //ex: [LEVEL]
                    var level = Level.getLevel(letterPattern.matcher(text).replaceAll(""));
                    fileLogFragment = new FileLogFragment(level, text);
                    levelText = false;
                } else {
                    fileLogFragment = new FileLogFragment(null, text);
                }
                result.add(fileLogFragment);
            } else {
                FunctionFragment functionFragment = (FunctionFragment) fragment;
                if (functionFragment.getFunction() == ControlSequenceFunction.SGR) {
                    levelText = true;
                }
            }
        }
        return result;
    }
}
