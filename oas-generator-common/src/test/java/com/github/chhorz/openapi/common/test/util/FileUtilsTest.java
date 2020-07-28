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
package com.github.chhorz.openapi.common.test.util;

import com.github.chhorz.openapi.common.domain.OpenAPI;
import com.github.chhorz.openapi.common.domain.Reference;
import com.github.chhorz.openapi.common.domain.Schema;
import com.github.chhorz.openapi.common.domain.Schema.Format;
import com.github.chhorz.openapi.common.domain.Schema.Type;
import com.github.chhorz.openapi.common.properties.domain.ParserProperties;
import com.github.chhorz.openapi.common.util.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import static org.assertj.core.api.Assertions.assertThat;

public class FileUtilsTest {

	private static Map<String, Schema> schemaMap;
	private static Schema article;
	private static Schema order;

	@BeforeAll
	static void setUp() {
		article = new Schema();
		article.setType(Type.OBJECT);
		article.setFormat(null);
		article.setDescription("An article that can be ordered.");
		article.putProperty("name", prop(Type.STRING, null, ""));
		article.putProperty("number", prop(Type.INTEGER, Format.INT64, "The basic number of the resource."));
		article.putProperty("price", prop(Type.NUMBER, Format.DOUBLE, ""));
		article.putProperty("type", enumProp(Type.STRING, null, "", "SIMPLE", "PREMIUM"));

		order = new Schema();
		order.setType(Type.OBJECT);
		order.setFormat(null);
		order.setDescription("The order.");
		order.putProperty("article",
				arrayProp(Type.ARRAY, null, "The ordered article.", new Reference("#/components/schemas/Article")));
		order.putProperty("number", prop(Type.INTEGER, Format.INT64, "The basic number of the resource."));
		order.putProperty("orderTs", prop(Type.STRING, Format.DATE_TIME, ""));
		order.putProperty("referenceNumber", arrayProp(Type.ARRAY, null, "", prop(Type.STRING, null, null)));

		schemaMap = new TreeMap<>();
		schemaMap.put("Article", article);
		schemaMap.put("Order", order);
	}

	private static Schema arrayProp(final Type type, final Format format, final String description, final Schema item) {
		Schema property = prop(type, format, description);
		property.setItems(item);
		return property;
	}

	private static Schema arrayProp(final Type type, final Format format, final String description, final Reference ref) {
		Schema property = prop(type, format, description);
		property.setItems(ref);
		return property;
	}

	private static Schema enumProp(final Type type, final Format format, final String description, final String... enumValues) {
		Schema property = prop(type, format, description);
		Arrays.asList(enumValues).forEach(value -> property.addEnumValue(value));
		return property;
	}

	private static Schema prop(final Type type, final Format format, final String description) {
		Schema property = new Schema();
		property.setType(type);
		property.setFormat(format);
		property.setDescription(description);
		return property;
	}

	@Test
	void testReadDefaultFile() {
		// given
		ParserProperties properties = new ParserProperties();
		properties.setSchemaDir("./src/test/resources");

		FileUtils fileUtils = new FileUtils(properties);

		// when
		Optional<OpenAPI> optionalOpenAPI = fileUtils.readOpenAPIObjectFromFile();

		// then
		assertThat(optionalOpenAPI)
			.isNotNull()
			.isPresent();

		OpenAPI openAPI = optionalOpenAPI.get();

		assertThat(openAPI)
			.isNotNull()
			.extracting(OpenAPI::getComponents)
				.isNotNull();

		assertThat(openAPI.getComponents().getSchemas())
			.isNotNull()
			.isNotEmpty()
			.hasSize(2)
				.containsKeys("Article", "Order");

		assertThat(openAPI.getComponents().getSchemas().toString()).isEqualTo(schemaMap.toString());
	}

	@Test
	void testRead() {
		// given
		ParserProperties properties = new ParserProperties();
		properties.setSchemaDir("./src/test/resources");
		properties.setSchemaFile("openapi-custom-schema");

		FileUtils fileUtils = new FileUtils(properties);

		// when
		Optional<OpenAPI> optionalOpenAPI = fileUtils.readOpenAPIObjectFromFile();

		// then
		assertThat(optionalOpenAPI)
			.isNotNull()
			.isPresent();

		OpenAPI openAPI = optionalOpenAPI.get();

		assertThat(openAPI)
			.isNotNull()
			.extracting(OpenAPI::getComponents)
			.isNotNull();

		assertThat(openAPI.getComponents().getSchemas())
			.isNotNull()
			.isNotEmpty()
			.hasSize(2)
			.containsKeys("Article", "Order");

		assertThat(openAPI.getComponents().getSchemas().toString()).isEqualTo(schemaMap.toString());
	}

	@Test
	void testReadWithFileSuffix() {
		// given
		ParserProperties properties = new ParserProperties();
		properties.setSchemaDir("./src/test/resources");
		properties.setSchemaFile("openapi-custom-schema.json");

		FileUtils fileUtils = new FileUtils(properties);

		// when
		Optional<OpenAPI> optionalOpenAPI = fileUtils.readOpenAPIObjectFromFile();

		// then
		assertThat(optionalOpenAPI)
			.isNotNull()
			.isPresent();

		OpenAPI openAPI = optionalOpenAPI.get();

		assertThat(openAPI)
			.isNotNull()
			.extracting(OpenAPI::getComponents)
			.isNotNull();

		assertThat(openAPI.getComponents().getSchemas())
			.isNotNull()
			.isNotEmpty()
			.hasSize(2)
			.containsKeys("Article", "Order");

		assertThat(openAPI.getComponents().getSchemas().toString()).isEqualTo(schemaMap.toString());
	}

	@Test
	void testInvalidPath() {
		// given
		ParserProperties properties = new ParserProperties();
		properties.setSchemaFile("./src/test/resources/openapi-schema-unknown-file.json");

		FileUtils fileUtils = new FileUtils(properties);

		// when
		Optional<OpenAPI> optionalOpenAPI = fileUtils.readOpenAPIObjectFromFile();

		// then
		assertThat(optionalOpenAPI)
			.isNotNull()
			.isEmpty();
	}
}
