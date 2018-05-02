package com.github.chhorz.openapi.spring.test.controller.resource;

import java.math.BigDecimal;

/**
 * An article that can be ordered.
 *
 * @author chhorz
 *
 */
public class Article extends BaseResource {

	private String name;
	private Type type;
	/*
	 * The current price.
	 */
	private BigDecimal price;

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public Type getType() {
		return type;
	}

	public void setType(final Type type) {
		this.type = type;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(final BigDecimal price) {
		this.price = price;
	}

}
