/**
 *
 *    Copyright 2018-2020 the original author or authors.
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
package com.github.chhorz.openapi.common.properties.domain;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class GeneratorProperties {

	private InfoProperties info;
	private List<ServerProperties> servers;
	private ExternalDocsProperties externalDocs;
	private Map<String, SecuritySchemeProperties> securitySchemes;
	private Map<String, TagProperties> tags;
	private ParserProperties parser;

	public GeneratorProperties() {
		info = new InfoProperties();
		servers = null;
		externalDocs = null;
		securitySchemes = new TreeMap<>();
		tags = new TreeMap<>();
		parser = new ParserProperties();
	}

	public InfoProperties getInfo() {
		return info;
	}

	public void setInfo(final InfoProperties info) {
		this.info = info;
	}

	public List<ServerProperties> getServers() {
		return servers;
	}

	public void setServers(final List<ServerProperties> servers) {
		this.servers = servers;
	}

	public ExternalDocsProperties getExternalDocs() {
		return externalDocs;
	}

	public void setExternalDocs(final ExternalDocsProperties externalDocs) {
		this.externalDocs = externalDocs;
	}

	public Map<String, SecuritySchemeProperties> getSecuritySchemes() {
		return securitySchemes;
	}

	public void setSecuritySchemes(final Map<String, SecuritySchemeProperties> securitySchemes) {
		this.securitySchemes = securitySchemes;
	}

	public Map<String, TagProperties> getTags() {
		return tags;
	}

	public void setTags(final Map<String, TagProperties> tags) {
		this.tags = tags;
	}

	public ParserProperties getParser() {
		return parser;
	}

	public void setParser(final ParserProperties parser) {
		this.parser = parser;
	}

}
