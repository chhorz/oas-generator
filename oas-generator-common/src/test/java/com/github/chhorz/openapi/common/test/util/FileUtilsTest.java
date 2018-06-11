package com.github.chhorz.openapi.common.test.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.github.chhorz.openapi.common.domain.OpenAPI;
import com.github.chhorz.openapi.common.domain.Reference;
import com.github.chhorz.openapi.common.domain.Schema;
import com.github.chhorz.openapi.common.domain.Schema.Format;
import com.github.chhorz.openapi.common.domain.Schema.Type;
import com.github.chhorz.openapi.common.properties.ParserProperties;
import com.github.chhorz.openapi.common.util.FileUtils;

public class FileUtilsTest {

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
	void testRead() {
		// given
		ParserProperties properties = new ParserProperties();
		properties.setSchemaFile("./src/test/resources/openapi-schema.json");

		FileUtils fileUtils = new FileUtils(properties);

		// when
		Optional<OpenAPI> optionalOpenAPI = fileUtils.readFromFile();

		// then
		assertThat(optionalOpenAPI)
			.isNotNull()
			.isPresent();

		OpenAPI openAPI = optionalOpenAPI.get();

		assertThat(openAPI)
			.isNotNull()
			.extracting(o -> o.getComponents())
				.isNotNull()
				.isNotEmpty();

		assertThat(openAPI.getComponents().getSchemas())
			.isNotNull()
			.isNotEmpty()
			.hasSize(2)
				.containsKeys("Article", "Order")
				.containsValues(article, order);
	}

	@Test
	void testInvalidPath() {
		// given
		ParserProperties properties = new ParserProperties();
		properties.setSchemaFile("./src/test/resources/openapi-schema-unknown-file.json");

		FileUtils fileUtils = new FileUtils(properties);

		// when
		Optional<OpenAPI> optionalOpenAPI = fileUtils.readFromFile();

		// then
		assertThat(optionalOpenAPI)
			.isNotNull()
			.isEmpty();
	}
}
