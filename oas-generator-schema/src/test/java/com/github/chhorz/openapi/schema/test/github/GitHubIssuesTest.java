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
import static org.assertj.core.api.Assertions.tuple;

class GitHubIssuesTest extends AbstractProcessorTest {

	@Test
	@GitHubIssue("#166")
	@GitHubIssue("#171")
	void githubResourcesTest() {
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
			.hasSize(2)
			.containsOnlyKeys("JsonPropertyTest", "ExtendedResource");
		assertThat(components.getSchemas().get("JsonPropertyTest").getProperties())
			.hasSize(1)
			.containsKey("jsonProperty")
			.extractingByKey("jsonProperty")
			.hasFieldOrPropertyWithValue("deprecated", false)
			.hasFieldOrPropertyWithValue("type", Schema.Type.STRING)
			.hasFieldOrPropertyWithValue("description", "");

		assertThat(components.getSchemas().get("ExtendedResource").getProperties())
			.hasSize(3)
			.containsKeys("id", "content", "additionalContent");
		assertThat(components.getSchemas().get("ExtendedResource").getProperties().values())
			.extracting("deprecated", "type", "format", "description")
			.contains(tuple(false, Schema.Type.STRING, null, ""),
				tuple(false, Schema.Type.INTEGER, Schema.Format.INT64, ""));
	}

}
