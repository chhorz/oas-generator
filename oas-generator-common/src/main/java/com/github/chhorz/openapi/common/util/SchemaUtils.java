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
package com.github.chhorz.openapi.common.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.chhorz.javadoc.JavaDoc;
import com.github.chhorz.javadoc.JavaDocParser;
import com.github.chhorz.javadoc.JavaDocParserBuilder;
import com.github.chhorz.javadoc.OutputType;
import com.github.chhorz.openapi.common.domain.MediaType;
import com.github.chhorz.openapi.common.domain.Reference;
import com.github.chhorz.openapi.common.domain.Schema;
import com.github.chhorz.openapi.common.domain.Schema.Format;
import com.github.chhorz.openapi.common.domain.Schema.Type;

import javax.lang.model.element.*;
import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.function.Function;

public class SchemaUtils {

	private final Elements elements;
	private final Types types;

	private final LogUtils log;
	private final TypeMirrorUtils typeMirrorUtils;

	private final JavaDocParser parser;

	private final TypeMirror object;
	private final TypeMirror enumeration;

	private final PackageElement javaLangPackage;
	private final PackageElement javaMathPackage;
	private final PackageElement javaTimePackage;

	public SchemaUtils(final Elements elements, final Types types, final LogUtils log) {
		this.elements = elements;
		this.types = types;
		this.log = log;

		typeMirrorUtils = new TypeMirrorUtils(elements, types);
		parser = JavaDocParserBuilder.withBasicTags().withOutputType(OutputType.HTML).build();

		object = elements.getTypeElement(Object.class.getCanonicalName()).asType();
		enumeration = elements.getTypeElement(Enum.class.getCanonicalName()).asType();

		javaLangPackage = elements.getPackageElement("java.lang");
		javaMathPackage = elements.getPackageElement("java.math");
		javaTimePackage = elements.getPackageElement("java.time");
	}

	public Map<TypeMirror, Schema> parsePackages(final List<String> packages) {
		Map<TypeMirror, Schema> typeMirrorMap = new HashMap<>();

		if (packages == null) {
			return typeMirrorMap;
		}

		packages.stream()
				.filter(p -> p != null && !p.isEmpty())
				.map(elements::getPackageElement)
				.filter(Objects::nonNull)
				.map(this::parsePackage)
				.flatMap(map -> map.entrySet().stream())
				.filter(entry -> !typeMirrorMap.containsKey(entry.getKey()))
				.forEach(entry -> typeMirrorMap.put(entry.getKey(), entry.getValue()));

		return typeMirrorMap;
	}

	private Map<TypeMirror, Schema> parsePackage(final PackageElement packageElement) {
		Map<TypeMirror, Schema> typeMirrorMap = new HashMap<>();

		packageElement.getEnclosedElements()
				.stream()
				.map(Element::asType)
				.map(this::mapTypeMirrorToSchema)
				.flatMap(map -> map.entrySet().stream())
				.filter(entry -> !typeMirrorMap.containsKey(entry.getKey()))
				.forEach(entry -> typeMirrorMap.put(entry.getKey(), entry.getValue()));

		return typeMirrorMap;
	}

	public MediaType createMediaType(final TypeMirror typeMirror) {
		Schema schema = mapTypeMirrorToSchema(typeMirror).get(typeMirror);

		MediaType mediaType = new MediaType();
		if (Type.ARRAY.equals(schema.getType())) {
			mediaType.setSchema(schema);
		} else {
			mediaType.setSchema(ReferenceUtils.createSchemaReference(typeMirror));
		}

		return mediaType;
	}

	public Map<TypeMirror, Schema> mapTypeMirrorToSchema(final TypeMirror typeMirror) {
		if (typeMirror == null || typeMirror.getKind().equals(TypeKind.VOID)) {
			return Collections.emptyMap();
		}

		Map<TypeMirror, Schema> schemaMap = new LinkedHashMap<>();

		log.logDebug("Parsing type: %s", typeMirror.toString());

		Schema schema = new Schema();

		Element e = types.asElement(typeMirror);
		if (e != null && e.getAnnotation(Deprecated.class) != null) {
			schema.setDeprecated(true);
		}

		if (typeMirror.getKind().isPrimitive()) {
			SimpleEntry<Type, Format> typeAndFormat = getPrimitiveTypeAndFormat(typeMirror);
			if (typeAndFormat != null) {
				schema.setType(typeAndFormat.getKey());
				schema.setFormat(typeAndFormat.getValue());
				// schema.setDescription(propertyDoc.getDescription());
			}
			schemaMap.put(typeMirror, schema);
		} else if (TypeKind.VOID.equals(typeMirror.getKind()) || isAssignableFrom(typeMirror, Void.class)) {
			// TODO how to handle void type
			schema.setDescription("Type is Void.");

			schemaMap.put(typeMirror, schema);
		} else if (TypeKind.TYPEVAR.equals(typeMirror.getKind())) {
			// TODO check ... at the moment all typevars are ignored

			schema.setType(Type.OBJECT);

			schemaMap.put(typeMirror, schema);
		} else if (isTypeOf(typeMirror, Object.class)) {
			schema.setType(Type.OBJECT);

			schemaMap.put(typeMirror, schema);
		} else if (TypeKind.ARRAY.equals(typeMirror.getKind())) {
			schema.setType(Type.ARRAY);

			TypeMirror type = elements.getTypeElement(typeMirror.toString().replaceAll("\\[]", "")).asType();
			Map<TypeMirror, Schema> propertySchemaMap = mapTypeMirrorToSchema(type);

			if (isTypeInPackage(type, javaLangPackage)) {
				SimpleEntry<Type, Format> typeAndFormat = getJavaLangTypeAndFormat(type);
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
		} else if (isTypeInPackage(typeMirror, javaLangPackage)) {
			JavaDoc javaDoc = parser.parse(elements.getDocComment(types.asElement(typeMirror)));
			schema.setDescription(javaDoc.getDescription());

			SimpleEntry<Type, Format> typeAndFormat = getJavaLangTypeAndFormat(typeMirror);
			if (typeAndFormat != null) {
				schema.setType(typeAndFormat.getKey());
				schema.setFormat(typeAndFormat.getValue());
			}

			schemaMap.put(typeMirror, schema);
		} else if (isTypeInPackage(typeMirror, javaMathPackage)) {
			JavaDoc javaDoc = parser.parse(elements.getDocComment(types.asElement(typeMirror)));
			schema.setDescription(javaDoc.getDescription());

			schema.setType(Type.NUMBER);
			schema.setFormat(Format.DOUBLE);

			schemaMap.put(typeMirror, schema);
		} else if (isTypeInPackage(typeMirror, javaTimePackage)) {
			JavaDoc javaDoc = parser.parse(elements.getDocComment(types.asElement(typeMirror)));
			schema.setDescription(javaDoc.getDescription());

			SimpleEntry<Type, Format> typeAndFormat = getJavaTimeTypeAndFormat(typeMirror);
			if (typeAndFormat != null) {
				schema.setType(typeAndFormat.getKey());
				schema.setFormat(typeAndFormat.getValue());
			}
			schemaMap.put(typeMirror, schema);
		} else if (isAssignableFrom(typeMirror, Date.class)) {
			JavaDoc javaDoc = parser.parse(elements.getDocComment(types.asElement(typeMirror)));
			schema.setDescription(javaDoc.getDescription());

			schema.setType(Type.STRING);
			schema.setFormat(Format.DATE_TIME);

			schemaMap.put(typeMirror, schema);
		} else if (isAssignableFrom(typeMirror, Optional.class)) {
			TypeMirror type = typeMirrorUtils.removeEnclosingType(typeMirror, Optional.class)[0];
			Map<TypeMirror, Schema> propertySchemaMap = mapTypeMirrorToSchema(type);

			if (isTypeInPackage(type, javaLangPackage)) {
				SimpleEntry<Type, Format> typeAndFormat = getJavaLangTypeAndFormat(type);
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
		} else if (isAssignableFrom(typeMirror, List.class)) {
			schema.setType(Type.ARRAY);

			TypeMirror type = typeMirrorUtils.removeEnclosingType(typeMirror, List.class)[0];
			Map<TypeMirror, Schema> propertySchemaMap = mapTypeMirrorToSchema(type);

			if (isTypeInPackage(type, javaLangPackage)) {
				SimpleEntry<Type, Format> typeAndFormat = getJavaLangTypeAndFormat(type);
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
		} else if (isAssignableFrom(typeMirror, Set.class)) {
			schema.setType(Type.ARRAY);

			TypeMirror type = typeMirrorUtils.removeEnclosingType(typeMirror, Set.class)[0];
			Map<TypeMirror, Schema> propertySchemaMap = mapTypeMirrorToSchema(type);

			if (isTypeInPackage(type, javaLangPackage)) {
				SimpleEntry<Type, Format> typeAndFormat = getJavaLangTypeAndFormat(type);
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
		} else if (isAssignableFrom(typeMirror, Map.class)) {
			// TODO implement
		} else {
			// Element element = elements.getTypeElement(typeMirror.toString()); (#26)
			Element element = types.asElement(typeMirror);

			JavaDoc javaDoc = parser.parse(elements.getDocComment(element));
			schema.setDescription(javaDoc.getDescription());

			if (element.getKind().equals(ElementKind.ENUM)) {
				schema.setType(Type.STRING);

				element.getEnclosedElements()
					.stream()
					.filter(VariableElement.class::isInstance)
					.forEach(vElement -> schema.addEnumValue(vElement.toString()));

			} else {
				schema.setType(Type.OBJECT);

				TypeMirror superType = element.asType();

				while (!(superType instanceof NoType) && notTypeOf(superType, object) && notTypeOf(types.erasure(superType), enumeration)) {
					TypeElement typeElement = elements.getTypeElement(types.erasure(superType).toString());

					typeElement.getEnclosedElements()
							.stream()
							.filter(VariableElement.class::isInstance)
							.filter(this::isValidAttribute)
							.forEach(vElement -> {

								log.logDebug(String.format("Parsing attribute: %s", vElement.toString()));

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
												if (vElement.getAnnotation(Deprecated.class) != null) {
													propertySchema.setDeprecated(true);
												}
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
		boolean valid = true;

		if (element.getAnnotation(JsonIgnore.class) != null) {
			valid = false;
		} else if (element.getModifiers().contains(Modifier.STATIC)) {
			valid = false;
		}

		// TODO check for getter visibility

		return valid;
	}

	private boolean isTypeInPackage(final TypeMirror typeMirror, final PackageElement packageElement) {
		return types.asElement(typeMirror).getEnclosingElement().toString().equals(packageElement.toString());
	}

	private boolean notTypeOf(final TypeMirror type1, final TypeMirror type2) {
		return !types.isSameType(type1, type2) && !type1.toString().equalsIgnoreCase(type2.toString());
	}

	private String getPropertyName(final Element element) {
		JsonProperty jsonProperty = element.getAnnotation(JsonProperty.class);
		if (jsonProperty != null) {
			return jsonProperty.value();
		} else {
			return element.toString();
		}
	}

	private SimpleEntry<Type, Format> getPrimitiveTypeAndFormat(final TypeMirror typeMirror) {
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

	private SimpleEntry<Type, Format> getJavaLangTypeAndFormat(final TypeMirror typeMirror) {
		SimpleEntry<Type, Format> typeAndFormat = null;

		if (isTypeOf(typeMirror, String.class)) {
			typeAndFormat = new SimpleEntry<>(Type.STRING, null);
		}

		try {
			typeAndFormat = getPrimitiveTypeAndFormat(types.unboxedType(typeMirror));
		} catch (IllegalArgumentException e) {
			// TODO: handle finally clause
		}

		return typeAndFormat;
	}

	private SimpleEntry<Type, Format> getJavaTimeTypeAndFormat(final TypeMirror typeMirror) {
		SimpleEntry<Type, Format> typeAndFormat = null;

		if (isTypeOf(typeMirror, LocalDate.class)) {
			typeAndFormat = new SimpleEntry<>(Type.STRING, Format.DATE);
		} else if (isTypeOf(typeMirror, LocalDateTime.class)) {
			typeAndFormat = new SimpleEntry<>(Type.STRING, Format.DATE_TIME);
		}

		return typeAndFormat;
	}

	public boolean isTypeOf(final TypeMirror typeMirror, final Class<?> clazz) {
		return types.isSameType(typeMirror, elements.getTypeElement(clazz.getCanonicalName()).asType());
	}

	public boolean isAssignableFrom(final TypeMirror typeMirror, final Class<?> clazz) {
		return types.isAssignable(types.erasure(typeMirror), elements.getTypeElement(clazz.getCanonicalName()).asType());
	}

	public static Schema mergeSchemas(final Schema one, final Schema two) {
		Schema result = new Schema();

		result.setDeprecated(one.getDeprecated() || two.getDeprecated());
		result.setFormat(merge(one, two, Schema::getFormat));
		result.setType(merge(one, two, Schema::getType));
		result.setDescription(mergeString(one, two, Schema::getDescription));
		result.setDefaultValue(merge(one, two, Schema::getDefaultValue));
		result.setPattern(mergeString(one, two, Schema::getPattern));

		if (one.getEnumValues() != null || two.getEnumValues() != null) {
			merge(one, two, Schema::getEnumValues).forEach(result::addEnumValue);
		}

		if (one.getProperties() != null) {
			one.getProperties().forEach((key, value) -> {
				Function<Schema, Object> function = schema -> schema.getProperties().get(key);
				Object propertyOne = function.apply(one);
				Object propertyTwo = function.apply(two);

				if (notNullReference(propertyOne) && notNullReference(propertyTwo)) {
					result.putProperty(key, (Reference) propertyOne);
				} else if (notNullSchema(propertyOne) && notNullSchema(propertyTwo)) {
					result.putProperty(key, mergeSchemas((Schema) propertyOne, (Schema) propertyTwo));
				} else if (notNullReference(propertyOne)) {
					result.putProperty(key, (Reference) propertyOne);
				} else if (notNullSchema(propertyOne)) {
					result.putProperty(key, (Schema) propertyOne);
				}
			});
		}

		result.setItems(merge(one, two, Schema::getItems));

		return result;
	}

	private static boolean notNullSchema(final Object object) {
		return object instanceof Schema;
	}

	private static boolean notNullReference(final Object object) {
		return object instanceof Reference;
	}

	private static <T> T merge(final Schema one, final Schema two, final Function<Schema, T> function) {
		return function.apply(one) != null ? function.apply(one) : function.apply(two);
	}

	private static String mergeString(final Schema one, final Schema two, final Function<Schema, String> function) {
		if (function.apply(one) != null && !function.apply(one).isEmpty()) {
			return function.apply(one);
		} else {
			return function.apply(two);
		}
	}
}
