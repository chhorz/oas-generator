package com.github.chhorz.openapi.common.domain;

/**
 * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#oauth-flows-object
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
