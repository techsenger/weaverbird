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

package com.techsenger.alpha.core.impl.component;

import com.techsenger.alpha.core.api.component.ComponentConfigInfo;
import com.techsenger.alpha.core.api.module.ModuleConfig;
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

    private static final String OS_FAMILY = "os.family";

    @Test
    public void read_nestedIfChooseConditionsForLinuxSystemProperty_success()
            throws URISyntaxException, ParserConfigurationException, SAXException, IOException {
        var uri = this.getClass().getResource("conditional-configuration.xml").toURI();
        var reader = new ConfigXmlReader();
        var configInfo = new ComponentConfigInfo();
        configInfo.put(OS_FAMILY, "linux");
        var config = reader.read(new File(uri).toPath(), configInfo, null);
        var modules = config.getModules();
        assertThat(modules).hasSize(8);
        assertModule(modules.get(0), 1, "linux");
        assertModule(modules.get(1), 2, "linux");
        assertModule(modules.get(2), 3, "linux");
        assertModule(modules.get(3), 4, "linux");
        assertModule(modules.get(4), 8, "linux");
        assertModule(modules.get(5), 9, "linux");
        assertModule(modules.get(6), 29, "linux");
        assertModule(modules.get(7), 30, "linux");

    }

    @Test
    public void read_nestedIfChooseConditionsForWindowsSystemProperty_success()
            throws ParserConfigurationException, URISyntaxException, SAXException, IOException {
        var uri = this.getClass().getResource("conditional-configuration.xml").toURI();
        var reader = new ConfigXmlReader();
        var configInfo = new ComponentConfigInfo();
        configInfo.put(OS_FAMILY, "windows");
        var config = reader.read(new File(uri).toPath(), configInfo, null);
        var modules = config.getModules();
        assertThat(modules).hasSize(5);
        assertModule(modules.get(0), 1, "windows");
        assertModule(modules.get(1), 16, "windows");
        assertModule(modules.get(2), 17, "windows");
        assertModule(modules.get(3), 29, "windows");
        assertModule(modules.get(4), 30, "windows");
    }

    @Test
    public void read_nestedIfChooseConditionsForMacSystemProperty_success()
            throws URISyntaxException, ParserConfigurationException, SAXException, IOException {
        var uri = this.getClass().getResource("conditional-configuration.xml").toURI();
        var reader = new ConfigXmlReader();
        var configInfo = new ComponentConfigInfo();
        configInfo.put(OS_FAMILY, "mac");
        var config = reader.read(new File(uri).toPath(), configInfo, null);
        var modules = config.getModules();
        assertThat(modules).hasSize(8);
        assertModule(modules.get(0), 1, "mac");
        assertModule(modules.get(1), 18, "mac");
        assertModule(modules.get(2), 19, "mac");
        assertModule(modules.get(3), 20, "mac");
        assertModule(modules.get(4), 22, "mac");
        assertModule(modules.get(5), 23, "mac");
        assertModule(modules.get(6), 29, "mac");
        assertModule(modules.get(7), 30, "mac");
    }

    @Test
    public void read_nestedIfChooseConditionsForSolarisSystemProperty_success()
            throws URISyntaxException, ParserConfigurationException, SAXException, IOException {
        var uri = this.getClass().getResource("conditional-configuration.xml").toURI();
        var reader = new ConfigXmlReader();
        var configInfo = new ComponentConfigInfo();
        configInfo.put(OS_FAMILY, "solaris");
        var config = reader.read(new File(uri).toPath(), configInfo, null);
        var modules = config.getModules();
        assertThat(modules).hasSize(5);
        assertModule(modules.get(0), 1, "solaris");
        assertModule(modules.get(1), 26, "solaris");
        assertModule(modules.get(2), 28, "solaris");
        assertModule(modules.get(3), 29, "solaris");
        assertModule(modules.get(4), 30, "solaris");
    }

    private void assertModule(ModuleConfig module, int index, String classifier) {
        assertThat(module.getGroupId()).isEqualTo("group" + index);
        assertThat(module.getArtifactId()).isEqualTo("artifact" + index);
        assertThat(module.getVersion()).isEqualTo(index + ".0.0");
        assertThat(module.getClassifier()).isEqualTo(classifier);
    }
}
