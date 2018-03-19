package com.github.chhorz.openapi.common.test.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.util.Map;

import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.github.chhorz.openapi.common.domain.Schema;
import com.github.chhorz.openapi.common.util.SchemaUtils;
import com.google.testing.compile.CompilationRule;

public class SchemaUtilsTest {

	@Rule
	public CompilationRule compilationRule = new CompilationRule();

	private Elements elements;
	private Types types;

	private SchemaUtils schemaUtils;

	@Before
	public void setUp() {
		elements = compilationRule.getElements();
		types = compilationRule.getTypes();

		schemaUtils = new SchemaUtils();
	}

	@Test
	public void primitiveTest() {
		// given
		PrimitiveType longType = types.getPrimitiveType(TypeKind.LONG);

		// when
		Map<String, Schema> schemaMap = schemaUtils.mapTypeMirrorToSchema(elements, types, longType);

		// then
		assertThat(schemaMap)
				.hasSize(1)
				.containsKey("long")
				.extracting(map -> map.get("long"))
				.extracting("type", "format")
				.contains(tuple("integer", "int64"));
	}

	@Test
	public void objectTypeTest() {
		// given
		TypeMirror doubleType = elements.getTypeElement("java.lang.Double").asType();

		// when
		Map<String, Schema> schemaMap = schemaUtils.mapTypeMirrorToSchema(elements, types, doubleType);

		// then
		assertThat(schemaMap)
				.hasSize(1)
				.containsKey("Double")
				.extracting(map -> map.get("Double"))
				.extracting("type", "format")
				.contains(tuple("number", "double"));
	}

	@Test
	public void objectTest() {
		// given
		TypeMirror test = elements.getTypeElement("com.github.chhorz.openapi.common.test.util.resources.Test").asType();

		// when
		Map<String, Schema> schemaMap = schemaUtils.mapTypeMirrorToSchema(elements, types, test);

		// then
		assertThat(schemaMap)
				.hasSize(1)
				.containsKey("Test")
				.extracting(map -> map.get("Test"))
				.extracting("type", "format")
				.contains(tuple("object", null));

		assertThat(schemaMap.get("Test").getProperties())
				.hasSize(3)
				.containsKeys("l", "b", "f");
	}

}
