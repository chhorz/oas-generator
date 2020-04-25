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
package com.github.chhorz.openapi.spring.test.github;

import com.github.chhorz.openapi.common.OpenAPIConstants;
import com.github.chhorz.openapi.common.domain.*;
import com.github.chhorz.openapi.common.test.AbstractProcessorTest;
import com.github.chhorz.openapi.common.test.github.GithubIssue;
import com.github.chhorz.openapi.spring.SpringWebOpenApiProcessor;
import com.github.chhorz.openapi.spring.test.github.controller.*;
import com.github.chhorz.openapi.spring.test.github.resources.Resource;
import com.jayway.jsonpath.DocumentContext;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author chhorz
 */
class GitHubIssuesTest extends AbstractProcessorTest {

	@Test
	@GithubIssue("#2")
	void getGithubIssue002() {
		// run annotation processor
		testCompilation(new SpringWebOpenApiProcessor(), GitHubIssue002.class);

		// create json-path context
		DocumentContext ctx = createJsonPathDocumentContext();

		// assertions
		String openApiVersion = ctx.read("$.openapi", String.class);

		assertThat(openApiVersion)
			.isNotNull()
			.isEqualTo("3.0.3");

		validateDefaultInfoObject(ctx);

		Response response = ctx.read("$.paths./github/issue.get.responses.default", Response.class);

		assertThat(response.getContent())
			.isNotNull()
			.containsOnlyKeys("*/*");

		assertThat(response.getContent().get("*/*").getSchema())
			.asInstanceOf(InstanceOfAssertFactories.MAP)
			.containsOnlyKeys("$ref")
			.extractingByKey("$ref")
			.isEqualTo("#/components/schemas/ResponseEntity");

		Schema schema = ctx.read("$.components.schemas.T", Schema.class);

		assertThat(schema)
			.isNotNull()
			.hasFieldOrPropertyWithValue("type", Schema.Type.OBJECT)
			.hasFieldOrPropertyWithValue("deprecated", false)
			.hasAllNullFieldsOrPropertiesExcept("type", "deprecated");
	}

	@Test
	@GithubIssue("#8")
	void getGithubIssue008() {
		// run annotation processor
		testCompilation(new SpringWebOpenApiProcessor(), createConfigMap("oas-generator-withoutparser.yml"), GitHubIssue008.class);

		// create json-path context
		DocumentContext ctx = createJsonPathDocumentContext();

		// assertions
		String openApiVersion = ctx.read("$.openapi", String.class);

		assertThat(openApiVersion)
			.isNotNull()
			.isEqualTo("3.0.3");

		Info info = ctx.read("$.info", Info.class);

		assertThat(info)
			.isNotNull()
			.hasFieldOrPropertyWithValue("title", "MyService")
			.hasFieldOrPropertyWithValue("version", "1.2.3-SNAPSHOT")
			.hasFieldOrPropertyWithValue("xGeneratedBy", "oas-generator");

		assertThat(info.getxGeneratedTs())
			.isNotNull()
			.isNotEmpty();

		Operation operation = ctx.read("$.paths./github/issue.get", Operation.class);

		assertThat(operation)
			.isNotNull();

		assertThat(operation.getSecurity())
			.isNotNull()
			.isNotEmpty()
			.hasSize(1);

		assertThat(operation.getSecurity().get(0))
			.isNotNull()
			.containsKeys("read_role");

		Schema schema = ctx.read("$.components.schemas.Test", Schema.class);

		assertThat(schema)
			.isNotNull()
			.hasFieldOrPropertyWithValue("type", Schema.Type.OBJECT);

		SecuritySchemeHttp securityScheme = ctx.read("$.components.securitySchemes.read_role", SecuritySchemeHttp.class);

		assertThat(securityScheme)
			.isNotNull()
			.hasFieldOrPropertyWithValue("type", SecurityScheme.Type.http)
			.hasFieldOrPropertyWithValue("description", "Basic LDAP read role.")
			.hasFieldOrPropertyWithValue("scheme", "basic");
	}

	@Test
	@GithubIssue("#11")
	void getGithubIssue011() {
		// run annotation processor
		testCompilation(new SpringWebOpenApiProcessor(), GitHubIssue011.class);

		// create json-path context
		DocumentContext ctx = createJsonPathDocumentContext();

		// assertions
		String openApiVersion = ctx.read("$.openapi", String.class);

		assertThat(openApiVersion)
			.isNotNull()
			.isEqualTo("3.0.3");

		validateDefaultInfoObject(ctx);

		Components components = ctx.read("$.components", Components.class);

		assertThat(components.getSchemas())
			.containsOnlyKeys("Test", "Link", "LinkRelation");

		Schema schema = ctx.read("$.components.schemas.Test", Schema.class);

		assertThat(schema)
			.isNotNull()
			.hasFieldOrPropertyWithValue("type", Schema.Type.OBJECT);
	}

	@Test
	@GithubIssue("#12")
	void getGithubIssue012() {
		// run annotation processor
		testCompilation(new SpringWebOpenApiProcessor(), GitHubIssue012.class, Resource.class);

		// create json-path context
		DocumentContext ctx = createJsonPathDocumentContext();

		// assertions
		String openApiVersion = ctx.read("$.openapi", String.class);

		assertThat(openApiVersion)
			.isNotNull()
			.isEqualTo("3.0.3");

		validateDefaultInfoObject(ctx);

		Components components = ctx.read("$.components", Components.class);

		assertThat(components.getSchemas())
			.containsOnlyKeys("Resource");

		Schema schema = ctx.read("$.components.schemas.Resource", Schema.class);

		assertThat(schema)
			.isNotNull()
			.hasFieldOrPropertyWithValue("type", Schema.Type.OBJECT);

		assertThat(components.getRequestBodies())
			.containsOnlyKeys("Resource");

		RequestBody requestBody = ctx.read("$.components.requestBodies.Resource", RequestBody.class);

		assertThat(requestBody)
			.hasFieldOrPropertyWithValue("description", null)
			.hasFieldOrPropertyWithValue("required", true);

		Schema requestBodySchema = ctx.read("$.components.requestBodies.Resource.content.application/json.schema", Schema.class);

		assertThat(requestBodySchema)
			.isNotNull()
			.hasFieldOrPropertyWithValue("deprecated", false)
			.hasFieldOrPropertyWithValue("type", Schema.Type.ARRAY);

		String testObjectReference = ctx.read("$.components.requestBodies.Resource.content.application/json.schema.items.$ref", String.class);

		assertThat(testObjectReference)
			.isNotNull()
			.isEqualTo("#/components/schemas/Resource");
	}

	@Test
	@GithubIssue("#15")
	void getGithubIssue015() {
		// run annotation processor
		testCompilation(new SpringWebOpenApiProcessor(), GitHubIssue015.class, Resource.class);

		// create json-path context
		DocumentContext ctx = createJsonPathDocumentContext();

		// assertions
		String openApiVersion = ctx.read("$.openapi", String.class);

		assertThat(openApiVersion)
			.isNotNull()
			.isEqualTo("3.0.3");

		validateDefaultInfoObject(ctx);

		Parameter[] parameters = ctx.read("$.paths./github/issue.get.parameters", Parameter[].class);

		assertThat(parameters)
			.isNotNull()
			.hasSize(1);

		assertThat(parameters[0])
			.hasFieldOrPropertyWithValue("in", Parameter.In.QUERY)
			.hasFieldOrPropertyWithValue("name", "filter")
			.hasFieldOrPropertyWithValue("required", false);

		Components components = ctx.read("$.components", Components.class);

		assertThat(components.getSchemas())
			.containsOnlyKeys("Resource");

		validateSchemaForTestResource(ctx);
	}

	@Test
	@GithubIssue("#20")
	void getGithubIssue020() {
		// run annotation processor
		testCompilation(new SpringWebOpenApiProcessor(), GitHubIssue020.class, Resource.class);

		// create json-path context
		DocumentContext ctx = createJsonPathDocumentContext();

		// assertions
		String openApiVersion = ctx.read("$.openapi", String.class);

		assertThat(openApiVersion)
			.isNotNull()
			.isEqualTo("3.0.3");

		validateDefaultInfoObject(ctx);

		Components components = ctx.read("$.components", Components.class);

		assertThat(components.getSchemas())
			.containsOnlyKeys("Resource");

		validateSchemaForTestResource(ctx);
		validateRequestBodyForTestResource(ctx);
	}

	private Map<String, String> createConfigMap(String configFile) {
		return singletonMap(OpenAPIConstants.OPTION_PROPERTIES_PATH, configFile);
	}

	/**
	 * Validates the default info object.
	 *
	 * @param documentContext the json document context
	 */
	private void validateDefaultInfoObject(final DocumentContext documentContext){
		Info info = documentContext.read("$.info", Info.class);

		assertThat(info)
			.isNotNull()
			.hasFieldOrPropertyWithValue("title", "Title")
			.hasFieldOrPropertyWithValue("version", "Version")
			.hasFieldOrPropertyWithValue("xGeneratedBy", "oas-generator");

		assertThat(info.getxGeneratedTs())
			.isNotNull()
			.isNotEmpty();
	}

	/**
	 * Validates the given test resource.
	 *
	 * @param documentContext the json document context
	 *
	 * @see Resource
	 */
	private void validateSchemaForTestResource(final DocumentContext documentContext) {
		Schema schema = documentContext.read("$.components.schemas.Resource", Schema.class);

		assertThat(schema)
			.isNotNull()
			.hasFieldOrPropertyWithValue("deprecated", false)
			.hasFieldOrPropertyWithValue("description","a test resource for GitHub issue tests")
			.hasFieldOrPropertyWithValue("type", Schema.Type.OBJECT);

		assertThat(schema.getProperties())
			.isNotNull()
			.hasSize(1)
			.containsOnlyKeys("value");

		assertThat(schema.getProperties().get("value"))
			.isNotNull()
			.hasFieldOrPropertyWithValue("deprecated", false)
			.hasFieldOrPropertyWithValue("type", Schema.Type.STRING)
			.hasFieldOrPropertyWithValue("description","some test value");
	}

	/**
	 * Validates the given test resource.
	 *
	 * @param documentContext the json document context
	 *
	 * @see Resource
	 */
	private void validateRequestBodyForTestResource(final DocumentContext documentContext) {
		RequestBody requestBody = documentContext.read("$.components.requestBodies.Resource", RequestBody.class);

		assertThat(requestBody)
			.isNotNull()
			.hasFieldOrPropertyWithValue("required", true)
			.hasFieldOrPropertyWithValue("description","the request body");

		assertThat(requestBody.getContent())
			.isNotNull()
			.hasSize(1)
			.containsOnlyKeys("application/json");

		assertThat(requestBody.getContent().get("application/json"))
			.isNotNull();

		String resourceReference = documentContext.read("$.components.requestBodies.Resource.content.application/json.schema.$ref", String.class);

		assertThat(resourceReference)
			.isNotNull()
			.isEqualTo("#/components/schemas/Resource");
	}

}
