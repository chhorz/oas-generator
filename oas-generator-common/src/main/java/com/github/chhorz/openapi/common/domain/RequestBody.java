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

import com.github.chhorz.openapi.common.domain.meta.Required;

import java.util.Map;
import java.util.TreeMap;

/**
 * https://spec.openapis.org/oas/v3.1.0#request-body-object
 *
 * @author chhorz
 *
 */
public class RequestBody {

	private String description;
	@Required
	private Map<String, MediaType> content;
	private Boolean required = Boolean.FALSE;

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public Map<String, MediaType> getContent() {
		return content;
	}

	public void putContent(final String key, final MediaType mediaType) {
		if (content == null) {
			content = new TreeMap<>();
		}
		content.put(key, mediaType);
	}

	public Boolean getRequired() {
		return required;
	}

	public void setRequired(final Boolean required) {
		this.required = required;
	}

}
