package com.github.chhorz.openapi.common.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.chhorz.javadoc.JavaDoc;
import com.github.chhorz.javadoc.JavaDocParser;
import com.github.chhorz.javadoc.JavaDocParserBuilder;
import com.github.chhorz.javadoc.OutputType;
import com.github.chhorz.openapi.common.domain.Reference;
import com.github.chhorz.openapi.common.domain.Schema;
import com.github.chhorz.openapi.common.domain.Schema.Format;
import com.github.chhorz.openapi.common.domain.Schema.Type;

public class SchemaUtils {

	private Elements elements;
	private Types types;

	private LoggingUtils log;
	private TypeMirrorUtils typeMirrorUtils;

	private JavaDocParser parser;

	private TypeMirror object;
	private TypeMirror enumeration;

	private PackageElement javaLangPackage;
	private PackageElement javaMathPackage;
	private PackageElement javaTimePackage;

	public SchemaUtils(final Elements elements, final Types types, final LoggingUtils log) {
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
				.forEach(entry -> {
					typeMirrorMap.put(entry.getKey(), entry.getValue());
				});

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
				.forEach(entry -> {
					typeMirrorMap.put(entry.getKey(), entry.getValue());
				});

		return typeMirrorMap;
	}

	public Map<TypeMirror, Schema> mapTypeMirrorToSchema(final TypeMirror typeMirror) {
		Map<TypeMirror, Schema> schemaMap = new HashMap<>();

		log.info(String.format("Parsing type: %s", typeMirror.toString()));

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
		} else if (typeMirror.getKind().equals(TypeKind.ARRAY)) {
			schema.setType(Type.ARRAY);

			TypeMirror type = elements.getTypeElement(typeMirror.toString().replaceAll("\\[]", "")).asType();
			Map<TypeMirror, Schema> propertySchemaMap = mapTypeMirrorToSchema(type);

			if (isTypeInPackage(type, javaLangPackage)) {
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
		} else if (isTypeInPackage(typeMirror, javaLangPackage)) {
			JavaDoc javaDoc = parser.parse(elements.getDocComment(types.asElement(typeMirror)));
			schema.setDescription(javaDoc.getDescription());

			SimpleEntry<Type, Format> typeAndFormat = getJavaLangTypeAndFormat(elements, types, typeMirror);
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

			SimpleEntry<Type, Format> typeAndFormat = getJavaTimeTypeAndFormat(elements, types, typeMirror);
			if (typeAndFormat != null) {
				schema.setType(typeAndFormat.getKey());
				schema.setFormat(typeAndFormat.getValue());
			}
			schemaMap.put(typeMirror, schema);
		} else if (isAssignableFrom(elements, types, typeMirror, List.class)) {
			schema.setType(Type.ARRAY);

			TypeMirror type = typeMirrorUtils.removeEnclosingType(typeMirror, List.class)[0];
			Map<TypeMirror, Schema> propertySchemaMap = mapTypeMirrorToSchema(type);

			if (isTypeInPackage(type, javaLangPackage)) {
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
		} else if (isAssignableFrom(elements, types, typeMirror, Set.class)) {
			schema.setType(Type.ARRAY);

			TypeMirror type = typeMirrorUtils.removeEnclosingType(typeMirror, Set.class)[0];
			Map<TypeMirror, Schema> propertySchemaMap = mapTypeMirrorToSchema(type);

			if (isTypeInPackage(type, javaLangPackage)) {
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
		} else if (isAssignableFrom(elements, types, typeMirror, Map.class)) {
			// TODO implement
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

				while (!isSameType(superType, object) && !isSameType(types.erasure(superType), enumeration)) {
					TypeElement typeElement = elements.getTypeElement(types.erasure(superType).toString());

					typeElement.getEnclosedElements()
							.stream()
							.filter(VariableElement.class::isInstance)
							.filter(this::isValidAttribute)
							.forEach(vElement -> {

								log.debug(String.format("Parsing attribute: %s", vElement.toString()));

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

	private boolean isSameType(final TypeMirror type1, final TypeMirror type2) {
		return types.isSameType(type1, type2) || type1.toString().equalsIgnoreCase(type2.toString());
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

	private SimpleEntry<Type, Format> getJavaLangTypeAndFormat(final Elements elements, final Types types,
			final TypeMirror typeMirror) {
		SimpleEntry<Type, Format> typeAndFormat = null;

		if (isTypeOf(elements, types, typeMirror, String.class)) {
			typeAndFormat = new SimpleEntry<>(Type.STRING, null);
		}

		try {
			typeAndFormat = getPrimitiveTypeAndFormat(types.unboxedType(typeMirror));
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

	private boolean isAssignableFrom(final Elements elements, final Types types, final TypeMirror typeMirror,
			final Class<?> clazz) {
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
			one.getProperties().entrySet().forEach(entry -> {
				System.out.println(one);
				System.out.println(two);
				Function<Schema, Object> function = schema -> schema.getProperties().get(entry.getKey());
				Object propertyOne = function.apply(one);
				Object propertyTwo = function.apply(two);

				if (notNullReference(propertyOne) && notNullReference(propertyTwo)) {
					result.putProperty(entry.getKey(), (Reference) propertyOne);
				} else if (notNullSchema(propertyOne) && notNullSchema(propertyTwo)) {
					result.putProperty(entry.getKey(), mergeSchemas((Schema) propertyOne, (Schema) propertyTwo));
				} else if (notNullReference(propertyOne)) {
					result.putProperty(entry.getKey(), (Reference) propertyOne);
				} else if (notNullSchema(propertyOne)) {
					result.putProperty(entry.getKey(), (Schema) propertyOne);
				}
			});
		}

		Object items = merge(one, two, Schema::getItems);
		if (items instanceof Reference) {
			result.setItems((Reference) items);
		} else if (items instanceof Schema) {
			result.setItems((Schema) items);
		}

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
