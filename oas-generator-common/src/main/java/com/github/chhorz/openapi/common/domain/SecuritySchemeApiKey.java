package com.github.chhorz.openapi.common.domain;

import com.github.chhorz.openapi.common.domain.meta.Required;

/**
 * https://github.com/OAI/OpenAPI-Specification/blob/v3.0.1/versions/3.0.1.md#security-scheme-object
 *
 * @author chhorz
 *
 */
public class SecuritySchemeApiKey extends SecurityScheme {

	@Required
	private String name;
	@Required
	private In in;

	enum In {
		query,
		header,
		cookie
	}

}
