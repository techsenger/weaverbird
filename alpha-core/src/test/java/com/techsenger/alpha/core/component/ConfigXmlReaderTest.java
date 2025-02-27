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

package com.techsenger.alpha.core.component;

import com.techsenger.alpha.api.component.ComponentConfigInfo;
import com.techsenger.alpha.api.module.ModuleDescriptor;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.xml.parsers.ParserConfigurationException;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 *
 * @author Pavel Castornii
 */
public class ConfigXmlReaderTest {

    private static final Logger logger = LoggerFactory.getLogger(ConfigXmlReaderTest.class);

    @Test
    public void read_nestedIfChooseConditionsForLinuxSystemProperty_success()
            throws URISyntaxException, ParserConfigurationException, SAXException, IOException {
        var uri = this.getClass().getResource("conditional-configuration.xml").toURI();
        var reader = new ConfigXmlReader();
        var configInfo = new ComponentConfigInfo();
        configInfo.put(ComponentConfigInfo.OS_FAMILY, "linux");
        var config = reader.read(new File(uri).toPath(), configInfo, null);
        var descriptors = config.getModules();
        assertThat(descriptors).hasSize(8);
        assertModuleDescriptor(descriptors.get(0), 1, "linux");
        assertModuleDescriptor(descriptors.get(1), 2, "linux");
        assertModuleDescriptor(descriptors.get(2), 3, "linux");
        assertModuleDescriptor(descriptors.get(3), 4, "linux");
        assertModuleDescriptor(descriptors.get(4), 8, "linux");
        assertModuleDescriptor(descriptors.get(5), 9, "linux");
        assertModuleDescriptor(descriptors.get(6), 29, "linux");
        assertModuleDescriptor(descriptors.get(7), 30, "linux");

    }

    @Test
    public void read_nestedIfChooseConditionsForWindowsSystemProperty_success()
            throws ParserConfigurationException, URISyntaxException, SAXException, IOException {
        var uri = this.getClass().getResource("conditional-configuration.xml").toURI();
        var reader = new ConfigXmlReader();
        var configInfo = new ComponentConfigInfo();
        configInfo.put(ComponentConfigInfo.OS_FAMILY, "windows");
        var config = reader.read(new File(uri).toPath(), configInfo, null);
        var descriptors = config.getModules();
        assertThat(descriptors).hasSize(5);
        assertModuleDescriptor(descriptors.get(0), 1, "windows");
        assertModuleDescriptor(descriptors.get(1), 16, "windows");
        assertModuleDescriptor(descriptors.get(2), 17, "windows");
        assertModuleDescriptor(descriptors.get(3), 29, "windows");
        assertModuleDescriptor(descriptors.get(4), 30, "windows");
    }

    @Test
    public void read_nestedIfChooseConditionsForMacSystemProperty_success()
            throws URISyntaxException, ParserConfigurationException, SAXException, IOException {
        var uri = this.getClass().getResource("conditional-configuration.xml").toURI();
        var reader = new ConfigXmlReader();
        var configInfo = new ComponentConfigInfo();
        configInfo.put(ComponentConfigInfo.OS_FAMILY, "mac");
        var config = reader.read(new File(uri).toPath(), configInfo, null);
        var descriptors = config.getModules();
        assertThat(descriptors).hasSize(8);
        assertModuleDescriptor(descriptors.get(0), 1, "mac");
        assertModuleDescriptor(descriptors.get(1), 18, "mac");
        assertModuleDescriptor(descriptors.get(2), 19, "mac");
        assertModuleDescriptor(descriptors.get(3), 20, "mac");
        assertModuleDescriptor(descriptors.get(4), 22, "mac");
        assertModuleDescriptor(descriptors.get(5), 23, "mac");
        assertModuleDescriptor(descriptors.get(6), 29, "mac");
        assertModuleDescriptor(descriptors.get(7), 30, "mac");
    }

    @Test
    public void read_nestedIfChooseConditionsForSolarisSystemProperty_success()
            throws URISyntaxException, ParserConfigurationException, SAXException, IOException {
        var uri = this.getClass().getResource("conditional-configuration.xml").toURI();
        var reader = new ConfigXmlReader();
        var configInfo = new ComponentConfigInfo();
        configInfo.put(ComponentConfigInfo.OS_FAMILY, "solaris");
        var config = reader.read(new File(uri).toPath(), configInfo, null);
        var descriptors = config.getModules();
        assertThat(descriptors).hasSize(5);
        assertModuleDescriptor(descriptors.get(0), 1, "solaris");
        assertModuleDescriptor(descriptors.get(1), 26, "solaris");
        assertModuleDescriptor(descriptors.get(2), 28, "solaris");
        assertModuleDescriptor(descriptors.get(3), 29, "solaris");
        assertModuleDescriptor(descriptors.get(4), 30, "solaris");
    }

    private void assertModuleDescriptor(ModuleDescriptor descriptor, int index, String classifier) {
        assertThat(descriptor.getGroupId()).isEqualTo("group" + index);
        assertThat(descriptor.getArtifactId()).isEqualTo("artifact" + index);
        assertThat(descriptor.getVersion()).isEqualTo(index + ".0.0");
        assertThat(descriptor.getClassifier()).isEqualTo(classifier);
    }
}
