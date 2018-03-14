package com.github.chhorz.openapi.spring.test.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.chhorz.openapi.spring.test.controller.resource.Article;

public class ArticleController {

	@RequestMapping(path = "/articles/{id}", method = RequestMethod.GET)
	public Article getArticle(@PathVariable final Long id, @RequestParam final String filter) {
		return new Article();
	}

	@RequestMapping(path = "/articles", method = RequestMethod.POST)
	public void createArticle(@RequestBody final Article article) {

	}

}
