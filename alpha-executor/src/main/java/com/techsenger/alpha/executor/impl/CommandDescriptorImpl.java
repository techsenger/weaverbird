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

package com.techsenger.alpha.executor.impl;

import com.beust.jcommander.Parameter;
import com.techsenger.alpha.executor.api.command.DefaultCommandInfo;
import com.techsenger.alpha.executor.api.command.DefaultParameterDescriptor;
import com.techsenger.alpha.executor.spi.Command;
import com.techsenger.alpha.executor.spi.CommandDescriptor;
import com.techsenger.alpha.executor.spi.CommandMeta;
import com.techsenger.alpha.executor.spi.LocalCommand;
import com.techsenger.alpha.executor.spi.MainParameter;
import com.techsenger.alpha.executor.spi.RemoteCommand;
import com.techsenger.toolkit.core.ClassUtils;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
class CommandDescriptorImpl extends DefaultCommandInfo implements CommandDescriptor {

    private static final Logger logger = LoggerFactory.getLogger(CommandDescriptorImpl.class);

    private final transient Class<? extends Command> type;

    CommandDescriptorImpl(Class<? extends Command> commandClass) {
        this.type = commandClass;
        var annArray = commandClass.getAnnotations();
        var metaFound = false;
        for (var a : annArray) {
            //to get annotation class use annotationType, because getClass will return some proxy.
            if (a.annotationType() == LocalCommand.class) {
                setLocal(true);
            }
            if (a.annotationType() == RemoteCommand.class) {
                setRemote(true);
            }
            if (a.annotationType() == CommandMeta.class) {
                CommandMeta meta = (CommandMeta) a;
                setName(meta.name());
                setDescription(meta.description());
                metaFound = true;
            }
        }
        setModuleName(commandClass.getModule().getName());
        setParameters(this.createParameterDescriptors(commandClass));
        if (!metaFound) {
            logger.error("CommandClass {} has no meta", commandClass.getName());
        }
    }

    @Override
    public Class<? extends Command> getType() {
        return type;
    }

    private List<DefaultParameterDescriptor> createParameterDescriptors(Class<? extends Command> commandClass) {
        List<DefaultParameterDescriptor> result = new ArrayList<>();

        for (final Field field : ClassUtils.getFields(commandClass)) {
            if (field.isAnnotationPresent(Parameter.class)) {
                Parameter parameter = field.getAnnotation(Parameter.class);
                var descriptor = new DefaultParameterDescriptor();
                if (parameter.required()) {
                    descriptor.setRequired(true);
                } else {
                    descriptor.setRequired(false);
                }
                for (var i = 0; i < parameter.names().length; i++) {
                    var param = parameter.names()[i];
                    if (param.startsWith("--")) {
                        descriptor.setLongName(param);
                    } else {
                        descriptor.setShortName(param);
                    }
                }
                if (parameter.names().length == 0) {
                    descriptor.setMain(true);
                    if (field.isAnnotationPresent(MainParameter.class)) {
                        var mainParameter = field.getAnnotation(MainParameter.class);
                        descriptor.setLongName(mainParameter.value());
                    }
                }
                descriptor.setDescription(parameter.description());
                result.add(descriptor);
            }
        }

        if (result.isEmpty()) {
            return null;
        } else {
            return result;
        }
    }

}
