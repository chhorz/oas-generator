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
package com.github.chhorz.openapi.common.javadoc;

import com.github.chhorz.javadoc.tags.StructuredTag;

/**
 * Custom Javadoc tag for structured parsing.
 *
 * @see com.github.chhorz.javadoc.JavaDocParser
 *
 * @author chhorz
 */
public class ResponseTag extends StructuredTag {

	private static final String TAG_NAME = "response";

	private static final String STATUS_CODE = "statusCode";
	private static final String RESPONSE_TYPE = "responseType";
	private static final String DESCRIPTION = "description";

	/**
	 * Represents a tag in the following form: {@code @response 200 String the content as string}
	 *
	 * @response 200 String the content as string
	 */
	public ResponseTag() {
		super(TAG_NAME, STATUS_CODE, RESPONSE_TYPE , DESCRIPTION);
	}

	public String getStatusCode() {
		return getValues().get(STATUS_CODE);
	}

	public String getResponseType() {
		return getValues().get(RESPONSE_TYPE);
	}

	public String getDescription() {
		return getValues().get(DESCRIPTION);
	}

}
