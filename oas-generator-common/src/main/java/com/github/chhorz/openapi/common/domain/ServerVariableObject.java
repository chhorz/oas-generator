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

import java.util.List;
import java.util.Objects;

/**
 * https://spec.openapis.org/oas/v3.1.0#server-variable-object
 *
 * @author chhorz
 *
 */
public class ServerVariableObject {

	@JsonProperty("enum")
	private List<String> enumValue;
	@Required
	@JsonProperty("default")
	private String defaultValue;
	@Markdown
	private String description;

	public List<String> getEnumValue() {
		return enumValue;
	}

	public void setEnumValue(List<String> enumValue) {
		this.enumValue = enumValue;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ServerVariableObject that = (ServerVariableObject) o;
		return Objects.equals(enumValue, that.enumValue) &&
			defaultValue.equals(that.defaultValue) &&
			Objects.equals(description, that.description);
	}

	@Override
	public int hashCode() {
		return Objects.hash(enumValue, defaultValue, description);
	}
}
