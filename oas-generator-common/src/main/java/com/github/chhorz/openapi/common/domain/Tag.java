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

import com.github.chhorz.openapi.common.domain.meta.Markdown;
import com.github.chhorz.openapi.common.domain.meta.Required;

import java.util.Objects;

/**
 * https://spec.openapis.org/oas/v3.1.0#tag-object
 *
 * @author chhorz
 *
 */
public class Tag {

	@Required
	private String name;
	@Markdown
	private String description;
	private ExternalDocumentation externalDocs;

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public ExternalDocumentation getExternalDocs() {
		return externalDocs;
	}

	public void setExternalDocs(final ExternalDocumentation externalDocs) {
		this.externalDocs = externalDocs;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Tag tag = (Tag) o;
		return name.equals(tag.name) &&
			Objects.equals(description, tag.description) &&
			Objects.equals(externalDocs, tag.externalDocs);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, description, externalDocs);
	}
}
