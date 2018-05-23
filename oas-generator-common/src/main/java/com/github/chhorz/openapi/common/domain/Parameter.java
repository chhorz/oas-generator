package com.github.chhorz.openapi.common.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.chhorz.openapi.common.domain.meta.Required;

/**
 * https://github.com/OAI/OpenAPI-Specification/blob/v3.0.1/versions/3.0.1.md#parameter-object
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
	private Boolean allowEmptyValue;
	// style
	private Schema schema;

	public enum In {
		query,
		header,
		path,
		cookie
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
