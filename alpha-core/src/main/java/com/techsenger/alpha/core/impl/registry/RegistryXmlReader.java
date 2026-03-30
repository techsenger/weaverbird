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

package com.techsenger.alpha.core.impl.registry;

import com.techsenger.alpha.core.api.registry.ComponentEntry;
import com.techsenger.toolkit.core.version.Version;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Pavel Castornii
 */
class RegistryXmlReader {

    private static final Logger logger = LoggerFactory.getLogger(RegistryXmlReader.class);

    /**
     * As we use SAX model (SAX Parser --> Handler ) we need handler.
     * Here we use nested class.
     */
    private class SaxHandler extends DefaultHandler {

        private DefaultRegistry registry;

        private List<ComponentEntry> components;

        SaxHandler(DefaultRegistry registry) {
            this.registry = registry;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes)
                throws SAXException {
            switch (qName) {
                case Xml.ADDED_COMPONENTS_TAG:
                    components = registry.getModifiableAddedComponents();
                    break;
                case Xml.RESOLVED_COMPONENTS_TAG:
                    components = registry.getModifiableResolvedComponents();
                    break;
                case Xml.COMPONENT_TAG:
                    var component = new ComponentEntry(attributes.getValue(Xml.NAME_ATTRIBUTE),
                            Version.parse(attributes.getValue(Xml.VERSION_ATTRIBUTE)));
                    this.components.add(component);
                    break;
            }
        }

        @Override
        public void error(SAXParseException e) throws SAXException {
            throw e;
        }
    };

    private final DefaultRegistry registry;

    RegistryXmlReader(DefaultRegistry registry) {
        this.registry = registry;
    }

    public void read(final URL url) throws IOException, SAXException, ParserConfigurationException {
        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        SAXParser saxParser = parserFactory.newSAXParser();
        var handler = new SaxHandler(registry);
        try (var stream = url.openStream()) {
            saxParser.parse(stream, handler);
        }
    }
}
