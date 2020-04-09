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
package com.github.chhorz.openapi.spi.asciidoctor.test;

import com.github.chhorz.openapi.common.properties.GeneratorPropertyLoader;
import com.github.chhorz.openapi.spi.asciidoctor.AsciidoctorProperties;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;

class PropertiesTest {

	@Test
	void testPropertyLoading() {
		// given
		GeneratorPropertyLoader generatorPropertyLoader = new GeneratorPropertyLoader(emptyMap());

		// when
		Optional<AsciidoctorProperties> optionalAsciidoctorProperties = generatorPropertyLoader.getParserProperties()
			.getPostProcessor("asciidoctor", AsciidoctorProperties.class);

		// then
		assertThat(optionalAsciidoctorProperties)
			.isNotNull()
			.isPresent();

		AsciidoctorProperties asciidoctorProperties = optionalAsciidoctorProperties.get();

		assertThat(asciidoctorProperties.getPostProcessorProperties())
			.containsOnlyKeys("standaloneFile", "templateLocalizedLookup", "templatePath", "templateFile", "outputPath", "outputFile","logging");

		assertThat(asciidoctorProperties.getStandaloneFile()).isFalse();
		assertThat(asciidoctorProperties.getLocalizedLookup()).isFalse();
		assertThat(asciidoctorProperties.getTemplatePath()).isEqualTo("./templates/freemarker");
		assertThat(asciidoctorProperties.getTemplateFile()).isEqualTo("openapi.ftlh");
		assertThat(asciidoctorProperties.getOutputPath()).isEqualTo("./target/openapi");
		assertThat(asciidoctorProperties.getOutputFile()).isEqualTo("/openapi.adoc");
		assertThat(asciidoctorProperties.getExceptionLogging()).isTrue();
	}

	@Test
	void testEmptyPropertyLoading() {
		// given
		GeneratorPropertyLoader generatorPropertyLoader = new GeneratorPropertyLoader(singletonMap("propertiesPath", "oas-generator-empty.yml"));

		// when
		Optional<AsciidoctorProperties> optionalAsciidoctorProperties = generatorPropertyLoader.getParserProperties()
			.getPostProcessor("asciidoctor", AsciidoctorProperties.class);

		// then
		assertThat(optionalAsciidoctorProperties)
			.isNotNull()
			.isPresent();

		assertThat(optionalAsciidoctorProperties.get().getStandaloneFile())
			.isTrue();
	}

	@Test
	void testNullPropertyLoading() {
		// given
		GeneratorPropertyLoader generatorPropertyLoader = new GeneratorPropertyLoader(singletonMap("propertiesPath", "oas-generator-null.yml"));

		// when
		Optional<AsciidoctorProperties> optionalAsciidoctorProperties = generatorPropertyLoader.getParserProperties()
			.getPostProcessor("asciidoctor", AsciidoctorProperties.class);

		// then
		assertThat(optionalAsciidoctorProperties)
			.isNotNull()
			.isPresent();

		assertThat(optionalAsciidoctorProperties.get().getStandaloneFile())
			.isTrue();
	}

}
