package com.github.chhorz.openapi.common.domain;

import com.github.chhorz.openapi.common.domain.meta.Required;

/**
 * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#security-scheme-object
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
