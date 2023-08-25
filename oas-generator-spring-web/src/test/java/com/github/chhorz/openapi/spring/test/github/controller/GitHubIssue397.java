package com.github.chhorz.openapi.spring.test.github.controller;

import com.github.chhorz.openapi.common.test.github.GitHubIssue;
import com.github.chhorz.openapi.spring.test.github.resources.Resource;
import com.github.chhorz.openapi.spring.test.github.resources.TestResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@GitHubIssue("#397")
@RequestMapping("github")
public class GitHubIssue397 {

	/**
	 * Version 1
	 *
	 * @param test some test variable
	 * @return the response
	 */
	@PostMapping(path = "/issues", consumes = {"application/json", "application/vnd.test.v1+json"})
	public ResponseEntity<Void> test1(@RequestBody Resource test) {
		return ResponseEntity.ok().build();
	}

	/**
	 * Version 2
	 *
	 * @param test some test variable
	 * @return the new response
	 */
	@PostMapping(path = "/issues", consumes = "application/vnd.test.v2+json")
	public ResponseEntity<Void> test2(@RequestBody TestResource test) {
		return ResponseEntity.ok().build();
	}

}
