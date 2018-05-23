package com.github.chhorz.openapi.common.util;

import java.util.Objects;

import javax.lang.model.type.TypeMirror;

import com.github.chhorz.openapi.common.domain.Reference;

public class ReferenceUtils {

	private static String REF_FORMAT = "#/components/%s/%s";

	private static String REQUEST_BODIES = "requestBodies";
	private static String SCHEMAS = "schemas";

	public static Reference createSchemaReference(final TypeMirror typeMirror) {
		Objects.requireNonNull(typeMirror, "TypeMirror must not be null.");
		return new Reference(String.format(REF_FORMAT, SCHEMAS, shortName(typeMirror)));
	}

	public static Reference createRequestBodyReference(final TypeMirror typeMirror) {
		Objects.requireNonNull(typeMirror, "TypeMirror must not be null.");
		return new Reference(String.format(REF_FORMAT, REQUEST_BODIES, shortName(typeMirror)));
	}

	private static String shortName(final TypeMirror typeMirror) {
		final String type = getType(typeMirror);
		return type.substring(type.lastIndexOf('.') + 1);
	}

	private static String getType(final TypeMirror typeMirror) {
		String typeString = typeMirror.toString();
		while (typeString.contains("<")) {
			typeString = typeString.substring(typeString.indexOf('<') + 1, typeString.indexOf('>'));
		}
		return typeString.substring(typeString.lastIndexOf('.') + 1);
	}

}
