package com.github.chhorz.openapi.common.util;

import javax.lang.model.type.TypeMirror;

import com.github.chhorz.openapi.common.domain.Reference;

public class ReferenceUtils {

	private static String REF_FORMAT = "#/components/%s/%s";

	private static String REQUEST_BODIES = "requestBodies";
	private static String SCHEMAS = "schemas";

	public static Reference createSchemaReference(final TypeMirror typeMirror) {
		String reference = typeMirror.toString().substring(typeMirror.toString().lastIndexOf('.') + 1);
		return new Reference(String.format(REF_FORMAT, SCHEMAS, reference));
	}

	public static Reference createRequestBodyReference(final TypeMirror typeMirror) {
		String reference = typeMirror.toString().substring(typeMirror.toString().lastIndexOf('.') + 1);
		return new Reference(String.format(REF_FORMAT, REQUEST_BODIES, reference));
	}

}
