package com.techsenger.weaverbird.gui.file;

///*
// * Copyright 2018-2026 Pavel Castornii.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.techsenger.weaverbird.console.gui.file;
//
//import com.techsenger.weaverbird.api.module.ModuleType;
//import com.techsenger.weaverbird.api.repo.DefaultModuleArtifact;
//import com.techsenger.weaverbird.api.repo.ModuleArtifact;
//import com.techsenger.toolkit.core.xml.XmlUtils;
//import java.io.IOException;
//import java.io.StringReader;
//import java.io.StringWriter;
//import java.util.ArrayList;
//import java.util.List;
//import javax.xml.parsers.ParserConfigurationException;
//import javax.xml.parsers.SAXParser;
//import javax.xml.parsers.SAXParserFactory;
//import javax.xml.stream.XMLOutputFactory;
//import javax.xml.stream.XMLStreamException;
//import javax.xml.stream.XMLStreamWriter;
//import org.xml.sax.Attributes;
//import org.xml.sax.InputSource;
//import org.xml.sax.SAXException;
//import org.xml.sax.SAXParseException;
//import org.xml.sax.helpers.DefaultHandler;
//
///**
// *
// * @author Pavel Castornii
// */
//class AlphaDependencyConverter {
//
//    /**
//     * As we use SAX model (SAX Parser --> Handler ) we need handler.
//     * Here we use nested class.
//     */
//    private class SaxHandler extends DefaultHandler {
//
//        private final String mavenDependencyInput;
//
//        private List<ModuleArtifact> modules = new ArrayList<>();
//
//        SaxHandler(String mavenDependencyInput) {
//            this.mavenDependencyInput = mavenDependencyInput;
//        }
//
//        @Override
//        public void startElement(String uri, String localName, String qName, Attributes attributes)
//                throws SAXException {
//            if (qName.equals("Module")) {
//                ModuleType type = ModuleType.JAR;
//                if (attributes.getValue("type") != null) {
//                    type = ModuleType.valueOf(attributes.getValue("type").toUpperCase());
//                }
//                var module = new DefaultModuleArtifact(
//                        attributes.getValue("groupId"),
//                        attributes.getValue("artifactId"),
//                        attributes.getValue("version"),
//                        attributes.getValue("classifier"),
//                        type
//                    );
//                this.modules.add(module);
//            }
//        }
//
//        @Override
//        public void error(SAXParseException e) throws SAXException {
//            throw e;
//        }
//
//        public List<ModuleArtifact> getModules() {
//            return modules;
//        }
//    };
//
//    private String write(List<ModuleArtifact> modules) throws XMLStreamException, IOException {
//        var headerComment = "<!-- Alpha dependencies converted to Maven dependencies. "
//                + "Processed " + modules.size() + " dependencies." + " -->";
//        try (StringWriter stringWriter = new StringWriter()) {
//            XMLOutputFactory xMLOutputFactory = XMLOutputFactory.newInstance();
//            XMLStreamWriter xmlStreamWriter = xMLOutputFactory.createXMLStreamWriter(stringWriter);
//            xmlStreamWriter.writeStartDocument();
//            xmlStreamWriter.writeStartElement("dependencies");
//
//            for (var module : modules) {
//                xmlStreamWriter.writeStartElement("dependency");
//
//                xmlStreamWriter.writeStartElement("groupId");
//                xmlStreamWriter.writeCharacters(module.getGroupId());
//                xmlStreamWriter.writeEndElement();
//
//                xmlStreamWriter.writeStartElement("artifactId");
//                xmlStreamWriter.writeCharacters(module.getArtifactId());
//                xmlStreamWriter.writeEndElement();
//
//                if (module.getClassifier() != null) {
//                    xmlStreamWriter.writeStartElement("classifier");
//                    xmlStreamWriter.writeCharacters(module.getClassifier());
//                    xmlStreamWriter.writeEndElement();
//                }
//
//                if (module.getVersion() != null) {
//                    xmlStreamWriter.writeStartElement("version");
//                    xmlStreamWriter.writeCharacters(module.getVersion());
//                    xmlStreamWriter.writeEndElement();
//                }
//
//                if (module.getType() != null) {
//                    xmlStreamWriter.writeStartElement("type");
//                    xmlStreamWriter.writeCharacters(module.getType().toString().toLowerCase());
//                    xmlStreamWriter.writeEndElement();
//                }
//                xmlStreamWriter.writeEndElement();
//            }
//
//            xmlStreamWriter.writeEndElement();
//            xmlStreamWriter.writeEndDocument();
//
//            xmlStreamWriter.flush();
//            xmlStreamWriter.close();
//
//            String xmlString = stringWriter.getBuffer().toString();
//            //transforming
//            xmlString = XmlUtils.transformToTree(xmlString, headerComment);
//            return xmlString;
//        }
//    }
//
//    public String convert(String alphaInput) throws SAXException, IOException,
//            ParserConfigurationException, XMLStreamException {
//        //reading modules
//        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
//        SAXParser saxParser = parserFactory.newSAXParser();
//        var handler = new SaxHandler(alphaInput);
//        var alphaInputSrc = new InputSource(new StringReader(alphaInput));
//        saxParser.parse(alphaInputSrc, handler);
//        List<ModuleArtifact> modules = handler.getModules();
//        //writing modules
//        var mavenOutput = this.write(modules);
//        return mavenOutput;
//    }
//}
