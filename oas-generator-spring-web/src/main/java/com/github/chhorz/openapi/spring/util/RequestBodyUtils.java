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
package com.github.chhorz.openapi.spring.util;

import com.github.chhorz.openapi.common.domain.RequestBody;

import java.util.function.Predicate;

import static java.lang.String.format;

public class RequestBodyUtils {

	private static final Predicate<String> PRESENCE = s -> s != null && !s.isEmpty();

	public RequestBody mergeRequestBodies(RequestBody requestBodyOne, RequestBody requestBodyTwo) {
		RequestBody mergedRequestBody = new RequestBody();
		mergedRequestBody.setDescription(mergeDocumentation(requestBodyOne.getDescription(), requestBodyTwo.getDescription()));
		mergedRequestBody.setRequired(requestBodyOne.getRequired() || requestBodyTwo.getRequired());
		requestBodyOne.getContent().forEach(mergedRequestBody::putContent);
		requestBodyTwo.getContent().forEach(mergedRequestBody::putContent);
		return mergedRequestBody;
	}

	private String mergeDocumentation(String documentationOne, String documentationTwo) {
		if (PRESENCE.test(documentationOne) && PRESENCE.test(documentationTwo) && documentationOne.equalsIgnoreCase(documentationTwo)) {
			return documentationOne;
		} else if (PRESENCE.test(documentationOne) && PRESENCE.test(documentationTwo)) {
			return format("%s\n<hr>\n%s", documentationOne, documentationTwo).trim();
		} else if (PRESENCE.test(documentationOne) || PRESENCE.test(documentationTwo)) {
			return format("%s\n\n%s", documentationOne, documentationTwo).trim();
		} else {
			return "";
		}
	}

}
