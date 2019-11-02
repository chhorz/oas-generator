package com.github.chhorz.openapi.common.properties;

import java.util.Map;

public class ServerProperties {

	private String url;
	private String description;
	private Map<String, ServerVariableProperties> variables;

	public String getUrl() {
		return url;
	}

	public void setUrl(final String url) {
		this.url = url;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public Map<String, ServerVariableProperties> getVariables() {
		return variables;
	}

	public void setVariables(Map<String, ServerVariableProperties> variables) {
		this.variables = variables;
	}
}
