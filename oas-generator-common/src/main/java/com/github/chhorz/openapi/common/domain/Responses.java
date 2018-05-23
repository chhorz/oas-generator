package com.github.chhorz.openapi.common.domain;

import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * https://github.com/OAI/OpenAPI-Specification/blob/v3.0.1/versions/3.0.1.md#responses-object
 *
 * @author chhorz
 *
 */
public class Responses {

	// default fields
	@JsonProperty("default")
	private Response defaultResponse;
	// @JsonProperty("default")
	// private Reference defaultReference;
	// patterned fields
	private Map<String, Response> responseObject;
	// private Map<String, Reference> responseReference;

	public Response getDefaultResponse() {
		return defaultResponse;
	}

	public void setDefaultResponse(final Response defaultResponse) {
		this.defaultResponse = defaultResponse;
	}

	public Map<String, Response> getResponseResponse() {
		return responseObject;
	}

	public void putResponseResponse(final String statusCode, final Response responseObject) {
		if (this.responseObject == null) {
			this.responseObject = new TreeMap<>();
		}
		this.responseObject.put(statusCode, responseObject);
	}

}
