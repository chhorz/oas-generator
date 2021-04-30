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
package com.github.chhorz.openapi.common.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * https://spec.openapis.org/oas/v3.1.0#media-type-object
 *
 * @author chhorz
 *
 */
public class MediaType {

	private Object schema;
	@Deprecated
	private Object example;
	@JsonProperty("examples")
	private Map<String, Example> exampleObjects;
	// @JsonProperty("examples")
	// private Map<String, Reference> exampleReferences;
	private Map<String, Encoding> encoding;

	public Object getSchema() {
		return schema;
	}

	public void setSchema(final Object schema) {
		this.schema = schema;
	}

}
