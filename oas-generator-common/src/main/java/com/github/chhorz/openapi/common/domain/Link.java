package com.github.chhorz.openapi.common.domain;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * https://spec.openapis.org/oas/v3.0.3#link-object
 *
 * @author chhorz
 *
 */
public class Link {

	private String operationRef;
	private String operationId;
	@JsonProperty("parameters")
	private Map<String, Object> parameterObjects;
	// @JsonProperty("parameters")
	// private Map<String, Expression> parameterExpression;
	@JsonProperty("requestBody")
	private Object requestBodyObject;
	// @JsonProperty("requestBody")
	// private Expression requestBodyExpression;
	private String description;
	private Server server;

}
