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

	enum Type {
		apiKey,
		http,
		oauth2,
		openIdConnect
	}

}
