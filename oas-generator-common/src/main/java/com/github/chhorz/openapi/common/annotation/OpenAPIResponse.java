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
package com.github.chhorz.openapi.common.annotation;

import java.lang.annotation.*;

/**
 * Structured annotation to document operation responses in a more structured way as the
 * Javadoc tag {@code @response}.
 *
 * @author chhorz
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.ANNOTATION_TYPE)
public @interface OpenAPIResponse {

	/**
	 * The status code of the response. Must be a valid status code of three numbers or a
	 * range: 1XX, 2XX, 3XX, 4XX or 5XX which represents all status codes between 100 and 199
	 * (https://spec.openapis.org/oas/v3.1.0#patterned-fields-0)
	 */
	String status();

	/**
	 * Class of the given response schema.
	 */
	Class<?> schema();

	/**
	 * Description of the response with the given status code and schema.
	 */
	String description();

}
