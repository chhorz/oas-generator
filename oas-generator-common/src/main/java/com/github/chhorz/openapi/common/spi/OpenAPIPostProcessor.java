/**
 *
 *    Copyright 2018-2021 the original author or authors.
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

import com.github.chhorz.openapi.common.domain.OpenAPI;

import java.nio.file.Path;
import java.util.List;

/**
 * This interface declares the actual post-processing method. All post-processing
 * methods that should be supported must override the default implementation within
 * this interface.
 *
 * @author chhorz
 */
public interface OpenAPIPostProcessor {

	/**
	 * This method must be overridden for the actual post-processing call. Otherwise,
	 * the default implementation will throw an {@link UnsupportedOperationException}.
	 *
	 * @param openApi the parsed {@link OpenAPI} for which the post-processing
	 *                should be done
	 */
	default void execute(OpenAPI openApi) {
		throw new UnsupportedOperationException(
			"PostProcessor of type "+ PostProcessorType.DOMAIN_OBJECT +" is defined but method not overridden");
	}

	/**
	 * This method must be overridden for the actual post-processing call. Otherwise,
	 * the default implementation will throw an {@link UnsupportedOperationException}.
	 *
	 * @param content 			the written file content as JSON or YAML format
	 * @param postProcessorType the post processor type that can be used
	 *							to distinguish calls for JSON and YAML
	 */
	default void execute(String content, PostProcessorType postProcessorType) {
		throw new UnsupportedOperationException(
			"PostProcessor of type '" + postProcessorType + "' is defined but method not overridden");
	}

	/**
	 * This method must be overridden for the actual post-processing call. Otherwise,
	 * the default implementation will throw an {@link UnsupportedOperationException}.
	 *
	 * @param file 				the generated json or yaml file
	 * @param postProcessorType the post processor type that can be used
	 *							to distinguish calls for JSON and YAML
	 */
	default void execute(Path file, PostProcessorType postProcessorType) {
		throw new UnsupportedOperationException(
			"PostProcessor of type '" + postProcessorType + "' is defined but method not overridden");
	}

	/**
	 * Returns the value for the order in which the post processor should be
	 * executed. Possible values are between {@code Integer.MIN_VALUE} and
	 * {@code Integer.MAX_VALUE}. The processors will be executed <b>starting with
	 * the highest</b> value.
	 *
	 * @return the order in which the post processor should be executed
	 */
	int getPostProcessorOrder();

	/**
	 * Defines the input types of the current post processor. The post processor is
	 * executed at a different step of the generation process depending on the input
	 * type.
	 *
	 * @return a list of input types for the post processor
	 */
	List<PostProcessorType> getPostProcessorType();

}
