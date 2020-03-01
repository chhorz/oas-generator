package com.github.chhorz.openapi.common.domain;

import com.fasterxml.jackson.annotation.JsonValue;
import com.github.chhorz.openapi.common.domain.meta.Required;

/**
 * https://spec.openapis.org/oas/v3.0.3#parameter-object
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

}
