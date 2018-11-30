package com.github.chhorz.openapi.common.domain;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#media-type-object
 *
 * @author chhorz
 *
 */
public class MediaType {

	private Object schema;
	private Object example;
	@JsonProperty("examples")
	private Map<String, Example> exampleObjects;
	// @JsonProperty("examples")
	// private Map<String, Reference> exampleReferences;
	private Map<String, Encoding> encoding;

	public Object getSchema() {
		return schema;
	}

	public void setSchema(final Object schema) {
		this.schema = schema;
	}

}
