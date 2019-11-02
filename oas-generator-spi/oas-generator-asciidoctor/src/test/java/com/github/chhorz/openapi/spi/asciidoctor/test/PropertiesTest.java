package com.github.chhorz.openapi.spi.asciidoctor.test;

import com.github.chhorz.openapi.common.properties.GeneratorPropertyLoader;
import com.github.chhorz.openapi.spi.asciidoctor.AsciidoctorProperties;
import org.junit.jupiter.api.Test;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;

class PropertiesTest {

	@Test
	void testPropertyLoading() {
		// given
		GeneratorPropertyLoader generatorPropertyLoader = new GeneratorPropertyLoader(emptyMap());

		// when

		AsciidoctorProperties asciidoctorProperties = generatorPropertyLoader.getParserProperties()
			.getPostProcessor("asciidoctor", AsciidoctorProperties.class);

		// then
		assertThat(asciidoctorProperties)
			.isNotNull();

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
