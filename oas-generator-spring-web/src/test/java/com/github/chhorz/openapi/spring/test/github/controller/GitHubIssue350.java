/**
 * Copyright 2018-2020 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.chhorz.openapi.spring.test.github.controller;

import com.github.chhorz.openapi.common.test.github.GitHubIssue;
import com.github.chhorz.openapi.spring.test.github.resources.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("github")
public class GitHubIssue350 {

	/**
	 * Lorem ipsum
	 *
	 * @param id the resource id
	 * @return the resource
	 *
	 * @response 200 Resource the resource by id
	 * @response 4XX java.lang.Void the 400 status code
	 */
	@GitHubIssue("#350")
	@GetMapping(path = "/issues/{id}")
	public ResponseEntity<Resource> get(@PathVariable String id) {
		return ResponseEntity.ok().build();
	}

	/**
	 * Lorem ipsum
	 *
	 * @return en empty body with an error status code
	 *
	 * @response 400 java.lang.Void the 400 status code
	 */
	@GitHubIssue("#350")
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Void> exceptionHandlerForIllegalArguments() {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	}

}
