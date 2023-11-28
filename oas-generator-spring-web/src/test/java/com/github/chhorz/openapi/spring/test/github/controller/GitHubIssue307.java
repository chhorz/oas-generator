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

import com.github.chhorz.openapi.common.test.github.GitHubIssue;
import com.github.chhorz.openapi.spring.test.github.resources.*;
import org.springframework.beans.factory.parsing.Problem;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("github")
public class GitHubIssue307 {


	/**
	 * Lorem ipsum
	 *
	 * @param id the resource id
	 * @return the resource
	 *
	 * @response 200 Resource the resource by id
	 * @response 4XX ProblemResource the problem resource
	 * @response 5XX ErrorResource the error resource
	 */
	@GitHubIssue("#307")
	@GetMapping(path = "/issues/{id}")
	public ResponseEntity<Resource> get(@PathVariable String id) {
		return ResponseEntity.ok().build();
	}

	/**
	 * Lorem ipsum
	 *
	 * @return an error resource with additional information
	 *
	 * @response 500 ErrorResource additional error information
	 */
	@GitHubIssue("#307")
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ErrorResource> exceptionHandler() {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}

	/**
	 * Lorem ipsum
	 *
	 * @return an error resource with additional information
	 *
	 * @response 400 ProblemResource additional error information
	 */
	@GitHubIssue("#307")
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ProblemResource> exceptionHandlerForIllegalArguments() {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	}

}
