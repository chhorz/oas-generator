package com.github.chhorz.openapi.common.domain;

import com.github.chhorz.openapi.common.domain.meta.Required;

/**
 * http://spec.openapis.org/oas/v3.0.3#implicit-oauth2-sample
 *
 * @author chhorz
 *
 */
public class SecuritySchemeOAuth2 extends SecurityScheme {

	@Required
	private OAuthFlows flows;

}
