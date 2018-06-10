package com.github.chhorz.openapi.common.util.jackson;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.github.chhorz.openapi.common.domain.Reference;
import com.github.chhorz.openapi.common.domain.Schema;
import com.github.chhorz.openapi.common.domain.Schema.Format;
import com.github.chhorz.openapi.common.domain.Schema.Type;

public class SchemaPropertyDeserializer extends StdDeserializer<Map<String, Object>> {

	public SchemaPropertyDeserializer() {
		this(null);
	}

	public SchemaPropertyDeserializer(final Class<?> vc) {
		super(vc);
	}

	private static final long serialVersionUID = -7441283374884415436L;

	@Override
	public Map<String, Object> deserialize(final JsonParser jp, final DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		JsonNode node = jp.getCodec().readTree(jp);

		Map<String, Object> propertyMap = new TreeMap<>();

		node.fieldNames().forEachRemaining(fieldName -> {
			JsonNode childNode = node.get(fieldName);
			if (childNode.has("$ref")) {
				propertyMap.put(fieldName, new Reference(childNode.get(fieldName).asText()));
			} else {
				Schema schema = new Schema();
				schema.setType(deserializeType(Type.values(), childNode));
				schema.setFormat(deserializeFormat(Format.values(), childNode));
				schema.setDescription(childNode.get("description").asText());
				schema.setPattern(childNode.has("pattern") ? childNode.get("pattern").asText() : null);
				if (childNode.has("items")) {
					JsonNode itemsNode = childNode.get("items");
					if (itemsNode.has("$ref")) {
						schema.setItems(new Reference(itemsNode.get("$ref").asText()));
					} else {
						Schema itemsSchema = new Schema();
						itemsSchema.setType(deserializeType(Type.values(), itemsNode));
						itemsSchema.setFormat(deserializeFormat(Format.values(), itemsNode));
						// TODO check attributes for items
						schema.setItems(itemsSchema);
					}
				}
				// TODO enum
				// TODO defaultValue
				propertyMap.put(fieldName, schema);
			}
		});

		return propertyMap;
	}

	private Type deserializeType(final Type[] types, final JsonNode jsonNode) {
		if (jsonNode != null) {
			return Stream.of(types).filter(type -> type.getOpenApiValue().equals(jsonNode.asText())).findFirst().orElse(null);
		}
		return null;
	}

	private Format deserializeFormat(final Format[] formats, final JsonNode jsonNode) {
		if (jsonNode != null) {
			return Stream.of(formats).filter(format -> format.getOpenApiValue().equals(jsonNode.asText())).findFirst().orElse(
					null);
		}
		return null;
	}
}
