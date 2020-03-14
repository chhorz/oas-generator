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
