package com.github.chhorz.openapi.common.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import com.github.chhorz.javadoc.JavaDoc;
import com.github.chhorz.javadoc.JavaDocParser;
import com.github.chhorz.javadoc.JavaDocParserBuilder;
import com.github.chhorz.openapi.common.domain.Schema;

public class SchemaUtils {

	private JavaDocParser parser;

	public SchemaUtils() {
		parser = JavaDocParserBuilder.withBasicTags().build();
	}

	public Map<TypeMirror, Schema> mapTypeMirrorToSchema(final Elements elements, final Types types, final TypeMirror typeMirror) {
		Map<TypeMirror, Schema> schemaMap = new HashMap<>();

		System.out.println("TypeMirror: " + typeMirror);

		Schema schema = new Schema();

		if (typeMirror.getKind().isPrimitive()) {
			SimpleEntry<String, String> typeAndFormat = getPrimitiveTypeAndFormat(types, typeMirror);
			if (typeAndFormat != null) {
				schema.setType(typeAndFormat.getKey());
				schema.setFormat(typeAndFormat.getValue());
				// schema.setDescription(propertyDoc.getDescription());
			}
			schemaMap.put(typeMirror, schema);
		} else if (typeMirror.toString().startsWith("java.lang")) {
			JavaDoc javaDoc = parser.parse(elements.getDocComment(types.asElement(typeMirror)));
			schema.setDescription(javaDoc.getDescription());

			SimpleEntry<String, String> typeAndFormat = getJavaLangTypeAndFormat(elements, types, typeMirror);
			if (typeAndFormat != null) {
				schema.setType(typeAndFormat.getKey());
				schema.setFormat(typeAndFormat.getValue());
			}

			schemaMap.put(typeMirror, schema);
		} else if (typeMirror.toString().startsWith("java.math")) {
			JavaDoc javaDoc = parser.parse(elements.getDocComment(types.asElement(typeMirror)));

			schema.setType("number");
			schema.setFormat("double");
			schema.setDescription(javaDoc.getDescription());

			schemaMap.put(typeMirror, schema);
		} else if (typeMirror.toString().startsWith("java.time")) {
			JavaDoc javaDoc = parser.parse(elements.getDocComment(types.asElement(typeMirror)));

			SimpleEntry<String, String> typeAndFormat = getJavaTimeTypeAndFormat(elements, types, typeMirror);
			if (typeAndFormat != null) {
				schema.setType(typeAndFormat.getKey());
				schema.setFormat(typeAndFormat.getValue());
				schema.setDescription(javaDoc.getDescription());
			}
			schemaMap.put(typeMirror, schema);
		} else if (isAssignableFrom(elements, types, typeMirror, List.class)){
			schema.setType("array");

			TypeMirrorUtils utils = new TypeMirrorUtils(elements, types);
			TypeMirror type = utils.removeEnclosingType(typeMirror, List.class);
			Map<TypeMirror, Schema> propertySchemaMap = mapTypeMirrorToSchema(elements, types, type);

			if (type.toString().startsWith("java.lang")) {
				SimpleEntry<String, String> typeAndFormat = getJavaLangTypeAndFormat(elements, types, type);
				Schema typeSchema = new Schema();
				if (typeAndFormat != null) {
					typeSchema.setType(typeAndFormat.getKey());
					typeSchema.setFormat(typeAndFormat.getValue());
				}
				schema.setItems(typeSchema);
			} else {
				schema.setItems(ReferenceUtils.createSchemaReference(type));
			}

			schemaMap.putAll(propertySchemaMap);

			schemaMap.put(typeMirror, schema);
		} else if (isAssignableFrom(elements, types, typeMirror, Set.class)){
			schema.setType("array");

			TypeMirrorUtils utils = new TypeMirrorUtils(elements, types);
			TypeMirror type = utils.removeEnclosingType(typeMirror, Set.class);
			Map<TypeMirror, Schema> propertySchemaMap = mapTypeMirrorToSchema(elements, types, type);

			if (type.toString().startsWith("java.lang")) {
				SimpleEntry<String, String> typeAndFormat = getJavaLangTypeAndFormat(elements, types, type);
				Schema typeSchema = new Schema();
				if (typeAndFormat != null) {
					typeSchema.setType(typeAndFormat.getKey());
					typeSchema.setFormat(typeAndFormat.getValue());
				}
				schema.setItems(typeSchema);
			} else {
				schema.setItems(ReferenceUtils.createSchemaReference(type));
			}

			schemaMap.putAll(propertySchemaMap);

			schemaMap.put(typeMirror, schema);
		} else {
			Element element = elements.getTypeElement(typeMirror.toString());

			JavaDoc javaDoc = parser.parse(elements.getDocComment(element));
			schema.setDescription(javaDoc.getDescription());

			String type;
			if (element.getKind().equals(ElementKind.ENUM)) {
				type = "string";

				schema.setType(type);

				element.getEnclosedElements().stream().filter(VariableElement.class::isInstance).forEach(vElement -> {
					schema.addEnumValue(vElement.toString());
				});

			} else {
				type = "object";

				schema.setType(type);


				element.getEnclosedElements().stream().filter(VariableElement.class::isInstance).forEach(vElement -> {
					System.out.println(vElement.toString());

					JavaDoc propertyDoc = parser.parse(elements.getDocComment(vElement));

					// lets do some recursion
					Map<TypeMirror, Schema> propertySchemaMap = mapTypeMirrorToSchema(elements, types, vElement.asType());
					// the schema is an object or enum -> we add it to the map
					propertySchemaMap.entrySet()
							.stream()
							.filter(entry -> "object".equals(entry.getValue().getType()) || "enum".equals(entry.getValue().getType()))
							.forEach(entry -> schemaMap.put(entry.getKey(), entry.getValue()));

					propertySchemaMap.entrySet()
							.stream()
							.filter(entry -> entry.getKey().equals(vElement.asType()))
							.peek(entry -> System.out.println("Key: " + entry.getKey().toString()))
							.forEach(entry -> {
								if ("object".equals(entry.getValue().getType()) || "enum".equals(entry.getValue().getType())) {
									schema.putProperty(vElement.toString(), ReferenceUtils.createSchemaReference(vElement.asType()));
								} else {
									Schema propertySchema = entry.getValue();
									propertySchema.setDescription(propertyDoc.getDescription());
									schema.putProperty(vElement.toString(), propertySchema);
								}
							});
				});
			}
			schemaMap.put(typeMirror, schema);
		}

		return schemaMap;
	}

	private SimpleEntry<String, String> getPrimitiveTypeAndFormat(final Types types, final TypeMirror typeMirror) {
		switch (typeMirror.getKind()) {
			case BOOLEAN:
				return new SimpleEntry<>("boolean", null);
			case BYTE:
				return new SimpleEntry<>("string", "byte");
			case CHAR:
				return new SimpleEntry<>("string", null);
			case INT:
				return new SimpleEntry<>("integer", "int32");
			case LONG:
				return new SimpleEntry<>("integer", "int64");
			case FLOAT:
				return new SimpleEntry<>("number", "float");
			case DOUBLE:
				return new SimpleEntry<>("number", "double");
			case SHORT:
				return new SimpleEntry<>("integer", "int32");
			default:
				return null;
		}
	}

	private SimpleEntry<String, String> getJavaLangTypeAndFormat(final Elements elements, final Types types,
			final TypeMirror typeMirror) {
		SimpleEntry<String, String> typeAndFormat = null;

		if (isTypeOf(elements, types, typeMirror, String.class)) {
			typeAndFormat = new SimpleEntry<>("string", null);
		}

		try {
			typeAndFormat = getPrimitiveTypeAndFormat(types, types.unboxedType(typeMirror));
		} catch (IllegalArgumentException e) {
			// TODO: handle finally clause
		}

		return typeAndFormat;
	}

	private SimpleEntry<String, String> getJavaTimeTypeAndFormat(final Elements elements, final Types types,
			final TypeMirror typeMirror) {
		SimpleEntry<String, String> typeAndFormat = null;

		if (isTypeOf(elements, types, typeMirror, LocalDate.class)) {
			typeAndFormat = new SimpleEntry<>("string", "date");
		} else if (isTypeOf(elements, types, typeMirror, LocalDateTime.class)) {
			typeAndFormat = new SimpleEntry<>("string", "date-time");
		}

		return typeAndFormat;
	}

	private boolean isTypeOf(final Elements elements, final Types types, final TypeMirror typeMirror, final Class<?> clazz) {
		return types.isSameType(typeMirror, elements.getTypeElement(clazz.getCanonicalName()).asType());
	}

	private boolean isAssignableFrom(final Elements elements, final Types types, final TypeMirror typeMirror, final Class<?> clazz) {
		return types.isAssignable(types.erasure(typeMirror), elements.getTypeElement(clazz.getCanonicalName()).asType());
	}

}
