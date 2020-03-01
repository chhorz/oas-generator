package com.github.chhorz.openapi.common.domain;

import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.chhorz.openapi.common.domain.meta.Required;

/**
 * https://spec.openapis.org/oas/v3.0.3#response-object
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
			this.content = new TreeMap<>();
		}
		this.content.put(key, content);
	}

}
