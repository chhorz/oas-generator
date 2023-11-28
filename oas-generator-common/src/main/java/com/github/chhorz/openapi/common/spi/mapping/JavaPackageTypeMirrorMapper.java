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

import com.github.chhorz.javadoc.JavaDoc;
import com.github.chhorz.openapi.common.domain.Schema;
import com.github.chhorz.openapi.common.domain.Schema.Format;
import com.github.chhorz.openapi.common.domain.Schema.Type;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import java.time.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.LinkedHashMap;
import java.util.Map;

public class JavaPackageTypeMirrorMapper extends AbstractTypeMirrorMapper {

	@Override
	public boolean test(TypeMirror typeMirror) {
		return processingUtils.isTypeInPackage(typeMirror, javaLangPackage)
			|| processingUtils.isTypeInPackage(typeMirror, javaMathPackage)
			|| processingUtils.isTypeInPackage(typeMirror, javaTimePackage);
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

		if (processingUtils.isTypeInPackage(typeMirror, javaLangPackage)) {
			JavaDoc javaDoc = parser.parse(elements.getDocComment(element));
			schema.setDescription(javaDoc.getDescription());

			SimpleEntry<Type, Format> typeAndFormat = getJavaLangTypeAndFormat(typeMirror);
			if (typeAndFormat != null) {
				schema.setType(typeAndFormat.getKey());
				schema.setFormat(typeAndFormat.getValue());
			}

			schemaMap.put(typeMirror, schema);
		} else if (processingUtils.isTypeInPackage(typeMirror, javaMathPackage)) {
			JavaDoc javaDoc = parser.parse(elements.getDocComment(element));
			schema.setDescription(javaDoc.getDescription());

			schema.setType(Type.NUMBER);
			schema.setFormat(Format.DOUBLE);

			schemaMap.put(typeMirror, schema);
		} else if (processingUtils.isTypeInPackage(typeMirror, javaTimePackage)) {
			JavaDoc javaDoc = parser.parse(elements.getDocComment(element));
			schema.setDescription(javaDoc.getDescription());

			SimpleEntry<Type, Format> typeAndFormat = getJavaTimeTypeAndFormat(typeMirror);
			if (typeAndFormat != null) {
				schema.setType(typeAndFormat.getKey());
				schema.setFormat(typeAndFormat.getValue());
			}
			schemaMap.put(typeMirror, schema);
		}

		return schemaMap;
	}

	private SimpleEntry<Type, Format> getJavaTimeTypeAndFormat(final TypeMirror typeMirror) {
		SimpleEntry<Type, Format> typeAndFormat = null;

		if (processingUtils.isSameType(typeMirror, LocalTime.class)) {
			typeAndFormat = new SimpleEntry<>(Type.STRING, Format.TIME);
		} else if (processingUtils.isSameType(typeMirror, LocalDate.class)) {
			typeAndFormat = new SimpleEntry<>(Type.STRING, Format.DATE);
		} else if (processingUtils.isSameType(typeMirror, LocalDateTime.class)
			|| processingUtils.isSameType(typeMirror, ZonedDateTime.class)
			|| processingUtils.isSameType(typeMirror, OffsetDateTime.class)) {
			typeAndFormat = new SimpleEntry<>(Type.STRING, Format.DATE_TIME);
		}

		return typeAndFormat;
	}
}
