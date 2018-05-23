package com.github.chhorz.openapi.common.properties;

import java.net.URL;

public class InfoProperties {

	private String title;
	private String description;
	private URL termsOfService;
	private ContactProperties contact;
	private LicenseProperties license;
	private String version;

	public InfoProperties() {
		title = GeneratorPropertiesDefaults.INFO_TITLE;
		description = GeneratorPropertiesDefaults.INFO_DESCRIPTION;
		termsOfService = GeneratorPropertiesDefaults.INFO_TERMS_OF_SERVICE;
		contact = new ContactProperties();
		license = new LicenseProperties();
		version = GeneratorPropertiesDefaults.INFO_VERSION;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public URL getTermsOfService() {
		return termsOfService;
	}

	public void setTermsOfService(final URL termsOfService) {
		this.termsOfService = termsOfService;
	}

	public ContactProperties getContact() {
		return contact;
	}

	public void setContact(final ContactProperties contact) {
		this.contact = contact;
	}

	public LicenseProperties getLicense() {
		return license;
	}

	public void setLicense(final LicenseProperties license) {
		this.license = license;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(final String version) {
		this.version = version;
	}

}
