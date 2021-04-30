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

import java.util.stream.Stream;

/**
 * https://spec.openapis.org/oas/v3.1.0#api-key-sample
 *
 * @author chhorz
 *
 */
public class SecuritySchemeApiKey extends SecurityScheme {

	@Required
	private String name;
	@Required
	private In in;

	public enum In {
		query,
		header,
		cookie;

		public static In of(String value) {
			return Stream.of(values())
				.filter(enumValue -> enumValue.name().equalsIgnoreCase(value))
				.findFirst()
				.orElseThrow(() -> new RuntimeException("Invalid value for enumeration SecuritySchemeApiKey#In"));
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public In getIn() {
		return in;
	}

	public void setIn(In in) {
		this.in = in;
	}

}
