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
