package com.github.chhorz.openapi.spring.test.controller.resource;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The order.
 *
 * @author chhorz
 *
 */
public class Order extends BaseResource {

	/**
	 * The ordered article.
	 */
	private List<Article> article;
	/*
	 * A list of reference numbers.
	 */
	private List<String> referenceNumber;
	/*
	 * The date of the order.
	 */
	private LocalDateTime orderTs;
	/**
	 * An internal reference.
	 */
	@JsonIgnore
	private Long internalReference;

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

	public LocalDateTime getOrderTs() {
		return orderTs;
	}

	public void setOrderTs(final LocalDateTime orderTs) {
		this.orderTs = orderTs;
	}

	public Long getInternalReference() {
		return internalReference;
	}

	public void setInternalReference(final Long internalReference) {
		this.internalReference = internalReference;
	}

}
