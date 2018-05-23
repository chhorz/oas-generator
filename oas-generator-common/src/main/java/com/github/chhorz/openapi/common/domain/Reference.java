package com.github.chhorz.openapi.common.domain;

import com.github.chhorz.openapi.common.domain.meta.Required;

/**
 * https://github.com/OAI/OpenAPI-Specification/blob/v3.0.1/versions/3.0.1.md#reference-object
 *
 * @author chhorz
 *
 */
public class Reference {

	@Required
	private String $ref;

	public Reference(final String $ref) {
		this.$ref = $ref;
	}

	public String get$ref() {
		return $ref;
	}

	@Override
	public String toString() {
		return "Reference [$ref=" + $ref + "]";
	}

}
