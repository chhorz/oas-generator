package com.github.chhorz.openapi.common.util;

import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

import com.github.chhorz.javadoc.JavaDoc;
import com.github.chhorz.openapi.common.domain.MediaType;
import com.github.chhorz.openapi.common.domain.Response;
import com.github.chhorz.openapi.common.domain.Responses;
import com.github.chhorz.openapi.common.domain.Schema;
import com.github.chhorz.openapi.common.javadoc.ResponseTag;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class ResponseUtils {

	private Elements elements;

	public ResponseUtils(final Elements elements) {
		this.elements = elements;
	}

	public Response mapTypeMirrorToResponse(final TypeMirror typeMirror, final String[] produces) {
		Response response = new Response();
		response.setDescription("");

		MediaType mediaType = new MediaType();
		mediaType.setSchema(ReferenceUtils.createSchemaReference(typeMirror));

		if (produces == null || produces.length == 0) {
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
		mediaType.setSchema(schema);

		if (produces == null || produces.length == 0) {
			response.putContent("*/*", mediaType);
		} else {
			for (String producesHeader : produces) {
				response.putContent(producesHeader, mediaType);
			}
		}

		return response;
	}

	public Responses initializeFromJavadoc(final JavaDoc javaDoc, final String[] produces) {
		Responses responses = new Responses();

		if (javaDoc != null) {
			Predicate<String> nonNullOrEmpty = s -> s != null && !s.isEmpty();
			List<ResponseTag> responseTags = javaDoc.getTags(ResponseTag.class);
			responseTags.stream()
					.filter(tag -> nonNullOrEmpty.test(tag.getStatusCode()))
					.filter(tag -> nonNullOrEmpty.test(tag.getResponseType()))
					.forEach(responseTag -> {
						TypeMirror responseType = elements.getTypeElement(responseTag.getResponseType()).asType();
						Response response = mapTypeMirrorToResponse(responseType, produces);
						responses.putResponseResponse(responseTag.getStatusCode(), response);
					});
		}

		return responses;
	}

}
