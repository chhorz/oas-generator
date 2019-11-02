package com.github.chhorz.openapi.common.domain;

import java.util.Map;
import java.util.TreeMap;

import com.github.chhorz.openapi.common.domain.meta.Required;

/**
 * http://spec.openapis.org/oas/v3.0.2#server-object
 *
 * @author chhorz
 *
 */
public class Server {

	@Required
	private String url;
	private String description;
	private Map<String, ServerVariableObject> variables;

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

	public Map<String, ServerVariableObject> getVariables() {
		return variables;
	}

	public void setVariables(Map<String, ServerVariableObject> variables) {
		this.variables = variables;
	}

	public void addVariable(String variable, ServerVariableObject variableObject){
		if (variables == null) {
			variables = new TreeMap<>();
		}
		variables.put(variable, variableObject);
	}
}
