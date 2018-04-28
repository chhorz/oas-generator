package com.github.chhorz.openapi.common.test.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.util.Map;

import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.github.chhorz.openapi.common.domain.Schema;
import com.github.chhorz.openapi.common.domain.Schema.Format;
import com.github.chhorz.openapi.common.domain.Schema.Type;
import com.github.chhorz.openapi.common.test.extension.ProcessingUtilsExtension;
import com.github.chhorz.openapi.common.util.SchemaUtils;

public class SchemaUtilsTest {

	@RegisterExtension
	ProcessingUtilsExtension extension = new ProcessingUtilsExtension();

	private Elements elements;
	private Types types;

	private SchemaUtils schemaUtils;

	@BeforeEach
	void setUpEach() {
		elements = extension.getElements();
		types = extension.getTypes();

		schemaUtils = new SchemaUtils();
	}

	@Test
	void primitiveTest() {
		// given
		PrimitiveType longType = types.getPrimitiveType(TypeKind.LONG);

		// when
		Map<TypeMirror, Schema> schemaMap = schemaUtils.mapTypeMirrorToSchema(elements, types, longType);

		// then
		assertThat(schemaMap).hasSize(1)
				.containsKey(longType)
				.extracting(map -> map.get(longType))
				.extracting("type", "format")
				.contains(tuple(Type.INTEGER, Format.INT64));
	}

	@Test
	void objectTypeTest() {
		// given
		TypeMirror doubleType = elements.getTypeElement("java.lang.Double").asType();

		// when
		Map<TypeMirror, Schema> schemaMap = schemaUtils.mapTypeMirrorToSchema(elements, types, doubleType);

		// then
		assertThat(schemaMap).hasSize(1)
				.containsKey(doubleType)
				.extracting(map -> map.get(doubleType))
				.extracting("type", "format")
				.contains(tuple(Type.NUMBER, Format.DOUBLE));
	}

	@Test
	void objectTest() {
		// given
		TypeMirror test = elements.getTypeElement("com.github.chhorz.openapi.common.test.util.resources.Test").asType();

		// when
		Map<TypeMirror, Schema> schemaMap = schemaUtils.mapTypeMirrorToSchema(elements, types, test);

		// then
		assertThat(schemaMap)
				.hasSize(1)
				.containsKey(test)
				.extracting(map -> map.get(test))
				.extracting("type", "format")
				.contains(tuple(Type.OBJECT, null));

		assertThat(schemaMap.get(test).getProperties())
				.hasSize(3)
				.containsKeys("l", "b", "f");
	}

	@Test
	void enumTest() {
		// given
		TypeMirror test = elements.getTypeElement("com.github.chhorz.openapi.common.test.util.resources.TestEnum").asType();

		// when
		Map<TypeMirror, Schema> schemaMap = schemaUtils.mapTypeMirrorToSchema(elements, types, test);

		// then
		assertThat(schemaMap)
				.hasSize(1)
				.containsKey(test)
				.extracting(map -> map.get(test))
				.extracting("type", "format")
				.contains(tuple(Type.STRING, null));

		assertThat(schemaMap.get(test).getEnumValues())
				.hasSize(3)
				.contains("A", "B", "XYZ");
	}

}
