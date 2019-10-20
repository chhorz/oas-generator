package com.github.chhorz.openapi.common.domain;

/**
 * http://spec.openapis.org/oas/v3.0.2#oauth-flows-object
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
