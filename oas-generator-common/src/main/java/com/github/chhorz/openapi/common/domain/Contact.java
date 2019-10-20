package com.github.chhorz.openapi.common.domain;

/**
 * http://spec.openapis.org/oas/v3.0.2#contact-object
 *
 * @author chhorz
 *
 */
public class Contact {

	private String name;
	private String url;
	private String email;

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

	public String getEmail() {
		return email;
	}

	public void setEmail(final String email) {
		this.email = email;
	}

}
