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

import com.fasterxml.jackson.annotation.JsonValue;
import com.github.chhorz.openapi.common.domain.meta.Markdown;
import com.github.chhorz.openapi.common.domain.meta.Required;

import java.util.Objects;

/**
 * https://spec.openapis.org/oas/v3.1.0#parameter-object
 *
 * @author chhorz
 *
 */
public class Parameter {

	// fixed fields
	@Required
	private String name;
	@Required
	private In in;
	@Markdown
	private String description;
	private Boolean required = Boolean.FALSE;
	private Boolean deprecated;
	@Deprecated
	private Boolean allowEmptyValue;
	// style
	private Schema schema;

	public enum In {
		QUERY("query"),
		HEADER("header"),
		PATH("path"),
		COOKIE("cookie");

		private String openApiValue;

		In(final String openApiValue) {
			this.openApiValue = openApiValue;
		}

		@JsonValue
		public String getOpenApiValue() {
			return openApiValue;
		}
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public In getIn() {
		return in;
	}

	public void setIn(final In in) {
		this.in = in;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public Boolean getRequired() {
		return required;
	}

	public void setRequired(final Boolean required) {
		this.required = required;
	}

	public Boolean getDeprecated() {
		return deprecated;
	}

	public void setDeprecated(final Boolean deprecated) {
		this.deprecated = deprecated;
	}

	public Boolean getAllowEmptyValue() {
		return allowEmptyValue;
	}

	public void setAllowEmptyValue(final Boolean allowEmptyValue) {
		this.allowEmptyValue = allowEmptyValue;
	}

	public Schema getSchema() {
		return schema;
	}

	public void setSchema(final Schema schema) {
		this.schema = schema;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Parameter parameter = (Parameter) o;
		return Objects.equals(name, parameter.name) &&
			in == parameter.in;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, in);
	}

}
