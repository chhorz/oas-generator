package com.github.chhorz.openapi.spring.test.github.resources;

public abstract class AbstractResource<T> extends AbstractBaseResource<Long> {

	/**
	 * the resource content
	 */
	public T content;

}
