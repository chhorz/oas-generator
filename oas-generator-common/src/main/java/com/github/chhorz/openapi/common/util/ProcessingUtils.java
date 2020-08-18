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

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.List;

public class ProcessingUtils {

	private final Elements elements;
	private final Types types;

	private final LogUtils logUtils;

	public ProcessingUtils(final Elements elements, final Types types, final LogUtils logUtils) {
		this.elements = elements;
		this.types = types;
		this.logUtils = logUtils;
	}

	/**
	 * Check if both types are not equal. The check consists of {@link Types#isSameType(TypeMirror, TypeMirror)}
	 * and a string comparison
	 *
	 * @param typeMirrorOne the first type
	 * @param typeMirrorTwo the second type
	 * @return {@code true} if both types are equal
	 */
	public boolean doesTypeDiffer(final TypeMirror typeMirrorOne, final TypeMirror typeMirrorTwo) {
		return !types.isSameType(typeMirrorOne, typeMirrorTwo) && !typeMirrorOne.toString().equalsIgnoreCase(typeMirrorTwo.toString());
	}

	/**
	 * Checks if the given type is of a certain class.
	 *
	 * @param typeMirror the type that should be checked
	 * @param clazz the requested class
	 * @return {@code true} if the given type is of the given class
	 */
	public boolean isSameType(final TypeMirror typeMirror, final Class<?> clazz) {
		return types.isSameType(typeMirror, elements.getTypeElement(clazz.getCanonicalName()).asType());
	}

	/**
	 * Checks if the given type is assignable to the type of a given class.
	 *
	 * @param typeMirror the type that should be checked
	 * @param clazz the requested class
	 * @return {@code true} if the type is assignable to the class type
	 */
	public boolean isAssignableTo(final TypeMirror typeMirror, final Class<?> clazz) {
		return types.isAssignable(types.erasure(typeMirror), elements.getTypeElement(clazz.getCanonicalName()).asType());
	}

	/**
	 * Checks if the given type resides within the given package.
	 *
	 * @param typeMirror the requested type
	 * @param packageElement the given package
	 * @return {@code true} if the type resides in the given package
	 */
	public boolean isTypeInPackage(final TypeMirror typeMirror, final PackageElement packageElement) {
		return types.asElement(typeMirror).getEnclosingElement().toString().equals(packageElement.toString());
	}

	/**
	 * Checks if the given type belongs to an interface element.
	 *
	 * @param typeMirror the requested type
	 * @return {@code true} if the type is an interface
	 */
	public boolean isInterface(final TypeMirror typeMirror){
		return ElementKind.INTERFACE.equals(types.asElement(typeMirror).getKind());
	}

	public TypeMirror[] removeEnclosingType(final TypeMirror originalReturnType, final Class<?> removableClass) {
		// The given type has to be assignable to the type of the class: List<String> is assignable to List.class
		if (isAssignableTo(types.erasure(originalReturnType), removableClass)) {
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

	public static String getShortName(final TypeMirror typeMirror) {
		String typeString = typeMirror.toString();
		// type mirrors with annotation types like: (@javax.validation.Valid :: com.github.chhorz.openapi.spring.test.github.resources.Resource)
		if (typeString.contains("::")) {
			typeString = typeString.substring(typeString.indexOf("::") + 2, typeString.indexOf(')'));
		}
		// remove generic types
		while (typeString.contains("<")) {
			typeString = typeString.substring(typeString.indexOf('<') + 1, typeString.lastIndexOf('>'));
		}
		// get last part of the type string
		return typeString.substring(typeString.lastIndexOf('.') + 1).trim();
	}

}
