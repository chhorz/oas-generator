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
package com.github.chhorz.openapi.common.spi.mapping;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.chhorz.javadoc.JavaDoc;
import com.github.chhorz.openapi.common.OpenAPIProcessor;
import com.github.chhorz.openapi.common.domain.Reference;
import com.github.chhorz.openapi.common.domain.Schema;
import com.github.chhorz.openapi.common.domain.Schema.Type;
import com.github.chhorz.openapi.common.properties.domain.ParserProperties;
import com.github.chhorz.openapi.common.util.LogUtils;
import com.github.chhorz.openapi.common.util.ProcessingUtils;

import javax.lang.model.element.*;
import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.Function;

public class ObjectTypeMirrorMapper extends AbstractTypeMirrorMapper {

	private static final String GET_PREFIX = "get";
	private static final String IS_PREFIX = "is";

	private TypeMirror object;
	private TypeMirror enumeration;

	@Override
	public void setup(Elements elements, Types types, LogUtils logUtils, ParserProperties parserProperties, List<TypeMirrorMapper> typeMirrorMappers) {
		super.setup(elements, types, logUtils, parserProperties, typeMirrorMappers);

		object = elements.getTypeElement(Object.class.getCanonicalName()).asType();
		enumeration = elements.getTypeElement(Enum.class.getCanonicalName()).asType();
	}

	@Override
	public boolean test(TypeMirror typeMirror) {
		return true;
	}

	@Override
	public Map<TypeMirror, Schema> map(TypeMirror typeMirror, Map<TypeMirror, Schema> parsedSchemaMap) {
		Map<TypeMirror, Schema> schemaMap = new LinkedHashMap<>();
		if (parsedSchemaMap.entrySet().stream().anyMatch(entry -> entry.getKey().toString().equals(typeMirror.toString()))) {
			return parsedSchemaMap;
		} else {
			parsedSchemaMap.entrySet().stream()
				.filter(e -> e.getValue().getType().equals(Schema.Type.OBJECT) || e.getValue().getType().equals(Schema.Type.ENUM))
				.forEach(entry -> schemaMap.put(entry.getKey(), entry.getValue()));
		}

		logUtils.logDebug("Parsing type: %s", typeMirror.toString());

		Schema schema = new Schema();

		Element element = types.asElement(typeMirror);
		if (element != null && element.getAnnotation(Deprecated.class) != null) {
			schema.setDeprecated(true);
		}

		JavaDoc javaDoc = parser.parse(elements.getDocComment(element));
		schema.setDescription(javaDoc.getDescription());

		schemaMap.put(typeMirror, schema);

		schema.setType(Type.OBJECT);

		TypeMirror superType = element.asType();

		Map<TypeMirror, Map<TypeParameterElement, TypeMirror>> typeParameterMap = new HashMap<>();

		// follow class hierarchy until object or enum
		while (!(superType instanceof NoType) && processingUtils.doesTypeDiffer(superType, object)
			&& processingUtils.doesTypeDiffer(types.erasure(superType), enumeration)) {

			TypeElement typeElement = elements.getTypeElement(types.erasure(superType).toString());

			List<? extends TypeParameterElement> typeParameters = typeElement.getTypeParameters();

			// handle class attributes
			typeElement.getEnclosedElements()
				.stream()
				.filter(VariableElement.class::isInstance)
				.filter(this::isValidAttribute)
				.forEach(vElement -> {

					logUtils.logDebug(String.format("Parsing attribute: %s", vElement));

					JavaDoc propertyDoc = parser.parse(elements.getDocComment(vElement));

					TypeMirror variableElementTypeMirror;

					TypeMirror typeMirrorFromTypeArgument = null;
					Map<TypeParameterElement, TypeMirror> typeParametersMap = typeParameterMap.get(types.erasure(vElement.getEnclosingElement().asType()));
					if (typeParametersMap != null) {
						typeMirrorFromTypeArgument = typeParameters
							.stream()
							.filter(typeParameter -> typeParameter.asType().equals(vElement.asType()))
							.findFirst()
							.map(typeParametersMap::get)
							.orElse(null);
					}

					if (typeMirrorFromTypeArgument != null) {
						variableElementTypeMirror = typeMirrorFromTypeArgument;
					} else {
						variableElementTypeMirror = vElement.asType();
					}

					// lets do some recursion
					Map<TypeMirror, Schema> propertySchemaMap = recursion(variableElementTypeMirror, schemaMap);
					// the schema is an object or enum -> we add it to the map
					propertySchemaMap.entrySet()
						.stream()
						.filter(entry -> (Type.OBJECT.equals(entry.getValue().getType()) && entry.getValue().getAdditionalProperties() == null)
							|| (Type.ENUM.equals(entry.getValue().getType())))
						.forEach(entry -> schemaMap.put(entry.getKey(), entry.getValue()));

					propertySchemaMap.entrySet()
						.stream()
						.filter(entry -> entry.getKey().toString().equals(variableElementTypeMirror.toString()))
						.forEach(entry -> {
							final String propertyName = getPropertyName(vElement);

							if ((Type.OBJECT.equals(entry.getValue().getType()) && entry.getValue().getAdditionalProperties() == null)
								|| (Type.ENUM.equals(entry.getValue().getType()))) {
								schema.putProperty(propertyName,
									Reference.forSchema(ProcessingUtils.getShortName(variableElementTypeMirror)));
							} else {
								Schema propertySchema = entry.getValue();

								if (OpenAPIProcessor.isClassAvailable("javax.validation.constraints.Min")) {
									getValidationValue(vElement, NotNull.class, notNull -> true)
										.ifPresent(notNull -> schema.addRequired(propertyName));
									getValidationValue(vElement, Min.class, Min::value)
										.ifPresent(propertySchema::setMinimum);
									getValidationValue(vElement, Max.class, Max::value)
										.ifPresent(propertySchema::setMaximum);
									getValidationValue(vElement, Pattern.class, Pattern::regexp)
										.ifPresent(propertySchema::setPattern);
								}

								propertySchema.setDescription(propertyDoc.getDescription());
								if (vElement.getAnnotation(Deprecated.class) != null) {
									propertySchema.setDeprecated(true);
								}
								schema.putProperty(propertyName, propertySchema);
							}
						});
				});

			typeParameterMap.putAll(processingUtils.populateTypeParameterMap(typeElement, typeParameterMap));

			superType = typeElement.getSuperclass();
		}

		// handle getters
		if (processingUtils.isInterface(typeMirror) && parserProperties.getIncludeGetters()) {
			types.directSupertypes(typeMirror).stream()
				.filter(directSupertype -> processingUtils.doesTypeDiffer(directSupertype, object))
				.map(t -> recursion(t, schemaMap))
				.flatMap(typeMirrorSchemaMap -> typeMirrorSchemaMap.values().stream())
				.map(Schema::getProperties)
				.filter(Objects::nonNull)
				.flatMap(propertyMap -> propertyMap.entrySet().stream())
				.forEach(entry -> {
					if (entry.getValue() instanceof Schema) {
						schema.putProperty(entry.getKey(), (Schema) entry.getValue());
					} else if (entry.getValue() instanceof Reference) {
						schema.putProperty(entry.getKey(), (Reference) entry.getValue());
					}
				});

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
					Map<TypeMirror, Schema> propertySchemaMap = recursion(executableElement.getReturnType(), schemaMap);
					// the schema is an object or enum -> we add it to the map
					propertySchemaMap.entrySet()
						.stream()
						.filter(entry -> !entry.getKey().toString().equals(executableElement.getReturnType().toString()))
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
									Reference.forSchema(ProcessingUtils.getShortName(executableElement.getReturnType())));
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

	private <A extends Annotation, V> Optional<V> getValidationValue(final Element element, final Class<A> clazz, final Function<A, V> valueProvider) {
		A annotation = element.getAnnotation(clazz);
		if (annotation != null) {
			return Optional.ofNullable(valueProvider.apply(annotation));
		} else {
			return Optional.empty();
		}
	}

	private String getPropertyName(final Element element) {
		JsonProperty jsonProperty = element.getAnnotation(JsonProperty.class);
		if (jsonProperty != null && !JsonProperty.USE_DEFAULT_NAME.equals(jsonProperty.value())) {
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

}