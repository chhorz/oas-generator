package com.github.chhorz.openapi.common.domain;

import java.util.Map;
import java.util.TreeMap;

import com.github.chhorz.openapi.common.domain.meta.Required;

/**
 * http://spec.openapis.org/oas/v3.0.3#request-body-object
 *
 * @author chhorz
 *
 */
public class RequestBody {

	private String description;
	@Required
	private Map<String, MediaType> content;
	private Boolean required = Boolean.FALSE;

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public Map<String, MediaType> getContent() {
		return content;
	}

	public void putContent(final String key, final MediaType mediaType) {
		if (content == null) {
			content = new TreeMap<>();
		}
		content.put(key, mediaType);
	}

	public Boolean getRequired() {
		return required;
	}

	public void setRequired(final Boolean required) {
		this.required = required;
	}

}
