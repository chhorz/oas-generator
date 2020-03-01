package com.github.chhorz.openapi.common.domain;

/**
 * https://spec.openapis.org/oas/v3.0.3#oauth-flows-object
 *
 * @author chhorz
 *
 */
public class OAuthFlows {

	private OAuthFlow implicit;
	private OAuthFlow password;
	private OAuthFlow clientCredentials;
	private OAuthFlow authorizationCode;

}
