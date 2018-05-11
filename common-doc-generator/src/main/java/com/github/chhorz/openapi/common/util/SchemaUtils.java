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
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.chhorz.javadoc.JavaDoc;
import com.github.chhorz.javadoc.JavaDocParser;
import com.github.chhorz.javadoc.JavaDocParserBuilder;
import com.github.chhorz.openapi.common.domain.Schema;
import com.github.chhorz.openapi.common.domain.Schema.Format;
import com.github.chhorz.openapi.common.domain.Schema.Type;

public class SchemaUtils {

	private Elements elements;
	private Types types;

	private LoggingUtils log;

	private JavaDocParser parser;

	private TypeMirror object;
	private TypeMirror enumeration;

	public SchemaUtils(final Elements elements, final Types types, final LoggingUtils log) {
		this.elements = elements;
		this.types = types;
		this.log = log;

		parser = JavaDocParserBuilder.withBasicTags().build();

		object = elements.getTypeElement(Object.class.getCanonicalName()).asType();
		enumeration = elements.getTypeElement(Enum.class.getCanonicalName()).asType();
	}

	public Map<TypeMirror, Schema> mapTypeMirrorToSchema(final TypeMirror typeMirror) {
		Map<TypeMirror, Schema> schemaMap = new HashMap<>();

		log.info(String.format("Parsing %s", typeMirror.toString()));

		Schema schema = new Schema();

		if (typeMirror.getKind().isPrimitive()) {
			SimpleEntry<Type, Format> typeAndFormat = getPrimitiveTypeAndFormat(types, typeMirror);
			if (typeAndFormat != null) {
				schema.setType(typeAndFormat.getKey());
				schema.setFormat(typeAndFormat.getValue());
				// schema.setDescription(propertyDoc.getDescription());
			}
			schemaMap.put(typeMirror, schema);
		} else if (typeMirror.toString().startsWith("java.lang")) {
			JavaDoc javaDoc = parser.parse(elements.getDocComment(types.asElement(typeMirror)));
			schema.setDescription(javaDoc.getDescription());

			SimpleEntry<Type, Format> typeAndFormat = getJavaLangTypeAndFormat(elements, types, typeMirror);
			if (typeAndFormat != null) {
				schema.setType(typeAndFormat.getKey());
				schema.setFormat(typeAndFormat.getValue());
			}

			schemaMap.put(typeMirror, schema);
		} else if (typeMirror.toString().startsWith("java.math")) {
			JavaDoc javaDoc = parser.parse(elements.getDocComment(types.asElement(typeMirror)));
			schema.setDescription(javaDoc.getDescription());

			schema.setType(Type.NUMBER);
			schema.setFormat(Format.DOUBLE);

			schemaMap.put(typeMirror, schema);
		} else if (typeMirror.toString().startsWith("java.time")) {
			JavaDoc javaDoc = parser.parse(elements.getDocComment(types.asElement(typeMirror)));
			schema.setDescription(javaDoc.getDescription());

			SimpleEntry<Type, Format> typeAndFormat = getJavaTimeTypeAndFormat(elements, types, typeMirror);
			if (typeAndFormat != null) {
				schema.setType(typeAndFormat.getKey());
				schema.setFormat(typeAndFormat.getValue());
			}
			schemaMap.put(typeMirror, schema);
		} else if (isAssignableFrom(elements, types, typeMirror, List.class)){
			schema.setType(Type.ARRAY);

			TypeMirrorUtils utils = new TypeMirrorUtils(elements, types);
			TypeMirror type = utils.removeEnclosingType(typeMirror, List.class);
			Map<TypeMirror, Schema> propertySchemaMap = mapTypeMirrorToSchema(type);

			if (type.toString().startsWith("java.lang")) {
				SimpleEntry<Type, Format> typeAndFormat = getJavaLangTypeAndFormat(elements, types, type);
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
			schema.setType(Type.ARRAY);

			TypeMirrorUtils utils = new TypeMirrorUtils(elements, types);
			TypeMirror type = utils.removeEnclosingType(typeMirror, Set.class);
			Map<TypeMirror, Schema> propertySchemaMap = mapTypeMirrorToSchema(type);

			if (type.toString().startsWith("java.lang")) {
				SimpleEntry<Type, Format> typeAndFormat = getJavaLangTypeAndFormat(elements, types, type);
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

			if (element.getKind().equals(ElementKind.ENUM)) {
				schema.setType(Type.STRING);

				element.getEnclosedElements().stream().filter(VariableElement.class::isInstance).forEach(vElement -> {
					schema.addEnumValue(vElement.toString());
				});

			} else {
				schema.setType(Type.OBJECT);

				TypeMirror superType = element.asType();

				while (!types.isSameType(superType, object) && !types.isSameType(types.erasure(superType), enumeration)) {
					TypeElement typeElement = elements.getTypeElement(types.erasure(superType).toString());

					typeElement.getEnclosedElements()
							.stream()
							.filter(VariableElement.class::isInstance)
							.filter(this::isValidAttribute)
							.forEach(vElement -> {

								log.debug(String.format("Parsing attribute %s", vElement.toString()));

								JavaDoc propertyDoc = parser.parse(elements.getDocComment(vElement));

								// lets do some recursion
								Map<TypeMirror, Schema> propertySchemaMap = mapTypeMirrorToSchema(vElement.asType());
								// the schema is an object or enum -> we add it to the map
								propertySchemaMap.entrySet()
										.stream()
										.filter(entry -> Type.OBJECT.equals(entry.getValue().getType())
												|| Type.ENUM.equals(entry.getValue().getType()))
										.forEach(entry -> schemaMap.put(entry.getKey(), entry.getValue()));

								propertySchemaMap.entrySet()
										.stream()
										.filter(entry -> entry.getKey().equals(vElement.asType()))
										.forEach(entry -> {
											final String propertyName = getPropertyName(vElement);

											if (Type.OBJECT.equals(entry.getValue().getType())
													|| Type.ENUM.equals(entry.getValue().getType())) {
												schema.putProperty(propertyName,
														ReferenceUtils.createSchemaReference(vElement.asType()));
											} else {
												Schema propertySchema = entry.getValue();
												propertySchema.setDescription(propertyDoc.getDescription());
												schema.putProperty(propertyName, propertySchema);
											}
										});
							});

					superType = typeElement.getSuperclass();
				}

			}
			schemaMap.put(typeMirror, schema);
		}

		return schemaMap;
	}

	private boolean isValidAttribute(final Element element) {
		return element.getAnnotation(JsonIgnore.class) == null;
	}

	private String getPropertyName(final Element element) {
		JsonProperty jsonProperty = element.getAnnotation(JsonProperty.class);
		if (jsonProperty != null) {
			return jsonProperty.value();
		} else {
			return element.toString();
		}
	}

	private SimpleEntry<Type, Format> getPrimitiveTypeAndFormat(final Types types, final TypeMirror typeMirror) {
		switch (typeMirror.getKind()) {
			case BOOLEAN:
				return new SimpleEntry<>(Type.BOOLEAN, null);
			case BYTE:
				return new SimpleEntry<>(Type.STRING, Format.BYTE);
			case CHAR:
				return new SimpleEntry<>(Type.STRING, null);
			case INT:
				return new SimpleEntry<>(Type.INTEGER, Format.INT32);
			case LONG:
				return new SimpleEntry<>(Type.INTEGER, Format.INT64);
			case FLOAT:
				return new SimpleEntry<>(Type.NUMBER, Format.FLOAT);
			case DOUBLE:
				return new SimpleEntry<>(Type.NUMBER, Format.DOUBLE);
			case SHORT:
				return new SimpleEntry<>(Type.INTEGER, Format.INT32);
			default:
				return null;
		}
	}

	private SimpleEntry<Type, Format> getJavaLangTypeAndFormat(final Elements elements, final Types types,
			final TypeMirror typeMirror) {
		SimpleEntry<Type, Format> typeAndFormat = null;

		if (isTypeOf(elements, types, typeMirror, String.class)) {
			typeAndFormat = new SimpleEntry<>(Type.STRING, null);
		}

		try {
			typeAndFormat = getPrimitiveTypeAndFormat(types, types.unboxedType(typeMirror));
		} catch (IllegalArgumentException e) {
			// TODO: handle finally clause
		}

		return typeAndFormat;
	}

	private SimpleEntry<Type, Format> getJavaTimeTypeAndFormat(final Elements elements, final Types types,
			final TypeMirror typeMirror) {
		SimpleEntry<Type, Format> typeAndFormat = null;

		if (isTypeOf(elements, types, typeMirror, LocalDate.class)) {
			typeAndFormat = new SimpleEntry<>(Type.STRING, Format.DATE);
		} else if (isTypeOf(elements, types, typeMirror, LocalDateTime.class)) {
			typeAndFormat = new SimpleEntry<>(Type.STRING, Format.DATE_TIME);
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
