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
import com.github.chhorz.openapi.common.test.github.GitHubIssue;
import com.github.chhorz.openapi.spring.SpringWebOpenApiProcessor;
import com.github.chhorz.openapi.spring.test.github.controller.*;
import com.github.chhorz.openapi.spring.test.github.resources.ErrorResource;
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
	@GitHubIssue("#2")
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
	@GitHubIssue("#8")
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
		validateSecurityScheme(documentContext);
	}

	@Test
	@GitHubIssue("#11")
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
	@GitHubIssue("#12")
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
	@GitHubIssue("#15")
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
	@GitHubIssue("#18")
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
	@GitHubIssue("#20")
	@GitHubIssue("#25")
	void getGithubIssue020() {
		// run annotation processor
		testCompilation(new SpringWebOpenApiProcessor(), GitHubIssue020.class, Resource.class, ErrorResource.class);

		// create json-path context
		DocumentContext documentContext = createJsonPathDocumentContext();

		// assertions
		assertThat(documentContext.read("$.openapi", String.class))
			.isNotNull()
			.isEqualTo("3.0.3");

		validateDefaultInfoObject(documentContext);

		Operation operationOne = documentContext.read("$.paths./github/issues.post", Operation.class);
		assertThat(operationOne)
			.isNotNull()
			.hasFieldOrPropertyWithValue("summary", "Lorem ipsum")
			.hasFieldOrPropertyWithValue("description", "Lorem ipsum")
			.hasFieldOrPropertyWithValue("operationId", "GitHubIssue020#test")
			.hasFieldOrPropertyWithValue("deprecated", false)
			.hasFieldOrPropertyWithValue("security", emptyList());
		assertThat(operationOne.getResponses())
			.isNotNull()
			.hasSize(2)
			.containsOnlyKeys("default", "204");
		assertThat(operationOne.getResponses().get("default").getContent())
			.isNotNull()
			.hasSize(1)
			.containsOnlyKeys("*/*")
			.extractingByKey("*/*")
			.isInstanceOfSatisfying(MediaType.class, mediaType -> assertThat(mediaType)
				.isNotNull());
		assertThat(operationOne.getResponses().get("204").getContent())
			.isNull();
		Operation operationTwo = documentContext.read("$.paths./github/issues/{id}.delete", Operation.class);
		assertThat(operationTwo)
			.isNotNull()
			.hasFieldOrPropertyWithValue("summary", "Lorem ipsum")
			.hasFieldOrPropertyWithValue("description", "Lorem ipsum")
			.hasFieldOrPropertyWithValue("operationId", "GitHubIssue020#post")
			.hasFieldOrPropertyWithValue("deprecated", false)
			.hasFieldOrPropertyWithValue("security", emptyList());
		assertThat(operationOne.getResponses())
			.isNotNull()
			.hasSize(2)
			.containsOnlyKeys("default", "204");
		assertThat(operationOne.getResponses().get("default").getContent())
			.isNotNull()
			.hasSize(1)
			.containsOnlyKeys("*/*")
			.extractingByKey("*/*")
			.isInstanceOfSatisfying(MediaType.class, mediaType -> assertThat(mediaType)
				.isNotNull());
		assertThat(operationOne.getResponses().get("204").getContent())
			.isNull();

		Components components = documentContext.read("$.components", Components.class);

		assertThat(components.getSchemas())
			.containsOnlyKeys("Resource", "ErrorResource");

		validateSchemaForTestResource(documentContext);
		validateSchemaForErrorResource(documentContext);
		validateRequestBodyForTestResource(documentContext);
	}

	@Test
	@GitHubIssue("#22")
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

	@Test
	@GitHubIssue("#23")
	void getGithubIssue023() {
		// run annotation processor
		testCompilation(new SpringWebOpenApiProcessor(), GitHubIssue023.class, Resource.class);

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
			.hasFieldOrPropertyWithValue("operationId", "GitHubIssue023#testParamA")
			.hasFieldOrPropertyWithValue("deprecated", false)
			.hasFieldOrPropertyWithValue("security", emptyList());
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
		assertThat(operation.getParameterObjects())
			.isNotNull()
			.hasSize(2);
		assertThat(operation.getParameterObjects().get(0))
			.isNotNull()
			.hasFieldOrPropertyWithValue("name", "a")
			.hasFieldOrPropertyWithValue("in", Parameter.In.QUERY)
			.hasFieldOrPropertyWithValue("description", "")
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
			.hasFieldOrPropertyWithValue("description", "")
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
	@GitHubIssue("#23")
	void getGithubIssue023_2() {
		// run annotation processor
		testCompilation(new SpringWebOpenApiProcessor(), GitHubIssue023.class, GitHubIssue023_2.class, Resource.class);

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
			.hasFieldOrPropertyWithValue("summary", "Test method C.")
			.hasFieldOrPropertyWithValue("description", "Test method C.")
			.hasFieldOrPropertyWithValue("operationId", "GitHubIssue023#testParamA")
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
			.hasSize(3);
		assertThat(operation.getParameterObjects().get(0))
			.isNotNull()
			.hasFieldOrPropertyWithValue("name", "a")
			.hasFieldOrPropertyWithValue("in", Parameter.In.QUERY)
			.hasFieldOrPropertyWithValue("description", "")
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
			.hasFieldOrPropertyWithValue("description", "")
			.hasFieldOrPropertyWithValue("required", true)
			.hasFieldOrPropertyWithValue("deprecated", false)
			.hasFieldOrPropertyWithValue("allowEmptyValue", false)
			.extracting(Parameter::getSchema)
			.isInstanceOfSatisfying(Schema.class, schema -> assertThat(schema)
				.isNotNull()
				.hasFieldOrPropertyWithValue("deprecated", false)
				.hasFieldOrPropertyWithValue("type", Schema.Type.STRING)
				.hasFieldOrPropertyWithValue("description", ""));

		assertThat(operation.getParameterObjects().get(2))
			.isNotNull()
			.hasFieldOrPropertyWithValue("name", "c")
			.hasFieldOrPropertyWithValue("in", Parameter.In.QUERY)
			.hasFieldOrPropertyWithValue("description", "parameter c for method c")
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
	@GitHubIssue("#24")
	void getGithubIssue024() {
		// run annotation processor
		testCompilation(new SpringWebOpenApiProcessor(), createConfigMap("oas-generator05.yml"), GitHubIssue024.class, Resource.class);

		// create json-path context
		DocumentContext documentContext = createJsonPathDocumentContext();

		// assertions
		assertThat(documentContext.read("$.openapi", String.class))
			.isNotNull()
			.isEqualTo("3.0.3");

		validateDefaultInfoObject(documentContext, "MyService", "1.2.3-SNAPSHOT");

		Operation operation = documentContext.read("$.paths./github/issues.get", Operation.class);
		assertThat(operation)
			.isNotNull()
			.hasFieldOrPropertyWithValue("operationId", "GitHubIssue024#test");

		assertThat(operation.getSecurity())
			.isNotNull()
			.hasSize(1);

		assertThat(operation.getSecurity().get(0))
			.isNotNull()
			.hasSize(1)
			.containsOnlyKeys("read_role");



		validateSchemaForTestResource(documentContext);
		validateSecurityScheme(documentContext);
	}

	@Test
	@GitHubIssue("#26")
	void getGithubIssue026() {
		// run annotation processor
		testCompilation(new SpringWebOpenApiProcessor(), GitHubIssue026.class, Resource.class);

		// create json-path context
		DocumentContext documentContext = createJsonPathDocumentContext();

		// assertions
		assertThat(documentContext.read("$.openapi", String.class))
			.isNotNull()
			.isEqualTo("3.0.3");

		validateDefaultInfoObject(documentContext);

		Operation operation = documentContext.read("$.paths./github/issues.post", Operation.class);
		assertThat(operation)
			.isNotNull()
			.hasFieldOrPropertyWithValue("operationId", "GitHubIssue026#test");

		validateSchemaForTestResource(documentContext);
		validateRequestBodyForTestResource(documentContext);
	}

	@Test
	@GitHubIssue("#43")
	void getGithubIssue043() {
		// run annotation processor
		testCompilation(new SpringWebOpenApiProcessor(), createConfigMap("oas-generator05.yml"), GitHubIssue043.class);

		// create json-path context
		DocumentContext documentContext = createJsonPathDocumentContext();

		// assertions
		assertThat(documentContext.read("$.openapi", String.class))
			.isNotNull()
			.isEqualTo("3.0.3");

		validateDefaultInfoObject(documentContext, "MyService", "1.2.3-SNAPSHOT");

		Operation operation = documentContext.read("$.paths./github/issues.get", Operation.class);
		assertThat(operation)
			.isNotNull()
			.hasFieldOrPropertyWithValue("operationId", "GitHubIssue043#test1");

		assertThat(operation.getParameterObjects())
			.isNotNull()
			.hasSize(1);
		assertThat(operation.getParameterObjects().get(0))
			.isNotNull()
			.hasFieldOrPropertyWithValue("name", "test")
			.hasFieldOrPropertyWithValue("in", Parameter.In.PATH)
			.hasFieldOrPropertyWithValue("description", "")
			.hasFieldOrPropertyWithValue("required", true)
			.hasFieldOrPropertyWithValue("deprecated", false)
			.hasFieldOrPropertyWithValue("allowEmptyValue", false)
			.extracting(Parameter::getSchema)
			.isInstanceOfSatisfying(Schema.class, schema -> assertThat(schema)
				.isNotNull()
				.hasFieldOrPropertyWithValue("deprecated", false)
				.hasFieldOrPropertyWithValue("type", Schema.Type.STRING)
				.hasFieldOrPropertyWithValue("description", ""));

		assertThat(operation.getResponses())
			.isNotNull()
			.hasSize(2)
			.containsOnlyKeys("default", "200");
		assertThat(operation.getResponses().get("200").getContent())
			.isNotNull()
			.hasSize(1)
			.containsOnlyKeys("application/vnd.test.list+json")
			.extractingByKey("application/vnd.test.list+json")
			.isInstanceOfSatisfying(MediaType.class, mediaType -> assertThat(mediaType)
				.isNotNull()
				.extracting(MediaType::getSchema)
				.isNotNull()
				.hasFieldOrPropertyWithValue("$ref", "#/components/schemas/String"));
		assertThat(operation.getResponses().get("default").getContent())
			.isNotNull()
			.hasSize(2)
			.containsOnlyKeys("application/vnd.test.list+json", "application/vnd.test+json")
			.extractingByKey("application/vnd.test+json")
			.isInstanceOfSatisfying(MediaType.class, mediaType -> assertThat(mediaType)
				.isNotNull());
//				.extracting(MediaType::getSchema)
//				.isNotNull()
//				.hasFieldOrPropertyWithValue("$ref", "#/components/schemas/String"));

		validateSecurityScheme(documentContext);
	}

	@Test
	@GitHubIssue("#52")
	void getGithubIssue052() {
		// run annotation processor
		testCompilation(new SpringWebOpenApiProcessor(), GitHubIssue052.class);

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
			.hasFieldOrPropertyWithValue("operationId", "GitHubIssue052#test1");

		assertThat(operation.getParameterObjects())
			.isNotNull()
			.hasSize(1);
		assertThat(operation.getParameterObjects().get(0))
			.isNotNull()
			.hasFieldOrPropertyWithValue("name", "test")
			.hasFieldOrPropertyWithValue("in", Parameter.In.PATH)
			.hasFieldOrPropertyWithValue("description", "")
			.hasFieldOrPropertyWithValue("required", true)
			.hasFieldOrPropertyWithValue("deprecated", false)
			.hasFieldOrPropertyWithValue("allowEmptyValue", false)
			.extracting(Parameter::getSchema)
			.isInstanceOfSatisfying(Schema.class, schema -> assertThat(schema)
				.isNotNull()
				.hasFieldOrPropertyWithValue("deprecated", false)
				.hasFieldOrPropertyWithValue("type", Schema.Type.STRING)
				.hasFieldOrPropertyWithValue("description", ""));

		assertThat(operation.getResponses())
			.isNotNull()
			.hasSize(2)
			.containsOnlyKeys("default", "204");
		assertThat(operation.getResponses().get("204"))
			.isNotNull()
			.hasFieldOrPropertyWithValue("description", "the status code");
		assertThat(operation.getResponses().get("default"))
			.isNotNull()
			.hasFieldOrPropertyWithValue("description", "");
	}

}
