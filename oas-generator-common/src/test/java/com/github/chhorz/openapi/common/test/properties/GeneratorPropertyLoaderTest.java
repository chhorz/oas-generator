package com.github.chhorz.openapi.common.test.properties;

import com.github.chhorz.openapi.common.properties.GeneratorPropertyLoader;
import com.github.chhorz.openapi.common.properties.ParserProperties;
import com.github.chhorz.openapi.common.test.properties.test.ProcessorAProperties;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
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

		ProcessorAProperties one = parserProperties.getPostProcessor("one", ProcessorAProperties.class);
		assertThat(one)
			.isNotNull()
			.hasFieldOrPropertyWithValue("valueOne", "Test")
			.hasFieldOrPropertyWithValue("valueTwo", 2);
	}

}
