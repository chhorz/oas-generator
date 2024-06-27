package com.github.chhorz.openapi.spring.test.github.controller;

import com.github.chhorz.openapi.common.test.github.GitHubIssue;
import com.github.chhorz.openapi.spring.test.github.resources.EnumResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@GitHubIssue("#999")
@RequestMapping("github")
public class GitHubIssue495 {

	@GetMapping(path = "/issues", produces = "application/json")
	public ResponseEntity<EnumResource> test() {
		return ResponseEntity.ok(new EnumResource());
	}

}
