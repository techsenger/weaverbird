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

package com.techsenger.alpha.demo.web.app;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 *
 * @author Pavel Castornii
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {

        servletContext.setInitParameter("contextConfigLocation", "<NONE>");

        var webApplicationContext = new AnnotationConfigWebApplicationContext();
        webApplicationContext.register(MvcConfig.class);
        webApplicationContext.setServletContext(servletContext);
        // Manage the lifecycle of the root application context
        servletContext.addListener(new ContextLoaderListener(webApplicationContext));

        var dispatcherServlet = new DispatcherServlet(webApplicationContext);
        ServletRegistration.Dynamic theDispatcherServlet  =
                servletContext.addServlet("dispatcherServlet", dispatcherServlet);
        theDispatcherServlet.setLoadOnStartup(1);
        theDispatcherServlet.addMapping("/*");
    }
}

