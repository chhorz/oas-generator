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

import com.github.chhorz.openapi.common.domain.meta.Markdown;
import com.github.chhorz.openapi.common.domain.meta.Required;

/**
 * https://spec.openapis.org/oas/v3.1.0#security-scheme-object
 *
 * @author chhorz
 *
 */
public class SecurityScheme {

	@Required
	private Type type;
	@Markdown
	private String description;

	public enum Type {
		apiKey,
		http,
		mutualTLS,
		oauth2,
		openIdConnect
	}

	public Type getType() {
		return type;
	}

	public void setType(final Type type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

}
