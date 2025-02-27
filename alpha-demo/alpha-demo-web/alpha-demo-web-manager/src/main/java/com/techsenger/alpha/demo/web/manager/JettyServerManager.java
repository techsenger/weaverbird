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

import com.techsenger.alpha.api.Framework;
import com.techsenger.alpha.api.component.Component;
import com.techsenger.toolkit.core.jpms.ModuleUtils;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.Resource;
import io.github.classgraph.ScanResult;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.eclipse.jetty.ee10.annotations.AnnotationConfiguration;
import org.eclipse.jetty.ee10.webapp.WebAppContext;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
public class JettyServerManager implements ServerManager {

    private static final Logger logger = LoggerFactory.getLogger(JettyServerManager.class);

    private List<URL> jettyTldUrls;

    private Map<Component, WebAppContext> webContextsByComponent;

    private Server server;

    @Override
    public synchronized void startServer() {
        try {
            //Evaluator evaluator = new Evaluator();
            //we will use these tld urls for all web components so not to find them every time we do it only once.
            //we suppose that jetty is in the same layer as the webmanager
            //besides we do it before any other components will be added because we don't need their tld urls.
            jettyTldUrls = findTldUrls(this.getClass().getModule().getLayer());
            logger.debug("JettyTldUrls {}", jettyTldUrls);
            webContextsByComponent = new ConcurrentHashMap<>();
            server = new Server(8080);
            server.setDynamic(true);
            //disable Server@239963d8{STARTED}[9.4.21.v20190926] - STARTED ... message
            server.setDumpAfterStart(false);
            server.start();
        } catch (Throwable ex) {
            logger.error("Error starting server", ex);
        }
    }

    @Override
    public synchronized void stopServer() {
        try {
            //stopping all web contexts
            Set<Component> components = new HashSet<>();
            components.addAll(webContextsByComponent.keySet());
            components.forEach(this::undeployWebComponent);
            //now we can stop server
            server.stop();
            server = null;
            jettyTldUrls = null;
            webContextsByComponent = null;
        } catch (Throwable ex) {
            logger.error("Error stoping server", ex);
        }
    }

    @Override
    public synchronized void deployWebComponent(final Component component) {
        try {
            if (server == null || !server.isRunning()) {
                throw new Exception("Server is not ready");
            }
            Module warModule = null;
            List<Module> modules = component.getLayer()
                    .modules()
                    .stream()
                    .filter(module -> ModuleUtils.getPath(module).toString().toLowerCase().endsWith(".war"))
                    .collect(Collectors.toList());
            if (modules.isEmpty()) {
                throw new Exception("War module wasn't found");
            }
            warModule = modules.get(0);
            logger.debug("In component {} war module is {}", component, warModule.getName());
            WebAppContext webAppContext = new WebAppContext();
            //This webapp will use jsps and jstl. We need to enable the AnnotationConfiguration in order to correctly
            //set up the jsp container
            webAppContext.addConfiguration(new AnnotationConfiguration());
            //if parent of classloader is webserver classloader, then there will be duplication
            //of class loading - same classes will be in two places - component layer and webserver classloader
            //besides webapplication classes won't see its own layer and jpms services on it.
            //webAppContext.setClassLoader(new JpmsWebAppClassLoader(this.getClass().getClassLoader(), webAppContext,
            //      warModule));

            //if parent of classloader is war module classloader, then there is problem with Jasper compiler
            //as it won't find its own resources by war module classloader. The only way to solve this problem
            //is to make war module parent classloader = webserver classloader.
            webAppContext.setClassLoader(new JpmsWebAppClassLoader(warModule.getClassLoader(), webAppContext,
                    warModule));
            List<URL> webTldUrls = findTldUrls(component.getLayer());
            //adding jetty tld urls
            webTldUrls.addAll(jettyTldUrls);
            logger.debug("WebApp TildUlrs {}", webTldUrls);
            webAppContext.setAttribute("org.eclipse.jetty.tlds", webTldUrls);
            webAppContext.setContextPath("/");
            webAppContext.setWar(ModuleUtils.getPath(warModule).toString());
            webAppContext.setExtractWAR(true);
            webAppContext.setServer(server);
            //Set the ContainerIncludeJarPattern so that jetty examines these container-path jars for tlds,
            //web-fragments etc. If you omit the jar that contains the jstl .tlds, the jsp engine will
            //scan for them instead.
            webAppContext.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",
            ".*/jetty-jakarta-servlet-api-[^/]*\\.jar$|.*/jakarta.servlet.jsp.jstl-.*\\.jar$|.*/[^/]*taglibs.*\\.jar$");
            var parentConfig = component.getDescriptor().getParents().get(0).getConfig();
            var config = component.getDescriptor().getConfig();
            File tempDir = new File(Framework.getPathManager().getTempDirectoryPath()
                    + File.separator
                    + parentConfig.getName()
                    + File.separator
                    + parentConfig.getVersion().getFull()
                    + File.separator
                    + config.getName()
                    + File.separator
                    + config.getVersion().getFull());
            webAppContext.setTempDirectory(tempDir);
            webContextsByComponent.put(component, webAppContext);
            //webAppContext.setAttribute("org.eclipse.jetty.containerInitializers", jspInitializers());
            //A WebAppContext is a ContextHandler as well so it needs to be set to
            //the server so it is aware of where to send the appropriate requests.
            server.setHandler(webAppContext);
            //important - we need to start application explicitly
            webAppContext.start();
            logger.info("Component {} was deployed", component);
        } catch (Throwable ex) {
            logger.error("Error deploying component {}", component.getDescriptor(), ex);
        }
    }

    @Override
    public synchronized void undeployWebComponent(final Component component) {
        try {
            WebAppContext webAppContext = webContextsByComponent.remove(component);
            webAppContext.stop();
            logger.info("Component {} was undeployed", component);
        } catch (Throwable ex) {
            logger.error("Error undeploying component {}", component.getDescriptor(), ex);
        }
    }

    private List<URL> findTldUrls(final ModuleLayer layer) {
        final List<URL> result = new ArrayList<>();
        try {
            Thread.currentThread().setContextClassLoader(ClassLoader.getSystemClassLoader());
            // We need to scan this layer and the layer of the war module.
            try (ScanResult scanResult = new ClassGraph()
                    .ignoreParentModuleLayers()
                    .whitelistPaths("META-INF")
                    .addModuleLayer(layer)
                    .scan()) {
                scanResult
                    .getResourcesWithExtension("tld")
                    .forEach((Resource res) -> {
                        try {
                            URL url = new URL("jar:" + res.getClasspathElementURL() + "!/" + res.getPath());
                            //URL url = new URL("jar:" + res.getURL());
                            result.add(url);
                        } catch (MalformedURLException ex) {
                            ex.printStackTrace();
                        }
                    });
            }
        } finally {
            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
        }
        return result;
    }
}
