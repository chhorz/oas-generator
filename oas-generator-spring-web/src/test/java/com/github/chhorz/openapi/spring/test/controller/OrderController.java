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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.chhorz.openapi.spring.test.controller.resource.ErrorResource;
import com.github.chhorz.openapi.spring.test.controller.resource.Order;
import com.github.chhorz.openapi.spring.test.controller.resource.PrimitiveResource;

@RequestMapping(path = "/orders")
public class OrderController {

	/**
	 * Get an order with an {@code id}.
	 *
	 * Lorem ipsum.
	 *
	 * @category order
	 *
	 * @response 200 Order[] Returns a list of matching orders
	 * @response 500 ErrorResource The resource containing error information
	 *
	 * @param id
	 *            the identifier
	 * @param filter
	 *            the filter that can be applied
	 * @return a list of orders that match the optional filters
	 */
	@RequestMapping(value = "/{id:\\d+}", method = RequestMethod.GET, produces = { "application/json", "application/xml" })
	public List<Order> getOrder(@PathVariable final Long id,
			@RequestParam(defaultValue = "valid=true", required = false) final String filter) {
		return new ArrayList<>();
	}

	/**
	 *
	 * @category order
	 * @category test
	 */
	@RequestMapping(method = RequestMethod.POST, consumes = { "text/plain", "application/xml" })
	public Set<PrimitiveResource> createOrder(@RequestBody final Order order) {
		return null;
	}

	/**
	 *
	 * @category order
	 * @category test
	 */
	@PreAuthorize("read_role")
	@GetMapping(produces = { "application/xml" })
	public ResponseEntity<List<Order>> getOrders() {
		return null;
	}

	@ExceptionHandler
	public ResponseEntity<ErrorResource> handleException(final Exception e) {
		return null;
	}

}
