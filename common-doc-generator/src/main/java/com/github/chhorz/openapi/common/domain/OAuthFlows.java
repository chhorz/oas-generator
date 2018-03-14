package com.github.chhorz.openapi.common.domain;

/**
 * https://github.com/OAI/OpenAPI-Specification/blob/v3.0.1/versions/3.0.1.md#oauth-flows-object
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
