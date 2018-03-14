package com.github.chhorz.openapi.common.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.chhorz.openapi.common.domain.meta.Required;

/**
 * https://github.com/OAI/OpenAPI-Specification/blob/v3.0.1/versions/3.0.1.md#openapi-object
 *
 * @author chhorz
 *
 */
public class OpenAPI {

	@Required
	private String openapi;
	@Required
	private Info info;
	private List<Server> servers;
	@Required
	private Map<String, PathItemObject> paths;
	private Components components;
	private List<SecurityRequirement> security;
	private Tag tags;
	private ExternalDocumentation externalDocs;

	public String getOpenapi() {
		return openapi;
	}

	public void setOpenapi(final String openapi) {
		this.openapi = openapi;
	}

	public Info getInfo() {
		return info;
	}

	public void setInfo(final Info info) {
		this.info = info;
	}

	public List<Server> getServers() {
		return servers;
	}

	public void addServer(final Server server) {
		if (servers == null) {
			servers = new ArrayList<>();
		}
		if (server != null) {
			servers.add(server);
		}
	}

	public Map<String, PathItemObject> getPaths() {
		return paths;
	}

	public void putPathItemObject(final String path, final PathItemObject pathItemObject) {
		if (this.paths == null) {
			this.paths = new HashMap<>();
		}
		this.paths.put(path, pathItemObject);
	}

	public Components getComponents() {
		return components;
	}

	public void setComponents(final Components components) {
		this.components = components;
	}

	public List<SecurityRequirement> getSecurity() {
		return security;
	}

	public void setSecurity(final List<SecurityRequirement> security) {
		this.security = security;
	}

	public Tag getTags() {
		return tags;
	}

	public void setTags(final Tag tags) {
		this.tags = tags;
	}

	public ExternalDocumentation getExternalDocs() {
		return externalDocs;
	}

	public void setExternalDocs(final ExternalDocumentation externalDocs) {
		this.externalDocs = externalDocs;
	}

}
