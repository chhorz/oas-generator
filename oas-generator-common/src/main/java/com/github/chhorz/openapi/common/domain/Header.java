package com.github.chhorz.openapi.common.domain;

/**
 * http://spec.openapis.org/oas/v3.0.3#header-object
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
