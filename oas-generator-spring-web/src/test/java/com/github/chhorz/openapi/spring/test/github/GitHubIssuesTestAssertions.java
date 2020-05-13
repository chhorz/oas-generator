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
import com.github.chhorz.openapi.spring.test.github.resources.Resource;
import com.jayway.jsonpath.DocumentContext;

import java.util.Map;
import java.util.function.Consumer;

import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author chhorz
 */
class GitHubIssuesTestAssertions {

	public static Map<String, String> createConfigMap(String configFile) {
		return singletonMap(OpenAPIConstants.OPTION_PROPERTIES_PATH, configFile);
	}

	/**
	 * Validates a reference value. Usage for {@link org.assertj.core.api.AbstractAssert#isInstanceOfSatisfying}.
	 *
	 * @param expectedReference the expected reference value
	 * @return the consumer that could be used
	 */
	public static Consumer<Reference> refCheck(String expectedReference){
		return map -> assertThat(map)
			.isNotNull()
			.hasFieldOrPropertyWithValue("$ref", expectedReference);
	}

	/**
	 * Validates the default info object.
	 *
	 * @param documentContext the json document context
	 */
	public static void validateDefaultInfoObject(final DocumentContext documentContext){
		validateDefaultInfoObject(documentContext, "Title", "Version");
	}

	public static void validateDefaultInfoObject(final DocumentContext documentContext, final String title, final String version){
		Info info = documentContext.read("$.info", Info.class);

		assertThat(info)
			.isNotNull()
			.hasFieldOrPropertyWithValue("title", title)
			.hasFieldOrPropertyWithValue("version", version)
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
	public static void validateSchemaForTestResource(final DocumentContext documentContext) {
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
	public static void validateSchemaForHateoasTestResource(final DocumentContext documentContext) {
		Map<String, String> schemaMap = documentContext.read("$.components.schemas", Map.class);

		assertThat(schemaMap)
			.containsOnlyKeys("HateoasResource", "Link", "LinkRelation");

		// HateoasResource
		Schema schema = documentContext.read("$.components.schemas.HateoasResource", Schema.class);

		assertThat(schema)
			.isNotNull()
			.hasFieldOrPropertyWithValue("deprecated", false)
			.hasFieldOrPropertyWithValue("description","a test resource for GitHub issue tests")
			.hasFieldOrPropertyWithValue("type", Schema.Type.OBJECT);

		assertThat(schema.getProperties())
			.isNotNull()
			.hasSize(2)
			.containsOnlyKeys("value", "links");

		assertThat(schema.getProperties().get("value"))
			.isNotNull()
			.hasFieldOrPropertyWithValue("deprecated", false)
			.hasFieldOrPropertyWithValue("type", Schema.Type.STRING)
			.hasFieldOrPropertyWithValue("description","some test value");

		assertThat(schema.getProperties().get("links"))
			.isNotNull()
			.hasFieldOrPropertyWithValue("deprecated", false)
			.hasFieldOrPropertyWithValue("type", Schema.Type.ARRAY)
			.hasFieldOrPropertyWithValue("description","")
			.isInstanceOfSatisfying(Schema.class, propertySchema -> assertThat(propertySchema)
				.isNotNull()
				.extracting(Schema::getItems)
				.isInstanceOfSatisfying(Reference.class, refCheck("#/components/schemas/Link")));

		// Link
		Schema linkSchema = documentContext.read("$.components.schemas.Link", Schema.class);

		assertThat(linkSchema)
			.isNotNull()
			.hasFieldOrPropertyWithValue("deprecated", false)
			.hasFieldOrPropertyWithValue("description","")
			.hasFieldOrPropertyWithValue("type", Schema.Type.OBJECT);

		assertThat(linkSchema.getProperties())
			.isNotNull()
			.hasSize(9)
			.containsOnlyKeys("deprecation", "href", "hreflang", "media", "name", "profile", "rel", "title", "type");

		assertThat(linkSchema.getProperties().get("deprecation"))
			.isNotNull()
			.hasFieldOrPropertyWithValue("deprecated", false)
			.hasFieldOrPropertyWithValue("type", Schema.Type.STRING)
			.hasFieldOrPropertyWithValue("description", "");

		assertThat(linkSchema.getProperties().get("href"))
			.isNotNull()
			.hasFieldOrPropertyWithValue("deprecated", false)
			.hasFieldOrPropertyWithValue("type", Schema.Type.STRING)
			.hasFieldOrPropertyWithValue("description", "");

		assertThat(linkSchema.getProperties().get("hreflang"))
			.isNotNull()
			.hasFieldOrPropertyWithValue("deprecated", false)
			.hasFieldOrPropertyWithValue("type", Schema.Type.STRING)
			.hasFieldOrPropertyWithValue("description", "");

		assertThat(linkSchema.getProperties().get("media"))
			.isNotNull()
			.hasFieldOrPropertyWithValue("deprecated", false)
			.hasFieldOrPropertyWithValue("type", Schema.Type.STRING)
			.hasFieldOrPropertyWithValue("description", "");

		assertThat(linkSchema.getProperties().get("name"))
			.isNotNull()
			.hasFieldOrPropertyWithValue("deprecated", false)
			.hasFieldOrPropertyWithValue("type", Schema.Type.STRING)
			.hasFieldOrPropertyWithValue("description", "");

		assertThat(linkSchema.getProperties().get("profile"))
			.isNotNull()
			.hasFieldOrPropertyWithValue("deprecated", false)
			.hasFieldOrPropertyWithValue("type", Schema.Type.STRING)
			.hasFieldOrPropertyWithValue("description", "");

		assertThat(linkSchema.getProperties().get("title"))
			.isNotNull()
			.hasFieldOrPropertyWithValue("deprecated", false)
			.hasFieldOrPropertyWithValue("type", Schema.Type.STRING)
			.hasFieldOrPropertyWithValue("description", "");

		assertThat(linkSchema.getProperties().get("type"))
			.isNotNull()
			.hasFieldOrPropertyWithValue("deprecated", false)
			.hasFieldOrPropertyWithValue("type", Schema.Type.STRING)
			.hasFieldOrPropertyWithValue("description", "");

		assertThat(linkSchema.getProperties().get("rel"))
			.isNotNull()
			.isInstanceOfSatisfying(Reference.class, refCheck("#/components/schemas/LinkRelation"));

		// LinkRelation
		assertThat(documentContext.read("$.components.schemas.LinkRelation", Schema.class))
			.isNotNull()
			.hasFieldOrPropertyWithValue("deprecated", false)
			.hasFieldOrPropertyWithValue("type", Schema.Type.OBJECT)
			.hasFieldOrPropertyWithValue("description", "");
	}

	/**
	 * Validates all schema entries for a ResponseEntity class.
	 *
	 * @param documentContext the json document context
	 *
	 * @see org.springframework.http.ResponseEntity
	 */
	public static void validateSchemaForResponseEntity(final DocumentContext documentContext) {
		Map<String, String> schemaMap = documentContext.read("$.components.schemas", Map.class);

		assertThat(schemaMap)
			.containsOnlyKeys("HttpHeaders", "T", "Object", "ResponseEntity");

		// HttpHeaders
		assertThat(documentContext.read("$.components.schemas.HttpHeaders", Schema.class))
			.isNotNull()
			.hasFieldOrPropertyWithValue("deprecated", false)
			.hasFieldOrPropertyWithValue("description","")
			.hasFieldOrPropertyWithValue("type", Schema.Type.OBJECT);

		// Object
		assertThat(documentContext.read("$.components.schemas.Object", Schema.class))
			.isNotNull()
			.hasFieldOrPropertyWithValue("deprecated", false)
			.hasFieldOrPropertyWithValue("type", Schema.Type.OBJECT);

		// T
		assertThat(documentContext.read("$.components.schemas.T", Schema.class))
			.isNotNull()
			.hasFieldOrPropertyWithValue("deprecated", false)
			.hasFieldOrPropertyWithValue("type", Schema.Type.OBJECT);

		// ResponseEntity
		Schema responseEntity = documentContext.read("$.components.schemas.ResponseEntity", Schema.class);

		assertThat(responseEntity)
			.isNotNull()
			.hasFieldOrPropertyWithValue("deprecated", false)
			.hasFieldOrPropertyWithValue("description","")
			.hasFieldOrPropertyWithValue("type", Schema.Type.OBJECT);

		assertThat(responseEntity.getProperties())
			.isNotNull()
			.hasSize(3)
			.containsOnlyKeys("body", "headers", "status");

		assertThat(documentContext.read("$.components.schemas.ResponseEntity.properties.body.$ref", String.class))
			.isNotNull()
			.isEqualTo("#/components/schemas/T");

		assertThat(documentContext.read("$.components.schemas.ResponseEntity.properties.headers.$ref", String.class))
			.isNotNull()
			.isEqualTo("#/components/schemas/HttpHeaders");

		assertThat(documentContext.read("$.components.schemas.ResponseEntity.properties.status.$ref", String.class))
			.isNotNull()
			.isEqualTo("#/components/schemas/Object");

	}

	/**
	 * Validates the read_role security scheme
	 *
	 * @param documentContext the json document context
	 */
	public static void validateSecurityScheme(final DocumentContext documentContext) {
		SecuritySchemeHttp securityScheme = documentContext.read("$.components.securitySchemes.read_role", SecuritySchemeHttp.class);

		assertThat(securityScheme)
			.isNotNull()
			.hasFieldOrPropertyWithValue("description","Basic LDAP read role.")
			.hasFieldOrPropertyWithValue("type", SecurityScheme.Type.http)
			.hasFieldOrPropertyWithValue("scheme", "basic");
	}

	/**
	 * Validates the given test resource.
	 *
	 * @param documentContext the json document context
	 *
	 * @see Resource
	 */
	public static void validateRequestBodyForTestResource(final DocumentContext documentContext) {
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
