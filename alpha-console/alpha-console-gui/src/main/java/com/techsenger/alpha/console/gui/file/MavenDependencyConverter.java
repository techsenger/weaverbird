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
//package com.techsenger.alpha.console.gui.file;
//
//import com.techsenger.alpha.api.module.ModuleType;
//import com.techsenger.alpha.api.repo.DefaultModuleArtifact;
//import com.techsenger.alpha.api.repo.ModuleArtifact;
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
//class MavenDependencyConverter {
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
//        private DefaultModuleArtifact currentModule;
//
//        private String currentValueName = null;
//
//        private String groupId;
//
//        private String artifactId;
//
//        private String version;
//
//        private String classifier;
//
//        private ModuleType type;
//
//        SaxHandler(String mavenDependencyInput) {
//            this.mavenDependencyInput = mavenDependencyInput;
//        }
//
//        @Override
//        public void startElement(String uri, String localName, String qName, Attributes attributes)
//                throws SAXException {
//            this.currentValueName = null;
//            switch (qName) {
//                case "dependency":
//                    this.groupId = null;
//                    this.artifactId = null;
//                    this.version = null;
//                    this.classifier = null;
//                    this.type = ModuleType.JAR;
//                    break;
//                case "groupId":
//                case "artifactId":
//                case "version":
//                case "classifier":
//                case "type":
//                    this.currentValueName = qName;
//                break;
//            }
//        }
//
//        @Override
//        public void characters(char[] ch, int start, int length) throws SAXException {
//            super.characters(ch, start, length);
//            if (this.currentValueName == null) {
//                return;
//            }
//            switch (this.currentValueName) {
//                case "groupId":
//                    this.groupId = new String(ch, start, length);
//                    break;
//                case "artifactId":
//                    this.artifactId = new String(ch, start, length);
//                    break;
//                case "version":
//                    this.version = new String(ch, start, length);
//                    break;
//                case "classifier":
//                    this.classifier = new String(ch, start, length);
//                    break;
//                case "type":
//                    this.type = ModuleType.valueOf(new String(ch, start, length).toUpperCase());
//                    break;
//            }
//        }
//
//        @Override
//        public void endElement(String uri, String localName, String qName) throws SAXException {
//            switch (qName) {
//                case "dependency":
//                    this.currentModule = new DefaultModuleArtifact(this.groupId, this.artifactId, this.version,
//                            this.classifier, this.type);
//                    this.modules.add(currentModule);
//                break;
//            }
//            this.currentValueName = null;
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
//        var headerComment = "<!-- Maven dependencies converted to Alpha dependencies. "
//                + "Processed " + modules.size() + " dependencies." + " -->";
//        try (StringWriter stringWriter = new StringWriter()) {
//            XMLOutputFactory xMLOutputFactory = XMLOutputFactory.newInstance();
//            XMLStreamWriter xmlStreamWriter = xMLOutputFactory.createXMLStreamWriter(stringWriter);
//            xmlStreamWriter.writeStartDocument();
//            xmlStreamWriter.writeStartElement("Modules");
//
//            for (var module : modules) {
//                xmlStreamWriter.writeStartElement("Module");
//                xmlStreamWriter.writeAttribute("groupId", module.getGroupId());
//                xmlStreamWriter.writeAttribute("artifactId", module.getArtifactId());
//                if (module.getClassifier() != null) {
//                    xmlStreamWriter.writeAttribute("classifier", module.getClassifier());
//                }
//                if (module.getVersion() != null) {
//                    xmlStreamWriter.writeAttribute("version", module.getVersion());
//                }
//                if (module.getType() != null) {
//                    xmlStreamWriter.writeAttribute("type", module.getType().toString().toLowerCase());
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
//    public String convert(String mavenInput) throws SAXException, IOException,
//            ParserConfigurationException, XMLStreamException {
//        //reading modules
//        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
//        SAXParser saxParser = parserFactory.newSAXParser();
//        var handler = new SaxHandler(mavenInput);
//        var mavenInputSrc = new InputSource(new StringReader(mavenInput));
//        saxParser.parse(mavenInputSrc, handler);
//        List<ModuleArtifact> modules = handler.getModules();
//        //writing modules
//        var alphaOutput = this.write(modules);
//        return alphaOutput;
//    }
//}
