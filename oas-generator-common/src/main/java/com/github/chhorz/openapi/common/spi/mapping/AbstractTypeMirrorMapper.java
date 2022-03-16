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

import com.github.chhorz.javadoc.JavaDocParser;
import com.github.chhorz.javadoc.JavaDocParserBuilder;
import com.github.chhorz.javadoc.OutputType;
import com.github.chhorz.openapi.common.domain.Schema;
import com.github.chhorz.openapi.common.domain.Schema.Format;
import com.github.chhorz.openapi.common.domain.Schema.Type;
import com.github.chhorz.openapi.common.properties.domain.ParserProperties;
import com.github.chhorz.openapi.common.util.LogUtils;
import com.github.chhorz.openapi.common.util.ProcessingUtils;

import javax.lang.model.element.PackageElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.AbstractMap.SimpleEntry;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractTypeMirrorMapper implements TypeMirrorMapper {

	protected Elements elements;
	protected Types types;
	protected LogUtils logUtils;
	protected ParserProperties parserProperties;
	protected ProcessingUtils processingUtils;
	private List<TypeMirrorMapper> typeMirrorMappers;

	protected JavaDocParser parser;

	protected PackageElement javaLangPackage;
	protected PackageElement javaMathPackage;
	protected PackageElement javaTimePackage;


	@Override
	public void setup(Elements elements, Types types, LogUtils logUtils, ParserProperties parserProperties, List<TypeMirrorMapper> typeMirrorMappers) {
		this.elements = elements;
		this.types = types;
		this.logUtils = logUtils;
		this.parserProperties = parserProperties;
		this.processingUtils = new ProcessingUtils(elements, types, logUtils);
		this.typeMirrorMappers = typeMirrorMappers;

		parser = JavaDocParserBuilder.withBasicTags().withOutputType(OutputType.HTML).build();

		javaLangPackage = elements.getPackageElement("java.lang");
		javaMathPackage = elements.getPackageElement("java.math");
		javaTimePackage = elements.getPackageElement("java.time");
	}

	protected Map<TypeMirror, Schema> recursion(TypeMirror typeMirror, Map<TypeMirror, Schema> parsedSchemaMap){

		Map<TypeMirror, Schema> schemaMap = new LinkedHashMap<>();
		if (parsedSchemaMap.entrySet().stream().anyMatch(entry -> entry.getKey().toString().equals(typeMirror.toString()))) {
			return parsedSchemaMap;
		} else {
			parsedSchemaMap.entrySet().stream()
				.filter(e -> e.getValue().getType().equals(Type.OBJECT) || e.getValue().getType().equals(Type.ENUM))
				.forEach(entry -> schemaMap.put(entry.getKey(), entry.getValue()));
		}

		typeMirrorMappers.stream()
			.filter(mapper -> mapper.test(typeMirror))
			.findFirst()
			.map(mapper -> mapper.map(typeMirror, parsedSchemaMap))
			.ifPresent(schemaMap::putAll);

		return schemaMap;
	}

	protected SimpleEntry<Type, Format> getPrimitiveTypeAndFormat(final TypeMirror typeMirror) {
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

	protected SimpleEntry<Type, Format> getJavaLangTypeAndFormat(final TypeMirror typeMirror) {
		SimpleEntry<Type, Format> typeAndFormat = null;

		if (processingUtils.isSameType(typeMirror, String.class)) {
			typeAndFormat = new SimpleEntry<>(Type.STRING, null);
		}

		try {
			typeAndFormat = getPrimitiveTypeAndFormat(types.unboxedType(typeMirror));
		} catch (IllegalArgumentException e) {
			// TODO: handle finally clause
		}

		return typeAndFormat;
	}
}
