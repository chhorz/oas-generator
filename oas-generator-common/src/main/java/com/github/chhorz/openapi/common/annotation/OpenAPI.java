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
 * This annotation is a more structured way to document data that could be added with custom Javadoc
 * tags. Values from this annotation have precedence over the Javadoc values. If values from this
 * annotation are empty, data from the Javadoc comment will be used.
 *
 * @author chhorz
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface OpenAPI {

	/**
	 * List of OpenAPI tags that are described in the {@code oas-generator.yml} file.
	 * <p>
	 * If empty the Javadoc tags {@code @category} and {@code @tag} will be evaluated.
	 */
	String[] tags() default {};

	/**
	 * List of security scheme names from the {@code oas-generator.yml} file
	 * <p>
	 * If empty the Javadoc tags {@code @security} will be evaluated.
	 */
	String[] securitySchemes() default {};

	/**
	 * List of responses for the given method.
	 * <p>
	 * If empty the Javadoc tags {@code @response} will be evaluated.
	 */
	OpenAPIResponse[] responses() default {};

}
