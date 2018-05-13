package com.github.chhorz.openapi.common.properties;

public class InfoProperties {

	private String title;
	private String version;

	public InfoProperties() {
		title = GeneratorPropertiesDefaults.INFO_TITLE;
		version = GeneratorPropertiesDefaults.INFO_VERSION;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(final String version) {
		this.version = version;
	}

}
