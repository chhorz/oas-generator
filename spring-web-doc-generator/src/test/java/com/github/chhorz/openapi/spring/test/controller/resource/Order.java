package com.github.chhorz.openapi.spring.test.controller.resource;

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
	private Article article;

	public Long getNumber() {
		return number;
	}

	public void setNumber(final Long number) {
		this.number = number;
	}

	public Article getArticle() {
		return article;
	}

	public void setArticle(final Article article) {
		this.article = article;
	}

}
