package com.github.chhorz.openapi.spi.asciidoctor.test;

import com.github.chhorz.openapi.common.domain.*;
import com.github.chhorz.openapi.common.properties.ParserProperties;
import com.github.chhorz.openapi.spi.asciidoctor.AsciidoctorPostProcessor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class AsciidoctorPostProcessorTest {

	private AsciidoctorPostProcessor processor;

	@BeforeAll
	static void createFolder() throws IOException {
		Files.createDirectories(Paths.get("target", "generated-test-docs"));
	}

	@Test
	void testMinimalAsciidoctorPostProcessor() {
		// given
		Info info = new Info();
		info.setTitle("Test Service");
		info.setVersion("1.2.3-SNAPSHOT");

		OpenAPI openApi = new OpenAPI();
		openApi.setOpenapi("3.0.1");
		openApi.setInfo(info);

		processor = createAsciidoctorPostProcessor("/minimal");

		// when
		processor.execute(openApi);

		// then
		Path outputPath = Paths.get("target", "generated-test-docs", "minimal", "openapi.adoc");
		Path referencePath = new File("src/test/resources/referenceDocs/minimal.adoc").toPath();

		assertThat(outputPath).exists();
		assertThat(referencePath).exists();

		compareFiles(referencePath, outputPath);
	}

	@Test
	void testFullAsciidoctorPostProcessor() {
		// given
		License license = new License();
		license.setName("Apache License, Version 2.0");
		license.setUrl("http://www.apache.org/licenses/LICENSE-2.0");

		Contact contact = new Contact();
		contact.setName("John doe");
		contact.setEmail("john.doe@test.org");
		contact.setUrl("https://www.google.com");

		Info info = new Info();
		info.setTitle("Test Service");
		info.setVersion("1.2.3-SNAPSHOT");
		info.setDescription("Lorem ipsum");
		info.setTermsOfService("Terms of Service ...");
		info.setLicense(license);
		info.setContact(contact);

		ExternalDocumentation externalDocs = new ExternalDocumentation();
		externalDocs.setUrl("https://en.wikipedia.org");
		externalDocs.setDescription("Lorem ipsum dolor sit amet.");

		Tag tag1 = new Tag();
		tag1.setName("TAG_1");
		tag1.setDescription("This is a description");
		tag1.setExternalDocs(externalDocs);

		Tag tag2 = new Tag();
		tag2.setName("TAG_2");

		OpenAPI openApi = new OpenAPI();
		openApi.setOpenapi("3.0.1");
		openApi.setInfo(info);
		openApi.setExternalDocs(externalDocs);
		openApi.addTag(tag1);
		openApi.addTag(tag2);

		processor = createAsciidoctorPostProcessor("/full");

		// when
		processor.execute(openApi);

		// then
		Path outputPath = Paths.get("target", "generated-test-docs", "full", "openapi.adoc");
		Path referencePath = new File("src/test/resources/referenceDocs/full.adoc").toPath();

		assertThat(outputPath).exists();
		assertThat(referencePath).exists();

		compareFiles(referencePath, outputPath);
	}

	private void compareFiles(Path reference, Path output) {
		try {
			List<String> referenceLines = Files.readAllLines(reference);
			List<String> outputLines = Files.readAllLines(output);

			referenceLines.forEach(line -> {
				assertThat(line.trim()).isEqualTo(outputLines.get(referenceLines.indexOf(line)).trim());
			});
		} catch (IOException e) {
			fail("Either reference or output file has no lines.");
		}
	}

	private AsciidoctorPostProcessor createAsciidoctorPostProcessor(String folder) {
		Map<String, String> propertyMap = new HashMap<>();
		propertyMap.put("asciidoctor.output.dir", "target/generated-test-docs" + folder);

		ParserProperties parserProperties = new ParserProperties();
		parserProperties.setPostProcessor(propertyMap);

		return new AsciidoctorPostProcessor(parserProperties);
	}

}
