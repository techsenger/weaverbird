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

package com.techsenger.alpha.demo.web.manager;

import java.io.IOException;
import java.io.InputStream;
import org.eclipse.jetty.ee10.webapp.WebAppClassLoader;
import org.eclipse.jetty.ee10.webapp.WebAppClassLoader.Context;

/**
 *
 * @author Pavel Castornii
 */
public class JpmsWebAppClassLoader extends WebAppClassLoader {

    private Module warModule;

    private ClassLoader serverClassLoader = JpmsWebAppClassLoader.class.getClassLoader();

    public JpmsWebAppClassLoader(Context context, Module warModule) throws IOException {
        super(context);
        this.warModule = warModule;
    }

    public JpmsWebAppClassLoader(ClassLoader parent, Context context, Module warModule) throws IOException {
        super(parent, context);
        this.warModule = warModule;
    }

    /**
     * WE need to override this method! As org.apache.jasper.compiler.JDTCompiler in
     * INameEnvironment#findType(String className) uses classLoader.getResourceAsStream(resourceName).
     * @param name
     * @return
     */
    public InputStream getResourceAsStream(String name) {
        InputStream result = super.getResourceAsStream(name);
        if (result == null) {
            return warModule.getClassLoader().getResourceAsStream(name);
        } else {
            return result;
        }
    }

    @Override
    public Class<?>  loadClass​(String name) throws ClassNotFoundException {
        Class result = null;
        try {
            result = super.loadClass(name);
        } catch (Exception ex) {
            //just ignore
        }
        if (result == null) {
            result = warModule.getClassLoader().loadClass(name);
        }
        return result;
    }
}


