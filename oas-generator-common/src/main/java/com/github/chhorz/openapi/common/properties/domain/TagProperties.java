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

public class TagProperties {

	private String description;
	private ExternalDocsProperties externalDocs;

	public TagProperties() {
		description = GeneratorPropertiesDefaults.TAG_DESCRIPTION;
		externalDocs = new ExternalDocsProperties();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public ExternalDocsProperties getExternalDocs() {
		return externalDocs;
	}

	public void setExternalDocs(final ExternalDocsProperties externalDocs) {
		this.externalDocs = externalDocs;
	}

}
