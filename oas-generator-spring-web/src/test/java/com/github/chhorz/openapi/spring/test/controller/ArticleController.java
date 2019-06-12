package com.github.chhorz.openapi.spring.test.controller;

import org.springframework.web.bind.annotation.*;

import com.github.chhorz.openapi.spring.test.controller.resource.Article;

import java.util.ArrayList;
import java.util.List;

public class ArticleController {

	@RequestMapping(path = "/articles/{id}", method = RequestMethod.GET)
	public Article getArticle(
			@PathVariable final Long id,
			@RequestParam(required = false, defaultValue = "") final String filter) {
		return new Article();
	}

	@RequestMapping(path = "/articles", method = RequestMethod.POST)
	public void createArticle(@RequestBody final Article article) {}

	@GetMapping(path = "/articles")
	public List<Article> getArticles(){
		return new ArrayList<>();
	}

}
