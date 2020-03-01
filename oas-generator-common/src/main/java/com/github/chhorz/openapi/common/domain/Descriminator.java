package com.github.chhorz.openapi.common.domain;

import java.util.Map;

import com.github.chhorz.openapi.common.domain.meta.Required;

/**
 * https://spec.openapis.org/oas/v3.0.3#discriminator-object
 *
 * @author chhorz
 *
 */
public class Descriminator {

	@Required
	private String propertyName;
	private Map<String, String> mapping;

}
