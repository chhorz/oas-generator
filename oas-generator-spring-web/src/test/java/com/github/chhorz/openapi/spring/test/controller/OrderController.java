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
	 * @response 200 com.github.chhorz.openapi.spring.test.controller.resource.Order
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
