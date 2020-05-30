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

import com.github.chhorz.openapi.common.test.github.GithubIssue;
import com.github.chhorz.openapi.spring.test.github.resources.ErrorResource;
import com.github.chhorz.openapi.spring.test.github.resources.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("github")
public class GitHubIssue020 {

	/**
	 * Lorem ipsum
	 *
	 * @param resource the request body
	 * @return nothing but a http status
	 *
	 * @response 204 Void No Content
	 */
	@GithubIssue("#20")
	@PostMapping(path = "/issues", consumes = MediaType.APPLICATION_JSON_VALUE )
	public ResponseEntity<Void> test(@RequestBody Resource resource) {
		return ResponseEntity.noContent().build();
	}

	/**
	 * Lorem ipsum
	 *
	 * @param id the resource id
	 * @return nothing but a http status
	 *
	 * @response 204 Void No Content
	 */
	@GithubIssue("#25")
	@DeleteMapping(path = "/issues/{id}")
	public ResponseEntity<Void> post(@PathVariable String id) {
		return ResponseEntity.noContent().build();
	}

	/**
	 * Lorem ipsum
	 *
	 * @return an error resource with additional information
	 *
	 * @response 500 ErrorResource additional error information
	 */
	@GithubIssue("#25")
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ErrorResource> exceptionHandler() {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}

}
