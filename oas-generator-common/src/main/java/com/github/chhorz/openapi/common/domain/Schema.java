package com.github.chhorz.openapi.common.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.chhorz.openapi.common.util.jackson.SchemaPropertyDeserializer;

/**
 * https://github.com/OAI/OpenAPI-Specification/blob/v3.0.1/versions/3.0.1.md#schema-object
 *
 * @author chhorz
 *
 */
public class Schema {

	// private Boolean nullable = FALSE;
	// private Descriminator discriminator;
	// private Boolean readOnly = FALSE;
	// private Boolean writeOnly = FALSE;
	// private Xml xml;
	// private ExternalDocumentation externalDocs;
	// private Object example;
	// private Boolean deprecated = FALSE;

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
				"Schema [type=%s, format=%s, description=%s, properties=%s, defaultValue=%s, pattern=%s, enumValues=%s, items=%s]",
				type, format, description, properties, defaultValue, pattern, enumValues, items);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((defaultValue == null) ? 0 : defaultValue.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((enumValues == null) ? 0 : enumValues.hashCode());
		result = prime * result + ((format == null) ? 0 : format.hashCode());
		result = prime * result + ((items == null) ? 0 : items.hashCode());
		result = prime * result + ((pattern == null) ? 0 : pattern.hashCode());
		result = prime * result + ((properties == null) ? 0 : properties.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Schema other = (Schema) obj;
		if (defaultValue == null) {
			if (other.defaultValue != null) {
				return false;
			}
		} else if (!defaultValue.equals(other.defaultValue)) {
			return false;
		}
		if (description == null) {
			if (other.description != null) {
				return false;
			}
		} else if (!description.equals(other.description)) {
			return false;
		}
		if (enumValues == null) {
			if (other.enumValues != null) {
				return false;
			}
		} else if (!enumValues.equals(other.enumValues)) {
			return false;
		}
		if (format != other.format) {
			return false;
		}
		if (items == null) {
			if (other.items != null) {
				return false;
			}
		} else if (!items.equals(other.items)) {
			return false;
		}
		if (pattern == null) {
			if (other.pattern != null) {
				return false;
			}
		} else if (!pattern.equals(other.pattern)) {
			return false;
		}
		if (properties == null) {
			if (other.properties != null) {
				return false;
			}
		} else if (!properties.equals(other.properties)) {
			return false;
		}
		if (type != other.type) {
			return false;
		}
		return true;
	}

}
