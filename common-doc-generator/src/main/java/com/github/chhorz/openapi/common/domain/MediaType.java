package com.github.chhorz.openapi.common.domain;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * https://github.com/OAI/OpenAPI-Specification/blob/v3.0.1/versions/3.0.1.md#media-type-object
 *
 * @author chhorz
 *
 */
public class MediaType {

	@JsonProperty("schema")
	private Schema schemaObject;
	@JsonProperty("schema")
	private Reference schemaReference;
	private Object example;
	@JsonProperty("examples")
	private Map<String, Example> exampleObjects;
	// @JsonProperty("examples")
	// private Map<String, Reference> exampleReferences;
	private Map<String, Encoding> encoding;

	public Object getSchema() {
		return schemaReference != null ? schemaReference : schemaObject;
	}

	public void setSchemaReference(final Reference schemaReference) {
		this.schemaReference = schemaReference;
	}

	public void setSchemaObject(final Schema schemaObject) {
		this.schemaObject = schemaObject;
	}

}
