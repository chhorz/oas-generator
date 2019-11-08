package com.github.chhorz.openapi.spi.asciidoctor.test;

import com.github.chhorz.openapi.common.properties.GeneratorPropertyLoader;
import com.github.chhorz.openapi.spi.asciidoctor.AsciidoctorProperties;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static java.util.Collections.emptyMap;
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

		assertThat(asciidoctorProperties.getStandaloneFile()).isTrue();
		assertThat(asciidoctorProperties.getLocalizedLookup()).isFalse();
		assertThat(asciidoctorProperties.getTemplatePath()).isEqualTo("./templates/freemarker");
		assertThat(asciidoctorProperties.getTemplateFile()).isEqualTo("openapi.ftlh");
		assertThat(asciidoctorProperties.getOutputPath()).isEqualTo("./target/openapi");
		assertThat(asciidoctorProperties.getOutputFile()).isEqualTo("/openapi.adoc");
		assertThat(asciidoctorProperties.getExceptionLogging()).isTrue();
	}

}
