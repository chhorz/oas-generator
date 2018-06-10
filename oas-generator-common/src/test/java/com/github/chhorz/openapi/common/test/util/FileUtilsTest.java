package com.github.chhorz.openapi.common.test.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.chhorz.openapi.common.domain.OpenAPI;
import com.github.chhorz.openapi.common.properties.ParserProperties;
import com.github.chhorz.openapi.common.util.FileUtils;

public class FileUtilsTest {

	private FileUtils fileUtils;

	@BeforeEach
	void setUpEach() {
		ParserProperties properties = new ParserProperties();
		properties.setSchemaFile("");

		this.fileUtils = new FileUtils(properties);
	}

	@Test
	void testRead() {
		// when
		Optional<OpenAPI> optionalOpenAPI = fileUtils.readFromFile();

		// then
		assertThat(optionalOpenAPI).isPresent();

		OpenAPI openAPI = optionalOpenAPI.get();
	}
}
