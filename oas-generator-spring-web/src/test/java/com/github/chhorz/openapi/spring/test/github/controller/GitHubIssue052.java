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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("github")
public class GitHubIssue052 {

	/**
	 *
	 * @security read_role
	 *
	 * @response 204 java.lang.Void the status code
	 *
	 * @param test
	 * @return
	 */
	@GitHubIssue("#52")
	@GetMapping(path = "/issues")
	@Deprecated
	public ResponseEntity<Void> test1(@PathVariable String test) {
		return ResponseEntity.noContent().build();
	}

	/**
	 *
	 * @security read_role
	 *
	 * @param test
	 * @return
	 */
	@GitHubIssue("#52")
	@GetMapping(path = "/issues")
	public ResponseEntity<Void> test2(@PathVariable String test) {
		return ResponseEntity.ok().build();
	}

}
