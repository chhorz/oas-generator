package com.github.chhorz.openapi.common.properties;

import java.net.URL;

public class ExternalDocsProperties {

	private URL url;
	private String description;

	public ExternalDocsProperties() {
		url = GeneratorPropertiesDefaults.EXTERNAL_DOCS_URL;
		description = GeneratorPropertiesDefaults.EXTERNAL_DOCS_DESCRIPTION;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(final URL url) {
		this.url = url;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

}
