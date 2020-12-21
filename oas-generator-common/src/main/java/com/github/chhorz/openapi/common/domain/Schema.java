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
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.chhorz.openapi.common.domain.meta.Required;
import com.github.chhorz.openapi.common.util.jackson.SchemaPropertyDeserializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * https://spec.openapis.org/oas/v3.0.3#schema-object
 *
 * @author chhorz
 *
 */
public class Schema {

	// private Boolean nullable = FALSE;
	// private Discriminator discriminator;
	// private Boolean readOnly = FALSE;
	// private Boolean writeOnly = FALSE;
	// private Xml xml;
	// private ExternalDocumentation externalDocs;
	// private Object example;

	private Boolean deprecated = false;

	@Required
	private Type type;
	private Format format;
	private String description;
	@JsonDeserialize(using = SchemaPropertyDeserializer.class)
	private Map<String, Object> properties;

	@JsonProperty("default")
	private Object defaultValue;
	private String pattern;

	@JsonProperty("enum")
	private List<String> enumValues;

	private Object items;

	public enum Type {
		ARRAY("array"),
		BOOLEAN("boolean"),
		ENUM("enum"),
		INTEGER("integer"),
		NUMBER("number"),
		OBJECT("object"),
		STRING("string");

		private String openApiValue;

		Type(final String openApiValue) {
			this.openApiValue = openApiValue;
		}

		@JsonValue
		public String getOpenApiValue() {
			return openApiValue;
		}

	}

	public enum Format {
		BYTE("bype"),
		DATE("date"),
		DATE_TIME("date-time"),
		DOUBLE("double"),
		FLOAT("float"),
		INT32("int32"),
		INT64("int64");

		private String openApiValue;

		Format(final String openApiValue) {
			this.openApiValue = openApiValue;
		}

		@JsonValue
		public String getOpenApiValue() {
			return openApiValue;
		}
	}

	public Boolean getDeprecated() {
		return deprecated;
	}

	public void setDeprecated(final Boolean deprecated) {
		this.deprecated = deprecated;
	}

	public Type getType() {
		return type;
	}

	public void setType(final Type type) {
		this.type = type;
	}

	public Format getFormat() {
		return format;
	}

	public void setFormat(final Format format) {
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
			properties = new TreeMap<>();
		}
		properties.put(name, property);
	}

	public void putProperty(final String name, final Reference reference) {
		if (properties == null) {
			properties = new TreeMap<>();
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

	public void addEnumValue(final String enumValue) {
		if (enumValues == null) {
			enumValues = new ArrayList<>();
		}
		enumValues.add(enumValue);
	}

	public List<String> getEnumValues() {
		return enumValues;
	}

	public Object getItems() {
		return items;
	}

	public void setItems(final Object items) {
		this.items = items;
	}

	@Override
	public String toString() {
		return String.format(
				"Schema [deprecated=%s, type=%s, format=%s, description=%s, properties=%s, defaultValue=%s, pattern=%s, enumValues=%s, items=%s]",
				deprecated, type, format, description, properties, defaultValue, pattern, enumValues, items);
	}

}
