package com.github.chhorz.openapi.common.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * https://github.com/OAI/OpenAPI-Specification/blob/v3.0.1/versions/3.0.1.md#operation-object
 *
 * @author chhorz
 *
 */
public class Operation {

	private List<String> tags;
	private String summary;
	private String description;
	private ExternalDocumentation externalDocs;
	private String operationId;
	@JsonProperty("parameters")
	private List<Parameter> parameterObjects;
	// @JsonProperty("parameters")
	// private List<Reference> parameterReferences;
	// @JsonProperty("requestBody")
	// private RequestBody requestBodyObject;
	@JsonProperty("requestBody")
	private Reference requestBodyReference;
	private Responses responses;
	@JsonProperty("callbacks")
	private Map<String, Callback> callbackObjects;
	// @JsonProperty("callbacks")
	// private Map<String, Reference> callbackReferences;
	private Boolean deprecated = Boolean.FALSE;
	/**
	 * @see {@link SecurityRequirement}
	 */
	private Map<String, List<String>> security;
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

	public Reference getRequestBodyReference() {
		return requestBodyReference;
	}

	public void setRequestBodyReference(final Reference requestBodyReference) {
		this.requestBodyReference = requestBodyReference;
	}

	public Responses getResponses() {
		return responses;
	}

	public void setResponses(final Responses responses) {
		this.responses = responses;
	}

	public Boolean getDeprecated() {
		return deprecated;
	}

	public void setDeprecated(final Boolean deprecated) {
		this.deprecated = deprecated;
	}

	public Map<String, List<String>> getSecurity() {
		return security;
	}

	public void setSecurity(final Map<String, List<String>> security) {
		this.security = security;
	}

}
