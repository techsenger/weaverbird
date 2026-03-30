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

package com.techsenger.alpha.net.server.impl;

import java.nio.file.Files;
import java.nio.file.Path;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Pavel Castornii
 */
public class SettingsXmlReader {

    private static final class Handler extends DefaultHandler {

        private final ServerSettings settings = new ServerSettings();

        @Override
        public void startElement(String uri, String localName, String qName, org.xml.sax.Attributes attributes)
                throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            if ("Address".equals(qName)) {
                settings.setHost(attributes.getValue("host"));
                settings.setPort(Integer.parseInt(attributes.getValue("port")));
            }
        }
    }

    public ServerSettings read(Path path) throws Exception {
        var factory = SAXParserFactory.newInstance();
        var parser = factory.newSAXParser();
        var handler = new Handler();
        try (var inputStream = Files.newInputStream(path)) {
            parser.parse(inputStream, handler);
        }
        return handler.settings;
    }
}
