/**
 *
 *    Copyright 2018-2021 the original author or authors.
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
import com.github.chhorz.openapi.spring.test.github.resources.*;
import com.jayway.jsonpath.DocumentContext;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.github.chhorz.openapi.spring.test.github.GitHubIssuesTestAssertions.*;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
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
				.isNotNull()
				.extracting(MediaType::getSchema)
				.isInstanceOfSatisfying(LinkedHashMap.class, refCheckForMap("#/components/schemas/Resource")));

		validateSchemaForResource(documentContext);
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

		validateSchemaForResource(documentContext);
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

		validateSchemaForResource(documentContext);
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

		validateRequestBodyForResource(documentContext, false);

		Components components = documentContext.read("$.components", Components.class);

		assertThat(components.getSchemas())
			.containsOnlyKeys("Resource");

		validateSchemaForResource(documentContext);
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
			.hasFieldOrPropertyWithValue("summary", "Merged data - please see description.")
			.hasFieldOrPropertyWithValue("description", "Test method A.\n<hr></hr>\nTest method B.")
			.hasFieldOrPropertyWithValue("operationId", "GitHubIssue018#testParamA")
			.hasFieldOrPropertyWithValue("deprecated", false)
			.hasFieldOrPropertyWithValue("security", emptyList());
		assertThat(operation.getResponses())
			.isNotNull()
			.hasSize(1)
			.containsOnlyKeys("200")
			.extractingByKey("200")
			.isNotNull()
			.hasFieldOrPropertyWithValue("description", "success");
		assertThat(operation.getResponses().get("200").getContent())
			.isNotNull()
			.hasSize(1)
			.containsOnlyKeys("*/*")
			.extractingByKey("*/*")
			.isInstanceOfSatisfying(MediaType.class, mediaType -> assertThat(mediaType)
				.isNotNull()
				.extracting(MediaType::getSchema)
				.isInstanceOfSatisfying(LinkedHashMap.class, map -> assertThat(map)
					.isNotNull()
					.containsOnlyKeys("deprecated", "items", "type")
					.extractingByKey("items")
					.isInstanceOfSatisfying(LinkedHashMap.class, refCheckForMap("#/components/schemas/Resource"))));
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

		validateSchemaForResource(documentContext);
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
				.isNotNull()
				.extracting(MediaType::getSchema)
				.isInstanceOfSatisfying(LinkedHashMap.class, refCheckForMap("#/components/schemas/ErrorResource")));
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
				.isNotNull()
				.extracting(MediaType::getSchema)
				.isInstanceOfSatisfying(LinkedHashMap.class, refCheckForMap("#/components/schemas/ErrorResource")));;
		assertThat(operationOne.getResponses().get("204").getContent())
			.isNull();

		Components components = documentContext.read("$.components", Components.class);

		assertThat(components.getSchemas())
			.containsOnlyKeys("Resource", "ErrorResource");

		validateSchemaForResource(documentContext);
		validateSchemaForErrorResource(documentContext);
		validateRequestBodyForResource(documentContext, true);
	}

	@Test
	@GitHubIssue("#21")
	void getGithubIssue021() {
		// run annotation processor
		testCompilation(new SpringWebOpenApiProcessor(), GitHubIssue021.class, ValidResource.class);

		// create json-path context
		DocumentContext documentContext = createJsonPathDocumentContext();

		// assertions
		assertThat(documentContext.read("$.openapi", String.class))
			.isNotNull()
			.isEqualTo("3.0.3");

		validateDefaultInfoObject(documentContext);

		Components components = documentContext.read("$.components", Components.class);

		assertThat(components.getSchemas())
			.containsOnlyKeys("ValidResource");

		assertThat(components.getSchemas().get("ValidResource"))
			.hasFieldOrPropertyWithValue("required", singletonList("value"));

		assertThat(components.getSchemas().get("ValidResource").getProperties())
			.containsKeys("value", "minimum");

		assertThat(components.getSchemas().get("ValidResource").getProperties().get("value"))
			.isNotNull()
			.hasFieldOrPropertyWithValue("pattern", "\\s+");

		assertThat(components.getSchemas().get("ValidResource").getProperties().get("minimum"))
			.isNotNull()
			.isInstanceOfSatisfying(Schema.class, schema ->
				assertThat(schema)
					.hasFieldOrPropertyWithValue("minimum", 0L));
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

		validateSchemaForResource(documentContext);
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
			.hasFieldOrPropertyWithValue("description", "");
		assertThat(operation.getResponses().get("default").getContent())
			.isNotNull()
			.hasSize(1)
			.containsOnlyKeys("*/*")
			.extractingByKey("*/*")
			.isInstanceOfSatisfying(MediaType.class, mediaType -> assertThat(mediaType)
				.isNotNull()
				.extracting(MediaType::getSchema)
				.isInstanceOfSatisfying(LinkedHashMap.class, map -> assertThat(map)
					.isNotNull()
					.containsOnlyKeys("deprecated", "items", "type")
					.extractingByKey("items")
					.isInstanceOfSatisfying(LinkedHashMap.class, refCheckForMap("#/components/schemas/Resource"))));
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

		validateSchemaForResource(documentContext);
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
				.isNotNull()
				.extracting(MediaType::getSchema)
				.isInstanceOfSatisfying(LinkedHashMap.class, map -> assertThat(map)
					.isNotNull()
					.containsOnlyKeys("deprecated", "items", "type")
					.extractingByKey("items")
					.isInstanceOfSatisfying(LinkedHashMap.class, refCheckForMap("#/components/schemas/Resource"))));
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

		validateSchemaForResource(documentContext);
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


		validateSchemaForResource(documentContext);
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

		validateSchemaForResource(documentContext);
		validateRequestBodyForResource(documentContext, true);
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
			.hasSize(1)
			.containsOnlyKeys("application/vnd.test.v2+json")
			.extractingByKey("application/vnd.test.v2+json")
			.isInstanceOfSatisfying(MediaType.class, mediaType -> assertThat(mediaType)
				.isNotNull());
//				.extracting(MediaType::getSchema)
//				.isNotNull()
//				.hasFieldOrPropertyWithValue("$ref", "#/components/schemas/String"));

		validateSecurityScheme(documentContext);
	}

	@Test
	@GitHubIssue("#51")
	void getGithubIssue051() {
		// run annotation processor
		testCompilation(new SpringWebOpenApiProcessor(), createConfigFileOption("oas-generator05.yml"), GitHubIssue051.class, Resource.class);

		// create json-path context
		DocumentContext documentContext = createJsonPathDocumentContext();

		// assertions
		assertThat(documentContext.read("$.openapi", String.class))
			.isNotNull()
			.isEqualTo("3.0.3");

		validateDefaultInfoObject(documentContext, "MyService", "1.2.3-SNAPSHOT");

		Operation operationOne = documentContext.read("$.paths./github/issues/javadoc.get", Operation.class);
		assertThat(operationOne)
			.isNotNull()
			.hasFieldOrPropertyWithValue("operationId", "GitHubIssue051#testJavadoc");

		Operation operationTwo = documentContext.read("$.paths./github/issues/annotation.get", Operation.class);
		assertThat(operationTwo)
			.isNotNull()
			.hasFieldOrPropertyWithValue("operationId", "GitHubIssue051#testAnnotation");

		assertThat(operationOne)
			.usingRecursiveComparison()
			.ignoringFields("operationId")
			.isEqualTo(operationTwo);

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
	@GitHubIssue("#54")
	void getGithubIssue054() {
		// run annotation processor
		testCompilation(new SpringWebOpenApiProcessor(), GitHubIssue054.class, Resource.class);

		// create json-path context
		DocumentContext documentContext = createJsonPathDocumentContext();

		// assertions
		assertThat(documentContext.read("$.openapi", String.class))
			.isNotNull()
			.isEqualTo("3.0.3");

		validateDefaultInfoObject(documentContext);

		Operation operation = documentContext.read("$.paths./github/issues/one.get", Operation.class);
		assertThat(operation)
			.isNotNull()
			.hasFieldOrPropertyWithValue("operationId", "GitHubIssue054#test1");

		assertThat(operation.getResponses())
			.isNotNull()
			.hasSize(1)
			.containsOnlyKeys("200");
		assertThat(operation.getResponses().get("200"))
			.isNotNull()
			.hasFieldOrPropertyWithValue("description", "the status code");

		Operation operationTwo = documentContext.read("$.paths./github/issues/two.get", Operation.class);
		assertThat(operationTwo)
			.isNotNull()
			.hasFieldOrPropertyWithValue("operationId", "GitHubIssue054#test2");

		assertThat(operationTwo.getResponses())
			.isNotNull()
			.hasSize(1)
			.containsOnlyKeys("default");
		assertThat(operationTwo.getResponses().get("default"))
			.isNotNull()
			.hasFieldOrPropertyWithValue("description", "");

		validateSchemaForResource(documentContext);
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

		validatePageableRequestParameter(documentContext);

		assertThat(operation.getResponses())
			.isNotNull()
			.hasSize(1)
			.containsOnlyKeys("default");

		validateSchemaForResource(documentContext);
	}

	@Test
	@GitHubIssue("#61")
	void getGithubIssue061_2() {
		// run annotation processor
		testCompilation(new SpringWebOpenApiProcessor(), GitHubIssue061_2.class, Resource.class);

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
			.hasFieldOrPropertyWithValue("operationId", "GitHubIssue061_2#test");

		assertThat(operation.getResponses())
			.containsOnlyKeys("default")
			.extractingByKey("default")
			.isNotNull()
			.hasFieldOrPropertyWithValue("description", "");
		assertThat(operation.getResponses().get("default").getContent())
			.containsOnlyKeys("*/*")
			.extractingByKey("*/*")
			.isInstanceOfSatisfying(MediaType.class, mediaType ->  assertThat(mediaType)
					.isNotNull()
					.extracting(MediaType::getSchema)
					.isInstanceOfSatisfying(LinkedHashMap.class, refCheckForMap("#/components/schemas/Resource")));

		validatePageableRequestParameter(documentContext);

		// TODO assert component schema part
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

		validateSchemaForResource(documentContext);

		Schema schema = documentContext.read("$.components.schemas.OtherResource", Schema.class);

		assertThat(schema)
			.isNotNull()
			.hasFieldOrPropertyWithValue("deprecated", false)
			.hasFieldOrPropertyWithValue("description", "Another resource that should be included but in not used in API methods.")
			.hasFieldOrPropertyWithValue("type", Schema.Type.OBJECT);

		assertThat(schema.getProperties())
			.isNotNull()
			.hasSize(1)
			.containsOnlyKeys("value");

		assertThat(schema.getProperties().get("value"))
			.isNotNull()
			.hasFieldOrPropertyWithValue("deprecated", false)
			.hasFieldOrPropertyWithValue("type", Schema.Type.STRING)
			.hasFieldOrPropertyWithValue("description", "some value");
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

		validateSchemaForResource(documentContext);

		RequestBody requestBody = documentContext.read("$.components.requestBodies.Resource", RequestBody.class);

		assertThat(requestBody)
			.isNotNull()
			.hasFieldOrPropertyWithValue("description", null)
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

		validateSchemaForResource(documentContext);
	}

	@Test
	@GitHubIssue("#143")
	void getGithubIssue143() {
		// run annotation processor
		testCompilation(new SpringWebOpenApiProcessor(), createConfigFileOption("oas-generator04.yml"), GitHubIssue143.class, Resource.class);

		// create json-path context
		DocumentContext documentContext = createJsonPathDocumentContext();

		// assertions
		assertThat(documentContext.read("$.openapi", String.class))
			.isNotNull()
			.isEqualTo("3.0.3");

		validateDefaultInfoObject(documentContext, "MyService", "1.2.3-SNAPSHOT");

		Operation operation = documentContext.read("$.paths./issues.get", Operation.class);
		assertThat(operation)
			.isNotNull()
			.hasFieldOrPropertyWithValue("operationId", "GitHubIssue143#test");

		validateSchemaForResource(documentContext);
	}

	@Test
	@GitHubIssue("#172")
	void getGithubIssue172() {
		// run annotation processor
		testCompilation(new SpringWebOpenApiProcessor(), createConfigFileOption("oas-generator04.yml"), GitHubIssue172.class, InheritedResource.class,
			AbstractResource.class, AbstractBaseResource.class);

		// create json-path context
		DocumentContext documentContext = createJsonPathDocumentContext();

		// assertions
		assertThat(documentContext.read("$.openapi", String.class))
			.isNotNull()
			.isEqualTo("3.0.3");

		validateDefaultInfoObject(documentContext, "MyService", "1.2.3-SNAPSHOT");

		Operation operation = documentContext.read("$.paths./issues.get", Operation.class);
		assertThat(operation)
			.isNotNull()
			.hasFieldOrPropertyWithValue("operationId", "GitHubIssue172#test");

		validateSchemaForInheritedResource(documentContext);
	}

	@Test
	@GitHubIssue("#180")
	@GitHubIssue("#213")
	void getGithubIssue180() {
		// run annotation processor
		testCompilation(new SpringWebOpenApiProcessor(), createConfigFileOption("oas-generator04.yml"), GitHubIssue180.class, TestResource.class);

		// create json-path context
		DocumentContext documentContext = createJsonPathDocumentContext();

		// assertions
		assertThat(documentContext.read("$.openapi", String.class))
			.isNotNull()
			.isEqualTo("3.0.3");

		validateDefaultInfoObject(documentContext, "MyService", "1.2.3-SNAPSHOT");

		Operation operation = documentContext.read("$.paths./issues.get", Operation.class);
		assertThat(operation)
			.isNotNull()
			.hasFieldOrPropertyWithValue("operationId", "GitHubIssue180#test");

		validateSchemaForTestResource(documentContext);
	}

	@Test
	@GitHubIssue("#182")
	void getGithubIssue182() {
		// run annotation processor
		testCompilation(new SpringWebOpenApiProcessor(), createConfigFileOption("oas-generator04.yml"), GitHubIssue182.class, Resource.class);

		// create json-path context
		DocumentContext documentContext = createJsonPathDocumentContext();

		// assertions
		assertThat(documentContext.read("$.openapi", String.class))
			.isNotNull()
			.isEqualTo("3.0.3");

		validateDefaultInfoObject(documentContext, "MyService", "1.2.3-SNAPSHOT");

		Operation operation = documentContext.read("$.paths./test1/issues.get", Operation.class);
		assertThat(operation)
			.isNotNull()
			.hasFieldOrPropertyWithValue("operationId", "GitHubIssue182#test");

		operation = documentContext.read("$.paths./test2/issues.get", Operation.class);
		assertThat(operation)
			.isNotNull()
			.hasFieldOrPropertyWithValue("operationId", "GitHubIssue182#test_0001");

		validateSchemaForResource(documentContext);
	}

	@Test
	@GitHubIssue("#194")
	void getGithubIssue194() {
		// run annotation processor
		testCompilation(new SpringWebOpenApiProcessor(), createConfigFileOption("oas-generator04.yml"), GitHubIssue194.class, OrderResource.class, OrderItemResource.class);

		// create json-path context
		DocumentContext documentContext = createJsonPathDocumentContext();

		// assertions
		assertThat(documentContext.read("$.openapi", String.class))
			.isNotNull()
			.isEqualTo("3.0.3");

		validateDefaultInfoObject(documentContext, "MyService", "1.2.3-SNAPSHOT");

		Operation operation = documentContext.read("$.paths./test/issues.get", Operation.class);
		assertThat(operation)
			.isNotNull()
			.hasFieldOrPropertyWithValue("operationId", "GitHubIssue194#test");

		validateSchemaForOrderResources(documentContext);
	}

	@Test
	@GitHubIssue("#285")
	void getGithubIssue285() {
		// run annotation processor
		testCompilation(new SpringWebOpenApiProcessor(), createConfigFileOption("oas-generator04.yml"), GitHubIssue285.class, Resource.class);

		// create json-path context
		DocumentContext documentContext = createJsonPathDocumentContext();

		// assertions
		assertThat(documentContext.read("$.openapi", String.class))
			.isNotNull()
			.isEqualTo("3.0.3");

		validateDefaultInfoObject(documentContext, "MyService", "1.2.3-SNAPSHOT");

		Operation operation = documentContext.read("$.paths./.post", Operation.class);
		assertThat(operation)
			.isNotNull()
			.hasFieldOrPropertyWithValue("operationId", "GitHubIssue285#test");

		validateSchemaForResource(documentContext);
		validateRequestBodyForResource(documentContext, true);
	}

	@Test
	@GitHubIssue("#289")
	void getGithubIssue289() {
		// run annotation processor
		testCompilation(new SpringWebOpenApiProcessor(), createConfigFileOption("oas-generator04.yml"), GitHubIssue289.class, Resource.class);

		// create json-path context
		DocumentContext documentContext = createJsonPathDocumentContext();

		// assertions
		assertThat(documentContext.read("$.openapi", String.class))
			.isNotNull()
			.isEqualTo("3.0.3");

		validateDefaultInfoObject(documentContext, "MyService", "1.2.3-SNAPSHOT");

		Operation operation = documentContext.read("$.paths./test-1.get", Operation.class);
		assertThat(operation)
			.isNotNull()
			.hasFieldOrPropertyWithValue("operationId", "GitHubIssue289#test");

		Operation operationTwo = documentContext.read("$.paths./test-2.get", Operation.class);
		assertThat(operationTwo)
			.isNotNull()
			.hasFieldOrPropertyWithValue("operationId", "GitHubIssue289#test_0001");

		validateSchemaForResource(documentContext);
	}

	@Test
	@GitHubIssue("#295")
	void getGithubIssue295() {
		// run annotation processor
		testCompilation(new SpringWebOpenApiProcessor(), GitHubIssue295.class, ValidJakartaResource.class);

		// create json-path context
		DocumentContext documentContext = createJsonPathDocumentContext();

		// assertions
		assertThat(documentContext.read("$.openapi", String.class))
			.isNotNull()
			.isEqualTo("3.0.3");

		validateDefaultInfoObject(documentContext);

		Components components = documentContext.read("$.components", Components.class);

		assertThat(components.getSchemas())
			.containsOnlyKeys("ValidJakartaResource");

		assertThat(components.getSchemas().get("ValidJakartaResource"))
			.hasFieldOrPropertyWithValue("required", singletonList("value"));

		assertThat(components.getSchemas().get("ValidJakartaResource").getProperties())
			.containsKeys("value", "minimum");

		assertThat(components.getSchemas().get("ValidJakartaResource").getProperties().get("value"))
			.isNotNull()
			.hasFieldOrPropertyWithValue("pattern", "\\s+");

		assertThat(components.getSchemas().get("ValidJakartaResource").getProperties().get("minimum"))
			.isNotNull()
			.isInstanceOfSatisfying(Schema.class, schema ->
				assertThat(schema)
					.hasFieldOrPropertyWithValue("minimum", 0L));

		assertThat(components.getSchemas().get("ValidJakartaResource").getProperties().get("sizedString"))
			.isNotNull()
			.hasFieldOrPropertyWithValue("minLength", 10);

		assertThat(components.getSchemas().get("ValidJakartaResource").getProperties().get("sizedList"))
			.isNotNull()
			.hasFieldOrPropertyWithValue("minItems", 1)
			.hasFieldOrPropertyWithValue("maxItems", 5);

		assertThat(components.getSchemas().get("ValidJakartaResource").getProperties().get("nonEmptyString"))
			.isNotNull()
			.hasFieldOrPropertyWithValue("minLength", 1);
	}

	@Test
	@GitHubIssue("#307")
	void getGithubIssue307() {
		// run annotation processor
		testCompilation(new SpringWebOpenApiProcessor(), GitHubIssue307.class, Resource.class, ErrorResource.class, ProblemResource.class);

		// create json-path context
		DocumentContext documentContext = createJsonPathDocumentContext();

		// assertions
		assertThat(documentContext.read("$.openapi", String.class))
			.isNotNull()
			.isEqualTo("3.0.3");

		validateDefaultInfoObject(documentContext);

		Operation operation = documentContext.read("$.paths./github/issues/{id}.get", Operation.class);
		assertThat(operation)
			.isNotNull()
			.hasFieldOrPropertyWithValue("operationId", "GitHubIssue307#get");

		assertThat(operation.getResponses())
			.isNotNull()
			.containsOnlyKeys("200", "4XX", "5XX")
			.extractingByKey("200")
			.isNotNull()
			.hasFieldOrPropertyWithValue("description", "the resource by id");

		assertThat(operation.getResponses().get("200").getContent())
			.isNotNull()
			.hasSize(1)
			.containsOnlyKeys("*/*")
			.extractingByKey("*/*")
			.isInstanceOfSatisfying(MediaType.class, mediaType -> assertThat(mediaType)
				.isNotNull()
				.extracting(MediaType::getSchema)
				.isInstanceOfSatisfying(LinkedHashMap.class, refCheckForMap("#/components/schemas/Resource")));

		assertThat(operation.getResponses().get("4XX"))
			.isNotNull()
			.isNotNull()
			.hasFieldOrPropertyWithValue("description", "the problem resource");

		assertThat(operation.getResponses().get("4XX").getContent())
			.isNotNull()
			.hasSize(1)
			.containsOnlyKeys("*/*")
			.extractingByKey("*/*")
			.isInstanceOfSatisfying(MediaType.class, mediaType -> assertThat(mediaType)
				.isNotNull()
				.extracting(MediaType::getSchema)
				.isInstanceOfSatisfying(LinkedHashMap.class, refCheckForMap("#/components/schemas/ProblemResource")));

		assertThat(operation.getResponses().get("5XX"))
			.isNotNull()
			.isNotNull()
			.hasFieldOrPropertyWithValue("description", "the error resource");

		assertThat(operation.getResponses().get("5XX").getContent())
			.isNotNull()
			.hasSize(1)
			.containsOnlyKeys("*/*")
			.extractingByKey("*/*")
			.isInstanceOfSatisfying(MediaType.class, mediaType -> assertThat(mediaType)
				.isNotNull()
				.extracting(MediaType::getSchema)
				.isInstanceOfSatisfying(LinkedHashMap.class, refCheckForMap("#/components/schemas/ErrorResource")));

		validateSchemaForResource(documentContext);
		validateSchemaForErrorResource(documentContext);
		validateSchemaForProblemResource(documentContext);
	}

}
