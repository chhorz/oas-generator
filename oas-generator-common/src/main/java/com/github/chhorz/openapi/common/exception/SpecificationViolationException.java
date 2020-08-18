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
package com.github.chhorz.openapi.common.exception;

/**
 * This exception will be thrown if the openapi specification will be violated at some point. There may be some reasons
 * for the violation:
 * <ul>
 *     <li>Missing required field</li>
 * </ul>
 *
 * @author chhorz
 */
public class SpecificationViolationException extends RuntimeException {

	/**
	 * The openapi specification was violated because of the given reason.
	 *
	 * @param message the reason of the violation
	 */
	public SpecificationViolationException(String message) {
		super(message);
	}

}
