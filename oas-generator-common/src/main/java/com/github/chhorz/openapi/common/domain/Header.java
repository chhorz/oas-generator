package com.github.chhorz.openapi.common.domain;

/**
 * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#header-object
 *
 * @author chhorz
 *
 */
public class Header {

	// fixed fields
	private String description;
	private Boolean required = Boolean.FALSE;
	private Boolean deprecated;
	private Boolean allowEmptyValue;

}
