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
package com.github.chhorz.openapi.common.domain;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import com.github.chhorz.openapi.common.domain.meta.Required;

/**
 * https://spec.openapis.org/oas/v3.0.3#server-object
 *
 * @author chhorz
 *
 */
public class Server {

	@Required
	private String url;
	private String description;
	private Map<String, ServerVariableObject> variables;

	public String getUrl() {
		return url;
	}

	public void setUrl(final String url) {
		this.url = url;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public Map<String, ServerVariableObject> getVariables() {
		return variables;
	}

	public void setVariables(Map<String, ServerVariableObject> variables) {
		this.variables = variables;
	}

	public void addVariable(String variable, ServerVariableObject variableObject){
		if (variables == null) {
			variables = new TreeMap<>();
		}
		variables.put(variable, variableObject);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Server server = (Server) o;
		return url.equals(server.url) &&
			Objects.equals(description, server.description) &&
			Objects.equals(variables, server.variables);
	}

	@Override
	public int hashCode() {
		return Objects.hash(url, description, variables);
	}
}
