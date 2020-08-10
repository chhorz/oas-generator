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

import com.github.chhorz.openapi.common.domain.Reference;
import com.github.chhorz.openapi.common.domain.Schema;
import com.github.chhorz.openapi.common.domain.Schema.Format;
import com.github.chhorz.openapi.common.domain.Schema.Type;
import com.github.chhorz.openapi.common.properties.domain.ParserProperties;
import com.github.chhorz.openapi.common.test.extension.ProcessingUtilsExtension;
import com.github.chhorz.openapi.common.test.github.GitHubIssue;
import com.github.chhorz.openapi.common.test.util.resources.Other;
import com.github.chhorz.openapi.common.test.util.resources.TestClass;
import com.github.chhorz.openapi.common.test.util.resources.TestEnum;
import com.github.chhorz.openapi.common.util.LogUtils;
import com.github.chhorz.openapi.common.util.SchemaUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.JRE;
import org.junit.jupiter.api.extension.RegisterExtension;

import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@EnabledForJreRange(max = JRE.JAVA_8)
class SchemaUtilsTest {

	@RegisterExtension
	ProcessingUtilsExtension extension = new ProcessingUtilsExtension();

	private SchemaUtils schemaUtils;

	private Elements elements;
	private Types types;

	@BeforeEach
	void setUpEach() {
		ParserProperties parserProperties = new ParserProperties();
		parserProperties.setLogLevel(LogUtils.DEBUG);

		elements = extension.getElements();
		types = extension.getTypes();

		schemaUtils = new SchemaUtils(elements, types, new LogUtils(null, parserProperties));
	}

	@Test
	void parseNullPackages() {
		// given
		List<String> packages = null;

		// when
		Map<TypeMirror, Schema> schemaMap = schemaUtils.parsePackages(packages);

		// then
		assertThat(schemaMap).isEmpty();
	}

	@Test
	void parseEmptyPackages() {
		// given
		List<String> packages = Collections.emptyList();

		// when
		Map<TypeMirror, Schema> schemaMap = schemaUtils.parsePackages(packages);

		// then
		assertThat(schemaMap).isEmpty();
	}

	@Test
	void parsePackages() {
		// given
		List<String> packages = Collections.singletonList("com.github.chhorz.openapi.common.test.util.resources");

		// when
		Map<TypeMirror, Schema> schemaMap = schemaUtils.parsePackages(packages);

		// then
		assertThat(schemaMap).hasSize(4);
	}

	@Test
	void primitiveTest() {
		// given
		PrimitiveType longType = types.getPrimitiveType(TypeKind.LONG);

		// when
		Map<TypeMirror, Schema> schemaMap = schemaUtils.mapTypeMirrorToSchema(longType);

		// then
		assertThat(schemaMap).hasSize(1)
				.containsKey(longType)
				.extracting(map -> map.get(longType))
				.extracting("type", "format")
				.contains(Type.INTEGER, Format.INT64);
	}

	@Test
	void objectTypeTest() {
		// given
		TypeMirror doubleType = elements.getTypeElement(Double.class.getCanonicalName()).asType();

		// when
		Map<TypeMirror, Schema> schemaMap = schemaUtils.mapTypeMirrorToSchema(doubleType);

		// then
		assertThat(schemaMap).hasSize(1)
				.containsKey(doubleType)
				.extracting(map -> map.get(doubleType))
				.extracting("type", "format")
				.contains(Type.NUMBER, Format.DOUBLE);
	}

	@Test
	void objectTest() {
		// given
		TypeMirror objectType = elements.getTypeElement(Object.class.getCanonicalName()).asType();

		// when
		Map<TypeMirror, Schema> schemaMap = schemaUtils.mapTypeMirrorToSchema(objectType);

		// then
		assertThat(schemaMap).hasSize(1)
			.containsKey(objectType)
			.extracting(map -> map.get(objectType))
			.extracting("type", "format")
			.containsExactly(Type.OBJECT, null);
	}

	@Test
	void customObjectTest() {
		// given
		TypeMirror test = elements.getTypeElement(TestClass.class.getCanonicalName()).asType();
		TypeMirror other = elements.getTypeElement(Other.class.getCanonicalName()).asType();

		// when
		Map<TypeMirror, Schema> schemaMap = schemaUtils.mapTypeMirrorToSchema(test);

		System.out.println(schemaMap);

		// then
		assertThat(schemaMap)
				.hasSize(2)
				.containsKeys(test, other);

		assertThat(schemaMap.get(test))
				.extracting("type", "format", "deprecated")
				.containsExactly(Type.OBJECT, null, false);

		assertThat(schemaMap.get(other))
				.extracting("type", "format", "deprecated")
				.containsExactly(Type.OBJECT, null, true);

		assertThat(schemaMap.get(test).getProperties())
				.hasSize(7)
				.containsKeys("l", "b", "f", "doubleArray", "list", "set", "baseProperty");

		assertThat(schemaMap.get(test).getProperties().values())
				.extracting("type", "format")
				.contains(tuple(Type.INTEGER, Format.INT64),
						tuple(Type.BOOLEAN, null),
						tuple(Type.NUMBER, Format.FLOAT),
						tuple(Type.ARRAY, null));

		assertThat(schemaMap.get(test).getProperties().get("doubleArray"))
				.isInstanceOfSatisfying(Schema.class,
						schema -> assertThat(schema.getItems()).isInstanceOf(Schema.class));

		assertThat(schemaMap.get(test).getProperties().get("list"))
				.isInstanceOfSatisfying(Schema.class,
						schema -> assertThat(schema.getItems()).isInstanceOf(Schema.class));

		assertThat(schemaMap.get(test).getProperties().get("set"))
				.isInstanceOfSatisfying(Schema.class,
						schema -> assertThat(schema.getItems()).isInstanceOf(Reference.class));

		assertThat(schemaMap.get(other).getProperties())
				.hasSize(3)
				.containsKeys("int", "date", "time");

		assertThat(schemaMap.get(other).getProperties().values())
				.extracting("type", "format", "deprecated")
				.contains(tuple(Type.INTEGER, Format.INT32, false),
						tuple(Type.STRING, Format.DATE, false),
						tuple(Type.STRING, Format.DATE_TIME, true));
	}

	@Test
	void enumTest() {
		// given
		TypeMirror test = elements.getTypeElement(TestEnum.class.getCanonicalName()).asType();

		// when
		Map<TypeMirror, Schema> schemaMap = schemaUtils.mapTypeMirrorToSchema(test);

		// then
		assertThat(schemaMap)
				.hasSize(1)
				.containsKey(test)
				.extracting(map -> map.get(test))
				.extracting("type", "format")
				.contains(Type.STRING);

		assertThat(schemaMap.get(test).getEnumValues())
				.hasSize(3)
				.contains("A", "B", "XYZ");
	}

	@Test
	void optionalTest() {
		// given
		TypeMirror stringType = elements.getTypeElement(String.class.getCanonicalName()).asType();
		TypeMirror optionalType = types.getDeclaredType(elements.getTypeElement(Optional.class.getCanonicalName()), stringType);

		// when
		Map<TypeMirror, Schema> schemaMap = schemaUtils.mapTypeMirrorToSchema(optionalType);

		// then
		assertThat(schemaMap)
			.hasSize(2)
			.containsKeys(stringType, optionalType)
			.extracting(map -> map.get(stringType))
			.extracting("type", "format")
			.contains(Type.STRING);
	}

	@Test
	@GitHubIssue("#27")
	void dateTest(){
		// given
		TypeMirror dateType = elements.getTypeElement(Date.class.getCanonicalName()).asType();

		// when
		Map<TypeMirror, Schema> schemaMap = schemaUtils.mapTypeMirrorToSchema(dateType);

		// then
		assertThat(schemaMap)
			.hasSize(1)
			.containsKeys(dateType)
			.extracting(map -> map.get(dateType))
			.extracting("type", "format")
			.contains(Type.STRING, Format.DATE_TIME);
	}

}
