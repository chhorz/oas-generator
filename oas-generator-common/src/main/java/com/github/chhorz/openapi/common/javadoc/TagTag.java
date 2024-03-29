/**
 *
 *    Copyright 2018-2022 the original author or authors.
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
public class TagTag extends StructuredTag {

	private static final String TAG_NAME = "tag";

	private static final String OPENAPI_TAG_NAME = "tagName";

	/**
	 * Represents a tag in the following form: {@code @tag tag_name}
	 *
	 * @tag tag_name
	 */
	public TagTag() {
		super(TAG_NAME, new Segment(OPENAPI_TAG_NAME));
	}

	public String getTagName() {
		return getValues().get(OPENAPI_TAG_NAME);
	}
}
