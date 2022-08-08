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
package com.github.chhorz.openapi.common.util.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.github.chhorz.openapi.common.domain.Reference;
import com.github.chhorz.openapi.common.domain.Schema;
import com.github.chhorz.openapi.common.domain.Schema.Format;
import com.github.chhorz.openapi.common.domain.Schema.Type;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

public class SchemaPropertyDeserializer extends StdDeserializer<Map<String, Object>> {

	public SchemaPropertyDeserializer() {
		this(null);
	}

	public SchemaPropertyDeserializer(final Class<?> vc) {
		super(vc);
	}

	private static final long serialVersionUID = -7441283374884415436L;

	@Override
	public Map<String, Object> deserialize(final JsonParser jp, final DeserializationContext deserializationContext)
			throws IOException, JsonProcessingException {
		JsonNode node = jp.getCodec().readTree(jp);

		Map<String, Object> propertyMap = new TreeMap<>();

		node.fieldNames().forEachRemaining(fieldName -> {
			JsonNode childNode = node.get(fieldName);
			if (childNode.has("$ref")) {
				propertyMap.put(fieldName, new Reference(childNode.get("$ref").asText()));
			} else {
				Schema schema = new Schema();
				schema.setDeprecated(childNode.has("deprecated") && childNode.get("deprecated").asBoolean());
				schema.setType(deserializeType(Type.values(), childNode.get("type")));
				schema.setFormat(deserializeFormat(Format.values(), childNode.get("format")));
				schema.setDescription(childNode.get("description").asText());
				schema.setPattern(childNode.has("pattern") ? childNode.get("pattern").asText() : null);
				schema.setMinimum(childNode.has("minimum") ? childNode.get("minimum").asLong() : null);
				schema.setMaximum(childNode.has("maximum") ? childNode.get("maximum").asLong() : null);
				schema.setMaxItems(childNode.has("maxItems") ? childNode.get("maxItems").asInt() : null);
				schema.setMinItems(childNode.has("minItems") ? childNode.get("minItems").asInt() : null);
				schema.setMaxLength(childNode.has("maxLength") ? childNode.get("maxLength").asInt() : null);
				schema.setMinLength(childNode.has("minLength") ? childNode.get("minLength").asInt() : null);
				if (childNode.has("items")) {
					JsonNode itemsNode = childNode.get("items");
					if (itemsNode.has("$ref")) {
						schema.setItems(new Reference(itemsNode.get("$ref").asText()));
					} else {
						Schema itemsSchema = new Schema();
						itemsSchema.setType(deserializeType(Type.values(), itemsNode.get("type")));
						itemsSchema.setFormat(deserializeFormat(Format.values(), itemsNode.get("format")));
						// TODO check attributes for items
						schema.setItems(itemsSchema);
					}
				}

				if (childNode.has("enum")) {
					ArrayNode arrayNode = childNode.withArray("enum");
					arrayNode.forEach(arrayValue -> schema.addEnumValue(arrayValue.asText()));
				}

				// TODO defaultValue

				propertyMap.put(fieldName, schema);
			}
		});

		return propertyMap;
	}

	private Type deserializeType(final Type[] types, final JsonNode jsonNode) {
		if (jsonNode != null) {
			return Stream.of(types)
					.filter(type -> type.getOpenApiValue().equals(jsonNode.asText()))
					.findFirst()
					.orElse(null);
		}
		return null;
	}

	private Format deserializeFormat(final Format[] formats, final JsonNode jsonNode) {
		if (jsonNode != null) {
			return Stream.of(formats)
					.filter(format -> format.getOpenApiValue().equals(jsonNode.asText()))
					.findFirst()
					.orElse(null);
		}
		return null;
	}
}
