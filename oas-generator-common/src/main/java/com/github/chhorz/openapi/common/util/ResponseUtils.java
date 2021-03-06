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

import com.github.chhorz.javadoc.JavaDoc;
import com.github.chhorz.openapi.common.annotation.OpenAPI;
import com.github.chhorz.openapi.common.domain.MediaType;
import com.github.chhorz.openapi.common.domain.Response;
import com.github.chhorz.openapi.common.domain.Schema;
import com.github.chhorz.openapi.common.javadoc.ResponseTag;
import com.github.chhorz.openapi.common.properties.domain.ParserProperties;

import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ResponseUtils {

	private static final Predicate<String> NOT_NULL_OR_EMPTY = s -> s != null && !s.isEmpty();

	private final SchemaUtils schemaUtils;
	private final ProcessingUtils processingUtils;

	public ResponseUtils(final Elements elements, final Types types, final ParserProperties parserProperties, final LogUtils logUtils) {
		this.schemaUtils = new SchemaUtils(elements, types, parserProperties, logUtils);
		this.processingUtils = new ProcessingUtils(elements, types, logUtils);
	}

	public Response fromTypeMirror(final TypeMirror typeMirror, final String[] produces, String description) {
		Response response = new Response();
		response.setDescription(description != null ? description : "");

		if (typeMirror != null && !processingUtils.isAssignableTo(typeMirror, Void.class)) {
			MediaType mediaType = schemaUtils.createMediaType(typeMirror);

			if (produces == null || produces.length == 0) {
				response.putContent("*/*", mediaType);
			} else {
				for (String producesHeader : produces) {
					response.putContent(producesHeader, mediaType);
				}
			}
		}

		return response;
	}

	public Response fromSchema(final Schema schema, final String[] produces, String description) {
		Response response = new Response();
		response.setDescription(description != null ? description : "");

		if (schema.getType() != null) {
			MediaType mediaType = new MediaType();
			mediaType.setSchema(schema);

			if (produces == null || produces.length == 0) {
				response.putContent("*/*", mediaType);
			} else {
				for (String producesHeader : produces) {
					response.putContent(producesHeader, mediaType);
				}
			}
		}

		return response;
	}

	public Map<String, Response> initializeFromJavadoc(final JavaDoc javaDoc, final OpenAPI openApi, final String[] produces,
		final String description, Map<TypeMirror, Schema> schemaMap) {
		Map<String, Response> responses = new TreeMap<>();

		if (javaDoc != null) {
			javaDoc.getTags(ResponseTag.class)
					.stream()
					.filter(tag -> NOT_NULL_OR_EMPTY.test(tag.getStatusCode()))
					.filter(tag -> NOT_NULL_OR_EMPTY.test(tag.getResponseType()))
					.forEach(responseTag -> {
						final String responseDescription = NOT_NULL_OR_EMPTY.test(responseTag.getDescription()) ? responseTag.getDescription() : description;

						final TypeMirror responseType;
						if (responseTag.getResponseType().contains(".")) {
							// assume we have a FQN of a java type
							responseType = processingUtils.createTypeMirrorFromString(responseTag.getResponseType());
						} else {
							if (responseTag.getResponseType().contains("[]")) {
								String simpleResponseType = responseTag.getResponseType().replace("[]", "");

								// assume we have only the java class name
								responseType = schemaMap.keySet()
										.stream()
										.filter(key -> schemaMap.get(key).getType().equals(Schema.Type.ARRAY))
										.filter(key -> key.toString().substring(key.toString().lastIndexOf(".") + 1, key.toString().length() - 1).equalsIgnoreCase(simpleResponseType))
										.findAny()
										.orElse(null);
							} else {
								String simpleResponseType = responseTag.getResponseType();

								// assume we have only the java class name
								responseType = schemaMap.keySet()
										.stream()
										.filter(key -> key.toString().substring(key.toString().lastIndexOf(".") + 1).equalsIgnoreCase(simpleResponseType))
										.findAny()
										.orElse(null);
							}
						}

						responses.put(responseTag.getStatusCode(), fromTypeMirror(responseType, produces, responseDescription));
					});
		}

		if (openApi != null) {
			Stream.of(openApi.responses())
				.forEach(responseAnnotation -> {
					TypeMirror responseType = null;
					try {
						// the following line will throw an exception
						responseAnnotation.schema();
					} catch (MirroredTypeException exception) {
						// handle the thrown exception and get type mirror from the thrown exception
						responseType = exception.getTypeMirror();
					}
					Response response = fromTypeMirror(responseType, produces, responseAnnotation.description());
					responses.put(responseAnnotation.status(), response);
				});
		}

		return responses;
	}

}
