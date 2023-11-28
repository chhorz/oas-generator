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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.chhorz.openapi.common.domain.meta.Markdown;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * https://spec.openapis.org/oas/v3.1.0#operation-object
 *
 * @author chhorz
 *
 */
public class Operation {

	private List<String> tags;
	private String summary;
	@Markdown
	private String description;
	private ExternalDocumentation externalDocs;
	private String operationId;
	@JsonProperty("parameters")
	private List<Parameter> parameterObjects;
	// @JsonProperty("parameters")
	// private List<Reference> parameterReferences;
	@JsonProperty("requestBody")
	private RequestBody requestBodyObject;
	// @JsonProperty("requestBody")
	// private Reference requestBodyReference;
	/*
	 * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.1.0.md#responses-object
	 */
	private Map<String, Response> responses;
	@JsonProperty("callbacks")
	private Map<String, Callback> callbackObjects;
	// @JsonProperty("callbacks")
	// private Map<String, Reference> callbackReferences;
	private Boolean deprecated = Boolean.FALSE;
	/**
	 * @see {@link SecurityRequirement}
	 */
	private List<Map<String, List<String>>> security;
	private List<Server> servers;

	public List<String> getTags() {
		return tags;
	}

	public void addTag(final String tag) {
		if (tags == null) {
			tags = new ArrayList<>();
		}
		tags.add(tag);
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(final String summary) {
		this.summary = summary;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public String getOperationId() {
		return operationId;
	}

	public void setOperationId(final String operationId) {
		this.operationId = operationId;
	}

	public List<Parameter> getParameterObjects() {
		return parameterObjects;
	}

	public void addParameterObject(final Parameter parameterObject) {
		if (this.parameterObjects == null) {
			this.parameterObjects = new ArrayList<>();
		}
		this.parameterObjects.add(parameterObject);
	}

	public void addParameterObjects(final List<Parameter> parameterObjects) {
		if (this.parameterObjects == null) {
			this.parameterObjects = new ArrayList<>();
		}
		this.parameterObjects.addAll(parameterObjects);
	}

	public RequestBody getRequestBodyObject() {
		return requestBodyObject;
	}

	public void setRequestBodyObject(RequestBody requestBodyObject) {
		this.requestBodyObject = requestBodyObject;
	}

	public Map<String, Response> getResponses(){
		return responses;
	}

	public void setResponses(final Map<String, Response> responses) {
		this.responses = responses;
	}

	public void putResponse(final String status, final Response response) {
		if (this.responses == null) {
			this.responses = new TreeMap<>();
		}
		this.responses.put(status, response);
	}

	public void putDefaultResponse(final Response response) {
		putResponse("default", response);
	}

	public Boolean getDeprecated() {
		return deprecated;
	}

	public void setDeprecated(final Boolean deprecated) {
		this.deprecated = deprecated;
	}

	public List<Map<String, List<String>>> getSecurity() {
		return security;
	}

	public void setSecurity(final List<Map<String, List<String>>> security) {
		this.security = security;
	}

}
