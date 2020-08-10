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

import com.github.chhorz.openapi.common.properties.domain.ParserProperties;
import com.github.chhorz.openapi.common.util.LogUtils;

/**
 * Instances of this interface will be used to create post processor instances.
 *
 * @author chhorz
 */
public interface PostProcessorProvider {

	/**
	 * Creates a post processor instance with the given properties.
	 *
	 *
	 * @param logUtils the oas-generator internal logging utils class
	 * @param parserProperties the properties from the configuration file
	 * @return an instance of the post processor
	 */
	OpenAPIPostProcessor create(LogUtils logUtils, ParserProperties parserProperties);

}
