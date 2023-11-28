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
import com.github.chhorz.openapi.common.domain.meta.Markdown;
import com.github.chhorz.openapi.common.domain.meta.Required;
import com.github.chhorz.openapi.common.util.jackson.SchemaPropertyDeserializer;

import java.util.*;

/**
 * https://spec.openapis.org/oas/v3.1.0#schema-object
 *
 * @author chhorz
 *
 */
public class Schema {

	private List<String> required;
	private Long maximum;
	private Long minimum;
	private String pattern;
	private Integer maxItems;
	private Integer minItems;
	private Integer maxLength;
	private Integer minLength;

	private Boolean deprecated = false;

	@Required
	private Type type;
	/**
	 * The specification documents some example values, but the {@code format} attribute allows any values.
	 */
	private Format format;
	@Markdown
	private String description;
	@JsonDeserialize(using = SchemaPropertyDeserializer.class)
	private Map<String, Object> properties;

	@JsonProperty("default")
	private Object defaultValue;

	@JsonProperty("enum")
	private List<String> enumValues;

	private Object items;

	private Object additionalProperties;

	public List<String> getRequired() {
		return required;
	}

	public void addRequired(String required) {
		if (this.required == null) {
			this.required = new ArrayList<>();
		}
		this.required.add(required);
	}

	public Long getMaximum() {
		return maximum;
	}

	public void setMaximum(Long maximum) {
		this.maximum = maximum;
	}

	public Long getMinimum() {
		return minimum;
	}

	public void setMinimum(Long minimum) {
		this.minimum = minimum;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(final String pattern) {
		this.pattern = pattern;
	}

	public Integer getMaxItems() {
		return maxItems;
	}

	public void setMaxItems(Integer maxItems) {
		this.maxItems = maxItems;
	}

	public Integer getMinItems() {
		return minItems;
	}

	public void setMinItems(Integer minItems) {
		this.minItems = minItems;
	}

	public Integer getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(Integer maxLength) {
		this.maxLength = maxLength;
	}

	public Integer getMinLength() {
		return minLength;
	}

	public void setMinLength(Integer minLength) {
		this.minLength = minLength;
	}

	public void setEnumValues(List<String> enumValues) {
		this.enumValues = enumValues;
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

	public Object getAdditionalProperties() {
		return additionalProperties;
	}

	public void setAdditionalProperties(Object additionalProperties) {
		this.additionalProperties = additionalProperties;
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", Schema.class.getSimpleName() + "[", "]")
			.add("required=" + required)
			.add("maximum=" + maximum)
			.add("minimum=" + minimum)
			.add("pattern='" + pattern + "'")
			.add("deprecated=" + deprecated)
			.add("type=" + type)
			.add("format=" + format)
			.add("description='" + description + "'")
			.add("properties=" + properties)
			.add("defaultValue=" + defaultValue)
			.add("enumValues=" + enumValues)
			.add("items=" + items)
			.add("additionalProperties=" + additionalProperties)
			.toString();
	}

	public enum Type {
		ARRAY("array"),
		BOOLEAN("boolean"),
		ENUM("enum"),
		INTEGER("integer"),
		NUMBER("number"),
		OBJECT("object"),
		STRING("string");

		private final String openApiValue;

		Type(final String openApiValue) {
			this.openApiValue = openApiValue;
		}

		@JsonValue
		public String getOpenApiValue() {
			return openApiValue;
		}

	}

	public enum Format {
		BINARY("binary"),
		BYTE("byte"),
		TIME("time"), // value that is not documented within the specification
		DATE("date"),
		DATE_TIME("date-time"),
		DOUBLE("double"),
		FLOAT("float"),
		INT32("int32"),
		INT64("int64"),
		PASSWORD("password");


		private final String openApiValue;

		Format(final String openApiValue) {
			this.openApiValue = openApiValue;
		}

		@JsonValue
		public String getOpenApiValue() {
			return openApiValue;
		}
	}

}
