package com.github.chhorz.openapi.spring.test.controller.github;

import com.github.chhorz.openapi.common.domain.Response;
import com.github.chhorz.openapi.common.test.github.GithubIssue;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("github")
public class TestController {

	@GithubIssue(number = 2)
	@GetMapping("2")
	public ResponseEntity test(){
		return ResponseEntity.ok().build();
	}
}
