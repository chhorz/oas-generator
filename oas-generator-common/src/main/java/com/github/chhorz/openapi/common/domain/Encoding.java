package com.github.chhorz.openapi.common.domain;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#encoding-object
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
