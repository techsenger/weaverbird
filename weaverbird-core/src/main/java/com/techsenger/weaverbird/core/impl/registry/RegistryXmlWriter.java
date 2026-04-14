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

package com.techsenger.weaverbird.core.impl.registry;

import com.techsenger.weaverbird.core.api.registry.ComponentEntry;
import com.techsenger.weaverbird.core.api.registry.Registry;
import com.techsenger.toolkit.core.file.FileUtils;
import com.techsenger.toolkit.core.xml.XmlUtils;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 *
 * @author Pavel Castornii
 */
class RegistryXmlWriter {

    private final Registry registry;

    RegistryXmlWriter(Registry registry) {
        this.registry = registry;
    }

    public void write(final Path path) throws XMLStreamException, IOException {
        var headerComment = "<!-- THIS FILE IS GENERATED AUTOMATICALLY. DON'T MODIFY THIS FILE MANUALLY! -->";
        StringWriter stringWriter = new StringWriter();
        XMLOutputFactory xMLOutputFactory = XMLOutputFactory.newInstance();
        XMLStreamWriter xmlStreamWriter = xMLOutputFactory.createXMLStreamWriter(stringWriter);
        xmlStreamWriter.writeStartDocument();
        xmlStreamWriter.writeStartElement(Xml.REGISTRY_TAG);

        writeComponents(xmlStreamWriter, registry.getAddedComponents(), Xml.ADDED_COMPONENTS_TAG);
        writeComponents(xmlStreamWriter, registry.getResolvedComponents(), Xml.RESOLVED_COMPONENTS_TAG);

        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeEndDocument();

        xmlStreamWriter.flush();
        xmlStreamWriter.close();

        String xmlString = stringWriter.getBuffer().toString();
        stringWriter.close();
        //transforming
        xmlString = XmlUtils.transformToTree(xmlString, headerComment);
        FileUtils.writeFile(path, xmlString, StandardCharsets.UTF_8);
    }

    private void writeComponents(XMLStreamWriter xmlStreamWriter, List<ComponentEntry> components, String tag)
            throws XMLStreamException {
        if (!components.isEmpty()) {
            xmlStreamWriter.writeStartElement(tag);
            for (var component : components) {
                xmlStreamWriter.writeStartElement(Xml.COMPONENT_TAG);
                xmlStreamWriter.writeAttribute(Xml.NAME_ATTRIBUTE, component.getName());
                xmlStreamWriter.writeAttribute(Xml.VERSION_ATTRIBUTE, component.getVersion().getFull());
                xmlStreamWriter.writeEndElement();
            }
            xmlStreamWriter.writeEndElement();
        }
    }

}
