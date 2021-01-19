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
import com.github.chhorz.openapi.common.test.util.resources.*;
import com.github.chhorz.openapi.common.util.LogUtils;
import com.github.chhorz.openapi.common.util.SchemaUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.JRE;
import org.junit.jupiter.api.extension.RegisterExtension;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.time.ZonedDateTime;
import java.util.*;

import static java.util.Collections.singletonList;
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

		schemaUtils = new SchemaUtils(elements, types, parserProperties, new LogUtils(null, parserProperties));
	}

	@Test
	void parseNullPackages() {
		// given
		List<String> packages = null;

		// when
		Map<String, Schema> schemaMap = schemaUtils.parsePackages(packages);

		// then
		assertThat(schemaMap).isEmpty();
	}

	@Test
	void parseEmptyPackages() {
		// given
		List<String> packages = Collections.emptyList();

		// when
		Map<String, Schema> schemaMap = schemaUtils.parsePackages(packages);

		// then
		assertThat(schemaMap).isEmpty();
	}

	@Test
	void parsePackages() {
		// given
		List<String> packages = singletonList("com.github.chhorz.openapi.common.test.util.resources");

		// when
		Map<String, Schema> schemaMap = schemaUtils.parsePackages(packages);

		// then
		assertThat(schemaMap).hasSize(9);
	}

	@Test
	void primitiveTest() {
		// given
		PrimitiveType longType = types.getPrimitiveType(TypeKind.LONG);

		// when
		Map<TypeMirror, Schema> schemaMap = schemaUtils.createTypeMirrorSchemaMap(longType);

		// then
		assertThat(schemaMap)
			.hasSize(1)
			.containsKey(longType)
			.extracting(map -> map.get(longType))
			.extracting("type", "format")
			.contains(Type.INTEGER, Format.INT64);
	}

	@Test
	void primitiveArrayTest() {
		// given
		PrimitiveType charType = types.getPrimitiveType(TypeKind.CHAR);
		ArrayType charArrayType = types.getArrayType(charType);

		// when
		Map<TypeMirror, Schema> schemaMap = schemaUtils.createTypeMirrorSchemaMap(charArrayType);

		// then
		assertThat(schemaMap)
			.hasSize(2)
			.containsKeys(charType, charArrayType)
			.extracting(map -> map.get(charType))
			.extracting("type")
			.isEqualTo(Type.STRING);
	}

	@Test
	void objectTypeTest() {
		// given
		TypeMirror doubleType = elements.getTypeElement(Double.class.getCanonicalName()).asType();

		// when
		Map<TypeMirror, Schema> schemaMap = schemaUtils.createTypeMirrorSchemaMap(doubleType);

		// then
		assertThat(schemaMap).hasSize(1)
			.containsKey(doubleType)
			.extracting(map -> map.get(doubleType))
			.extracting("type", "format")
			.contains(Type.NUMBER, Format.DOUBLE);
	}

	@Test
	void objectArrayTypeTest() {
		// given
		TypeMirror doubleType = elements.getTypeElement(Double.class.getCanonicalName()).asType();
		ArrayType doubleArrayType = types.getArrayType(doubleType);

		// when
		Map<TypeMirror, Schema> schemaMap = schemaUtils.createTypeMirrorSchemaMap(doubleArrayType);

		// then
		assertThat(schemaMap)
			.hasSize(2)
			.containsKeys(doubleType, doubleArrayType)
			.extracting(map -> map.get(doubleType))
			.extracting("type", "format")
			.contains(Type.NUMBER, Format.DOUBLE);
	}

	@Test
	void objectListTypeTest() {
		// given
		TypeMirror doubleType = elements.getTypeElement(Double.class.getCanonicalName()).asType();
		TypeElement listTypeElement = elements.getTypeElement(List.class.getCanonicalName());
		TypeMirror doubleListType = types.getDeclaredType(listTypeElement, doubleType);

		// when
		Map<TypeMirror, Schema> schemaMap = schemaUtils.createTypeMirrorSchemaMap(doubleListType);

		// then
		assertThat(schemaMap)
			.hasSize(2)
			.containsKeys(doubleType, doubleListType)
			.extracting(map -> map.get(doubleType))
			.extracting("type", "format")
			.contains(Type.NUMBER, Format.DOUBLE);
	}

	@Test
	void objectTest() {
		// given
		TypeMirror objectType = elements.getTypeElement(Object.class.getCanonicalName()).asType();

		// when
		Map<TypeMirror, Schema> schemaMap = schemaUtils.createTypeMirrorSchemaMap(objectType);

		// then
		assertThat(schemaMap)
			.hasSize(1)
			.containsKey(objectType)
			.extracting(map -> map.get(objectType))
			.extracting("type", "format")
			.containsExactly(Type.OBJECT, null);
	}

	@Test
	void customObjectWithPrimitiveTypesTest() {
		// given
		TypeMirror classDType = elements.getTypeElement(ClassD.class.getCanonicalName()).asType();

		// when
		Map<TypeMirror, Schema> schemaMap = schemaUtils.createTypeMirrorSchemaMap(classDType);

		// then
		assertThat(schemaMap)
			.hasSize(1)
			.containsKeys(classDType);

		assertThat(schemaMap.get(classDType))
			.extracting("type", "format", "deprecated")
			.containsExactly(Type.OBJECT, null, false);

		assertThat(schemaMap.get(classDType).getProperties())
			.hasSize(2)
			.containsKeys("i", "l");

		assertThat(schemaMap.get(classDType).getProperties().values())
			.extracting("type", "format")
			.contains(tuple(Type.INTEGER, Format.INT32),
				tuple(Type.ARRAY, null));

		assertThat(schemaMap.get(classDType).getProperties().get("i"))
			.isInstanceOfSatisfying(Schema.class,
				schema -> assertThat(schema.getItems()).isNull());

		assertThat(schemaMap.get(classDType).getProperties().get("l"))
			.isInstanceOfSatisfying(Schema.class, schema -> assertThat(schema.getItems())
					.isInstanceOf(Schema.class)
					.hasFieldOrPropertyWithValue("type", Type.INTEGER)
					.hasFieldOrPropertyWithValue("format", Format.INT64));
	}

	@Test
	void customObjectTest() {
		// given
		TypeMirror classBType = elements.getTypeElement(ClassB.class.getCanonicalName()).asType();
		TypeMirror classCType = elements.getTypeElement(ClassC.class.getCanonicalName()).asType();

		// when
		Map<TypeMirror, Schema> schemaMap = schemaUtils.createTypeMirrorSchemaMap(classCType);

		// then
		assertThat(schemaMap)
				.hasSize(2)
				.containsKeys(classBType, classCType);

		assertThat(schemaMap.get(classBType))
			.extracting("type", "format", "deprecated")
			.containsExactly(Type.OBJECT, null, true);

		assertThat(schemaMap.get(classBType).getProperties())
			.hasSize(3)
			.containsKeys("int", "date", "time");

		assertThat(schemaMap.get(classBType).getProperties().values())
			.extracting("type", "format", "deprecated")
			.contains(tuple(Type.INTEGER, Format.INT32, false),
				tuple(Type.STRING, Format.DATE, false),
				tuple(Type.STRING, Format.DATE_TIME, true));

		assertThat(schemaMap.get(classCType))
			.extracting("type", "format", "deprecated")
			.containsExactly(Type.OBJECT, null, false);

		assertThat(schemaMap.get(classCType).getProperties())
				.hasSize(7)
				.containsKeys("l", "b", "f", "doubleArray", "list", "set", "baseProperty");

		assertThat(schemaMap.get(classCType).getProperties().values())
				.extracting("type", "format")
				.contains(tuple(Type.INTEGER, Format.INT64),
						tuple(Type.BOOLEAN, null),
						tuple(Type.NUMBER, Format.FLOAT),
						tuple(Type.ARRAY, null));

		assertThat(schemaMap.get(classCType).getProperties().get("doubleArray"))
				.isInstanceOfSatisfying(Schema.class,
						schema -> assertThat(schema.getItems()).isInstanceOf(Schema.class));

		assertThat(schemaMap.get(classCType).getProperties().get("list"))
				.isInstanceOfSatisfying(Schema.class,
						schema -> assertThat(schema.getItems()).isInstanceOf(Schema.class));

		assertThat(schemaMap.get(classCType).getProperties().get("set"))
				.isInstanceOfSatisfying(Schema.class,
						schema -> assertThat(schema.getItems()).isInstanceOf(Reference.class));
	}

	@Test
	@GitHubIssue("#21")
	void validationTest(){
		// given
		TypeMirror classEType = elements.getTypeElement(ClassE.class.getCanonicalName()).asType();

		// when
		Map<TypeMirror, Schema> schemaMap = schemaUtils.createTypeMirrorSchemaMap(classEType);

		// then
		assertThat(schemaMap)
			.hasSize(1)
			.containsKeys(classEType);

		assertThat(schemaMap.get(classEType))
			.hasFieldOrPropertyWithValue("required", singletonList("required"))
			.extracting("type", "format", "deprecated")
			.containsExactly(Type.OBJECT, null, false);

		assertThat(schemaMap.get(classEType).getProperties())
			.isNotNull()
			.hasSize(4)
			.containsKeys("min", "max", "pattern", "required");

		assertThat(schemaMap.get(classEType).getProperties().values())
			.isNotNull()
			.hasSize(4)
			.extracting("maximum", "minimum", "pattern")
			.contains(tuple(10L, null, null), // maximum
				tuple(null, 0L, null), // minimum
				tuple(null, null, "\\d+"), // pattern
				tuple(null, null, null)); // required
	}

	@Test
	void interfaceTest() {
		// given
		TypeMirror interfaceAType = elements.getTypeElement(InterfaceA.class.getCanonicalName()).asType();

		// when
		Map<TypeMirror, Schema> schemaMap = schemaUtils.createTypeMirrorSchemaMap(interfaceAType);

		// then
		assertThat(schemaMap)
			.hasSize(1)
			.containsKeys(interfaceAType);

		assertThat(schemaMap.get(interfaceAType))
			.extracting("type", "format", "deprecated")
			.containsExactly(Type.OBJECT, null, false);

		assertThat(schemaMap.get(interfaceAType).getProperties())
			.hasSize(1)
			.containsKeys("value");

		assertThat(schemaMap.get(interfaceAType).getProperties().values())
			.extracting("type", "format")
			.contains(tuple(Type.STRING, null));
	}

	@Test
	void interfaceExtendsTest() {
		// given
		TypeMirror interfaceCType = elements.getTypeElement(InterfaceC.class.getCanonicalName()).asType();

		// when
		Map<TypeMirror, Schema> schemaMap = schemaUtils.createTypeMirrorSchemaMap(interfaceCType);

		// then
		assertThat(schemaMap)
			.hasSize(1)
			.containsKeys(interfaceCType);

		assertThat(schemaMap.get(interfaceCType))
			.extracting("type", "format", "deprecated")
			.containsExactly(Type.OBJECT, null, false);

		assertThat(schemaMap.get(interfaceCType).getProperties())
			.hasSize(3)
			.containsKeys("value", "other", "test");

		assertThat(schemaMap.get(interfaceCType).getProperties().values())
			.extracting("type", "format")
			.contains(tuple(Type.STRING, null),
				tuple(Type.INTEGER, Format.INT32),
				tuple(Type.ARRAY, null));
	}

	@Test
	void enumTest() {
		// given
		TypeMirror enumAType = elements.getTypeElement(EnumA.class.getCanonicalName()).asType();

		// when
		Map<TypeMirror, Schema> schemaMap = schemaUtils.createTypeMirrorSchemaMap(enumAType);

		// then
		assertThat(schemaMap)
				.hasSize(1)
				.containsKey(enumAType)
				.extracting(map -> map.get(enumAType))
				.extracting("type", "format")
				.contains(Type.STRING);

		assertThat(schemaMap.get(enumAType).getEnumValues())
				.hasSize(3)
				.contains("A", "B", "XYZ");
	}

	@Test
	void optionalTest() {
		// given
		TypeMirror stringType = elements.getTypeElement(String.class.getCanonicalName()).asType();
		TypeMirror optionalType = types.getDeclaredType(elements.getTypeElement(Optional.class.getCanonicalName()), stringType);

		// when
		Map<TypeMirror, Schema> schemaMap = schemaUtils.createTypeMirrorSchemaMap(optionalType);

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
		Map<TypeMirror, Schema> schemaMap = schemaUtils.createTypeMirrorSchemaMap(dateType);

		// then
		assertThat(schemaMap)
			.hasSize(1)
			.containsKeys(dateType)
			.extracting(map -> map.get(dateType))
			.extracting("type", "format")
			.contains(Type.STRING, Format.DATE_TIME);
	}

	@Test
	@GitHubIssue("#101")
	void zonedDateTimeTest(){
		// given
		TypeMirror zonedDateTimeType = elements.getTypeElement(ZonedDateTime.class.getCanonicalName()).asType();

		// when
		Map<TypeMirror, Schema> schemaMap = schemaUtils.createTypeMirrorSchemaMap(zonedDateTimeType);

		// then
		assertThat(schemaMap)
			.hasSize(1)
			.containsKeys(zonedDateTimeType)
			.extracting(map -> map.get(zonedDateTimeType))
			.extracting("type", "format")
			.contains(Type.STRING, Format.DATE_TIME);
	}

}
