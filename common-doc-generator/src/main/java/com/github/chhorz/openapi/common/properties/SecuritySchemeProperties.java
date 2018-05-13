package com.github.chhorz.openapi.common.properties;

public class SecuritySchemeProperties {

	private String type;
	private String description;
	private String scheme;

	public SecuritySchemeProperties() {
		type = GeneratorPropertiesDefaults.SECURITY_SCHEME_TYPE;
		description = GeneratorPropertiesDefaults.SECURITY_SCHEME_DESCRIPTION;
		scheme = GeneratorPropertiesDefaults.SECURITY_SCHEME_SCHEME;
	}

	public String getType() {
		return type;
	}

	public void setType(final String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public String getScheme() {
		return scheme;
	}

	public void setScheme(final String scheme) {
		this.scheme = scheme;
	}

}
