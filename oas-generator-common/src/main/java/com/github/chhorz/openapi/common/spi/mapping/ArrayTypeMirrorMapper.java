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
import com.github.chhorz.openapi.common.util.ProcessingUtils;

import javax.lang.model.element.Element;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ArrayTypeMirrorMapper extends AbstractTypeMirrorMapper {

	@Override
	public boolean test(TypeMirror typeMirror) {
		return TypeKind.ARRAY.equals(typeMirror.getKind());
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

		schema.setType(Schema.Type.ARRAY);

		TypeMirror componentType;
		if (typeMirror instanceof ArrayType) {
			componentType = ((ArrayType) typeMirror).getComponentType();
		} else {
			componentType = elements.getTypeElement(typeMirror.toString().replaceAll("\\[]", "")).asType();
		}
		Map<TypeMirror, Schema> propertySchemaMap = recursion(componentType, schemaMap);

		if (componentType.getKind().isPrimitive()) {
			AbstractMap.SimpleEntry<Schema.Type, Schema.Format> typeAndFormat = getPrimitiveTypeAndFormat(componentType);
			Schema typeSchema = new Schema();
			if (typeAndFormat != null) {
				typeSchema.setType(typeAndFormat.getKey());
				typeSchema.setFormat(typeAndFormat.getValue());
			}
			schema.setItems(typeSchema);
		} else if (processingUtils.isTypeInPackage(componentType, javaLangPackage)) {
			AbstractMap.SimpleEntry<Schema.Type, Schema.Format> typeAndFormat = getJavaLangTypeAndFormat(componentType);
			Schema typeSchema = new Schema();
			if (typeAndFormat != null) {
				typeSchema.setType(typeAndFormat.getKey());
				typeSchema.setFormat(typeAndFormat.getValue());
			}
			schema.setItems(typeSchema);
		} else {
			schema.setItems(Reference.forSchema(ProcessingUtils.getShortName(componentType)));
		}

		schemaMap.putAll(propertySchemaMap);

		schemaMap.put(typeMirror, schema);
		return schemaMap;
	}
}
