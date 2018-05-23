package com.github.chhorz.openapi.common.properties;

import java.net.URL;

public class ContactProperties {

	private String name;
	private URL url;
	private String email;

	public ContactProperties() {
		name = GeneratorPropertiesDefaults.CONTACT_NAME;
		url = GeneratorPropertiesDefaults.CONTACT_URL;
		email = GeneratorPropertiesDefaults.CONTACT_EMAIL;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(final String email) {
		this.email = email;
	}

}
