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

import com.github.chhorz.openapi.spring.test.controller.resource.Article;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RequestMapping("test")
public class HttpMethodsController {

    @RequestMapping(value = "getrequest", method = GET)
    public List<Article> getRequest(){
        return Collections.emptyList();
    }

    @GetMapping("get")
    public Article get(){
        return new Article();
    }

    @PostMapping("post")
    public void post(Article article){

    }

    @PutMapping("put")
    public Article put(Article article){
        return new Article();
    }

    @PatchMapping("patch")
    public Article patch(Article article){
        return new Article();
    }

    @DeleteMapping("delete")
    public void delete(Long id){

    }

}
