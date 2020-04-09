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
package com.github.chhorz.openapi.spring.test;

import com.github.chhorz.openapi.common.OpenAPIConstants;
import com.github.chhorz.openapi.common.domain.*;
import com.github.chhorz.openapi.common.test.AbstractProcessorTest;
import com.github.chhorz.openapi.common.test.github.GithubIssue;
import com.github.chhorz.openapi.spring.SpringWebOpenApiProcessor;
import com.github.chhorz.openapi.spring.test.controller.ArticleController;
import com.github.chhorz.openapi.spring.test.controller.HelloWorldController;
import com.github.chhorz.openapi.spring.test.controller.HttpMethodsController;
import com.github.chhorz.openapi.spring.test.controller.OrderController;
import com.github.chhorz.openapi.spring.test.controller.external.ExternalResource;
import com.github.chhorz.openapi.spring.test.controller.github.GitHubIssue001;
import com.github.chhorz.openapi.spring.test.controller.github.GitHubIssue002;
import com.github.chhorz.openapi.spring.test.controller.github.GitHubIssue011;
import com.github.chhorz.openapi.spring.test.controller.github.GitHubIssue012;
import com.github.chhorz.openapi.spring.test.controller.resource.*;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;

import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.*;

/**
 * https://jcavallotti.blogspot.de/2013/05/how-to-unit-test-annotation-processor.html
 *
 * @author chhorz
 */
class SpringWebOpenApiProcessorTest extends AbstractProcessorTest {

	@Test
	void aTest() throws IOException {
		// run annotation processor
		testCompilation(new SpringWebOpenApiProcessor(), createConfigMap("oas-generator01.yml"), OrderController.class, BaseResource.class, Order.class, Article.class,
			PrimitiveResource.class, ErrorResource.class, ExternalResource.class);

		// compare result with reference documentation
//		compareFileContent("expected/openapi01.json", "oas-test/openapi01.json");

		DocumentContext ctx = JsonPath.parse(Paths.get("target/oas-test/openapi01.json").toFile(), Configuration.builder().mappingProvider(new JacksonMappingProvider()).build());

		// openapi version
		assertThat(ctx.read("$.openapi", String.class)).isEqualTo("3.0.3");

		// info
		Info info = ctx.read("$.info", Info.class);
		assertThat(info)
			.isNotNull()
			.hasFieldOrPropertyWithValue("title", "MyService")
			.hasFieldOrPropertyWithValue("version", "1.2.3-SNAPSHOT")
			.hasNoNullFieldsOrPropertiesExcept("description", "termsOfService");

		assertThat(info.getContact())
			.isNotNull()
			.hasFieldOrPropertyWithValue("name", "John Doe")
			.hasFieldOrPropertyWithValue("url", "https://www.google.com")
			.hasFieldOrPropertyWithValue("email", "john@doe.com");


		assertThat(info.getLicense())
			.isNotNull()
			.hasFieldOrPropertyWithValue("name" , "Apache License, Version 2.0")
			.hasFieldOrPropertyWithValue("url", "https://www.apache.org/licenses/LICENSE-2.0");

		// servers
		ServerVariableObject port = new ServerVariableObject();
		port.setDefaultValue("443");
		port.setDescription("The port of the application");
		port.setEnumValue(Arrays.asList("8080", "443"));

		Server server01 = new Server();
		server01.setDescription("Internal DEV-Stage");
		server01.setUrl("dev01.server.lan");
		server01.setVariables(singletonMap("port", port));

		Server server02 = new Server();
		server02.setDescription("Internal DEV-Stage");
		server02.setUrl("dev02.server.lan");
		server02.setVariables(singletonMap("port", port));

		Server[] servers = ctx.read("$.servers", Server[].class);
		assertThat(servers)
			.isNotNull()
			.hasSize(2)
			.contains(server01, server02);

		// tags
		ExternalDocumentation orderTagDocs = new ExternalDocumentation();
		orderTagDocs.setUrl("https://www.google.com");

		Tag orderTag = new Tag();
		orderTag.setName("order");
		orderTag.setDescription("The category collects all methods for orders.");
		orderTag.setExternalDocs(orderTagDocs);

		Tag testTag = new Tag();
		testTag.setName("test");

		Tag[] tags = ctx.read("$.tags", Tag[].class);
		assertThat(tags)
			.isNotNull()
			.hasSize(2)
			.contains(orderTag, atIndex(0))
			.contains(testTag, atIndex(1));

		// external docs
		ExternalDocumentation externalDocumentation = ctx.read("$.externalDocs", ExternalDocumentation.class);
		assertThat(externalDocumentation)
			.isNotNull()
			.hasFieldOrPropertyWithValue("description", "Lorem ipsum ...")
			.hasFieldOrPropertyWithValue("url", "https://www.openapis.org/");

	}

	@Test
	void testArticleController() {
		// run annotation processor
		testCompilation(new SpringWebOpenApiProcessor(), createConfigMap("oas-generator02.yml"),
			ArticleController.class, Article.class);

		// compare result with reference documentation
		compareFileContent("expected/openapi02.json", "oas-test/openapi02.json");
	}

	@Test
	void testHttpMethods() {
		// run annotation processor
		testCompilation(new SpringWebOpenApiProcessor(), HttpMethodsController.class, Article.class);

		// create json-path context
		DocumentContext ctx = createJsonPathDocumentContext();

		// TODO check http methods

		// validate the used schema component
		Schema article = ctx.read("$.components.schemas.Article", Schema.class);

		assertThat(article)
			.isNotNull()
			.hasFieldOrPropertyWithValue(  "deprecated", false)
			.hasFieldOrPropertyWithValue("type", Schema.Type.OBJECT)
			.hasFieldOrPropertyWithValue("description", "An article that can be ordered.");

		assertThat(article.getProperties())
			.hasSize(4)
			.containsOnlyKeys("name", "number", "price", "type");
	}

	@Test
	void testArticleControllerWithMinimalConfigFile() {
		// run annotation processor
		testCompilation(new SpringWebOpenApiProcessor(), createConfigMap("oas-generator04.yml"), HelloWorldController.class);

		// compare result with reference documentation
		compareFileContent("expected/openapi04.json", "openapi/openapi.json");
	}

	@Test
	@GithubIssue("#1")
	void getGithubIssue001() {
		// run annotation processor
		testCompilation(new SpringWebOpenApiProcessor(), GitHubIssue001.class);

		// create json-path context
		DocumentContext ctx = createJsonPathDocumentContext();

		// assertions
		Response response = ctx.read("$.paths./github/issue.get.responses.default", Response.class);

		assertThat(response.getContent())
			.isNull();
	}

	@Test
	@GithubIssue("#2")
	void getGithubIssue002() {
		// run annotation processor
		testCompilation(new SpringWebOpenApiProcessor(), GitHubIssue002.class);

		// create json-path context
		DocumentContext ctx = createJsonPathDocumentContext();

		// assertions
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
	@GithubIssue("#11")
	void getGithubIssue011() {
		// run annotation processor
		testCompilation(new SpringWebOpenApiProcessor(), GitHubIssue011.class);

		// create json-path context
		DocumentContext ctx = createJsonPathDocumentContext();

		// assertions
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
		testCompilation(new SpringWebOpenApiProcessor(), GitHubIssue012.class);

		// create json-path context
		DocumentContext ctx = createJsonPathDocumentContext();

		// assertions
		Components components = ctx.read("$.components", Components.class);

		assertThat(components.getSchemas())
			.containsOnlyKeys("Test");

		Schema schema = ctx.read("$.components.schemas.Test", Schema.class);

		assertThat(schema)
			.isNotNull()
			.hasFieldOrPropertyWithValue("type", Schema.Type.OBJECT);

		assertThat(components.getRequestBodies())
			.containsOnlyKeys("Test");

		RequestBody requestBody = ctx.read("$.components.requestBodies.Test", RequestBody.class);

		assertThat(requestBody)
			.hasFieldOrPropertyWithValue("description", "")
			.hasFieldOrPropertyWithValue("required", true);

		String reference = ctx.read("$.components.requestBodies.Test.content.application/json.schema.$ref", String.class);

		assertThat(reference)
			.isEqualTo("#/components/schemas/Test");
	}

	/**
	 * Compares JSON files for identical content. The order of properties is not checked. If the content does not match
	 * the test will be failed.
	 *
	 * @param expectedFile filename of the expected json content. The file must be placed in {@code src/test/resources/}
	 *                     directory.
	 * @param actualFile   filename of the actual json content. The file must be placed in {@code target/} directory.
	 */
	private void compareFileContent(String expectedFile, String actualFile) {
		try {
			String expected = String.join("", Files.readAllLines(Paths.get("src/test/resources/" + expectedFile)));
			String actual = String.join("", Files.readAllLines(Paths.get("target/" + actualFile)));
			JSONAssert.assertEquals(expected, actual, true);
		} catch (JSONException | IOException e) {
			fail("Could not check openapi.json against expected file.", e);
		}
	}

	private Map<String, String> createConfigMap(String configFile) {
		return singletonMap(OpenAPIConstants.OPTION_PROPERTIES_PATH, configFile);
	}

}
