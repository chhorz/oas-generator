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
import com.github.chhorz.openapi.common.util.LogUtils;

/**
 * Utility class to merge request bodies.
 *
 * @author chhorz
 */
public class RequestBodyUtils extends AbstractMergeUtils {

	/**
	 * Creates a new instance with a given {@link LogUtils} instance.
	 *
	 * @param logUtils the current logger
	 */
	public RequestBodyUtils(LogUtils logUtils) {
		super(logUtils);
	}

	/**
	 * Creates a new {@link RequestBody} instance with combined values form both parameter.
	 *
	 * @param requestBodyOne the first request body
	 * @param requestBodyTwo the second request body
	 * @return the combined request body
	 */
	public RequestBody mergeRequestBodies(RequestBody requestBodyOne, RequestBody requestBodyTwo) {
		logUtils.logDebug("Merging request bodies");

		RequestBody mergedRequestBody = new RequestBody();
		mergedRequestBody.setDescription(mergeDocumentation(requestBodyOne.getDescription(), requestBodyTwo.getDescription()));
		mergedRequestBody.setRequired(requestBodyOne.getRequired() || requestBodyTwo.getRequired());
		requestBodyOne.getContent().forEach(mergedRequestBody::putContent);
		requestBodyTwo.getContent().forEach(mergedRequestBody::putContent);
		return mergedRequestBody;
	}

}
