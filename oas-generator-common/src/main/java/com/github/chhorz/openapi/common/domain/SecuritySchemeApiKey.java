package com.github.chhorz.openapi.common.domain;

import com.github.chhorz.openapi.common.domain.meta.Required;

/**
 * https://spec.openapis.org/oas/v3.0.3#api-key-sample
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
