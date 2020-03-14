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
