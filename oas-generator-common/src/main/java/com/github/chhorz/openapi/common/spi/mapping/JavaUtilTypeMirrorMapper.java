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

import com.github.chhorz.openapi.common.domain.Reference;
import com.github.chhorz.openapi.common.domain.Schema;
import com.github.chhorz.openapi.common.domain.Schema.Format;
import com.github.chhorz.openapi.common.domain.Schema.Type;
import com.github.chhorz.openapi.common.util.ProcessingUtils;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.type.TypeMirror;
import java.util.AbstractMap.SimpleEntry;
import java.util.*;

public class JavaUtilTypeMirrorMapper extends AbstractTypeMirrorMapper {

	@Override
	public boolean test(TypeMirror typeMirror) {
		return processingUtils.isAssignableTo(typeMirror, Optional.class)
			|| processingUtils.isAssignableTo(typeMirror, List.class)
			|| processingUtils.isAssignableTo(typeMirror, Set.class)
			|| processingUtils.isAssignableTo(typeMirror, Map.class);
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

		if (processingUtils.isAssignableTo(typeMirror, Optional.class)) {
			TypeMirror type = processingUtils.removeEnclosingType(typeMirror, Optional.class)[0];
			Map<TypeMirror, Schema> propertySchemaMap = recursion(type, schemaMap);

			if (processingUtils.isTypeInPackage(type, javaLangPackage)) {
				SimpleEntry<Type, Format> typeAndFormat = getJavaLangTypeAndFormat(type);
				if (typeAndFormat != null) {
					schema.setType(typeAndFormat.getKey());
					schema.setFormat(typeAndFormat.getValue());
				}
			} else {
				schema.setItems(Reference.forSchema(ProcessingUtils.getShortName(type)));
			}

			schemaMap.putAll(propertySchemaMap);

			schemaMap.put(typeMirror, schema);
		} else if (processingUtils.isAssignableTo(typeMirror, List.class)) {
			schema.setType(Type.ARRAY);

			TypeMirror type = processingUtils.removeEnclosingType(typeMirror, List.class)[0];
			Map<TypeMirror, Schema> propertySchemaMap = recursion(type, schemaMap);

			if (processingUtils.isTypeInPackage(type, javaLangPackage)) {
				SimpleEntry<Type, Format> typeAndFormat = getJavaLangTypeAndFormat(type);
				Schema typeSchema = new Schema();
				if (typeAndFormat != null) {
					typeSchema.setType(typeAndFormat.getKey());
					typeSchema.setFormat(typeAndFormat.getValue());
				}
				schema.setItems(typeSchema);
			} else if (types.asElement(type).getKind().equals(ElementKind.ENUM)) {
				Schema typeSchema = new Schema();
				typeSchema.setType(Type.STRING);

				types.asElement(type).getEnclosedElements()
					.stream()
					.filter(e -> ElementKind.ENUM_CONSTANT.equals(e.getKind()))
					.forEach(vElement -> typeSchema.addEnumValue(vElement.toString()));

				schema.setItems(typeSchema);
			} else {
				schema.setItems(Reference.forSchema(ProcessingUtils.getShortName(type)));
			}

			schemaMap.putAll(propertySchemaMap);

			schemaMap.put(typeMirror, schema);
		} else if (processingUtils.isAssignableTo(typeMirror, Set.class)) {
			schema.setType(Type.ARRAY);

			TypeMirror type = processingUtils.removeEnclosingType(typeMirror, Set.class)[0];
			Map<TypeMirror, Schema> propertySchemaMap = recursion(type, schemaMap);

			if (processingUtils.isTypeInPackage(type, javaLangPackage)) {
				SimpleEntry<Type, Format> typeAndFormat = getJavaLangTypeAndFormat(type);
				Schema typeSchema = new Schema();
				if (typeAndFormat != null) {
					typeSchema.setType(typeAndFormat.getKey());
					typeSchema.setFormat(typeAndFormat.getValue());
				}
				schema.setItems(typeSchema);
			} else if (types.asElement(type).getKind().equals(ElementKind.ENUM)) {
				Schema typeSchema = new Schema();
				typeSchema.setType(Type.STRING);

				types.asElement(type).getEnclosedElements()
					.stream()
					.filter(e -> ElementKind.ENUM_CONSTANT.equals(e.getKind()))
					.forEach(vElement -> typeSchema.addEnumValue(vElement.toString()));

				schema.setItems(typeSchema);
			} else {
				schema.setItems(Reference.forSchema(ProcessingUtils.getShortName(type)));
			}

			schemaMap.putAll(propertySchemaMap);

			schemaMap.put(typeMirror, schema);
		} else if (processingUtils.isAssignableTo(typeMirror, Map.class)) {
			schema.setType(Type.OBJECT);

			TypeMirror type = processingUtils.removeEnclosingType(typeMirror, Map.class)[1];
			Map<TypeMirror, Schema> propertySchemaMap = recursion(type, schemaMap);

			if (processingUtils.isTypeInPackage(type, javaLangPackage)) {
				SimpleEntry<Type, Format> typeAndFormat = getJavaLangTypeAndFormat(type);
				Schema typeSchema = new Schema();
				if (typeAndFormat != null) {
					typeSchema.setType(typeAndFormat.getKey());
					typeSchema.setFormat(typeAndFormat.getValue());
				}
				schema.setAdditionalProperties(typeSchema);
			} else {
				schema.setAdditionalProperties(Reference.forSchema(ProcessingUtils.getShortName(type)));
			}

			schemaMap.putAll(propertySchemaMap);

			schemaMap.put(typeMirror, schema);
		}

		return schemaMap;
	}

}
