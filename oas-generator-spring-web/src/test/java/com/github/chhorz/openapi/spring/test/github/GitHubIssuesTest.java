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

import com.github.chhorz.openapi.common.domain.*;
import com.github.chhorz.openapi.common.test.AbstractProcessorTest;
import com.github.chhorz.openapi.common.test.github.GithubIssue;
import com.github.chhorz.openapi.spring.SpringWebOpenApiProcessor;
import com.github.chhorz.openapi.spring.test.github.controller.*;
import com.github.chhorz.openapi.spring.test.github.resources.HateoasResource;
import com.github.chhorz.openapi.spring.test.github.resources.Resource;
import com.jayway.jsonpath.DocumentContext;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.github.chhorz.openapi.spring.test.github.GitHubIssuesTestAssertions.*;
import static java.util.Collections.emptyList;
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
		DocumentContext documentContext = createJsonPathDocumentContext();

		// assertions
		assertThat(documentContext.read("$.openapi", String.class))
			.isNotNull()
			.isEqualTo("3.0.3");

		validateDefaultInfoObject(documentContext);

		Operation operation = documentContext.read("$.paths./github/issues.get", Operation.class);
		assertThat(operation)
			.isNotNull()
			.hasFieldOrPropertyWithValue("summary", "")
			.hasFieldOrPropertyWithValue("description", "")
			.hasFieldOrPropertyWithValue("operationId", "GitHubIssue002#test")
			.hasFieldOrPropertyWithValue("deprecated", false)
			.hasFieldOrPropertyWithValue("security", emptyList())
			.hasFieldOrPropertyWithValue("parameterObjects", emptyList());
		assertThat(operation.getResponses())
			.isNotNull()
			.hasSize(1)
			.containsOnlyKeys("default")
			.extractingByKey("default")
			.isNotNull()
			.hasFieldOrPropertyWithValue("description", "");
		assertThat(operation.getResponses().get("default").getContent())
			.isNotNull()
			.hasSize(1)
			.containsOnlyKeys("*/*")
			.extractingByKey("*/*")
			.isInstanceOfSatisfying(MediaType.class, mediaType -> assertThat(mediaType)
				.isNotNull());
//				.extracting(MediaType::getSchema)
//				.isInstanceOfSatisfying(Reference.class, refCheck("#/components/schemas/ResponseEntity")));

		validateSchemaForResponseEntity(documentContext);
	}

	@Test
	@GithubIssue("#8")
	void getGithubIssue008() {
		// run annotation processor
		testCompilation(new SpringWebOpenApiProcessor(), createConfigMap("oas-generator-withoutparser.yml"), GitHubIssue008.class, Resource.class);

		// create json-path context
		DocumentContext documentContext = createJsonPathDocumentContext();

		// assertions // TODO
		assertThat(documentContext.read("$.openapi", String.class))
			.isNotNull()
			.isEqualTo("3.0.3");

		Info info = documentContext.read("$.info", Info.class);

		assertThat(info)
			.isNotNull()
			.hasFieldOrPropertyWithValue("title", "MyService")
			.hasFieldOrPropertyWithValue("version", "1.2.3-SNAPSHOT")
			.hasFieldOrPropertyWithValue("xGeneratedBy", "oas-generator");

		assertThat(info.getxGeneratedTs())
			.isNotNull()
			.isNotEmpty();

		Operation operation = documentContext.read("$.paths./github/issues.get", Operation.class);

		assertThat(operation)
			.isNotNull();

		assertThat(operation.getSecurity())
			.isNotNull()
			.isNotEmpty()
			.hasSize(1);

		assertThat(operation.getSecurity().get(0))
			.isNotNull()
			.containsKeys("read_role");

		validateSchemaForTestResource(documentContext);

		SecuritySchemeHttp securityScheme = documentContext.read("$.components.securitySchemes.read_role", SecuritySchemeHttp.class);

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
		testCompilation(new SpringWebOpenApiProcessor(), GitHubIssue011.class, HateoasResource.class);

		// create json-path context
		DocumentContext documentContext = createJsonPathDocumentContext();

		// assertions
		assertThat(documentContext.read("$.openapi", String.class))
			.isNotNull()
			.isEqualTo("3.0.3");

		validateDefaultInfoObject(documentContext);

		Operation operation = documentContext.read("$.paths./github/issues.get", Operation.class);
		assertThat(operation)
			.isNotNull()
			.hasFieldOrPropertyWithValue("summary", "")
			.hasFieldOrPropertyWithValue("description", "")
			.hasFieldOrPropertyWithValue("operationId", "GitHubIssue011#test")
			.hasFieldOrPropertyWithValue("deprecated", false)
			.hasFieldOrPropertyWithValue("security", emptyList())
			.hasFieldOrPropertyWithValue("parameterObjects", emptyList());
		assertThat(operation.getResponses())
			.isNotNull()
			.hasSize(1)
			.containsOnlyKeys("default")
			.extractingByKey("default")
			.isNotNull()
			.hasFieldOrPropertyWithValue("description", "");
		assertThat(operation.getResponses().get("default").getContent())
			.isNotNull()
			.hasSize(1)
			.containsOnlyKeys("application/json")
			.extractingByKey("application/json")
			.isInstanceOfSatisfying(MediaType.class, mediaType -> assertThat(mediaType)
				.isNotNull()
				.extracting(MediaType::getSchema)
				.isInstanceOfSatisfying(Map.class, map -> {
					assertThat(map)
						.isNotNull()
						.containsOnlyKeys("deprecated", "items", "type");
//						.containsEntry("deprecated", false)
//						.containsEntry("type", "array")
//						.extractingByKey("items")
//						.isInstanceOfSatisfying(Reference.class, refCheck("#/components/schemas/ResponseEntity"));
				}));

		validateSchemaForHateoasTestResource(documentContext);
	}

	@Test
	@GithubIssue("#12")
	void getGithubIssue012() {
		// run annotation processor
		testCompilation(new SpringWebOpenApiProcessor(), GitHubIssue012.class, Resource.class);

		// create json-path context
		DocumentContext documentContext = createJsonPathDocumentContext();

		// assertions
		assertThat(documentContext.read("$.openapi", String.class))
			.isNotNull()
			.isEqualTo("3.0.3");

		validateDefaultInfoObject(documentContext);

		Components components = documentContext.read("$.components", Components.class);

		assertThat(components.getSchemas())
			.containsOnlyKeys("Resource");

		Schema schema = documentContext.read("$.components.schemas.Resource", Schema.class);

		assertThat(schema)
			.isNotNull()
			.hasFieldOrPropertyWithValue("type", Schema.Type.OBJECT);

		assertThat(components.getRequestBodies())
			.containsOnlyKeys("Resource");

		RequestBody requestBody = documentContext.read("$.components.requestBodies.Resource", RequestBody.class);

		assertThat(requestBody)
			.hasFieldOrPropertyWithValue("description", null)
			.hasFieldOrPropertyWithValue("required", true);

		Schema requestBodySchema = documentContext.read("$.components.requestBodies.Resource.content.application/json.schema", Schema.class);

		assertThat(requestBodySchema)
			.isNotNull()
			.hasFieldOrPropertyWithValue("deprecated", false)
			.hasFieldOrPropertyWithValue("type", Schema.Type.ARRAY);

		String testObjectReference = documentContext.read("$.components.requestBodies.Resource.content.application/json.schema.items.$ref", String.class);

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
		DocumentContext documentContext = createJsonPathDocumentContext();

		// assertions
		assertThat(documentContext.read("$.openapi", String.class))
			.isNotNull()
			.isEqualTo("3.0.3");

		validateDefaultInfoObject(documentContext);

		Parameter[] parameters = documentContext.read("$.paths./github/issues.get.parameters", Parameter[].class);

		assertThat(parameters)
			.isNotNull()
			.hasSize(1);

		assertThat(parameters[0])
			.hasFieldOrPropertyWithValue("in", Parameter.In.QUERY)
			.hasFieldOrPropertyWithValue("name", "filter")
			.hasFieldOrPropertyWithValue("required", false);

		Components components = documentContext.read("$.components", Components.class);

		assertThat(components.getSchemas())
			.containsOnlyKeys("Resource");

		validateSchemaForTestResource(documentContext);
	}

	@Test
	@GithubIssue("#18")
	void getGithubIssue018() {
		// run annotation processor
		testCompilation(new SpringWebOpenApiProcessor(), GitHubIssue018.class, Resource.class);

		// create json-path context
		DocumentContext documentContext = createJsonPathDocumentContext();

		// assertions
		assertThat(documentContext.read("$.openapi", String.class))
			.isNotNull()
			.isEqualTo("3.0.3");

		validateDefaultInfoObject(documentContext);

		Operation operation = documentContext.read("$.paths./github/issues.get", Operation.class);
		assertThat(operation)
			.isNotNull()
			.hasFieldOrPropertyWithValue("summary", "Test method A.\n\nTest method B.")
			.hasFieldOrPropertyWithValue("description", "Test method A.\n\nTest method B.")
			.hasFieldOrPropertyWithValue("operationId", "GitHubIssue018#testParamA")
			.hasFieldOrPropertyWithValue("deprecated", false)
			.hasFieldOrPropertyWithValue("security", emptyList());
		assertThat(operation.getResponses())
			.isNotNull()
			.hasSize(1)
			.containsOnlyKeys("default")
			.extractingByKey("default")
			.isNotNull()
			.hasFieldOrPropertyWithValue("description", "a list of resources");
		assertThat(operation.getResponses().get("default").getContent())
			.isNotNull()
			.hasSize(1)
			.containsOnlyKeys("*/*")
			.extractingByKey("*/*")
			.isInstanceOfSatisfying(MediaType.class, mediaType -> assertThat(mediaType)
				.isNotNull());
//				.extracting(MediaType::getSchema)
//				.isInstanceOfSatisfying(Reference.class, refCheck("#/components/schemas/ResponseEntity")));
		assertThat(operation.getParameterObjects())
			.isNotNull()
			.hasSize(2);
		assertThat(operation.getParameterObjects().get(0))
			.isNotNull()
			.hasFieldOrPropertyWithValue("name", "a")
			.hasFieldOrPropertyWithValue("in", Parameter.In.QUERY)
			.hasFieldOrPropertyWithValue("description", "parameter a for method a")
			.hasFieldOrPropertyWithValue("required", true)
			.hasFieldOrPropertyWithValue("deprecated", false)
			.hasFieldOrPropertyWithValue("allowEmptyValue", false)
			.extracting(Parameter::getSchema)
			.isInstanceOfSatisfying(Schema.class, schema -> assertThat(schema)
				.isNotNull()
				.hasFieldOrPropertyWithValue("deprecated", false)
				.hasFieldOrPropertyWithValue("type", Schema.Type.STRING)
				.hasFieldOrPropertyWithValue("description", ""));

		assertThat(operation.getParameterObjects().get(1))
			.isNotNull()
			.hasFieldOrPropertyWithValue("name", "b")
			.hasFieldOrPropertyWithValue("in", Parameter.In.QUERY)
			.hasFieldOrPropertyWithValue("description", "parameter b for method b")
			.hasFieldOrPropertyWithValue("required", true)
			.hasFieldOrPropertyWithValue("deprecated", false)
			.hasFieldOrPropertyWithValue("allowEmptyValue", false)
			.extracting(Parameter::getSchema)
			.isInstanceOfSatisfying(Schema.class, schema -> assertThat(schema)
				.isNotNull()
				.hasFieldOrPropertyWithValue("deprecated", false)
				.hasFieldOrPropertyWithValue("type", Schema.Type.STRING)
				.hasFieldOrPropertyWithValue("description", ""));

		validateSchemaForTestResource(documentContext);
	}

	@Test
	@GithubIssue("#20")
	void getGithubIssue020() {
		// run annotation processor
		testCompilation(new SpringWebOpenApiProcessor(), GitHubIssue020.class, Resource.class);

		// create json-path context
		DocumentContext documentContext = createJsonPathDocumentContext();

		// assertions
		assertThat(documentContext.read("$.openapi", String.class))
			.isNotNull()
			.isEqualTo("3.0.3");

		validateDefaultInfoObject(documentContext);

		Components components = documentContext.read("$.components", Components.class);

		assertThat(components.getSchemas())
			.containsOnlyKeys("Resource");

		validateSchemaForTestResource(documentContext);
		validateRequestBodyForTestResource(documentContext);
	}

	@Test
	@GithubIssue("#22")
	void getGithubIssue022() {
		// run annotation processor
		testCompilation(new SpringWebOpenApiProcessor(), GitHubIssue022.class, Resource.class);

		// create json-path context
		DocumentContext documentContext = createJsonPathDocumentContext();

		// assertions
		assertThat(documentContext.read("$.openapi", String.class))
			.isNotNull()
			.isEqualTo("3.0.3");

		validateDefaultInfoObject(documentContext);

		PathItemObject pathItemObject = documentContext.read("$.paths./github/issues/{id}", PathItemObject.class);

		assertThat(pathItemObject)
			.isNotNull()
			.hasAllNullFieldsOrPropertiesExcept("get");

		Components components = documentContext.read("$.components", Components.class);

		assertThat(components.getSchemas())
			.containsOnlyKeys("Resource");

		validateSchemaForTestResource(documentContext);
	}

}
