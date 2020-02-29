package com.github.chhorz.openapi.spi.asciidoctor.test;

import com.github.chhorz.openapi.common.domain.*;
import com.github.chhorz.openapi.common.properties.ParserProperties;
import com.github.chhorz.openapi.spi.asciidoctor.AsciidoctorPostProcessor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class AsciidoctorPostProcessorTest {

	private AsciidoctorPostProcessor processor;

	@BeforeAll
	static void createFolder() throws IOException {
		Files.createDirectories(Paths.get("target", "generated-test-docs"));
	}

	@Test
	void testMinimalEmbeddedAsciidoctorPostProcessor() {
		// given
		Info info = new Info();
		info.setTitle("Test Service");
		info.setVersion("1.2.3-SNAPSHOT");

		OpenAPI openApi = new OpenAPI();
		openApi.setOpenapi("3.0.3");
		openApi.setInfo(info);

		processor = createAsciidoctorPostProcessor("/embedded", false);

		// when
		processor.execute(openApi);

		// then
		Path outputPath = Paths.get("target", "generated-test-docs", "embedded", "openapi.adoc");
		Path referencePath = new File("src/test/resources/referenceDocs/embedded.adoc").toPath();

		assertThat(outputPath).exists();
		assertThat(referencePath).exists();

		compareFiles(referencePath, outputPath);
	}

	@Test
	void testMinimalAsciidoctorPostProcessor() {
		// given
		Info info = new Info();
		info.setTitle("Test Service");
		info.setVersion("1.2.3-SNAPSHOT");

		OpenAPI openApi = new OpenAPI();
		openApi.setOpenapi("3.0.3");
		openApi.setInfo(info);

		processor = createAsciidoctorPostProcessor("/minimal");

		// when
		processor.execute(openApi);

		// then
		Path outputPath = Paths.get("target", "generated-test-docs", "minimal", "openapi.adoc");
		Path referencePath = new File("src/test/resources/referenceDocs/minimal.adoc").toPath();

		assertThat(outputPath).exists();
		assertThat(referencePath).exists();

		compareFiles(referencePath, outputPath);
	}

	@Test
	void testFullAsciidoctorPostProcessor() {
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

		ExternalDocumentation externalDocs = new ExternalDocumentation();
		externalDocs.setUrl("https://en.wikipedia.org");
		externalDocs.setDescription("Lorem ipsum dolor sit amet.");

		Tag tag1 = new Tag();
		tag1.setName("TAG_1");
		tag1.setDescription("This is a description");
		tag1.setExternalDocs(externalDocs);

		Tag tag2 = new Tag();
		tag2.setName("TAG_2");

		ServerVariableObject v1 = new ServerVariableObject();
		v1.setDescription("Lorem ipsum");
		v1.setDefaultValue("8080");
		v1.setEnumValue(Arrays.asList("8080", "443"));

		ServerVariableObject v2 = new ServerVariableObject();
		v2.setDefaultValue("8080");

		Server s1 = new Server();
		s1.setUrl("www.github.com");
		s1.setDescription("We love code.");
		s1.addVariable("port", v1);
		s1.addVariable("test", v2);

		Server s2 = new Server();
		s2.setUrl("www.gitlab.com");

		Schema stringSchema = new Schema();
		stringSchema.setType(Schema.Type.STRING);
		stringSchema.setFormat(null);
		stringSchema.setDescription("The name of the selected article.");

		Schema longSchema = new Schema();
		longSchema.setType(Schema.Type.INTEGER);
		longSchema.setFormat(Schema.Format.INT64);

		Schema stringsSchema = new Schema();
		stringsSchema.setDescription("All categories assigned to the given article.");
		stringsSchema.setType(Schema.Type.ARRAY);
		stringsSchema.setItems(stringSchema);

		Schema articleSchema = new Schema();
		articleSchema.setDescription("This is an article resource.");
		articleSchema.setDeprecated(true);
		articleSchema.putProperty("number", longSchema);
		articleSchema.putProperty("name", stringSchema);
		articleSchema.putProperty("ean", longSchema);
		articleSchema.putProperty("categories", stringsSchema);
		articleSchema.putProperty("order", new Reference("#/components/schemas/OrderResource"));

		Schema articlesSchema = new Schema();
		articlesSchema.setType(Schema.Type.ARRAY);
		articlesSchema.setDescription("All ordered articles.");
		articlesSchema.setItems(new Reference("#/components/schemas/ArticleResource"));

		Schema orderTypeSchema = new Schema();
		orderTypeSchema.setType(Schema.Type.ENUM);
		orderTypeSchema.addEnumValue("STANDARD");
		orderTypeSchema.addEnumValue("RETURN");

		Schema orderSchema = new Schema();
		orderSchema.setDescription("This is an order resource.");
		orderSchema.putProperty("number", longSchema);
		orderSchema.putProperty("articles", articlesSchema);
		orderSchema.putProperty("type", orderTypeSchema);

		Map<String, Schema> schemas = new HashMap<>();
		schemas.put("ArticleResource", articleSchema);
		schemas.put("OrderResource", orderSchema);

		SecurityScheme scheme = new SecurityScheme();
		scheme.setType(SecurityScheme.Type.http);
		scheme.setDescription("This is the scheme for <b>authorized</b> users.");

		Map<String, SecurityScheme> securitySchemes = new HashMap<>();
		securitySchemes.put("key", scheme);

		Components components = new Components();
		components.putAllParsedSchemas(schemas);
		components.setSecuritySchemes(securitySchemes);

		Parameter filter = new Parameter();
		filter.setDeprecated(false);
		filter.setDescription("The filter that should be applied");
		filter.setIn(Parameter.In.QUERY);
		filter.setName("filter");
		filter.setRequired(false);
		filter.setAllowEmptyValue(false);
		filter.setSchema(longSchema);

		MediaType mediaType = new MediaType();
		mediaType.setSchema(articlesSchema);

		Response response = new Response();
		response.setDescription("The response description");
		response.putContent("*/*", mediaType);

		Operation getArticles = new Operation();
		getArticles.setOperationId("ArticleController#getArticles");
		getArticles.setSummary("Here we get some articles.");
		getArticles.setDescription("Here we get some articles. Or something else.");
		getArticles.addTag("TAG_1");
		getArticles.setSecurity(Collections.singletonList(Collections.singletonMap("key", new ArrayList<>())));
		getArticles.addParameterObject(filter);
		getArticles.putDefaultResponse(response);

		Operation getOrders = new Operation();
		getOrders.setDeprecated(true);
		getOrders.setOperationId("OrderController#getOrders");
		getOrders.setSummary("Here we get some orders.");
		getOrders.setDescription("Here we get some orders. Or something else.");
		getOrders.addTag("TAG_1");
		getOrders.addTag("TAG_2");

		PathItemObject orders = new PathItemObject();
		orders.setGet(getOrders);

		PathItemObject articles = new PathItemObject();
		articles.setGet(getArticles);

		OpenAPI openApi = new OpenAPI();
		openApi.setOpenapi("3.0.3");
		openApi.setInfo(info);
		openApi.setExternalDocs(externalDocs);
		openApi.addTag(tag1);
		openApi.addTag(tag2);
		openApi.setServers(Arrays.asList(s1, s2));
		openApi.putPathItemObject("/orders", orders);
		openApi.putPathItemObject("/articles", articles);
		openApi.setComponents(components);

		processor = createAsciidoctorPostProcessor("/full");

		// when
		processor.execute(openApi);

		// then
		Path outputPath = Paths.get("target", "generated-test-docs", "full", "openapi.adoc");
		Path referencePath = new File("src/test/resources/referenceDocs/full.adoc").toPath();

		assertThat(outputPath).exists();
		assertThat(referencePath).exists();

		compareFiles(referencePath, outputPath);
	}

	private void compareFiles(Path reference, Path output) {
		try {
			List<String> referenceLines = Files.readAllLines(reference);
			List<String> outputLines = Files.readAllLines(output);

			if (referenceLines.size() != outputLines.size()) {
				fail("The reference and ouput file must have the same line count.");
			}

			referenceLines.forEach(line -> {
				assertThat(line.trim()).isEqualTo(outputLines.get(referenceLines.indexOf(line)).trim());
			});
		} catch (IOException e) {
			fail("Either reference or output file has no lines.");
		}
	}

	private AsciidoctorPostProcessor createAsciidoctorPostProcessor(String folder) {
		return createAsciidoctorPostProcessor(folder, true);
	}

	private AsciidoctorPostProcessor createAsciidoctorPostProcessor(String folder, boolean standalone) {
		LinkedHashMap<String, Object> propertyMap = new LinkedHashMap<>();
		propertyMap.put("outputPath", "target/generated-test-docs" + folder);
		propertyMap.put("standaloneFile", String.valueOf(standalone));

		ParserProperties parserProperties = new ParserProperties();
		parserProperties.setPostProcessor(Collections.singletonMap("asciidoctor", propertyMap));

		return new AsciidoctorPostProcessor(parserProperties);
	}

}
