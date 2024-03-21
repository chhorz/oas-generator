package com.github.chhorz.openapi.spring.test.github.controller;

import com.github.chhorz.openapi.common.annotation.OpenAPI;
import com.github.chhorz.openapi.common.annotation.OpenAPIResponse;
import com.github.chhorz.openapi.common.test.github.GitHubIssue;
import com.github.chhorz.openapi.spring.test.github.resources.TypedResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@GitHubIssue("#999")
@RequestMapping("github")
public class GitHubIssue463 {

	@GetMapping(path = "/issues/1")
	public ResponseEntity<TypedResource<Long>> test1() {
		return ResponseEntity.ok().build();
	}

	@OpenAPI(responses = {
		@OpenAPIResponse(status = "2xx", schema = TypedResource.class, description = "...")
	})
	@GetMapping(path = "/issues/2")
	public ResponseEntity<TypedResource<String>> test2() {
		return ResponseEntity.ok().build();
	}

}
