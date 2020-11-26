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
package com.github.chhorz.openapi.spring.test.github.controller;

import com.github.chhorz.openapi.common.annotation.OpenAPI;
import com.github.chhorz.openapi.common.annotation.OpenAPIResponse;
import com.github.chhorz.openapi.common.test.github.GitHubIssue;
import com.github.chhorz.openapi.spring.test.github.resources.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("github")
public class GitHubIssue051 {

	/**
	 * @security read_role
	 *
	 * @response 200 Resource the response resource
	 * @response 404 java.lang.Void an empty body if nothing was found
	 *
	 * @tag test
	 * @category another_test
	 *
	 * @param id the requested id
	 * @return the response for the given id
	 */
	@GitHubIssue("#52")
	@GetMapping(path = "/issues/javadoc")
	public ResponseEntity<Resource> testJavadoc(@PathVariable Long id) {
		return ResponseEntity.ok().build();
	}

	/**
	 * @param id the requested id
	 * @return the response for the given id
	 */
	@GitHubIssue("#52")
	@OpenAPI(
		tags = {"test", "another_test"},
		securitySchemes = "read_role",
		responses = {
			@OpenAPIResponse(status = "200", schema = Resource.class, description = "the response resource"),
			@OpenAPIResponse(status = "404", schema = Void.class, description = "an empty body if nothing was found")
		})
	@GetMapping(path = "/issues/annotation")
	public ResponseEntity<Resource> testAnnotation(@PathVariable Long id) {
		return ResponseEntity.ok().build();
	}

}
