package com.github.chhorz.openapi.common.domain;

import com.github.chhorz.openapi.common.domain.meta.Required;

/**
 * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#license-object
 *
 * @author chhorz
 *
 */
public class License {

	@Required
	private String name;
	private String url;

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(final String url) {
		this.url = url;
	}

}
