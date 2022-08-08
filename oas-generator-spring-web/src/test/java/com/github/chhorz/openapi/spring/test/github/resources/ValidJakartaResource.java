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
package com.github.chhorz.openapi.spring.test.github.resources;

import jakarta.validation.constraints.*;

import java.util.List;

/**
 * a test resource for GitHub issue tests
 */
public class ValidJakartaResource {
	/**
	 * some test value
	 */
	@NotNull
	@Pattern(regexp = "\\s+")
	public String value;
	/**
	 * some zero or positive value
	 */
	@Min(0)
	public Long minimum;

	@Size(min = 10)
	public String sizedString;

	@Size(min = 1, max = 5)
	public List<String> sizedList;

	@NotEmpty
	public String nonEmptyString;
}
