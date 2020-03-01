package com.github.chhorz.openapi.common.domain;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * https://spec.openapis.org/oas/v3.0.3#encoding-object
 *
 * @author chhorz
 *
 */
public class Encoding {

	private String contentType;
	@JsonProperty("headers")
	private Map<String, Header> headerObjects;
	@JsonProperty("headers")
	private Map<String, Reference> headerReferences;
	private String style;
	private Boolean explode;
	private Boolean allowReserved;

}
