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

package com.techsenger.alpha.executor.impl.commands;

import com.beust.jcommander.Parameter;
import com.techsenger.alpha.core.api.message.MessagePrinter;
import com.techsenger.alpha.core.api.module.DefaultModuleArtifact;
import com.techsenger.alpha.core.api.module.ModuleType;
import com.techsenger.alpha.executor.api.CommandContext;
import com.techsenger.alpha.executor.spi.AbstractCommand;
import com.techsenger.alpha.executor.spi.CommandMeta;
import com.techsenger.alpha.executor.spi.LocalCommand;
import com.techsenger.alpha.executor.spi.ParameterUtils;
import com.techsenger.alpha.executor.spi.RemoteCommand;
import com.techsenger.alpha.net.client.api.DomainClient;
import com.techsenger.toolkit.core.StringUtils;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Pavel Castornii
 */
@LocalCommand
@RemoteCommand
@CommandMeta(name = "module:update",
        description = "Updates module in repo (unresolves and resolves it from specific repo)")
public class ModuleUpdateCommand extends AbstractCommand {

    private static final String MAVEN_CENTRAL_REPO_URL = "https://repo1.maven.org/maven2/";

    @Parameter(names = {"-g", "--group-id"}, required = true, description = "sets the group id of the module")
    private String groupId;

    @Parameter(names = {"-a", "--artifact-id"}, required = true, description = "sets the artifact id of the module")
    private String artifactId;

    @Parameter(names = {"-v", "--version"}, required = true, description = "sets the version of the module")
    private String version;

    @Parameter(names = {"-c", "--classifier"}, required = false, description = "sets the classifier of the module")
    private String classifier;

    @Parameter(names = {"-t", "--type"}, required = false,
            description = "sets the type of the module (jar/war), default is jar")
    private String type;

    /**
     * Using central maven repo.
     */
    @Parameter(names = {"--central"}, required = false,
            description = "sets flag to use central maven repo or not, default false")
    private boolean useCentral = false;

    /**
     * Custom maven repo.
     */
    @Parameter(names = {"-r", "--repo"}, required = false,
            description = "sets custom repo (this repo has the highest priority)")
    private String customRepo;

    @Override
    public String getTitle() {
        return StringUtils.format("Updating module groupId {}, artifactId {}, version {}", this.groupId,
                this.artifactId, this.version);
    }

    @Override
    public void execute(final CommandContext context, MessagePrinter printer) throws Exception {
        //we use linked hashmap to keep order
        Map<String, String> remoteReposByName = new LinkedHashMap<>();
        if (this.customRepo != null) {
            this.customRepo = ParameterUtils.unquote(this.customRepo);
            remoteReposByName.put("custom", this.customRepo);
        }
        if (this.useCentral) {
            remoteReposByName.put("central", MAVEN_CENTRAL_REPO_URL);
        }
        if (remoteReposByName.isEmpty()) {
            throw new IllegalArgumentException("Remote repos are undefined");
        }
        ModuleType t = null;
        if (this.type != null) {
            t = ModuleType.valueOf(type.toUpperCase());
        }
        var artifact = new DefaultModuleArtifact(groupId, artifactId, version, classifier, t);
        String message = "Module was uninstalled";
        if (context.isExecutionLocal()) {
            var framework = context.getFramework();
            var repoService = framework.getRepoService();
            repoService.unresolve(artifact, printer);
            printer.printlnMessage(message);
            framework.getRepoService().resolve(remoteReposByName, artifact, printer);
        } else {
            var client = new DomainClient(context.getClient(), context.getSession());
            client.unresolveModule(artifact);
            printer.printlnMessage(message);
            client.resolveModule(artifact, remoteReposByName);
        }
        printer.printlnError("Module was installed");
    }
}
