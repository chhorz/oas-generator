package com.github.chhorz.openapi.spring.test.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.chhorz.openapi.spring.test.controller.resource.Order;
import com.github.chhorz.openapi.spring.test.controller.resource.PrimitiveResource;

public class OrderController {

	/**
	 * Get an order with an id.
	 *
	 * Lorem ipsum.
	 *
	 * @category order
	 *
	 * @param id
	 *            the identifier
	 * @param filter
	 *            the filter that can be applied
	 */
	@RequestMapping(path = "/orders/{id:\\d+}", method = RequestMethod.GET, produces = { "application/json", "application/xml" })
	public Order getOrder(@PathVariable final Long id,
			@RequestParam(defaultValue = "valid=true", required = false) final String filter) {
		return new Order();
	}

	/**
	 *
	 * @category order
	 * @category test
	 */
	@RequestMapping(path = "/orders", method = RequestMethod.POST, consumes = { "text/plain", "application/xml" })
	public PrimitiveResource createOrder(@RequestBody final Order order) {
		return null;
	}

}
