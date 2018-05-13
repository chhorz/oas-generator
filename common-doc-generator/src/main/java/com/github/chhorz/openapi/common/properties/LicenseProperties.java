package com.github.chhorz.openapi.common.properties;

import java.net.URL;

public class LicenseProperties {

	private String name;
	private URL url;

	public LicenseProperties() {
		name = GeneratorPropertiesDefaults.LICENSE_NAME;
		url = GeneratorPropertiesDefaults.LICENSE_URL;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(final URL url) {
		this.url = url;
	}

}
