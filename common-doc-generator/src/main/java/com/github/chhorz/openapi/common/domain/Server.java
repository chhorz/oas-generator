package com.github.chhorz.openapi.common.domain;

import java.util.Map;

import com.github.chhorz.openapi.common.domain.meta.Required;

/**
 * https://github.com/OAI/OpenAPI-Specification/blob/v3.0.1/versions/3.0.1.md#server-object
 *
 * @author chhorz
 *
 */
public class Server {

	@Required
	private String url;
	private String description;
	private Map<String, ServerVariableObject> variables;

	public String getUrl() {
		return url;
	}

	public void setUrl(final String url) {
		this.url = url;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

}
