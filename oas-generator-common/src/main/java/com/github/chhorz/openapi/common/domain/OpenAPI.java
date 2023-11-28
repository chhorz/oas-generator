/**
 *
 *    Copyright 2018-2023 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.github.chhorz.openapi.common.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.github.chhorz.openapi.common.domain.meta.Required;

import static java.util.Comparator.comparing;

/**
 * https://spec.openapis.org/oas/v3.1.0#openapi-object
 *
 * @author chhorz
 *
 */
public class OpenAPI {

	@Required
	private String openapi;
	@Required
	private Info info;
	private String jsonSchemaDialect;
	private List<Server> servers;
	@Required
	private final Map<String, PathItemObject> paths = new TreeMap<>();
	private Map<String, Object> webhooks;
	private Components components;
	private List<SecurityRequirement> security;
	private List<Tag> tags;
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

	public void setServers(final List<Server> servers) {
		this.servers = servers;
	}

	public Map<String, PathItemObject> getPaths() {
		return paths;
	}

	public void putPathItemObject(final String path, final PathItemObject pathItemObject) {
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

	public List<Tag> getTags() {
		if (tags != null) {
			tags.sort(comparing(Tag::getName));
		}
		return tags;
	}

	public void addTag(final Tag tag) {
		if (tags == null) {
			tags = new ArrayList<>();
		}

		if (tags.stream().noneMatch(t -> t.getName().equalsIgnoreCase(tag.getName()))) {
			tags.add(tag);
		}
	}

	public ExternalDocumentation getExternalDocs() {
		return externalDocs;
	}

	public void setExternalDocs(final ExternalDocumentation externalDocs) {
		this.externalDocs = externalDocs;
	}

}
