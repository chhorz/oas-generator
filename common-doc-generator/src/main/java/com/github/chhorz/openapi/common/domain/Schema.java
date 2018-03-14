package com.github.chhorz.openapi.common.domain;

import static java.lang.Boolean.FALSE;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * https://github.com/OAI/OpenAPI-Specification/blob/v3.0.1/versions/3.0.1.md#schema-object
 *
 * @author chhorz
 *
 */
public class Schema {

	private Boolean nullable = FALSE;
	private Descriminator discriminator;
	private Boolean readOnly = FALSE;
	private Boolean writeOnly = FALSE;
	private Xml xml;
	private ExternalDocumentation externalDocs;
	private Object example;
	private Boolean deprecated = FALSE;

	private String type;
	private String format;
	private String description;
	private Map<String, Object> properties;

	@JsonProperty("default")
	private Object defaultValue;
	private String pattern;

	public String getType() {
		return type;
	}

	public void setType(final String type) {
		this.type = type;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(final String format) {
		this.format = format;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	public void putProperty(final String name, final Schema property) {
		if (properties == null) {
			properties = new HashMap<>();
		}
		properties.put(name, property);
	}

	public void putProperty(final String name, final Reference reference) {
		if (properties == null) {
			properties = new HashMap<>();
		}
		properties.put(name, reference);
	}

	public Object getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(final Object defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(final String pattern) {
		this.pattern = pattern;
	}

	@Override
	public String toString() {
		return String.format("Schema [type=%s, format=%s, description=%s, properties=%s]", type, format, description, properties);
	}

}
