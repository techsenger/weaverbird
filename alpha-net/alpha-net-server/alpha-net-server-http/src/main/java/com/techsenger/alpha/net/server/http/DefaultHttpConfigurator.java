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

package com.techsenger.alpha.net.server.http;

import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;

/**
 *
 * @author Pavel Castornii
 */
public class DefaultHttpConfigurator extends HttpsConfigurator {

    public DefaultHttpConfigurator(SSLContext sslContext) {
        super(sslContext);
    }

    @Override
    public  void configure(HttpsParameters params) {
        SSLContext sslContext = getSSLContext();
        SSLEngine engine = sslContext.createSSLEngine();
        params.setCipherSuites(engine.getEnabledCipherSuites());
        params.setProtocols(engine.getEnabledProtocols());

        SSLParameters  sslParams = sslContext.getDefaultSSLParameters();
        //this flag shows if this is one or two way SSL
        sslParams.setNeedClientAuth(true);
        params.setNeedClientAuth(true);
        params.setSSLParameters(sslParams);
    }
}
