package com.github.chhorz.openapi.common.domain;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.chhorz.openapi.common.domain.meta.Required;

/**
 * https://github.com/OAI/OpenAPI-Specification/blob/v3.0.1/versions/3.0.1.md#response-object
 *
 * @author chhorz
 *
 */
public class Response {

	@Required
	private String description;
	@JsonProperty("headers")
	private Map<String, Header> headerObjects;
//	@JsonProperty("headers")
//	private Map<String, Reference> headerReferences;
	private Map<String, MediaType> content;
	@JsonProperty("links")
	private Map<String, Link> linkObjects;
//	@JsonProperty("links")
//	private Map<String, Reference> linkReferences;

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public Map<String, MediaType> getContent() {
		return content;
	}

	public void putContent(final String key, final MediaType content) {
		if (this.content == null) {
			this.content = new HashMap<>();
		}
		this.content.put(key, content);
	}

}
