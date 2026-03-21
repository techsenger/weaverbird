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

package com.techsenger.alpha.core.impl.component;

import com.techsenger.alpha.core.api.component.ComponentConfig;
import com.techsenger.alpha.core.api.component.ComponentConfigInfo;
import com.techsenger.alpha.core.api.component.ComponentConfigUtils;
import com.techsenger.alpha.core.api.component.RepositoryDescriptor;
import com.techsenger.alpha.core.api.module.DirectiveType;
import com.techsenger.alpha.core.api.module.ModuleDescriptor;
import com.techsenger.alpha.core.api.module.ModuleDirective;
import com.techsenger.alpha.core.api.module.ModuleType;
import com.techsenger.alpha.core.impl.module.DefaultModuleDescriptor;
import com.techsenger.alpha.core.impl.module.DefaultModuleDirective;
import com.techsenger.alpha.core.impl.repo.DefaultRepositoryDescriptor;
import com.techsenger.toolkit.core.version.Version;
import jakarta.el.ELProcessor;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public class ConfigXmlReader {

    /**
     * Logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(ConfigXmlReader.class);

    private enum ConditionTag {

        CHOOSE, WHEN, OTHERWISE, IF
    }

    private static Object convert(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
            return Boolean.parseBoolean(value);
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
            //empty
        }

        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ignored) {
            //empty
        }

        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException ignored) {
            //empty
        }
        return value;
    }

    private static final class ConditionNode {

        private final ConditionTag tag;

        /**
         * To be truthful at lease two conditions must be met: 1) all nodes up to the branch must be truth
         * 2) this node `test` must be evaluated to true.
         *
         * Besides `when` and `otherwise` requires all previous `when` must be false.
         */
        private final boolean evaluatedToTrue;

        private final List<ConditionNode> children = new ArrayList<>();

        ConditionNode(ConditionTag tag, boolean evaluatedToTrue) {
            this.tag = tag;
            this.evaluatedToTrue = evaluatedToTrue;
        }

        public ConditionTag getTag() {
            return tag;
        }

        public boolean wasEvaluatedToTrue() {
            return evaluatedToTrue;
        }

        public List<ConditionNode> getChildren() {
            return children;
        }

        public boolean hasChildEvaluatedToTrue() {
            boolean result = false;
            for (var child : children) {
                if (child.evaluatedToTrue) {
                    result = true;
                    break;
                }
            }
            return result;
        }

        @Override
        public String toString() {
            return "ConditionNode{" + "tag=" + tag + ", evaluatedToTrue=" + evaluatedToTrue
                    + ", children=" + children + '}';
        }
    }

    /**
     * As we use SAX model (SAX Parser --> Handler ) we need handler.
     * Here we use nested class.
     */
    private static class SaxHandler extends DefaultHandler {

        private static final String CHOOSE_TAG = "Choose";

        private static final String WHEN_TAG = "When";

        private static final String OTHERWISE_TAG = "Otherwise";

        private static final String IF_TAG = "If";

        private static final String CONFIGURATION_TAG = "Configuration";

        private static final String METADATA_TAG = "Metadata";

        private static final String ENTRY_TAG = "Entry";

        private static final String REPOSITORY_TAG = "Repository";

        private static final String MODULE_TAG = "Module";

        private static final String DIRECTIVES_TAG = "Directives";

        private static final String DIRECTIVE_TAG = "Directive";

        private static final String PROPERTY_TAG = "Property";

        private DefaultComponentConfig config;

        private final Map<String, String> metadata = new HashMap<>();

        private final List<RepositoryDescriptor> repositories = new ArrayList<>();

        private final List<ModuleDescriptor> modules = new ArrayList<>();

        private DefaultModuleDescriptor module;

        private List<ModuleDirective> directives;

        /**
         * When a deque is used as a stack, elements are pushed and popped from the beginning of the deque.
         */
        private final Deque<ConditionNode> conditionNodes = new ArrayDeque<>();

        private ELProcessor eLProcessor = new ELProcessor();

        private final Map<String, Object> properties = new HashMap<>();

        SaxHandler(Map<Object, Object> info, Map<Object, Object> utils) {
            this.eLProcessor.defineBean("sys", System.getProperties());
            this.eLProcessor.defineBean("env", System.getenv());
            this.eLProcessor.defineBean("config", this.properties);
            this.eLProcessor.defineBean("info", info);
            this.eLProcessor.defineBean("utils", utils);
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes)
                throws SAXException {
            ConditionNode node = null;
            Boolean result = null;
            String test = null;
            switch (qName) {
                case CHOOSE_TAG:
                    if (this.areBranchConditionNodesValid()) {
                        node = new ConditionNode(ConditionTag.CHOOSE, true);
                    } else {
                        node = new ConditionNode(ConditionTag.CHOOSE, false);
                    }
                    this.conditionNodes.offerFirst(node);
                    break;
                case WHEN_TAG:
                    if (this.areBranchConditionNodesValid()
                            && !this.conditionNodes.peekFirst().hasChildEvaluatedToTrue()) {
                        test = attributes.getValue("test");
                        result = processTest(test);
                        logger.trace("Processed <When>; test: {}, result: {}", test, result);
                        node = new ConditionNode(ConditionTag.WHEN, result);
                    } else {
                        node = new ConditionNode(ConditionTag.WHEN, false);
                    }
                    this.conditionNodes.peekFirst().getChildren().add(node);
                    this.conditionNodes.offerFirst(node);
                    break;
                case OTHERWISE_TAG:
                    if (this.areBranchConditionNodesValid()
                            && !this.conditionNodes.peekFirst().hasChildEvaluatedToTrue()) {
                        node = new ConditionNode(ConditionTag.OTHERWISE, true);
                    } else {
                        node = new ConditionNode(ConditionTag.OTHERWISE, false);
                    }
                    this.conditionNodes.offerFirst(node);
                    break;
                case IF_TAG:
                    if (this.areBranchConditionNodesValid()) {
                        test = attributes.getValue("test");
                        result = processTest(test);
                        logger.trace("Processed <If>; test: {}, result: {}", test, result);
                        node = new ConditionNode(ConditionTag.IF, result);
                    } else {
                        node = new ConditionNode(ConditionTag.IF, false);
                    }
                    this.conditionNodes.offerFirst(node);
                    break;
                case CONFIGURATION_TAG:
                    if (this.areBranchConditionNodesValid()) {
                        processConfigTag(attributes);
                    }
                    break;
                case ENTRY_TAG:
                    if (this.areBranchConditionNodesValid()) {
                        this.metadata.put(attributes.getValue("key"), attributes.getValue("value"));
                    }
                    break;
                case REPOSITORY_TAG:
                    if (this.areBranchConditionNodesValid()) {
                        var name = processValue(attributes.getValue("name"));
                        var url = processValue(attributes.getValue("url"));
                        repositories.add(new DefaultRepositoryDescriptor(name, url));
                    }
                    break;
                case MODULE_TAG:
                    if (this.areBranchConditionNodesValid()) {
                        processModuleTag(attributes);
                    }
                    break;
                case DIRECTIVES_TAG:
                    if (this.areBranchConditionNodesValid()) {
                        this.directives = new ArrayList<>();
                    }
                    break;
                case DIRECTIVE_TAG:
                    if (this.areBranchConditionNodesValid()) {
                        var dType = processValue(attributes.getValue("type"));
                        dType = camelCaseToScreamingSnakeCase(dType);
                        var directive = new DefaultModuleDirective(
                                DirectiveType.valueOf(dType),
                                processValue(attributes.getValue("package")),
                                processValue(attributes.getValue("module")),
                                processValue(attributes.getValue("layer")));
                        this.directives.add(directive);
                    }
                    break;
                case PROPERTY_TAG:
                    if (this.areBranchConditionNodesValid()) {
                        var name = attributes.getValue("name");
                        var value = convert(attributes.getValue("value"));
                        this.properties.put(name, value);
                        logger.debug("Property '{}' got a value '{}'", name, value);
                    }
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            ConditionNode condition = null;
            switch (qName) {
                case CHOOSE_TAG:
                    condition = this.conditionNodes.removeFirst();
                    break;
                case WHEN_TAG:
                    condition = this.conditionNodes.removeFirst();
                    break;
                case OTHERWISE_TAG:
                    condition = this.conditionNodes.removeFirst();
                    break;
                case IF_TAG:
                    condition = this.conditionNodes.removeFirst();
                    break;
                case DIRECTIVES_TAG:
                    if (!this.directives.isEmpty()) {
                        this.module.setDirectives(Collections.unmodifiableList(this.directives));
                    }
                    break;
            }
        }

        @Override
        public void error(SAXParseException e) throws SAXException {
            throw e;
        }

        /**
         * Converts str from camelCase to SCREAMING_SNAKE_CASE.
         *
         * @param str
         * @return
         */
        private String camelCaseToScreamingSnakeCase(String str) {
            StringBuilder result = new StringBuilder();
            for (char c : str.toCharArray()) {
                if (Character.isUpperCase(c) && result.length() > 0) {
                    result.append('_');
                }
                result.append(Character.toUpperCase(c));
            }
            return result.toString();
        }

        private void processConfigTag(Attributes attributes) throws SAXException {
            var title = processValue(attributes.getValue("title"));
            var name = processValue(attributes.getValue("name"));
            if (name == null) {
                throw new SAXException("The name of the component was not set");
            }
            var version = processValue(attributes.getValue("version"));
            if (version == null) {
                throw new SAXException("The version of the component was not set");
            }
            var componentType = processValue(attributes.getValue("type"));
            if (componentType == null) {
                throw new SAXException("The type of the component was not set");
            }
            this.config = new DefaultComponentConfig(title, name, Version.parse(version), componentType);
            config.setMetadata(metadata);
            config.setRepositories(repositories);
            config.setModules(modules);
        }

        private void processModuleTag(Attributes attributes) {
            ModuleType moduleType = ModuleType.JAR;
            var t = processValue(attributes.getValue("type"));
            if (t != null) {
                moduleType = ModuleType.valueOf(t.trim().toUpperCase());
            }
            this.module = new DefaultModuleDescriptor(
                    processValue(attributes.getValue("groupId")),
                    processValue(attributes.getValue("artifactId")),
                    processValue(attributes.getValue("version")),
                    processValue(attributes.getValue("classifier")),
                    moduleType);
            var activeStr = processValue(attributes.getValue("active"));
            if (activeStr != null && activeStr.equalsIgnoreCase("true")) {
                this.module.setActive(true);
            }
            var nativeAccess = processValue(attributes.getValue("nativeAccessEnabled"));
            if (nativeAccess != null && nativeAccess.equalsIgnoreCase("true")) {
                this.module.setNativeAccessEnabled(true);
            }
            modules.add(module);
        }

        private String processValue(String value) {
            if (value == null) {
                return null;
            }
            value = value.trim();
            if (value.startsWith("${")) {
                value = value.substring(2, value.length() - 1);
                value = (String) eLProcessor.eval(value);
            }
            return value;
        }

        private Boolean processTest(String test) {
            if (test == null) {
                return null;
            }
            test = test.trim();
            Boolean result = null;
            if (test.startsWith("${")) {
                test = test.substring(2, test.length() - 1);
                result = (Boolean) eLProcessor.eval(test);
            }
            return result;
        }

        private boolean areBranchConditionNodesValid() {
            var node = this.conditionNodes.peekFirst();
            return (node == null || node.wasEvaluatedToTrue());
        }
    };

    /**
     * Constructor.
     */
    public ConfigXmlReader() {
        //empty.
    }

    public ComponentConfig read(final Path path, ComponentConfigInfo configInfo, ComponentConfigUtils configUtils)
            throws ParserConfigurationException, SAXException,
            MalformedURLException, IOException {
        try (var stream = path.toUri().toURL().openStream()) {
            return read(stream, configInfo, configUtils);
        }
    }

    public ComponentConfig read(final InputStream stream, ComponentConfigInfo configInfo,
            ComponentConfigUtils configUtils) throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        SAXParser saxParser = parserFactory.newSAXParser();
        var handler = new SaxHandler(configInfo, configUtils);
        saxParser.parse(stream, handler);
        return handler.config;
    }
}
