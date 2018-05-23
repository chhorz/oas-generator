package com.github.chhorz.openapi.common.domain;

import com.github.chhorz.openapi.common.domain.meta.Required;

/**
 * https://github.com/OAI/OpenAPI-Specification/blob/v3.0.1/versions/3.0.1.md#security-scheme-object
 *
 * @author chhorz
 *
 */
public class SecurityScheme {

	@Required
	private Type type;
	private String description;

	public enum Type {
		apiKey,
		http,
		oauth2,
		openIdConnect
	}

	public Type getType() {
		return type;
	}

	public void setType(final Type type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

}
