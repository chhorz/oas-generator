/**
 *
 *    Copyright 2018-2020 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.github.chhorz.openapi.common.properties.domain;

import com.github.chhorz.openapi.common.properties.GeneratorPropertiesDefaults;

import java.net.URL;

public class InfoProperties {

	private String title;
	private String summary;
	private String description;
	private URL termsOfService;
	private ContactProperties contact;
	private LicenseProperties license;
	private String version;

	public InfoProperties() {
		title = GeneratorPropertiesDefaults.INFO_TITLE;
		summary = GeneratorPropertiesDefaults.INFO_SUMMARY;
		description = GeneratorPropertiesDefaults.INFO_DESCRIPTION;
		termsOfService = GeneratorPropertiesDefaults.INFO_TERMS_OF_SERVICE;
		contact = null;
		license = null;
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
