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

import com.github.chhorz.openapi.common.domain.Components;
import com.github.chhorz.openapi.common.domain.Schema;
import com.github.chhorz.openapi.common.exception.SpecificationViolationException;

import javax.lang.model.type.TypeMirror;
import java.util.Map;
import java.util.TreeMap;

public class ComponentUtils {

	public static Map<String, Schema> convertSchemaMap(Map<TypeMirror, Schema> typeMirrorSchemaMap){
		Map<String, Schema> stringSchemaMap = new TreeMap<>();
		typeMirrorSchemaMap.forEach((typeMirror, schema) -> {
			String typeString = getKey(typeMirror);
			if (stringSchemaMap.containsKey(typeString)) {
				Schema existingType = stringSchemaMap.get(typeString);
				if (existingType.getType().equals(schema.getType())) {
					stringSchemaMap.put(typeString, SchemaUtils.mergeSchemas(existingType, schema));
				}
			} else {
				stringSchemaMap.put(typeString, schema);
			}
		});
		return stringSchemaMap;
	}

	public static String getKey(final TypeMirror typeMirror) {
		final String key = ProcessingUtils.getShortName(typeMirror);

		if (!Components.isValidKey(key)) {
			throw new SpecificationViolationException("The current key '" + key + "' is not valid within component maps.");
		}

		return key;
	}

}
