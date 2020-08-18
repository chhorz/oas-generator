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

import java.util.Objects;

import static java.lang.String.format;

/**
 * https://spec.openapis.org/oas/v3.0.3#reference-object
 *
 * @author chhorz
 *
 */
public class Reference {

	private static final String SCHEMA_REFERENCE_FORMAT = "#/components/schemas/%s";
	private static final String REQUEST_BODY_REFERENCE_FORMAT = "#/components/requestBodies/%s";

	@Required
	private String $ref;

	public Reference() {
	}

	public Reference(final String $ref) {
		this.$ref = $ref;
	}

	/**
	 * Creates a new schema reference.
	 *
	 * @param referenceString the schema to create a reference for
	 * @return the OpenAPI {@link Reference}
	 */
	public static Reference forSchema(final String referenceString) {
		Objects.requireNonNull(referenceString, "Reference string must not be null.");
		return new Reference(format(SCHEMA_REFERENCE_FORMAT, referenceString));
	}

	/**
	 * Creates a new reference for a request body.
	 *
	 * @param referenceString the response body to create a reference for
	 * @return the OpenAPI {@link Reference}
	 */
	public static Reference forRequestBody(final String referenceString) {
		Objects.requireNonNull(referenceString, "Reference string must not be null.");
		return new Reference(format(REQUEST_BODY_REFERENCE_FORMAT, referenceString));
	}

	public void set$ref(String $ref) {
		this.$ref = $ref;
	}

	public String get$ref() {
		return $ref;
	}

	@Override
	public String toString() {
		return "Reference [$ref=" + $ref + "]";
	}

}
