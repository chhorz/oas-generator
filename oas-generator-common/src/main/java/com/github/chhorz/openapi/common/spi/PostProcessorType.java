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
package com.github.chhorz.openapi.common.spi;

/**
 * Enumeration of post processor types. Each type will be called at a different step of the generation process.
 *
 * @author chhorz
 */
public enum PostProcessorType {

	/**
	 * Processing of the internal domain object.
	 */
	DOMAIN_OBJECT,

	/**
	 * Processing type for JSON content as string.
	 */
	JSON_STRING,

	/**
	 * Processing type for YAML content as string.
	 */
	YAML_STRING,

	/**
	 * Processing type for generated JSON file.
	 */
	JSON_FILE,

	/**
	 * Processing type for generated YAML file.
	 */
	YAML_FILE;

}
