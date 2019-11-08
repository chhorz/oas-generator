package com.github.chhorz.openapi.common.test.properties;

import com.github.chhorz.openapi.common.properties.GeneratorPropertyLoader;
import com.github.chhorz.openapi.common.properties.ParserProperties;
import com.github.chhorz.openapi.common.test.github.GithubIssue;
import com.github.chhorz.openapi.common.test.properties.test.ProcessorAProperties;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;

class GeneratorPropertyLoaderTest {

	@Test
	void testPostProcessorProperties() {
		// given
		Map<String, String> processorOptions = singletonMap("propertiesPath", "./properties/postProcessorTest.yml");
		GeneratorPropertyLoader generatorPropertyLoader = new GeneratorPropertyLoader(processorOptions);

		// when
		ParserProperties parserProperties = generatorPropertyLoader.getParserProperties();

		// then
		assertThat(parserProperties)
			.isNotNull();

		assertThat(parserProperties.getPostProcessor("one", ProcessorAProperties.class))
			.isNotNull()
			.isNotEmpty()
			.get()
			.hasFieldOrPropertyWithValue("valueOne", "Test")
			.hasFieldOrPropertyWithValue("valueTwo", 2);
	}

	@GithubIssue("#3")
	@Test
	void testEmptyPostProcessorProperties() {
		// given
		Map<String, String> processorOptions = singletonMap("propertiesPath", "./properties/postProcessorEmptyTest.yml");
		GeneratorPropertyLoader generatorPropertyLoader = new GeneratorPropertyLoader(processorOptions);

		// when
		ParserProperties parserProperties = generatorPropertyLoader.getParserProperties();

		// then
		assertThat(parserProperties)
			.isNotNull();

		assertThat(parserProperties.getPostProcessor("one", ProcessorAProperties.class))
			.isNotNull()
			.isEmpty();
	}

}
