package com.github.chhorz.openapi.common.domain;

import java.util.Map;

import com.github.chhorz.openapi.common.domain.meta.Required;

/**
 * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#discriminator-object
 *
 * @author chhorz
 *
 */
public class Descriminator {

	@Required
	private String propertyName;
	private Map<String, String> mapping;

}
