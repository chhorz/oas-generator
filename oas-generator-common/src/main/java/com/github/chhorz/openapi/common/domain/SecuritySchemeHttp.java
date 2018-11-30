package com.github.chhorz.openapi.common.domain;

import com.github.chhorz.openapi.common.domain.meta.Required;

/**
 * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#security-scheme-object
 *
 * @author chhorz
 *
 */
public class SecuritySchemeHttp extends SecurityScheme {

	@Required
	private String scheme;
	private String bearerFormat;

	public String getScheme() {
		return scheme;
	}

	public void setScheme(final String scheme) {
		this.scheme = scheme;
	}

	public String getBearerFormat() {
		return bearerFormat;
	}

	public void setBearerFormat(final String bearerFormat) {
		this.bearerFormat = bearerFormat;
	}

}
