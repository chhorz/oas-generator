package com.github.chhorz.openapi.schema.test.github;

import com.github.chhorz.openapi.common.domain.Components;
import com.github.chhorz.openapi.common.domain.Schema;
import com.github.chhorz.openapi.common.test.AbstractProcessorTest;
import com.github.chhorz.openapi.common.test.github.GitHubIssue;
import com.github.chhorz.openapi.schema.SchemaOpenApiProcessor;
import com.github.chhorz.openapi.schema.test.github.resources.JsonPropertyTest;
import com.jayway.jsonpath.DocumentContext;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GitHubIssuesTest extends AbstractProcessorTest {

	@Test
	@GitHubIssue("#166")
	void testJsonPropertyAnnotation() {
		// run annotation processor
		testCompilation(new SchemaOpenApiProcessor(), createConfigFileOption("oas-generator-github.yml"), JsonPropertyTest.class);

		// create json-path context
		DocumentContext documentContext = createJsonPathDocumentContext("target/openapi/openapi-schema.json");

		// assertions
		Components components = documentContext.read("$.components", Components.class);

		assertThat(components)
			.isNotNull();

		assertThat(components.getSchemas())
			.isNotNull()
			.hasSize(1)
			.containsKey("JsonPropertyTest");
		assertThat(components.getSchemas().get("JsonPropertyTest").getProperties())
			.hasSize(1)
			.containsKey("jsonProperty")
			.extractingByKey("jsonProperty")
			.hasFieldOrPropertyWithValue("deprecated", false)
			.hasFieldOrPropertyWithValue("type", Schema.Type.STRING)
			.hasFieldOrPropertyWithValue("description", "");
	}

}
