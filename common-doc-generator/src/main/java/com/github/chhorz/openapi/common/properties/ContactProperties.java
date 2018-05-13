package com.github.chhorz.openapi.common.properties;

import java.net.URL;

public class ContactProperties {

	private String name;
	private String email;
	private URL url;

	public ContactProperties() {
		name = GeneratorPropertiesDefaults.CONTACT_NAME;
		email = GeneratorPropertiesDefaults.CONTACT_EMAIL;
		url = GeneratorPropertiesDefaults.CONTACT_URL;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(final String email) {
		this.email = email;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(final URL url) {
		this.url = url;
	}

}
