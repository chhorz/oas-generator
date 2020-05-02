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
import com.github.chhorz.openapi.spring.test.github.resources.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("github")
public class GitHubIssue018 {

	/**
	 * Test method A.
	 *
	 * @param a parameter a for method a
	 * @return a list of resources
	 */
	@GithubIssue("#18")
	@GetMapping(path = "/issues", params = "a", produces = "*/*")
	public ResponseEntity<List<Resource>> testParamA(@RequestParam String a) {
		return ResponseEntity.ok().build();
	}

	/**
	 * Test method B.
	 *
	 * @param b parameter b for method b
	 * @return a list of resources
	 */
	@GetMapping(path = "/issues", params = "b", produces = "*/*")
	public ResponseEntity<List<Resource>> testParamB(@RequestParam String b) {
		return ResponseEntity.ok().build();
	}

}
