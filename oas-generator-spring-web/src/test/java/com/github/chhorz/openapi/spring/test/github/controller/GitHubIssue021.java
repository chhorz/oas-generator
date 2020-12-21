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
import com.github.chhorz.openapi.spring.test.github.resources.ValidResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("github")
public class GitHubIssue021 {

	/**
	 * Lorem ipsum
	 *
	 * @param resource the request body
	 * @return nothing but a http status
	 *
	 * @response 201 ValidResource The created resource
	 */
	@GitHubIssue("#21")
	@PostMapping(path = "/issues", consumes = MediaType.APPLICATION_JSON_VALUE )
	public ResponseEntity<ValidResource> test(@RequestBody ValidResource resource) {
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

}
