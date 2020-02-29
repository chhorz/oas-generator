package com.github.chhorz.openapi.common.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.chhorz.openapi.common.domain.meta.Required;

/**
 * http://spec.openapis.org/oas/v3.0.3#server-variable-object
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


}
