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

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.List;

public class TypeMirrorUtils {

	private final Elements elements;
	private final Types types;

	private final LogUtils logUtils;

	public TypeMirrorUtils(final Elements elements, final Types types, final LogUtils logUtils) {
		this.elements = elements;
		this.types = types;
		this.logUtils = logUtils;
	}

	public TypeMirror[] removeEnclosingType(final TypeMirror originalReturnType, final Class<?> removableClass) {
		// The given type has to be assignable to the type of the class: List<String> is assignable to List.class
		if (types.isAssignable(types.erasure(originalReturnType), createTypeMirror(removableClass))) {
			if (originalReturnType instanceof DeclaredType) {
				List<? extends TypeMirror> typeArguments = ((DeclaredType) originalReturnType).getTypeArguments();
				if (typeArguments != null && !typeArguments.isEmpty()) {
					return typeArguments.stream()
							.filter(TypeMirror.class::isInstance)
							.map(TypeMirror.class::cast)
							.toArray(TypeMirror[]::new);
				}
			}
		}
		return new TypeMirror[] { originalReturnType };
	}

	private TypeMirror createTypeMirror(final Class<?> clazz) {
		return elements.getTypeElement(clazz.getCanonicalName()).asType();
	}

	public TypeMirror createTypeMirrorFromString(final String typeString) {
		TypeMirror typeMirror = null;

		if (typeString.contains("<")) {
			// TODO handle types of List<String>
		} else if (typeString.endsWith("[]")) {
			TypeElement typeElement = elements.getTypeElement(typeString.substring(0, typeString.length() - 2));
			if (typeElement == null) {
				logUtils.logError("No class type found for %s", typeString);
			} else {
				typeMirror = types.getArrayType(typeElement.asType());
			}
		} else {
			TypeElement typeElement = elements.getTypeElement(typeString);
			if (typeElement == null) {
				logUtils.logError("No class type found for %s", typeString);
			} else {
				typeMirror = typeElement.asType();
			}
		}

		return typeMirror;
	}

}
