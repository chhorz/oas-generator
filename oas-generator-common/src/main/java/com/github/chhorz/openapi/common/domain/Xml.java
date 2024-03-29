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

/**
 * https://spec.openapis.org/oas/v3.1.0#xml-object
 *
 * @author chhorz
 *
 */
public class Xml {

	private String name;
	private String namespace;
	private String prefix;
	private Boolean attribute = Boolean.FALSE;
	private Boolean wrapped = Boolean.FALSE;

}
