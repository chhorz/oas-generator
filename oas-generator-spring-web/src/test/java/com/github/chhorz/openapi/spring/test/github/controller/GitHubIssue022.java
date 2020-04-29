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

import com.github.chhorz.openapi.common.annotation.OpenAPIExclusion;
import com.github.chhorz.openapi.common.test.github.GithubIssue;
import com.github.chhorz.openapi.spring.test.github.resources.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("github")
public class GitHubIssue022 {

	/**
	 * Lorem ipsum
	 *
	 * @param id the resource id
	 * @return some resource
	 *
	 * @response 200 Resource the resource that should be returned
	 */
	@GetMapping(path = "/issues/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
	public ResponseEntity<Resource> get(@PathVariable String id) {
		return ResponseEntity.ok().build();
	}

	@GithubIssue("#22")
	@OpenAPIExclusion
	@PostMapping(path = "/issues", consumes = MediaType.APPLICATION_JSON_VALUE )
	public ResponseEntity<Resource> post(@RequestBody Resource resource) {
		return ResponseEntity.status(HttpStatus.CREATED).body(resource);
	}

}
