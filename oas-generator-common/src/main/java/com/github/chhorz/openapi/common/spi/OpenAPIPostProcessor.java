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

import com.github.chhorz.openapi.common.domain.OpenAPI;

import java.nio.file.Path;
import java.util.List;

/**
 * This interface declares the actual post processing method. If only one or two
 * execute methods should be supported, the others should throw an {@link UnsupportedOperationException}.
 *
 * @author chhorz
 */
public interface OpenAPIPostProcessor {

	/**
	 * The actual post processing call. If this method for the internal domain
	 * model should not be supported from the custom post processor, the processor
	 * should throw an {@link UnsupportedOperationException}.
	 *
	 * @param openApi the parsed {@link OpenAPI} for which the post processing
	 *                should be done
	 */
	void execute(OpenAPI openApi);

	/**
	 * The actual post processing call. If this method for the generated OpenAPI
	 * content should not be supported from the custom post processor, the processor
	 * should throw an {@link UnsupportedOperationException}.
	 *
	 * @param content 			the written file content as JSON or YAML format
	 * @param postProcessorType the post processor type that can be used
	 *							to distinguish calls for JSON and YAML
	 */
	void execute(String content, PostProcessorType postProcessorType);

	/**
	 * The actual post processing call. If this method for the generated file
	 * should not be supported from the custom post processor, the processor
	 * should throw an {@link UnsupportedOperationException}.
	 *
	 * @param file 				the generated json or yaml file
	 * @param postProcessorType the post processor type that can be used
	 *							to distinguish calls for JSON and YAML
	 */
	void execute(Path file, PostProcessorType postProcessorType);

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
