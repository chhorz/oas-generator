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

/**
 * This interface declares the actual post processing method.
 *
 * @author chhorz
 */
public interface OpenAPIPostProcessor {

    /**
     * The actual post processing call.
     *
     * @param openApi the parsed {@link OpenAPI} for which the post processing
     *                should be done
     */
    void execute(OpenAPI openApi);

    /**
     * Returns the value for the order in which the post processor should be
     * executed. Possible values are between {@code Integer.MIN_VALUE} and
     * {@code Integer.MAX_VALUE}. The processors will be executed <b>starting with
     * the highest</b> value.
     *
     * @return the order in which the post processor should be executed
     */
    int getPostProcessorOrder();

}
