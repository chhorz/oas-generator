/**
 *
 * Copyright 2018-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.chhorz.openapi.common.util;

import com.github.chhorz.javadoc.JavaDoc;
import com.github.chhorz.openapi.common.domain.MediaType;
import com.github.chhorz.openapi.common.domain.Reference;
import com.github.chhorz.openapi.common.domain.Schema;
import com.github.chhorz.openapi.common.domain.Schema.Type;
import com.github.chhorz.openapi.common.javadoc.ResponseTag;
import com.github.chhorz.openapi.common.properties.domain.ParserProperties;
import com.github.chhorz.openapi.common.spi.mapping.TypeMirrorMapper;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.github.chhorz.openapi.common.util.ComponentUtils.convertSchemaMap;
import static java.util.ServiceLoader.load;

public class SchemaUtils {

	private final Elements elements;
	private final Types types;

	private final LogUtils logUtils;
	private final ProcessingUtils processingUtils;

	private final List<TypeMirror> baseTypeMirrors;

	private final List<TypeMirrorMapper> typeMirrorMappers;

	public SchemaUtils(final Elements elements, final Types types, final ParserProperties parserProperties, final LogUtils logUtils) {
		this(elements, types, parserProperties, logUtils, Collections.emptyList());
	}

	public SchemaUtils(final Elements elements, final Types types, final ParserProperties parserProperties, final LogUtils logUtils, final List<Class<?>> baseClasses) {
		this.elements = elements;
		this.types = types;
		this.logUtils = logUtils;

		processingUtils = new ProcessingUtils(elements, types, logUtils);

		this.baseTypeMirrors = baseClasses.stream()
			.map(clazz -> elements.getTypeElement(clazz.getCanonicalName()).asType())
			.map(types::erasure)
			.collect(Collectors.toList());

		ServiceLoader<TypeMirrorMapper> serviceLoader = load(TypeMirrorMapper.class, getClass().getClassLoader());

		typeMirrorMappers = StreamSupport.stream(serviceLoader.spliterator(), false)
			.collect(Collectors.toList());

		typeMirrorMappers.forEach(mapper -> mapper.setup(elements, types, logUtils, parserProperties, typeMirrorMappers));
	}

	public Map<String, Schema> parsePackages(final List<String> packages) {
		Map<TypeMirror, Schema> typeMirrorMap = new HashMap<>();

		if (packages == null) {
			return new HashMap<>();
		}

		packages.stream()
			.filter(p -> p != null && !p.isEmpty())
			.map(elements::getPackageElement)
			.filter(Objects::nonNull)
			.map(this::parsePackage)
			.flatMap(map -> map.entrySet().stream())
			.filter(entry -> !typeMirrorMap.containsKey(entry.getKey()))
			.forEach(entry -> typeMirrorMap.put(entry.getKey(), entry.getValue()));

		return convertSchemaMap(typeMirrorMap);
	}

	private Map<TypeMirror, Schema> parsePackage(final PackageElement packageElement) {
		Map<TypeMirror, Schema> typeMirrorMap = new HashMap<>();

		packageElement.getEnclosedElements()
			.stream()
			.map(Element::asType)
			.map(this::createTypeMirrorSchemaMap)
			.flatMap(map -> map.entrySet().stream())
			.filter(entry -> !typeMirrorMap.containsKey(entry.getKey()))
			.forEach(entry -> typeMirrorMap.put(entry.getKey(), entry.getValue()));

		return typeMirrorMap;
	}

	public MediaType createMediaType(final TypeMirror typeMirror) {
		Schema schema = createTypeMirrorSchemaMap(typeMirror).get(typeMirror);

		MediaType mediaType = new MediaType();
		if (Type.ARRAY.equals(schema.getType())) {
			mediaType.setSchema(schema);
		} else {
			mediaType.setSchema(Reference.forSchema(ProcessingUtils.getShortName(typeMirror)));
		}

		return mediaType;
	}

	public Schema getSchemaForTypeMirror(final TypeMirror typeMirror) {
		return createTypeMirrorSchemaMap(typeMirror).get(typeMirror);
	}

	public Map<String, Schema> createStringSchemaMap(final TypeMirror typeMirror) {
		return convertSchemaMap(createTypeMirrorSchemaMap(typeMirror));
	}

	public Map<TypeMirror, Schema> createTypeMirrorSchemaMap(final TypeMirror typeMirror) {
		return createTypeMirrorSchemaMap(typeMirror, new LinkedHashMap<>());
	}

	private Map<TypeMirror, Schema> createTypeMirrorSchemaMap(final TypeMirror typeMirror, Map<TypeMirror, Schema> parsedSchemaMap) {
		if (typeMirror == null || baseTypeMirrors.contains(typeMirror) || isVoidType(typeMirror) || isAbstractClass(typeMirror)) {
			return Collections.emptyMap();
		}

		Map<TypeMirror, Schema> schemaMap = new LinkedHashMap<>();
		if (parsedSchemaMap.entrySet().stream().anyMatch(entry -> entry.getKey().toString().equals(typeMirror.toString()))) {
			return parsedSchemaMap;
		} else {
			parsedSchemaMap.entrySet().stream()
				.filter(e -> e.getValue().getType().equals(Type.OBJECT) || e.getValue().getType().equals(Type.ENUM))
				.forEach(entry -> schemaMap.put(entry.getKey(), entry.getValue()));
		}

		logUtils.logDebug("Parsing type: %s", typeMirror.toString());

		typeMirrorMappers.stream()
			.filter(mapper -> mapper.test(typeMirror))
			.findFirst()
			.map(mapper -> mapper.map(typeMirror, parsedSchemaMap))
			.ifPresent(schemaMap::putAll);

		return schemaMap;
	}

	private boolean isVoidType(final TypeMirror typeMirror) {
		return TypeKind.VOID.equals(typeMirror.getKind())
			|| processingUtils.isAssignableTo(typeMirror, Void.class);
	}

	private boolean isAbstractClass(final TypeMirror typeMirror) {
		return TypeKind.DECLARED.equals(typeMirror.getKind())
			&& ElementKind.CLASS.equals(types.asElement(typeMirror).getKind())
			&& types.asElement(typeMirror).getModifiers().contains(Modifier.ABSTRACT);
	}

	public Map<TypeMirror, Schema> createSchemasFromDocComment(final JavaDoc javaDoc) {
		Map<TypeMirror, Schema> schemaMap = new LinkedHashMap<>();

		if (javaDoc != null) {
			List<ResponseTag> responseTags = javaDoc.getTags(ResponseTag.class);
			return responseTags.stream()
				.filter(tag -> tag.getResponseType() != null && !tag.getResponseType().isEmpty())
				.map(responseTag -> processingUtils.createTypeMirrorFromString(responseTag.getResponseType()))
				.map(this::createTypeMirrorSchemaMap)
				.flatMap(map -> map.entrySet().stream())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		}

		return schemaMap;
	}

	public static Schema mergeSchemas(final Schema one, final Schema two) {
		Schema result = new Schema();

		if (one.getRequired() != null || two.getRequired() != null) {
			merge(one, two, Schema::getRequired).forEach(result::addRequired);
		}
		result.setMinimum(merge(one, two, Schema::getMinimum));
		result.setMaximum(merge(one, two, Schema::getMaximum));
		result.setPattern(mergeString(one, two, Schema::getPattern));
		result.setMinLength(merge(one, two, Schema::getMinLength));
		result.setMaxLength(merge(one, two, Schema::getMaxLength));
		result.setMinItems(merge(one, two, Schema::getMinItems));
		result.setMaxItems(merge(one, two, Schema::getMaxItems));

		result.setDeprecated(one.getDeprecated() || two.getDeprecated());
		result.setFormat(merge(one, two, Schema::getFormat));
		result.setType(merge(one, two, Schema::getType));
		result.setDescription(mergeString(one, two, Schema::getDescription));
		result.setDefaultValue(merge(one, two, Schema::getDefaultValue));

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
