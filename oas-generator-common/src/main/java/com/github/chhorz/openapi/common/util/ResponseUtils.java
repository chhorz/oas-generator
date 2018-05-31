package com.github.chhorz.openapi.common.util;

import javax.lang.model.type.TypeMirror;

import com.github.chhorz.openapi.common.domain.MediaType;
import com.github.chhorz.openapi.common.domain.Response;
import com.github.chhorz.openapi.common.domain.Schema;

public class ResponseUtils {

	public Response mapTypeMirrorToResponse(final TypeMirror typeMirror, final String[] produces) {
		Response response = new Response();
		response.setDescription("");

		MediaType mediaType = new MediaType();
		mediaType.setSchemaReference(ReferenceUtils.createSchemaReference(typeMirror));

		if (produces.length == 0) {
			response.putContent("*/*", mediaType);
		} else {
			for (String producesHeader : produces) {
				response.putContent(producesHeader, mediaType);
			}
		}

		return response;
	}

	public Response mapSchemaToResponse(final Schema schema, final String[] produces) {
		Response response = new Response();
		response.setDescription("");

		MediaType mediaType = new MediaType();
		mediaType.setSchemaObject(schema);

		if (produces.length == 0) {
			response.putContent("*/*", mediaType);
		} else {
			for (String producesHeader : produces) {
				response.putContent(producesHeader, mediaType);
			}
		}

		return response;
	}

}
