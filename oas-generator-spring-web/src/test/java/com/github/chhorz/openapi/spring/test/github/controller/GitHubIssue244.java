package com.github.chhorz.openapi.spring.test.github.controller;

import com.github.chhorz.openapi.common.test.github.GitHubIssue;
import com.github.chhorz.openapi.spring.test.github.resources.TestRecord;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@GitHubIssue("#244")
@RequestMapping("github")
public class GitHubIssue244 {

	@GetMapping(path = "/issues", produces = "application/json")
	public ResponseEntity<TestRecord> test() {
		return ResponseEntity.ok().build();
	}

}
