package com.github.chhorz.openapi.common.test.util;

import com.github.chhorz.javadoc.JavaDoc;
import com.github.chhorz.openapi.common.domain.Reference;
import com.github.chhorz.openapi.common.domain.Response;
import com.github.chhorz.openapi.common.domain.Schema;
import com.github.chhorz.openapi.common.javadoc.ResponseTag;
import com.github.chhorz.openapi.common.properties.ParserProperties;
import com.github.chhorz.openapi.common.test.extension.ProcessingUtilsExtension;
import com.github.chhorz.openapi.common.test.util.resources.BaseClass;
import com.github.chhorz.openapi.common.util.LoggingUtils;
import com.github.chhorz.openapi.common.util.ResponseUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.Arrays;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ResponseUtilsTest {

	@RegisterExtension
	ProcessingUtilsExtension extension = new ProcessingUtilsExtension();

	private Elements elements;
	private Types types;

	private ResponseUtils responseUtils;

	@BeforeEach
	void setUpEach() {
		ParserProperties parserProperties = new ParserProperties();
		parserProperties.setLogLevel(LoggingUtils.DEBUG);

		LoggingUtils log = new LoggingUtils(parserProperties);

		this.elements = extension.getElements();
		this.types = extension.getTypes();

		responseUtils = new ResponseUtils(elements, types, log);
	}

	@Test
	void testMapTypeMirrorAsReference(){
		// given
		TypeMirror typeMirror = types.getArrayType(elements.getTypeElement(BaseClass.class.getCanonicalName()).asType());
		String[] produces = new String[]{"application/json"};

		// when
		Response response = responseUtils.mapTypeMirrorToResponse(typeMirror, produces);

		// then
		assertThat(response)
				.isNotNull();
		assertThat(response.getContent())
				.isNotNull()
				.isNotEmpty()
				.hasSize(1)
				.containsOnlyKeys("application/json");

		Object o = response.getContent().get("application/json").getSchema();

		assertThat(o).isInstanceOf(Schema.class);

		Schema schema = (Schema) o;

		assertThat(schema).hasFieldOrPropertyWithValue("type", Schema.Type.ARRAY);
	}

	@Test
	void testMapTypeMirrorAsSchemaWithReference(){
		// given
		TypeMirror typeMirror = elements.getTypeElement(BaseClass.class.getCanonicalName()).asType();
		String[] produces = null;

		// when
		Response response = responseUtils.mapTypeMirrorToResponse(typeMirror, produces);

		// then
		assertThat(response)
				.isNotNull();
		assertThat(response.getContent())
				.isNotNull()
				.isNotEmpty()
				.hasSize(1)
				.containsOnlyKeys("*/*");

		Object schema = response.getContent().get("*/*").getSchema();

		assertThat(schema).isInstanceOf(Reference.class);

		Reference reference = (Reference) schema;

		assertThat(reference).hasFieldOrPropertyWithValue("$ref", "#/components/schemas/BaseClass");
	}

	@Test
	void testResponsesInitialization(){
		// given
		ResponseTag r1 = new ResponseTag();
		r1.putValue("statusCode", "200");
		r1.putValue("responseType", BaseClass.class.getCanonicalName());
		ResponseTag r2 = new ResponseTag();
		r2.putValue("statusCode", "404");
		r2.putValue("responseType", BaseClass.class.getCanonicalName());

		JavaDoc javaDoc = new JavaDoc("", "", Arrays.asList(r1, r2));
		String[] produces = new String[]{"application/json"};

		// when
		Map<String, Response> responses = responseUtils.initializeFromJavadoc(javaDoc, produces);

		// then
		assertThat(responses)
				.isNotNull()
				.hasSize(2)
				.containsOnlyKeys("200", "404");
		assertThat(responses.get("200").getContent())
				.containsOnlyKeys("application/json");
		assertThat(responses.get("404").getContent())
				.containsOnlyKeys("application/json");
	}

	@Test
	void testResponsesEmptyOrNull(){
		// given
		ResponseTag r1 = new ResponseTag();
		r1.putValue("statusCode", "");
		r1.putValue("responseType", "");
		ResponseTag r2 = new ResponseTag();
		r2.putValue("statusCode", null);
		r2.putValue("responseType", null);

		JavaDoc javaDoc = new JavaDoc("", "", Arrays.asList(r1, r2));
		String[] produces = new String[]{"application/json"};

		// when
		Map<String, Response> responses = responseUtils.initializeFromJavadoc(javaDoc, produces);

		// then
		assertThat(responses)
				.isNotNull()
				.isEmpty();
	}

	@Test
	void testResponsesNullInput(){
		// given
		JavaDoc javaDoc = new JavaDoc("", "", null);
		String[] produces = new String[]{"application/json"};

		// when
		Map<String, Response> responses = responseUtils.initializeFromJavadoc(javaDoc, produces);

		// then
		assertThat(responses)
				.isNotNull()
				.isEmpty();
	}

	@Test
	void testResponsesNullInputs(){
		// given
		JavaDoc javaDoc = null;
		String[] produces = null;

		// when
		Map<String, Response> responses = responseUtils.initializeFromJavadoc(javaDoc, produces);

		// then
		assertThat(responses)
				.isNotNull()
				.isEmpty();
	}

	@Test
	void testResponsesNullProduces(){
		// given
		ResponseTag r1 = new ResponseTag();
		r1.putValue("statusCode", "200");
		r1.putValue("responseType", BaseClass.class.getCanonicalName());
		ResponseTag r2 = new ResponseTag();
		r2.putValue("statusCode", "404");
		r2.putValue("responseType", BaseClass.class.getCanonicalName());

		JavaDoc javaDoc = new JavaDoc("", "", Arrays.asList(r1, r2));
		String[] produces = null;

		// when
		Map<String, Response> responses = responseUtils.initializeFromJavadoc(javaDoc, produces);

		// then
		assertThat(responses)
				.isNotNull()
				.hasSize(2)
				.containsOnlyKeys("200", "404");
		assertThat(responses.get("200").getContent())
				.containsOnlyKeys("*/*");
		assertThat(responses.get("404").getContent())
				.containsOnlyKeys("*/*");
	}

	@Test
	@Disabled
	void testListResponseType(){
		// given
		ResponseTag r1 = new ResponseTag();
		r1.putValue("statusCode", "200");
		r1.putValue("responseType", String.format("java.util.List<%s>", BaseClass.class.getCanonicalName()));

		JavaDoc javaDoc = new JavaDoc("", "", Arrays.asList(r1));
		String[] produces = new String[]{"application/json"};

		// when
		Map<String, Response> responses = responseUtils.initializeFromJavadoc(javaDoc, produces);

		// then
		assertThat(responses)
				.isNotNull()
				.hasSize(1)
				.containsOnlyKeys("200");
		assertThat(responses.get("200").getContent())
				.containsOnlyKeys("application/json");
	}

	@Test
	void testArrayResponseType(){
		// given
		ResponseTag r1 = new ResponseTag();
		r1.putValue("statusCode", "200");
		r1.putValue("responseType", String.format("%s[]", BaseClass.class.getCanonicalName()));

		JavaDoc javaDoc = new JavaDoc("", "", Arrays.asList(r1));
		String[] produces = new String[]{"application/json"};

		// when
		Map<String, Response> responses = responseUtils.initializeFromJavadoc(javaDoc, produces);

		// then
		assertThat(responses)
				.isNotNull()
				.hasSize(1)
				.containsOnlyKeys("200");
		assertThat(responses.get("200").getContent())
				.containsOnlyKeys("application/json");
	}
}
