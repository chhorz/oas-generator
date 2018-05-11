package com.github.chhorz.openapi.common.util;

import java.util.List;

import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public class TypeMirrorUtils {

	private Elements elements;
	private Types types;

	public TypeMirrorUtils(final Elements elements, final Types types) {
		this.elements = elements;
		this.types = types;
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
							.toArray(size -> new TypeMirror[size]);
				}
			}
		}
		return new TypeMirror[] { originalReturnType };
	}

	public TypeMirror createTypeMirror(final Class<?> clazz) {
		return elements.getTypeElement(clazz.getCanonicalName()).asType();
	}

}
