package com.github.chhorz.openapi.common.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;

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

	public Map<String, Schema> mapTypeMirrorToSchema(final Elements elements, final Types types, final TypeMirror typeMirror) {
		Map<String, Schema> schemaMap = new HashMap<>();

		System.out.println("TypeMirror: " + typeMirror);

//		System.out.println(elements.getDocComment(elements.getTypeElement(typeMirror.toString())));
//		System.out.println(elements.getDocComment(typeElement));


		Schema schema = new Schema();
		schema.setDescription("");

		if (typeMirror.getKind().isPrimitive()) {
			SimpleEntry<String, String> typeAndFormat = getPrimitiveTypeAndFormat(types, typeMirror);
			if (typeAndFormat != null) {
				schema.setType(typeAndFormat.getKey());
				schema.setFormat(typeAndFormat.getValue());
//				schema.setDescription(propertyDoc.getDescription());
			}
			schemaMap.put(typeMirror.toString(), schema);
		} else if (typeMirror.toString().startsWith("java.lang")) {
			SimpleEntry<String, String> typeAndFormat = getJavaLangTypeAndFormat(elements, types, typeMirror);
			if (typeAndFormat != null) {
				schema.setType(typeAndFormat.getKey());
				schema.setFormat(typeAndFormat.getValue());
//				schema.setDescription(propertyDoc.getDescription());
			}
			schemaMap.put(typeMirror.toString().substring(typeMirror.toString().lastIndexOf('.') + 1), schema);
		} else if (typeMirror.toString().startsWith("java.time")) {
			SimpleEntry<String, String> typeAndFormat = getJavaTimeTypeAndFormat(elements, types, typeMirror);
			if (typeAndFormat != null) {
				schema.setType(typeAndFormat.getKey());
				schema.setFormat(typeAndFormat.getValue());
//				schema.setDescription(propertyDoc.getDescription());
			}
			schemaMap.put(typeMirror.toString().substring(typeMirror.toString().lastIndexOf('.') + 1), schema);
		} else {
			Element element = elements.getTypeElement(typeMirror.toString());

			JavaDocParser parser = JavaDocParserBuilder.withBasicTags().build();
			JavaDoc javaDoc = parser.parse(elements.getDocComment(types.asElement(typeMirror)));

			String type;
			if (element.getKind().equals(ElementKind.ENUM)) {
				type = "enum";
			} else {
				type = "object";
			}
			schema.setType(type);
			schema.setDescription(javaDoc.getDescription());

			element.getEnclosedElements().stream().filter(VariableElement.class::isInstance).forEach(vElement -> {
				System.out.println(vElement.toString());

				// lets do some recursion
				Map<String, Schema> propertySchemaMap = mapTypeMirrorToSchema(elements, types, vElement.asType());
				propertySchemaMap.entrySet()
						.stream()
						.filter(entry -> "object".equals(entry.getValue().getType()) || "enum".equals(entry.getValue().getType()))
						.forEach(entry -> schemaMap.put(entry.getKey(), entry.getValue()));

				propertySchemaMap.entrySet()
						.stream()
						.filter(entry -> !"object".equals(entry.getValue().getType()) && !"enum".equals(entry.getValue().getType()))
						.forEach(entry -> schema.putProperty(vElement.toString(), entry.getValue()));

				propertySchemaMap.entrySet()
						.stream()
						.filter(entry -> "object".equals(entry.getValue().getType()) || "enum".equals(entry.getValue().getType()))
						.forEach(entry -> schema.putProperty(vElement.toString(), ReferenceUtils.createSchemaReference(vElement.asType())));
			});

			schemaMap.put(typeMirror.toString().substring(typeMirror.toString().lastIndexOf('.') + 1), schema);
		}

		// System.out.println("SchemaMap: " + schemaMap);

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

}
