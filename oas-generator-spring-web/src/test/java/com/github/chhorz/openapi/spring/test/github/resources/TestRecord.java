package com.github.chhorz.openapi.spring.test.github.resources;

/**
 * Resource for content data
 *
 * @param id    the internal database id
 * @param value the content value
 */
public record TestRecord(Long id,
						 String value) {}
