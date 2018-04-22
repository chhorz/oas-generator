package com.github.chhorz.openapi.spring.test.controller.resource;

import java.util.List;

/**
 * The order.
 *
 * @author chhorz
 *
 */
public class Order {

	/**
	 * The order number.
	 */
	private Long number;
	/**
	 * The ordered article.
	 */
	private List<Article> article;
	/*
	 * A list of reference numbers.
	 */
	private List<String> referenceNumber;

	public Long getNumber() {
		return number;
	}

	public void setNumber(final Long number) {
		this.number = number;
	}

	public List<Article> getArticle() {
		return article;
	}

	public void setArticle(final List<Article> article) {
		this.article = article;
	}

	public List<String> getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(final List<String> referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

}
