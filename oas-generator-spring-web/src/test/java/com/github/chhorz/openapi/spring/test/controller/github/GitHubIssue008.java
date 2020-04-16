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
package com.github.chhorz.openapi.spring.test.controller.github;

import com.github.chhorz.openapi.common.test.github.GithubIssue;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.UUID;

import static java.util.Collections.singletonList;
import static org.springframework.http.ResponseEntity.ok;

@RequestMapping("github")
public class GitHubIssue008 {

	/**
	 * @security read_role
	 * @security dummy_test_role
	 *
	 * @return a list of tags
	 */
	@GithubIssue("#8")
	@GetMapping(path = "/issue")
	public ResponseEntity<List<Test>> test() {
		Test test = new Test(UUID.randomUUID().toString());
		return ok(singletonList(test));
	}

	public static class Test {
		private String id;

		public Test(String id) {
			this.id = id;
		}

		public String getId() {
			return id;
		}
	}

}
