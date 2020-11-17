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

import java.nio.file.Paths;
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
			.isNull();

		Components components = documentContext.read("$.components", Components.class);

		assertThat(components)
			.isNotNull();
		assertThat(components.getSchemas())
			.isNotNull()
			.isEmpty();
	}

	@Test
	@GitHubIssue("#2")
	void getGithubIssue002_2() {
		// run annotation processor
		testCompilation(new SpringWebOpenApiProcessor(), GitHubIssue002_2.class, Resource.class);

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
			.hasFieldOrPropertyWithValue("operationId", "GitHubIssue002_2#test")
			.hasFieldOrPropertyWithValue("deprecated", false)
			.hasFieldOrPropertyWithValue("security", emptyList())
			.hasFieldOrPropertyWithValue("parameterObjects", emptyList());
		assertThat(operation.getResponses())
			.isNotNull()
			.hasSize(1)
			.containsOnlyKeys("200")
			.extractingByKey("200")
			.isNotNull()
			.hasFieldOrPropertyWithValue("description", "Lorem ipsum");
		assertThat(operation.getResponses().get("200").getContent())
			.isNotNull()
			.hasSize(1)
			.containsOnlyKeys("*/*")
			.extractingByKey("*/*")
			.isInstanceOfSatisfying(MediaType.class, mediaType -> assertThat(mediaType)
				.isNotNull());

		validateSchemaForTestResource(documentContext);
	}

	@Test
	@GitHubIssue("#8")
	void getGithubIssue008() {
		// run annotation processor
		testCompilation(new SpringWebOpenApiProcessor(), createConfigFileOption("oas-generator-withoutparser.yml"), GitHubIssue008.class, Resource.class);

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
			.isNotNull()
			.hasFieldOrPropertyWithValue("in", Parameter.In.QUERY)
			.hasFieldOrPropertyWithValue("name", "filter")
			.hasFieldOrPropertyWithValue("required", false);

		assertThat(parameters[0].getSchema())
			.isNotNull()
			.hasFieldOrPropertyWithValue("deprecated", false)
			.hasFieldOrPropertyWithValue("type", Schema.Type.STRING)
			.hasAllNullFieldsOrPropertiesExcept("deprecated", "type");

		Components components = documentContext.read("$.components", Components.class);

		assertThat(components.getSchemas())
			.containsOnlyKeys("Resource");

		validateSchemaForTestResource(documentContext);
	}

	@Test
	@GitHubIssue("#15")
	void getGithubIssue015_2() {
		// run annotation processor
		testCompilation(new SpringWebOpenApiProcessor(), GitHubIssue015_2.class, Resource.class);

		// create json-path context
		DocumentContext documentContext = createJsonPathDocumentContext();

		// assertions
		assertThat(documentContext.read("$.openapi", String.class))
			.isNotNull()
			.isEqualTo("3.0.3");

		validateDefaultInfoObject(documentContext);

		validateRequestBodyForTestResource(documentContext, false);

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
			.hasFieldOrPropertyWithValue("summary", "Test method A.\n<hr>\nTest method B.")
			.hasFieldOrPropertyWithValue("description", "Test method A.\n<hr>\nTest method B.")
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
		validateRequestBodyForTestResource(documentContext, true);
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
			.hasFieldOrPropertyWithValue("summary", null)
			.hasFieldOrPropertyWithValue("description", null)
			.hasFieldOrPropertyWithValue("operationId", "GitHubIssue023#testParamA")
			.hasFieldOrPropertyWithValue("deprecated", false)
			.hasFieldOrPropertyWithValue("security", emptyList());
		assertThat(operation.getResponses())
			.isNotNull()
			.hasSize(1)
			.containsOnlyKeys("default")
			.extractingByKey("default")
			.isNotNull()
			.hasFieldOrPropertyWithValue("description", null);
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
		testCompilation(new SpringWebOpenApiProcessor(), createConfigFileOption("oas-generator05.yml"), GitHubIssue024.class, Resource.class);

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
		validateRequestBodyForTestResource(documentContext, true);
	}

	@Test
	@GitHubIssue("#43")
	void getGithubIssue043() {
		// run annotation processor
		testCompilation(new SpringWebOpenApiProcessor(), createConfigFileOption("oas-generator05.yml"), GitHubIssue043.class);

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

		assertThat(operation.getTags())
			.isNotNull()
			.hasSize(1)
			.contains("Test");

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
			.containsOnlyKeys("application/vnd.test.v1+json")
			.extractingByKey("application/vnd.test.v1+json")
			.isInstanceOfSatisfying(MediaType.class, mediaType -> assertThat(mediaType)
				.isNotNull()
				.extracting(MediaType::getSchema)
				.isNotNull()
				.hasFieldOrPropertyWithValue("$ref", "#/components/schemas/String"));
		assertThat(operation.getResponses().get("default").getContent())
			.isNotNull()
			.hasSize(2)
			.containsOnlyKeys("application/vnd.test.v1+json", "application/vnd.test.v2+json")
			.extractingByKey("application/vnd.test.v1+json")
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

	@Test
	@GitHubIssue("#53")
	void testProcessorDisabled() {
		// run annotation processor
		testCompilation(new SpringWebOpenApiProcessor(), createConfigFileOption("oas-generator-disabled.yml"), Resource.class);

		// assertions
		assertThat(Paths.get("target/openapi/openapi-schema-missing.json").toFile())
			.isNotNull()
			.doesNotExist();
	}

	@Test
	@GitHubIssue("#61")
	void getGithubIssue061() {
		// run annotation processor
		testCompilation(new SpringWebOpenApiProcessor(), GitHubIssue061.class, Resource.class);

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
			.hasFieldOrPropertyWithValue("operationId", "GitHubIssue061#test");

		assertThat(operation.getParameterObjects())
			.isNotNull()
			.hasSize(3);
		assertThat(operation.getParameterObjects().get(0))
			.isNotNull()
			.hasFieldOrPropertyWithValue("name", "size")
			.hasFieldOrPropertyWithValue("in", Parameter.In.QUERY)
			.hasFieldOrPropertyWithValue("description", "Requested page size")
			.hasFieldOrPropertyWithValue("required", false)
			.hasFieldOrPropertyWithValue("deprecated", false)
			.hasFieldOrPropertyWithValue("allowEmptyValue", null)
			.extracting(Parameter::getSchema)
			.isInstanceOfSatisfying(Schema.class, schema -> assertThat(schema)
				.isNotNull()
				.hasFieldOrPropertyWithValue("deprecated", false)
				.hasFieldOrPropertyWithValue("type", Schema.Type.INTEGER)
				.hasFieldOrPropertyWithValue("format", Schema.Format.INT32)
				.hasFieldOrPropertyWithValue("description", null));
		assertThat(operation.getParameterObjects().get(1))
			.isNotNull()
			.hasFieldOrPropertyWithValue("name", "page")
			.hasFieldOrPropertyWithValue("in", Parameter.In.QUERY)
			.hasFieldOrPropertyWithValue("description", "Requested page number")
			.hasFieldOrPropertyWithValue("required", false)
			.hasFieldOrPropertyWithValue("deprecated", false)
			.hasFieldOrPropertyWithValue("allowEmptyValue", null)
			.extracting(Parameter::getSchema)
			.isInstanceOfSatisfying(Schema.class, schema -> assertThat(schema)
				.isNotNull()
				.hasFieldOrPropertyWithValue("deprecated", false)
				.hasFieldOrPropertyWithValue("type", Schema.Type.INTEGER)
				.hasFieldOrPropertyWithValue("format", Schema.Format.INT32)
				.hasFieldOrPropertyWithValue("description", null));
		assertThat(operation.getParameterObjects().get(2))
			.isNotNull()
			.hasFieldOrPropertyWithValue("name", "sort")
			.hasFieldOrPropertyWithValue("in", Parameter.In.QUERY)
			.hasFieldOrPropertyWithValue("description", "Requested sort attribute and order")
			.hasFieldOrPropertyWithValue("required", false)
			.hasFieldOrPropertyWithValue("deprecated", false)
			.hasFieldOrPropertyWithValue("allowEmptyValue", null)
			.extracting(Parameter::getSchema)
			.isInstanceOfSatisfying(Schema.class, schema -> assertThat(schema)
				.isNotNull()
				.hasFieldOrPropertyWithValue("deprecated", false)
				.hasFieldOrPropertyWithValue("type", Schema.Type.STRING)
				.hasFieldOrPropertyWithValue("format", null)
				.hasFieldOrPropertyWithValue("description", null));

		assertThat(operation.getResponses())
			.isNotNull()
			.hasSize(1)
			.containsOnlyKeys("default");

		validateSchemaForTestResource(documentContext);
	}

	@Test
	@GitHubIssue("#67")
	void getGithubIssue067() {
		// run annotation processor
		testCompilation(new SpringWebOpenApiProcessor(), GitHubIssue067.class, Resource.class);

		// create json-path context
		DocumentContext documentContext = createJsonPathDocumentContext();

		// assertions
		assertThat(documentContext.read("$.openapi", String.class))
			.isNotNull()
			.isEqualTo("3.0.3");

		validateDefaultInfoObject(documentContext);

		validateSchemaForTestResource(documentContext);

		Schema schema = documentContext.read("$.components.schemas.OtherResource", Schema.class);

		assertThat(schema)
			.isNotNull()
			.hasFieldOrPropertyWithValue("deprecated", false)
			.hasFieldOrPropertyWithValue("description","Another resource that should be included but in not used in API methods.")
			.hasFieldOrPropertyWithValue("type", Schema.Type.OBJECT);

		assertThat(schema.getProperties())
			.isNotNull()
			.hasSize(1)
			.containsOnlyKeys("value");

		assertThat(schema.getProperties().get("value"))
			.isNotNull()
			.hasFieldOrPropertyWithValue("deprecated", false)
			.hasFieldOrPropertyWithValue("type", Schema.Type.STRING)
			.hasFieldOrPropertyWithValue("description","some value");
	}

	@Test
	@GitHubIssue("#82")
	void getGithubIssue082() {
		// run annotation processor
		testCompilation(new SpringWebOpenApiProcessor(), createConfigFileOption("oas-generator05.yml"), GitHubIssue082.class, Resource.class);

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
			.hasFieldOrPropertyWithValue("operationId", "GitHubIssue082#test1");

		assertThat(operation.getTags())
			.isNotNull()
			.hasSize(1)
			.contains("Test");

		assertThat(operation.getParameterObjects())
			.isNotNull()
			.hasSize(0);

		assertThat(operation.getRequestBodyReference())
			.isNotNull()
			.extracting(Reference::get$ref)
			.isEqualTo("#/components/requestBodies/Resource");

		assertThat(operation.getResponses())
			.isNotNull()
			.hasSize(2)
			.containsOnlyKeys("default", "200");

		validateSchemaForTestResource(documentContext);

		RequestBody requestBody = documentContext.read("$.components.requestBodies.Resource", RequestBody.class);

		assertThat(requestBody)
			.isNotNull()
			.hasFieldOrPropertyWithValue("description",null)
			.hasFieldOrPropertyWithValue("required", true);

		assertThat(requestBody.getContent())
			.isNotNull()
			.hasSize(2)
			.containsOnlyKeys("application/vnd.test.v1+json", "application/vnd.test.v2+json");

//		assertThat(requestBody.getContent())
//			.extractingByKey("application/vnd.test.v1+json")
//			.extracting(MediaType::getSchema)
//			.isInstanceOfSatisfying(Reference.class, refCheck("#/components/schemas/Resource"));

		validateSecurityScheme(documentContext);
	}

	@Test
	@GitHubIssue("#85")
	void getGithubIssue083() {
		// run annotation processor
		testCompilation(new SpringWebOpenApiProcessor(), createConfigFileOption("oas-generator04.yml"), GitHubIssue085.class, Resource.class);

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
			.hasFieldOrPropertyWithValue("operationId", "GitHubIssue085#test");

		assertThat(operation.getTags())
			.isNotNull()
			.hasSize(1)
			.contains("order");

		validateSchemaForTestResource(documentContext);
	}

}
