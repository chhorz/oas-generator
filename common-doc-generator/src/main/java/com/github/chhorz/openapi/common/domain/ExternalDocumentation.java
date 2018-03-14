package com.github.chhorz.openapi.common.domain;

import com.github.chhorz.openapi.common.domain.meta.Required;

/**
 * https://github.com/OAI/OpenAPI-Specification/blob/v3.0.1/versions/3.0.1.md#external-documentation-object
 *
 * @author chhorz
 *
 */
public class ExternalDocumentation {

	private String description;
	@Required
	private String url;

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(final String url) {
		this.url = url;
	}

}
