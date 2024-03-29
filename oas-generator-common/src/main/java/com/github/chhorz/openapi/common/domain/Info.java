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
package com.github.chhorz.openapi.common.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.chhorz.openapi.common.domain.meta.Markdown;
import com.github.chhorz.openapi.common.domain.meta.Required;
import com.github.chhorz.openapi.common.domain.meta.SpecificationExtension;

/**
 * https://spec.openapis.org/oas/v3.1.0#info-object
 *
 * @author chhorz
 *
 */
public class Info {

	@Required
	private String title;
	private String summary;
	@Markdown
	private String description;
	private String termsOfService;
	private Contact contact;
	private License license;
	@Required
	private String version;

	@SpecificationExtension
	@JsonProperty("x-generated-by")
	private String xGeneratedBy;
	@SpecificationExtension
	@JsonProperty("x-generated-ts")
	private String xGeneratedTs;

	public String getTitle() {
		return title;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public String getTermsOfService() {
		return termsOfService;
	}

	public void setTermsOfService(final String termsOfService) {
		this.termsOfService = termsOfService;
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(final Contact contact) {
		this.contact = contact;
	}

	public License getLicense() {
		return license;
	}

	public void setLicense(final License license) {
		this.license = license;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(final String version) {
		this.version = version;
	}

	public String getxGeneratedBy() {
		return xGeneratedBy;
	}

	public void setxGeneratedBy(String xGeneratedBy) {
		this.xGeneratedBy = xGeneratedBy;
	}

	public String getxGeneratedTs() {
		return xGeneratedTs;
	}

	public void setxGeneratedTs(String xGeneratedTs) {
		this.xGeneratedTs = xGeneratedTs;
	}
}
