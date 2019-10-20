package com.github.chhorz.openapi.common.domain;

import com.github.chhorz.openapi.common.domain.meta.Required;

/**
 * http://spec.openapis.org/oas/v3.0.2#security-scheme-object
 *
 * @author chhorz
 *
 */
public class SecuritySchemeOpenIdConnect extends SecurityScheme {

	@Required
	private String openIdConnectUrl;

}
