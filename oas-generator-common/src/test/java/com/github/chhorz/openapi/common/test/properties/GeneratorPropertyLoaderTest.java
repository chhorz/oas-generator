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
package com.github.chhorz.openapi.common.test.properties;

import com.github.chhorz.openapi.common.domain.ExternalDocumentation;
import com.github.chhorz.openapi.common.properties.GeneratorPropertyLoader;
import com.github.chhorz.openapi.common.properties.ParserProperties;
import com.github.chhorz.openapi.common.test.github.GithubIssue;
import com.github.chhorz.openapi.common.test.properties.test.ProcessorAProperties;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

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

		Optional<ProcessorAProperties> optionalProcessorAProperties = parserProperties.getPostProcessor("one", ProcessorAProperties.class);

		assertThat(optionalProcessorAProperties)
			.isNotNull()
			.isPresent()
			.get()
			.hasFieldOrPropertyWithValue("valueTwo", 0);
	}

	@Test
	void testTagWithExternalDocumentation(){
		// given
		String tagName = "tag_a";

		Map<String, String> processorOptions = singletonMap("propertiesPath", "./properties/tagTest.yml");
		GeneratorPropertyLoader generatorPropertyLoader = new GeneratorPropertyLoader(processorOptions);

		// when
		String tagDescription = generatorPropertyLoader.getDescriptionForTag(tagName);
		ExternalDocumentation externalDocumentationForTag = generatorPropertyLoader.getExternalDocumentationForTag(tagName);

		// then
		assertThat(tagDescription)
			.isNotNull()
			.isEqualTo("Lorem ipsum");

		assertThat(externalDocumentationForTag)
			.isNotNull()
			.hasFieldOrPropertyWithValue("url", "https://www.google.com")
			.hasAllNullFieldsOrPropertiesExcept("url");
	}

	@GithubIssue("#9")
	@Test
	void testTagWithoutExternalDocumentation(){
		// given
		String tagName = "tag_b";

		Map<String, String> processorOptions = singletonMap("propertiesPath", "./properties/tagTest.yml");
		GeneratorPropertyLoader generatorPropertyLoader = new GeneratorPropertyLoader(processorOptions);

		// when
		String tagDescription = generatorPropertyLoader.getDescriptionForTag(tagName);
		ExternalDocumentation externalDocumentationForTag = generatorPropertyLoader.getExternalDocumentationForTag(tagName);

		// then
		assertThat(tagDescription)
			.isNotNull()
			.isEqualTo("Lorem ipsum");

		assertThat(externalDocumentationForTag)
			.isNotNull()
			.hasAllNullFieldsOrProperties();
	}

}
