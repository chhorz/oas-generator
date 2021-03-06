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
package com.github.chhorz.openapi.common.test.util;

import com.github.chhorz.javadoc.JavaDoc;
import com.github.chhorz.openapi.common.annotation.OpenAPI;
import com.github.chhorz.openapi.common.annotation.OpenAPIResponse;
import com.github.chhorz.openapi.common.domain.Reference;
import com.github.chhorz.openapi.common.domain.Response;
import com.github.chhorz.openapi.common.domain.Schema;
import com.github.chhorz.openapi.common.javadoc.ResponseTag;
import com.github.chhorz.openapi.common.properties.domain.ParserProperties;
import com.github.chhorz.openapi.common.test.extension.ProcessingUtilsExtension;
import com.github.chhorz.openapi.common.test.util.resources.ClassA;
import com.github.chhorz.openapi.common.util.LogUtils;
import com.github.chhorz.openapi.common.util.ResponseUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.JRE;
import org.junit.jupiter.api.extension.RegisterExtension;

import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

@EnabledForJreRange(max = JRE.JAVA_8)
class ResponseUtilsTest {

	@RegisterExtension
	ProcessingUtilsExtension extension = new ProcessingUtilsExtension();

	private Elements elements;
	private Types types;

	private ResponseUtils responseUtils;

	@BeforeEach
	void setUpEach() {
		ParserProperties parserProperties = new ParserProperties();
		parserProperties.setLogLevel(LogUtils.DEBUG);

		LogUtils logUtils = new LogUtils(extension.getMessager(), parserProperties);

		this.elements = extension.getElements();
		this.types = extension.getTypes();

		responseUtils = new ResponseUtils(elements, types, parserProperties, logUtils);
	}

	@Test
	void testMapTypeMirrorAsReference(){
		// given
		TypeMirror classAType = types.getArrayType(elements.getTypeElement(ClassA.class.getCanonicalName()).asType());
		String[] produces = new String[]{"application/json"};
		String description = "";

		// when
		Response response = responseUtils.fromTypeMirror(classAType, produces, description);

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
		TypeMirror classAType = elements.getTypeElement(ClassA.class.getCanonicalName()).asType();
		String[] produces = null;
		String description = "";

		// when
		Response response = responseUtils.fromTypeMirror(classAType, produces, description);

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

		assertThat(reference).hasFieldOrPropertyWithValue("$ref", "#/components/schemas/ClassA");
	}

	@Test
	void testResponsesInitialization(){
		// given
		ResponseTag r1 = new ResponseTag();
		r1.putValue("statusCode", "200");
		r1.putValue("responseType", ClassA.class.getCanonicalName());
		ResponseTag r2 = new ResponseTag();
		r2.putValue("statusCode", "404");
		r2.putValue("responseType", ClassA.class.getCanonicalName());

		JavaDoc javaDoc = new JavaDoc("", "", Arrays.asList(r1, r2));
		String[] produces = new String[]{"application/json"};
		String description = "";

		OpenAPI openAPI = createOpenAPI();

		// when
		Map<String, Response> responses = responseUtils.initializeFromJavadoc(javaDoc, openAPI, produces, description, Collections.emptyMap());

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
		String description = "";

		OpenAPI openAPI = createOpenAPI();

		// when
		Map<String, Response> responses = responseUtils.initializeFromJavadoc(javaDoc, openAPI, produces, description, Collections.emptyMap());

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
		String description = "";

		OpenAPI openAPI = createOpenAPI();

		// when
		Map<String, Response> responses = responseUtils.initializeFromJavadoc(javaDoc, openAPI, produces, description, Collections.emptyMap());

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
		String description = null;

		OpenAPI openAPI = createOpenAPI();

		// when
		Map<String, Response> responses = responseUtils.initializeFromJavadoc(javaDoc, openAPI, produces, description, Collections.emptyMap());

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
		r1.putValue("responseType", ClassA.class.getCanonicalName());
		r1.putValue("description", "The happy case.");
		ResponseTag r2 = new ResponseTag();
		r2.putValue("statusCode", "404");
		r2.putValue("responseType", "ClassA");
		r2.putValue("description", "The sad case.");

		JavaDoc javaDoc = new JavaDoc("", "", Arrays.asList(r1, r2));
		String[] produces = null;
		String description = "";

		Map<TypeMirror, Schema> typeMap = new HashMap<>();
		typeMap.put(elements.getTypeElement(ClassA.class.getCanonicalName()).asType(), null);

		OpenAPI openAPI = createOpenAPI();

		// when
		Map<String, Response> responses = responseUtils.initializeFromJavadoc(javaDoc, openAPI, produces, description, typeMap);

		// then
		assertThat(responses)
				.isNotNull()
				.hasSize(2)
				.containsOnlyKeys("200", "404");
		assertThat(responses.get("200"))
				.hasFieldOrPropertyWithValue("description", "The happy case.");
		assertThat(responses.get("200").getContent())
				.containsOnlyKeys("*/*");
		assertThat(responses.get("200").getContent().get("*/*").getSchema())
				.isNotNull()
				.hasToString("Reference [$ref=#/components/schemas/ClassA]");
		assertThat(responses.get("404"))
				.hasFieldOrPropertyWithValue("description", "The sad case.");
		assertThat(responses.get("404").getContent())
				.containsOnlyKeys("*/*");
		assertThat(responses.get("404").getContent().get("*/*").getSchema())
				.isNotNull()
				.hasToString("Reference [$ref=#/components/schemas/ClassA]");
	}

	@Test
	@Disabled
	void testListResponseType(){
		// given
		ResponseTag r1 = new ResponseTag();
		r1.putValue("statusCode", "200");
		r1.putValue("responseType", String.format("java.util.List<%s>", ClassA.class.getCanonicalName()));

		JavaDoc javaDoc = new JavaDoc("", "", singletonList(r1));
		String[] produces = new String[]{"application/json"};
		String description = "";

		OpenAPI openAPI = createOpenAPI();

		// when
		Map<String, Response> responses = responseUtils.initializeFromJavadoc(javaDoc, openAPI, produces, description, Collections.emptyMap());

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
		r1.putValue("responseType", String.format("%s[]", ClassA.class.getCanonicalName()));

		JavaDoc javaDoc = new JavaDoc("", "", singletonList(r1));
		String[] produces = new String[]{"application/json"};
		String description = "";

		OpenAPI openAPI = createOpenAPI();

		// when
		Map<String, Response> responses = responseUtils.initializeFromJavadoc(javaDoc, openAPI, produces, description, Collections.emptyMap());

		// then
		assertThat(responses)
				.isNotNull()
				.hasSize(1)
				.containsOnlyKeys("200");
		assertThat(responses.get("200").getContent())
				.containsOnlyKeys("application/json");
	}

	private OpenAPI createOpenAPI() {
		return new OpenAPI() {
			@Override
			public Class<? extends Annotation> annotationType() {
				return null;
			}

			@Override
			public String[] tags() {
				return new String[0];
			}

			@Override
			public String[] securitySchemes() {
				return new String[0];
			}

			@Override
			public OpenAPIResponse[] responses() {
				return new OpenAPIResponse[0];
			}
		};
	}

}
