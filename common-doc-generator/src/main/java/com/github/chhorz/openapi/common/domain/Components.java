package com.github.chhorz.openapi.common.domain;

import java.util.Map;
import java.util.TreeMap;

import javax.lang.model.type.TypeMirror;

/**
 * https://github.com/OAI/OpenAPI-Specification/blob/v3.0.1/versions/3.0.1.md#components-object
 *
 * @author chhorz
 *
 */
public class Components {

	private Map<String, Schema> schemas;
	private Map<String, Response> responses;
	private Map<String, Parameter> parameters;
	private Map<String, Example> examples;
	private Map<String, RequestBody> requestBodies;
	private Map<String, Header> headers;
	private Map<String, SecurityScheme> securitySchemes;
	private Map<String, Link> links;
	private Map<String, Callback> callbacks;

	public static boolean isValidKey(final String key) {
		return key.matches("^[a-zA-Z0-9\\.\\-_]+$");
	}

	public void putAllSchemas(final Map<TypeMirror, Schema> schemas) {
		if (this.schemas == null) {
			this.schemas = new TreeMap<>();
		}

		schemas.forEach((typeMirror, schema) -> this.schemas.put(getKey(typeMirror), schema));
	}

	public Map<String, Schema> getSchemas() {
		return schemas;
	}

	public void putRequestBody(final String key, final RequestBody requestBody) {
		if (requestBodies == null) {
			requestBodies = new TreeMap<>();
		}
		requestBodies.put(normalizeKey(key), requestBody);
	}

	public Map<String, RequestBody> getRequestBodies() {
		return requestBodies;
	}

	public void putResponse(final String key, final Response response) {
		if (responses == null) {
			responses = new TreeMap<>();
		}
		responses.put(normalizeKey(key), response);
	}

	public Map<String, Response> getResponses() {
		return responses;
	}

	public Map<String, SecurityScheme> getSecuritySchemes() {
		return securitySchemes;
	}

	public void setSecuritySchemes(final Map<String, SecurityScheme> securitySchemes) {
		this.securitySchemes = securitySchemes;
	}

	private String normalizeKey(final String key) {
		return key.substring(key.lastIndexOf('.') + 1);
	}

	private String getKey(final TypeMirror typeMirror) {
		String typeString = typeMirror.toString();
		while (typeString.contains("<")) {
			typeString = typeString.substring(typeString.indexOf('<') + 1, typeString.indexOf('>'));
		}
		return typeString.substring(typeString.lastIndexOf('.') + 1);
	}
}
