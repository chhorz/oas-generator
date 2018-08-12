package com.github.chhorz.openapi.spi.asciidoctor.test;

import com.github.chhorz.openapi.common.domain.Contact;
import com.github.chhorz.openapi.common.domain.Info;
import com.github.chhorz.openapi.common.domain.License;
import com.github.chhorz.openapi.common.domain.OpenAPI;
import com.github.chhorz.openapi.common.properties.ParserProperties;
import com.github.chhorz.openapi.spi.asciidoctor.AsciidoctorPostProcessor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class AsciidoctorPostProcessorTest {

	private AsciidoctorPostProcessor processor;

	@BeforeAll
	static void createFolder() throws IOException {
		Files.createDirectories(Paths.get("target","generated-test-docs"));
	}

	@Test
	void testMinimalAsciidoctorPostProcessor(){
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
		assertThat(Paths.get("generated-test-docs", "minimal", "openapi.adoc")).exists();
	}

	@Test
	void testFullAsciidoctorPostProcessor(){
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

		OpenAPI openApi = new OpenAPI();
		openApi.setOpenapi("3.0.1");
		openApi.setInfo(info);

		processor = createAsciidoctorPostProcessor("/full");

		// when
		processor.execute(openApi);

		// then
		assertThat(Paths.get("generated-test-docs", "full", "openapi.adoc")).exists();
	}

	AsciidoctorPostProcessor createAsciidoctorPostProcessor(String folder){
		Map<String, String> propertyMap = new HashMap<>();
		propertyMap.put("asciidoctor.output.dir", "target/generated-test-docs" + folder);

		ParserProperties parserProperties = new ParserProperties();
		parserProperties.setPostProcessor(propertyMap);

		return new AsciidoctorPostProcessor(parserProperties);
	}

}
