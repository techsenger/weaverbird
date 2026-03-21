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

package com.techsenger.alpha.net.client.api;

import com.techsenger.alpha.core.api.component.ComponentConfigDto;
import com.techsenger.alpha.core.api.component.ComponentDescriptorDto;
import com.techsenger.alpha.core.api.message.DefaultMessage;
import com.techsenger.alpha.core.api.module.DefaultModuleArtifact;
import com.techsenger.alpha.net.shared.ComponentActivateRequest;
import com.techsenger.alpha.net.shared.ComponentActivateResponse;
import com.techsenger.alpha.net.shared.ComponentAddRequest;
import com.techsenger.alpha.net.shared.ComponentAddResponse;
import com.techsenger.alpha.net.shared.ComponentDeactivateRequest;
import com.techsenger.alpha.net.shared.ComponentDeactivateResponse;
import com.techsenger.alpha.net.shared.ComponentDeployRequest;
import com.techsenger.alpha.net.shared.ComponentDeployResponse;
import com.techsenger.alpha.net.shared.ComponentDescriptorRequest;
import com.techsenger.alpha.net.shared.ComponentDescriptorResponse;
import com.techsenger.alpha.net.shared.ComponentInstallRequest;
import com.techsenger.alpha.net.shared.ComponentInstallResponse;
import com.techsenger.alpha.net.shared.ComponentListRequest;
import com.techsenger.alpha.net.shared.ComponentListResponse;
import com.techsenger.alpha.net.shared.ComponentRemoveRequest;
import com.techsenger.alpha.net.shared.ComponentRemoveResponse;
import com.techsenger.alpha.net.shared.ComponentResolveRequest;
import com.techsenger.alpha.net.shared.ComponentResolveResponse;
import com.techsenger.alpha.net.shared.ComponentStartRequest;
import com.techsenger.alpha.net.shared.ComponentStartResponse;
import com.techsenger.alpha.net.shared.ComponentState;
import com.techsenger.alpha.net.shared.ComponentStopRequest;
import com.techsenger.alpha.net.shared.ComponentStopResponse;
import com.techsenger.alpha.net.shared.ComponentUndeployRequest;
import com.techsenger.alpha.net.shared.ComponentUndeployResponse;
import com.techsenger.alpha.net.shared.ComponentUninstallRequest;
import com.techsenger.alpha.net.shared.ComponentUninstallResponse;
import com.techsenger.alpha.net.shared.ComponentUnresolveRequest;
import com.techsenger.alpha.net.shared.ComponentUnresolveResponse;
import com.techsenger.alpha.net.shared.Endpoints;
import com.techsenger.alpha.net.shared.ModuleListResponse;
import com.techsenger.alpha.net.shared.ModuleResolveRequest;
import com.techsenger.alpha.net.shared.ModuleResolveResponse;
import com.techsenger.alpha.net.shared.ModuleUnresolveRequest;
import com.techsenger.alpha.net.shared.ModuleUnresolveResponse;
import com.techsenger.alpha.net.shared.ServerSessionDto;
import com.techsenger.alpha.net.shared.SessionListResponse;
import com.techsenger.alpha.net.shared.ThreadInfoListResponse;
import com.techsenger.alpha.net.shared.ThreadListResponse;
import com.techsenger.toolkit.core.model.ModuleModel;
import com.techsenger.toolkit.core.model.ThreadInfoModel;
import com.techsenger.toolkit.core.model.ThreadModel;
import com.techsenger.toolkit.core.version.Version;
import com.techsenger.toolkit.http.exceptions.ServerException;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Pavel Castornii
 */
public class DomainClient extends AbstractDomainClient {

    public DomainClient(ClientService client, ClientSession session) {
        super(client, session);
    }

    public ComponentDescriptorDto getComponentDescriptor(int id) throws ClientException, ServerException {
        var response = sendRequest(Endpoints.COMPONENT_DESCRIPTOR, new ComponentDescriptorRequest(id),
                ComponentDescriptorResponse.class);
        return response.getComponentDescriptor();
    }

    public ComponentDescriptorDto getComponentDescriptor(String alias) throws ClientException, ServerException {
        var response = sendRequest(Endpoints.COMPONENT_DESCRIPTOR, new ComponentDescriptorRequest(alias),
                ComponentDescriptorResponse.class);
        return response.getComponentDescriptor();
    }

    public ComponentConfigDto addComponent(String xmlConfig) throws ClientException, ServerException {
        var response = sendRequest(Endpoints.COMPONENT_ADD, new ComponentAddRequest(xmlConfig),
                ComponentAddResponse.class);
        return response.getComponentConfig();
    }

    public ComponentConfigDto removeComponent(String name, Version version) throws ClientException, ServerException {
        var response = sendRequest(Endpoints.COMPONENT_REMOVE, new ComponentRemoveRequest(name, version),
                ComponentRemoveResponse.class);
        return response.getComponentConfig();
    }

    public ComponentConfigDto resolveComponent(String name, Version version) throws ClientException, ServerException {
        var response = sendRequest(Endpoints.COMPONENT_RESOLVE, new ComponentResolveRequest(name, version),
                ComponentResolveResponse.class);
        return response.getComponentConfig();
    }

    public ComponentConfigDto unresolveComponent(String name, Version version) throws ClientException, ServerException {
        var response = sendRequest(Endpoints.COMPONENT_UNRESOLVE, new ComponentUnresolveRequest(name, version),
                ComponentUnresolveResponse.class);
        return response.getComponentConfig();
    }

    public ComponentDescriptorDto deployComponent(String name, Version version, String alias, List<Integer> parentIds,
            List<String> parentAliases, boolean useParentClassLoader) throws ClientException, ServerException {
        var request = new ComponentDeployRequest();
        request.setName(name);
        request.setVersion(version);
        request.setAlias(alias);
        request.setParentIds(parentIds);
        request.setParentAliases(parentAliases);
        request.setUseParentClassLoader(useParentClassLoader);
        var response = sendRequest(Endpoints.COMPONENT_DEPLOY, request, ComponentDeployResponse.class);
        return response.getComponentDescriptor();
    }

    public ComponentDescriptorDto undeployComponent(int id) throws ClientException, ServerException {
        var response = sendRequest(Endpoints.COMPONENT_UNDEPLOY, new ComponentUndeployRequest(id),
                ComponentUndeployResponse.class);
        return response.getComponentDescriptor();
    }

    public ComponentDescriptorDto undeployComponent(String alias) throws ClientException, ServerException {
        var response = sendRequest(Endpoints.COMPONENT_UNDEPLOY, new ComponentUndeployRequest(alias),
                ComponentUndeployResponse.class);
        return response.getComponentDescriptor();
    }

    public ComponentDescriptorDto activateComponent(int id) throws ClientException, ServerException {
        var response = sendRequest(Endpoints.COMPONENT_ACTIVATE, new ComponentActivateRequest(id),
                ComponentActivateResponse.class);
        return response.getComponentDescriptor();
    }

    public ComponentDescriptorDto activateComponent(String alias) throws ClientException, ServerException {
        var response = sendRequest(Endpoints.COMPONENT_ACTIVATE, new ComponentActivateRequest(alias),
                ComponentActivateResponse.class);
        return response.getComponentDescriptor();
    }

    public ComponentDescriptorDto deactivateComponent(int id) throws ClientException, ServerException {
        var response = sendRequest(Endpoints.COMPONENT_DEACTIVATE, new ComponentDeactivateRequest(id),
                ComponentDeactivateResponse.class);
        return response.getComponentDescriptor();
    }

    public ComponentDescriptorDto deactivateComponent(String alias) throws ClientException, ServerException {
        var response = sendRequest(Endpoints.COMPONENT_DEACTIVATE, new ComponentDeactivateRequest(alias),
                ComponentDeactivateResponse.class);
        return response.getComponentDescriptor();
    }

    public ComponentConfigDto installComponent(String xmlConfig) throws ClientException, ServerException {
        var response = sendRequest(Endpoints.COMPONENT_INSTALL, new ComponentInstallRequest(xmlConfig),
                ComponentInstallResponse.class);
        return response.getComponentConfig();
    }

    public ComponentConfigDto uninstallComponent(String name, Version version) throws ClientException, ServerException {
        var response = sendRequest(Endpoints.COMPONENT_UNINSTALL, new ComponentUninstallRequest(name, version),
                ComponentUninstallResponse.class);
        return response.getComponentConfig();
    }

    public ComponentDescriptorDto startComponent(String name, Version version, String alias, List<Integer> parentIds,
            List<String> parentAliases, boolean useParentClassLoader) throws ClientException, ServerException {
        var request = new ComponentStartRequest();
        request.setName(name);
        request.setVersion(version);
        request.setAlias(alias);
        request.setParentIds(parentIds);
        request.setParentAliases(parentAliases);
        request.setUseParentClassLoader(useParentClassLoader);
        var response = sendRequest(Endpoints.COMPONENT_START, request, ComponentStartResponse.class);
        return response.getComponentDescriptor();
    }

    public ComponentDescriptorDto stopComponent(int id) throws ClientException, ServerException {
        var response = sendRequest(Endpoints.COMPONENT_STOP, new ComponentStopRequest(id),
                ComponentStopResponse.class);
        return response.getComponentDescriptor();
    }

    public ComponentDescriptorDto stopComponent(String alias) throws ClientException, ServerException {
        var response = sendRequest(Endpoints.COMPONENT_STOP, new ComponentStopRequest(alias),
                ComponentStopResponse.class);
        return response.getComponentDescriptor();
    }

    public List<? extends ComponentConfigDto> getAddedComponents() throws ClientException, ServerException {
        var response = sendRequest(Endpoints.COMPONENT_LIST, new ComponentListRequest(ComponentState.ADDED),
                ComponentListResponse.class);
        return response.getComponentConfigs();
    }

    public List<? extends ComponentConfigDto> getResolvedComponents() throws ClientException, ServerException {
        var response = sendRequest(Endpoints.COMPONENT_LIST, new ComponentListRequest(ComponentState.RESOLVED),
                ComponentListResponse.class);
        return response.getComponentConfigs();
    }

    public List<? extends ComponentDescriptorDto> getDeployedComponents() throws ClientException, ServerException {
        var response = sendRequest(Endpoints.COMPONENT_LIST, new ComponentListRequest(ComponentState.DEPLOYED),
                ComponentListResponse.class);
        return response.getComponentDescriptors();
    }

    public List<? extends ComponentDescriptorDto> getActivatedComponents() throws ClientException, ServerException {
        var response = sendRequest(Endpoints.COMPONENT_LIST, new ComponentListRequest(ComponentState.ACTIVATED),
                ComponentListResponse.class);
        return response.getComponentDescriptors();
    }

    public List<DefaultMessage> resolveModule(DefaultModuleArtifact artifact, Map<String, String> remoteReposByName)
            throws ClientException, ServerException {
        var response = sendRequest(Endpoints.MODULE_RESOLVE, new ModuleResolveRequest(artifact, remoteReposByName),
                ModuleResolveResponse.class);
        return response.getMessages();
    }

    public List<DefaultMessage> unresolveModule(DefaultModuleArtifact artifact) throws ClientException,
            ServerException {
        var response = sendRequest(Endpoints.MODULE_UNRESOLVE, new ModuleUnresolveRequest(artifact),
                ModuleUnresolveResponse.class);
        return response.getMessages();
    }

    public Map<String, List<ModuleModel>> getModulesByLayerName() throws ClientException, ServerException {
        var response = sendRequest(Endpoints.MODULE_LIST, null, ModuleListResponse.class);
        return response.getModulesByLayerName();
    }

    public List<ServerSessionDto> getSessions() throws ClientException, ServerException {
        var response = sendRequest(Endpoints.SESSION_LIST, null, SessionListResponse.class);
        return response.getSessions();
    }

    public List<ThreadModel> getThreads() throws ClientException, ServerException {
        var response = sendRequest(Endpoints.THREAD_LIST, null, ThreadListResponse.class);
        return response.getThreads();
    }

    public List<ThreadInfoModel> getThreadInfos() throws ClientException, ServerException {
        var response = sendRequest(Endpoints.THREAD_INFO_LIST, null, ThreadInfoListResponse.class);
        return response.getThreadInfos();
    }
}
