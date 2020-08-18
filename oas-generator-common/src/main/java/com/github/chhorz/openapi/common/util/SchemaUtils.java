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
import com.github.chhorz.openapi.common.javadoc.ResponseTag;
import com.github.chhorz.openapi.common.properties.domain.ParserProperties;

import javax.lang.model.element.*;
import javax.lang.model.type.ArrayType;
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
import java.util.stream.Collectors;

public class SchemaUtils {

	private static final String GET_PREFIX = "get";
	private static final String IS_PREFIX = "is";

	private final Elements elements;
	private final Types types;

	private final ParserProperties parserProperties;
	private final LogUtils logUtils;
	private final TypeMirrorUtils typeMirrorUtils;

	private final JavaDocParser parser;

	private final TypeMirror object;
	private final TypeMirror enumeration;

	private final PackageElement javaLangPackage;
	private final PackageElement javaMathPackage;
	private final PackageElement javaTimePackage;

	private final List<TypeMirror> baseTypeMirrors;

	public SchemaUtils(final Elements elements, final Types types, final ParserProperties parserProperties, final LogUtils logUtils) {
		this(elements, types, parserProperties, logUtils, Collections.emptyList());
	}

	public SchemaUtils(final Elements elements, final Types types, final ParserProperties parserProperties, final LogUtils logUtils, final List<Class<?>> baseClasses) {
		this.elements = elements;
		this.types = types;
		this.parserProperties = parserProperties;
		this.logUtils = logUtils;

		typeMirrorUtils = new TypeMirrorUtils(elements, types, logUtils);
		parser = JavaDocParserBuilder.withBasicTags().withOutputType(OutputType.HTML).build();

		object = elements.getTypeElement(Object.class.getCanonicalName()).asType();
		enumeration = elements.getTypeElement(Enum.class.getCanonicalName()).asType();

		javaLangPackage = elements.getPackageElement("java.lang");
		javaMathPackage = elements.getPackageElement("java.math");
		javaTimePackage = elements.getPackageElement("java.time");

		this.baseTypeMirrors = baseClasses.stream()
			.map(clazz -> elements.getTypeElement(clazz.getCanonicalName()).asType())
			.map(types::erasure)
			.collect(Collectors.toList());
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
		if (typeMirror == null || baseTypeMirrors.contains(typeMirror)
			|| TypeKind.VOID.equals(typeMirror.getKind()) || typeMirrorUtils.isAssignableFrom(typeMirror, Void.class)) {
			return Collections.emptyMap();
		}

		Map<TypeMirror, Schema> schemaMap = new LinkedHashMap<>();

		logUtils.logDebug("Parsing type: %s", typeMirror.toString());

		Schema schema = new Schema();

		Element element = types.asElement(typeMirror);
		if (element != null && element.getAnnotation(Deprecated.class) != null) {
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
		} else if (TypeKind.TYPEVAR.equals(typeMirror.getKind())) {
			// TODO check ... at the moment all typevars are ignored

			schema.setType(Type.OBJECT);

			schemaMap.put(typeMirror, schema);
		} else if (typeMirrorUtils.isTypeOf(typeMirror, Object.class)) {
			schema.setType(Type.OBJECT);

			schemaMap.put(typeMirror, schema);
		} else if (TypeKind.ARRAY.equals(typeMirror.getKind())) {
			schema.setType(Type.ARRAY);

			TypeMirror componentType;
			if (typeMirror instanceof ArrayType) {
				componentType = ((ArrayType) typeMirror).getComponentType();
			} else {
				componentType = elements.getTypeElement(typeMirror.toString().replaceAll("\\[]", "")).asType();
			}
			Map<TypeMirror, Schema> propertySchemaMap = mapTypeMirrorToSchema(componentType);

			if (componentType.getKind().isPrimitive()) {
				SimpleEntry<Type, Format> typeAndFormat = getPrimitiveTypeAndFormat(componentType);
				Schema typeSchema = new Schema();
				if (typeAndFormat != null) {
					typeSchema.setType(typeAndFormat.getKey());
					typeSchema.setFormat(typeAndFormat.getValue());
				}
				schema.setItems(typeSchema);
			} else if (typeMirrorUtils.isTypeInPackage(componentType, javaLangPackage)) {
				SimpleEntry<Type, Format> typeAndFormat = getJavaLangTypeAndFormat(componentType);
				Schema typeSchema = new Schema();
				if (typeAndFormat != null) {
					typeSchema.setType(typeAndFormat.getKey());
					typeSchema.setFormat(typeAndFormat.getValue());
				}
				schema.setItems(typeSchema);
			} else {
				schema.setItems(ReferenceUtils.createSchemaReference(componentType));
			}

			schemaMap.putAll(propertySchemaMap);

			schemaMap.put(typeMirror, schema);
		} else if (typeMirrorUtils.isTypeInPackage(typeMirror, javaLangPackage)) {
			JavaDoc javaDoc = parser.parse(elements.getDocComment(element));
			schema.setDescription(javaDoc.getDescription());

			SimpleEntry<Type, Format> typeAndFormat = getJavaLangTypeAndFormat(typeMirror);
			if (typeAndFormat != null) {
				schema.setType(typeAndFormat.getKey());
				schema.setFormat(typeAndFormat.getValue());
			}

			schemaMap.put(typeMirror, schema);
		} else if (typeMirrorUtils.isTypeInPackage(typeMirror, javaMathPackage)) {
			JavaDoc javaDoc = parser.parse(elements.getDocComment(element));
			schema.setDescription(javaDoc.getDescription());

			schema.setType(Type.NUMBER);
			schema.setFormat(Format.DOUBLE);

			schemaMap.put(typeMirror, schema);
		} else if (typeMirrorUtils.isTypeInPackage(typeMirror, javaTimePackage)) {
			JavaDoc javaDoc = parser.parse(elements.getDocComment(element));
			schema.setDescription(javaDoc.getDescription());

			SimpleEntry<Type, Format> typeAndFormat = getJavaTimeTypeAndFormat(typeMirror);
			if (typeAndFormat != null) {
				schema.setType(typeAndFormat.getKey());
				schema.setFormat(typeAndFormat.getValue());
			}
			schemaMap.put(typeMirror, schema);
		} else if (typeMirrorUtils.isAssignableFrom(typeMirror, Date.class)) {
			JavaDoc javaDoc = parser.parse(elements.getDocComment(element));
			schema.setDescription(javaDoc.getDescription());

			schema.setType(Type.STRING);
			schema.setFormat(Format.DATE_TIME);

			schemaMap.put(typeMirror, schema);
		} else if (typeMirrorUtils.isAssignableFrom(typeMirror, Optional.class)) {
			TypeMirror type = typeMirrorUtils.removeEnclosingType(typeMirror, Optional.class)[0];
			Map<TypeMirror, Schema> propertySchemaMap = mapTypeMirrorToSchema(type);

			if (typeMirrorUtils.isTypeInPackage(type, javaLangPackage)) {
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
		} else if (typeMirrorUtils.isAssignableFrom(typeMirror, List.class)) {
			schema.setType(Type.ARRAY);

			TypeMirror type = typeMirrorUtils.removeEnclosingType(typeMirror, List.class)[0];
			Map<TypeMirror, Schema> propertySchemaMap = mapTypeMirrorToSchema(type);

			if (typeMirrorUtils.isTypeInPackage(type, javaLangPackage)) {
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
		} else if (typeMirrorUtils.isAssignableFrom(typeMirror, Set.class)) {
			schema.setType(Type.ARRAY);

			TypeMirror type = typeMirrorUtils.removeEnclosingType(typeMirror, Set.class)[0];
			Map<TypeMirror, Schema> propertySchemaMap = mapTypeMirrorToSchema(type);

			if (typeMirrorUtils.isTypeInPackage(type, javaLangPackage)) {
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
		} else if (typeMirrorUtils.isAssignableFrom(typeMirror, Map.class)) {
			// TODO implement
		} else {
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

				// follow class hierarchy until object or enum
				while (!(superType instanceof NoType) && typeMirrorUtils.notTypeOf(superType, object) && typeMirrorUtils.notTypeOf(types.erasure(superType), enumeration)) {
					TypeElement typeElement = elements.getTypeElement(types.erasure(superType).toString());

					// handle class attributes
					typeElement.getEnclosedElements()
							.stream()
							.filter(VariableElement.class::isInstance)
							.filter(this::isValidAttribute)
							.forEach(vElement -> {

								logUtils.logDebug(String.format("Parsing attribute: %s", vElement.toString()));

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

				// handle getters
				if (typeMirrorUtils.isInterface(typeMirror) && parserProperties.getIncludeGetters()) {
					element.getEnclosedElements()
						.stream()
						.filter(ExecutableElement.class::isInstance)
						.map(ExecutableElement.class::cast)
						.filter(executableElement -> {
							final String executableElementName = executableElement.getSimpleName().toString();
							return (executableElementName.startsWith(GET_PREFIX) && executableElementName.length() > GET_PREFIX.length())
								|| (executableElementName.startsWith(IS_PREFIX) && executableElementName.length() > IS_PREFIX.length());
						})
						.filter(this::isValidAttribute)
						.forEach(executableElement -> {

							logUtils.logDebug(String.format("Parsing getter: %s", executableElement.toString()));

							JavaDoc getterDoc = parser.parse(elements.getDocComment(executableElement));

							// lets do some recursion
							Map<TypeMirror, Schema> propertySchemaMap = mapTypeMirrorToSchema(executableElement.getReturnType());
							// the schema is an object or enum -> we add it to the map
							propertySchemaMap.entrySet()
								.stream()
								.filter(entry -> Type.OBJECT.equals(entry.getValue().getType())
									|| Type.ENUM.equals(entry.getValue().getType()))
								.forEach(entry -> schemaMap.put(entry.getKey(), entry.getValue()));

							propertySchemaMap.entrySet()
								.stream()
								.filter(entry -> entry.getKey().equals(executableElement.getReturnType()))
								.forEach(entry -> {
									final String propertyName = getPropertyName(executableElement);

									if (Type.OBJECT.equals(entry.getValue().getType())
										|| Type.ENUM.equals(entry.getValue().getType())) {
										schema.putProperty(propertyName,
											ReferenceUtils.createSchemaReference(executableElement.getReturnType()));
									} else {
										Schema propertySchema = entry.getValue();
										propertySchema.setDescription(getterDoc.getDescription());
										if (executableElement.getAnnotation(Deprecated.class) != null) {
											propertySchema.setDeprecated(true);
										}
										schema.putProperty(propertyName, propertySchema);
									}
								});
						});
				}

			}
			schemaMap.put(typeMirror, schema);
		}

		return schemaMap;
	}

	public Map<TypeMirror, Schema> createSchemasFromDocComment(final JavaDoc javaDoc) {
		Map<TypeMirror, Schema> schemaMap = new LinkedHashMap<>();

		if (javaDoc != null) {
			List<ResponseTag> responseTags = javaDoc.getTags(ResponseTag.class);
			return responseTags.stream()
				.filter(tag -> tag.getResponseType() != null && !tag.getResponseType().isEmpty())
				.map(responseTag -> typeMirrorUtils.createTypeMirrorFromString(responseTag.getResponseType()))
				.map(this::mapTypeMirrorToSchema)
				.flatMap(map -> map.entrySet().stream())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
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

	private String getPropertyName(final Element element) {
		JsonProperty jsonProperty = element.getAnnotation(JsonProperty.class);
		if (jsonProperty != null) {
			return jsonProperty.value();
		} else if (element instanceof ExecutableElement) {
			final String executableElementName = element.getSimpleName().toString();
			if (executableElementName.startsWith(GET_PREFIX) && executableElementName.length() > GET_PREFIX.length()) {
				String shortName = executableElementName.substring(GET_PREFIX.length());
				return shortName.substring(0, 1).toLowerCase() + shortName.substring(1);
			} else if (executableElementName.startsWith(IS_PREFIX) && executableElementName.length() > IS_PREFIX.length()) {
				String shortName = executableElementName.substring(IS_PREFIX.length());
				return shortName.substring(0, 1).toLowerCase() + shortName.substring(1);
			} else {
				// TODO
				throw new RuntimeException("Invalid method");
			}
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

		if (typeMirrorUtils.isTypeOf(typeMirror, String.class)) {
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

		if (typeMirrorUtils.isTypeOf(typeMirror, LocalDate.class)) {
			typeAndFormat = new SimpleEntry<>(Type.STRING, Format.DATE);
		} else if (typeMirrorUtils.isTypeOf(typeMirror, LocalDateTime.class)) {
			typeAndFormat = new SimpleEntry<>(Type.STRING, Format.DATE_TIME);
		}

		return typeAndFormat;
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
