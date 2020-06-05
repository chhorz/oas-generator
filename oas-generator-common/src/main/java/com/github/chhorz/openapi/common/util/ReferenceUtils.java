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

import java.util.Objects;

import javax.lang.model.type.TypeMirror;

import com.github.chhorz.openapi.common.domain.Reference;

public class ReferenceUtils {

	private static final String REF_FORMAT = "#/components/%s/%s";

	private static final String REQUEST_BODIES = "requestBodies";
	private static final String SCHEMAS = "schemas";

	public static Reference createSchemaReference(final TypeMirror typeMirror) {
		Objects.requireNonNull(typeMirror, "TypeMirror must not be null.");
		return new Reference(String.format(REF_FORMAT, SCHEMAS, shortName(typeMirror)));
	}

	public static Reference createRequestBodyReference(final TypeMirror typeMirror) {
		Objects.requireNonNull(typeMirror, "TypeMirror must not be null.");
		return new Reference(String.format(REF_FORMAT, REQUEST_BODIES, shortName(typeMirror)));
	}

	private static String shortName(final TypeMirror typeMirror) {
		String typeString = typeMirror.toString();
		// type mirrors with annotation types like: (@javax.validation.Valid :: com.github.chhorz.openapi.spring.test.github.resources.Resource)
		if (typeString.contains("::")) {
			typeString = typeString.substring(typeString.indexOf("::") + 2, typeString.indexOf(')'));
		}
		// remove generic types
		while (typeString.contains("<")) {
			typeString = typeString.substring(typeString.indexOf('<') + 1, typeString.indexOf('>'));
		}
		return typeString.substring(typeString.lastIndexOf('.') + 1).trim();
	}

}
